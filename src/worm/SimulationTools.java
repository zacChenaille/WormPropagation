/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package worm;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Zac Chenaille
 */
public class SimulationTools {
    
    /**
     * Generates a boolean value of true with the probability specified.
     * <p>
     * The specified probability should be greater than 0 and less than or equal to 1.
     * </p>
     * 
     * @param trueProbability The probability for the method to return true
     * @return True based on the given probability, False otherwise
     */
    public static boolean generateBoolean(double trueProbability) {
        return Math.random() <= trueProbability;
    }
    
    /**
     * Retrieves and returns a random element from a list of elements.
     * 
     * @param objectList The list of elements to pick from
     * @return A random element from the given list
     */
    public static Object randomElement(List objects) {
        int randomIndex = 0 + (int)(Math.random() * (objects.size()));
        return objects.get(randomIndex);
    }
    
    /**
     * Returns a random IP address from the total address space.
     * 
     * @param numAddresses The total number of IP addresses in the address space
     * @return A random IP
     */
    public static Integer randomIpAddress(int numAddresses) {
        int randomIP = 0 + (int)(Math.random() * (numAddresses));
        return randomIP;
    }
    
    /**
     * Returns a random IP address from within the given IP address' cluster.
     * Specifically, this method will retrieve a random IP that is in [IP - 10, IP + 10]
     * 
     * @param clusterIP The IP address from which to find a random IP in the same cluster
     * @return A random IP within the same cluster as the given IP
     */
    public static Integer localRandomIpAddress(int clusterIP) {
        List<Integer> possibleIPs = new ArrayList<Integer>();
        for (int offset = 1; offset <= 10; offset++) {
            possibleIPs.add(clusterIP - offset);
            possibleIPs.add(clusterIP + offset);
        }
        return (Integer)randomElement(possibleIPs);
    }
    
    
    
}
