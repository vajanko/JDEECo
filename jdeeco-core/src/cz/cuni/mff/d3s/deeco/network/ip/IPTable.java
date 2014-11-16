/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.ip;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Encapsulates a collection of IP addresses known by a particular host.
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class IPTable {
	private Set<String> register;
	
	/**
	 * Add given IP address to the current collection if it does not contain yet.
	 * If IP address already exists in the collection it won't be affected. 
	 * @param address IP address to be added.
	 */
	public void addAddress(String address) {
		register.add(address);
	}
	public void removeAddress(String address) {
		register.remove(address);
	}
	public Collection<String> getAddresses() {
		return register;
	}
	
	public IPTable() {
		this.register = new HashSet<String>();
	}
	public IPTable(Collection<String> initialEntries) {
		this.register = new HashSet<String>(initialEntries);
	}
}
