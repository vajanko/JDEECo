/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.ip;

import java.util.Collection;

/**
 * Manages and provides collection of IP addresses known by current host.
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public interface IPController {
	
	/**
	 * Retrieves IP table for a particular ensemble partition group.
	 *  
	 * @param partitionValue Value of partitionBy field name
	 * @return Instance of {@link IPTable} containing list of IP's which belongs to one ensemble
	 * partition group.
	 */
	IPTable getIPTable(Object partitionValue);
}
