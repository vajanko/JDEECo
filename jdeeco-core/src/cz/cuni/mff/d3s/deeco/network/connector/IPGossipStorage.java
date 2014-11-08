/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.connector;

import java.util.Set;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public interface IPGossipStorage {
	Set<String> getAndUpdate(Object key, String address);
}
