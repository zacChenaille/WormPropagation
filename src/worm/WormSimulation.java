package worm;

import java.io.PrintWriter;
import java.util.*;

/**
 *
 * @author Zac Chenaille
 */
public class WormSimulation {

    private static final int numSimulations = 100;
    
    private static final int numAddresses = 100000;
    private static final int hostsInCluster = 10;
    private static final int clusterOffset = 1000;
    
    /*
     * Derived variable based on what we've set for the total
     * address space, the cluster offset, and the number of hosts in a cluster
     */
    private static final int numHosts = numAddresses / (clusterOffset / hostsInCluster);
    
    //The number if scans that each infected host gets every time tick
    private static final int scanCount = 2;

    private static boolean localPreferenceMode = true;
    private static double localPrefProability = 0.4;
    
    public static void main(String[] args) {

        //Holds the data from all simulation runs (key=simulation run #, value=data)
        HashMap<Integer, List<Integer>> allRunsMap = new HashMap<Integer, List<Integer>>();
        
        for (int simNumber = 0; simNumber < numSimulations; simNumber++) {
            //Holds the I(t) values of each time tick
            List<Integer> infectedCounts = new ArrayList<Integer>();
            
            //Keeps track of all hosts that have been infected
            Set<Host> infectedHosts = new HashSet<Host>();
            
            //Maps the IP addresses that are actually being used to their respective hosts
            HashMap<Integer, Host> ipToHostMap = new HashMap<Integer, Host>();
            
            //Instantiate all of our hosts
            for (int clusterBase = 0; clusterBase < numAddresses; clusterBase += clusterOffset) {
                for (int clusterNum = 1; clusterNum <= hostsInCluster; clusterNum++) {
                    int ipAddress = clusterBase + clusterNum;
                    ipToHostMap.put(ipAddress, new Host(ipAddress));
                }
            }

            List<Integer> allHostIPs = new ArrayList<Integer>(ipToHostMap.keySet());

            //Initially infect one of the hosts randomly
            Integer infectedIP = (Integer)SimulationTools.randomElement(allHostIPs);
            ipToHostMap.get(infectedIP).infect();
            
            infectedHosts.add(ipToHostMap.get(infectedIP));
            infectedCounts.add(1);//We always have 1 infected host at time tick 0

            //Begin ticking...
            for (int tick = 1; infectedHosts.size() != ipToHostMap.values().size(); tick++) {
                //Create a copy of our infected hosts set to avoid concurrent modification of the original list
                Set<Host> infectedHostsCopy = new HashSet<Host>(infectedHosts);

                //Go through all infected hosts and have them attempt to infect others
                for (Host infectedHost : infectedHostsCopy) {
                    for (int scanNumber = 1; scanNumber <= scanCount; scanNumber++) {

                        Integer attemptIP;
                        if (localPreferenceMode && SimulationTools.generateBoolean(localPrefProability)) {
                            //If we are running in local preference mode, choose an IP address that is "close" to this infected host
                            attemptIP = SimulationTools.localRandomIpAddress(infectedHost.getIpAddress());
                        }
                        else {
                            //Choose a random IP from the entire IP address space
                            attemptIP = SimulationTools.randomIpAddress(numAddresses);
                        }

                        //If there is a host with the random IP we generated...
                        if (ipToHostMap.containsKey(attemptIP)) {
                            //Infect that host (won't matter if they are already infected)
                            Host hostToInfect = ipToHostMap.get(attemptIP);

                            hostToInfect.infect();
                            infectedHosts.add(hostToInfect);
                        }
                    }
                }
                infectedCounts.add(infectedHosts.size());
            }
            allRunsMap.put(simNumber, infectedCounts);
        }
        
        //Massage our average data so that it can be compared to the theoretical I(t) when it gets to MatLab
        
        //First, find the longest simulation run...
        int maxTimeTicks = 0;
        for (List<Integer> runData : allRunsMap.values()) {
            maxTimeTicks = Math.max(maxTimeTicks, runData.size());
        }
        
        int averageTimeToInfectAll = 0;
        //Next, make sure all of our run data are the same size (maximum time ticks of all simulations)
        for (List<Integer> runData : allRunsMap.values()) {
            averageTimeToInfectAll += runData.size();
            while(runData.size() != maxTimeTicks) {
                runData.add(numHosts);
            }
        }
        averageTimeToInfectAll = averageTimeToInfectAll / numSimulations;
        
        List<Integer> averageInfected = new ArrayList<Integer>();
        //Derive the average infected hosts per time tick
        for (int tick = 0; tick < maxTimeTicks; tick++) {
            int totalInfected = 0;
            for (List<Integer> runData : allRunsMap.values()) {
                totalInfected = totalInfected + runData.get(tick);
            }
            averageInfected.add(totalInfected/numSimulations);
        }

        PrintWriter avgWriter = null;
        PrintWriter run1Writer = null;
        PrintWriter run2Writer = null;
        PrintWriter run3Writer = null;
        try {
            //Write our data to file
            String fileName = "InfectedAvgData.csv";
            if (localPreferenceMode) {
                fileName = "InfectedAvgData-LocalPref.csv";
            }
            
            avgWriter = new PrintWriter(fileName, "UTF-8");
            for (int tick = 0; tick < averageInfected.size(); tick++) {
                avgWriter.print(averageInfected.get(tick));
                if (tick < averageInfected.size() - 1) {
                    avgWriter.print(",");
                }
            }
            
            //If we're not in local preference mode, also generate files for the first 3 runs
            if (!localPreferenceMode) {
                run1Writer = new PrintWriter("Run1Data.csv", "UTF-8");
                run2Writer = new PrintWriter("Run2Data.csv", "UTF-8");
                run3Writer = new PrintWriter("Run3Data.csv", "UTF-8");
                
                List<Integer> run1 = allRunsMap.get(0);
                List<Integer> run2 = allRunsMap.get(1);
                List<Integer> run3 = allRunsMap.get(2);
                
                for (int tick = 0; tick < maxTimeTicks; tick++) {
                    run1Writer.print(run1.get(tick));
                    run2Writer.print(run2.get(tick));
                    run3Writer.print(run3.get(tick));
                    if (tick < maxTimeTicks - 1) {
                        run1Writer.print(",");
                        run2Writer.print(",");
                        run3Writer.print(",");
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (avgWriter != null) {
                avgWriter.close();
            }
            if (run1Writer != null) {
                run1Writer.close();
            }
            if (run2Writer != null) {
                run2Writer.close();
            }
            if (run3Writer != null) {
                run3Writer.close();
            }
        }
        
        System.out.println("Average time to infect all:" + averageTimeToInfectAll);
        System.out.println();
        System.out.println("Total simulations run: " + allRunsMap.keySet().size());
    }    
}
