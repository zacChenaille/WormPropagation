/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package worm;

/**
 *
 * @author Zac Chenaille
 */
public class Host {
    
    public final static int CLEAN = 0;
    public final static int INFECTED = 1;
    
    private int status = CLEAN;
    
    private int ipAddress;
    
    public Host(int ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public int getIpAddress() {
        return ipAddress;
    }
    
    public boolean isInfected() {
        return (status == INFECTED);
    }
    
    public void infect() {
        status = INFECTED;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Host other = (Host) obj;
        if (this.ipAddress != other.ipAddress) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + this.ipAddress;
        return hash;
    }

}
