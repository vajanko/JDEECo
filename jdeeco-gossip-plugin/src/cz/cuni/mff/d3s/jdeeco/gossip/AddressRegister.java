/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import cz.cuni.mff.d3s.jdeeco.network.address.Address;

/**
 * Encapsulates a collection of node addresses.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class AddressRegister {
	private Set<Address> register;
	
	/**
	 * Add given address to the current register if it does not contain yet.
	 * If address already exists in the collection it won't be affected. 
	 * 
	 * @param addresses Addresses to be added.
	 */
	public void add(Address ... addresses) {
		addAll(Arrays.asList(addresses));
	}
	/**
	 * Add given collection of addresses to the current register if it does
	 * not contain yet. If any of address already exists the collection won't be
	 * affected.
	 * 
	 * @param addresses Collection of addresses to be added.
	 */
	public void addAll(Collection<Address> addresses) {
		register.addAll(addresses);
	}
	/**
	 * Remove given address from current address register.
	 * 
	 * @param address Address to be removed.
	 */
	public void remove(Address address) {
		register.remove(address);
	}
	/**
	 * Get collection of all addresses currently stored in the register.
	 * 
	 * @return Collection of addresses.
	 */
	public Collection<Address> getAddresses() {
		return register;
	}
	/**
	 * Get collection of all addresses currently stored in the registry as
	 * string values.
	 * 
	 * @return Collection of addresses as strings.
	 */
	public Collection<String> getStrings() {
		HashSet<String> res = new HashSet<String>();
		for (Address adr : this.register) {
			res.add(adr.toString());
		}
		
		return res;
	}
	/**
	 * Remove all addresses from the current register.
	 */
	public void clear() {
		register.clear();
	}
	/**
	 * Gets value indicating whether the register is empty.
	 * 
	 * @return true if register is empty otherwise false. 
	 */
	public boolean empty() {
		return register.isEmpty();
	}
	/**
	 * Gets number of addressed in current register.
	 * 
	 * @return number of addresses in current register.
	 */
	public int size() {
		return register.size();
	}
	/**
	 * Create a new empty instance of {@link IPRegister}
	 */
	public AddressRegister() {
		this.register = new HashSet<Address>();
	}
	/**
	 * Create a new instance of {@link AddressRegister} with initial list of entries
	 * @param initialEntries Collection of addresses to be initially added to the {@link AddressRegister}
	 */
	public AddressRegister(Collection<Address> initialEntries) {
		this.register = new HashSet<Address>(initialEntries);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return register.toString();
	}
}

