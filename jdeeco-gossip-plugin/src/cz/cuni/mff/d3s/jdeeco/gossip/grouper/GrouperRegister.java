/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.grouper;

import java.util.Collection;
import java.util.HashMap;

import cz.cuni.mff.d3s.jdeeco.gossip.AddressRegister;
import cz.cuni.mff.d3s.jdeeco.network.address.Address;

/**
 * Encapsulate register of groups managed by a grouper service.
 * TODO: remove addresses of outdated messages
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class GrouperRegister {
	// address of node where the grouper is deployed
	private Address address;
	// range of keys for which current grouper is responsible for
	//private GrouperRange range;
	
	/**
	 * Gets address of the node where the grouper is deployed.
	 * 
	 * @return Grouper node address.
	 */
	public Address getAddress() {
		return address;
	}
	
	// partition value associated with the group members
	private HashMap<Object, AddressRegister> register = new HashMap<Object, AddressRegister>();
	
	public void add(Object partitionValue, Address address) {
		AddressRegister reg = this.register.get(partitionValue);
		if (reg == null) {
			reg = new AddressRegister();
			this.register.put(partitionValue, reg);
			
			// grouper address is automatically added to the partition key belongs to its range
//			if (this.range.inRange(partitionValue))
//				reg.add(this.address);
		}
		
		reg.add(address);
	}
	/**
	 * Removes given address from all groups.
	 * 
	 * @param address Node address to be removed from all groups.
	 */
	public void remove(Address address) {
		for (AddressRegister reg : this.register.values()) {
			reg.remove(address);
		}
	}
	/**
	 * Removes given address from group with given partition value.
	 * 
	 * @param partitionValue Partition value of the group from which address should be removed.
	 * @param address Address which should be removed from the group.
	 */
	public void remove(Object partitionValue, Address address) {
		AddressRegister reg = this.register.get(partitionValue);
		if (reg == null)
			return;
		
		reg.remove(address);
		
		// If node register stays empty or only with single value (current grouper address)
		// the register can be removed. Range of a grouper can be very large and we don't
		// want to have a register for each possible value in the memory.
		if (reg.empty())
			this.register.remove(partitionValue);
		else if (reg.size() == 1 && reg.getAddresses().contains(this.address))
			this.register.remove(partitionValue);
	}
	/**
	 * Gets the collection of all groups in the grouper register.
	 * 
	 * @return Collection of address registers.
	 */
	public Collection<AddressRegister> getRegisters() {
		return this.register.values();
	}
	/**
	 * Gets members of group with particular partition value.
	 * 
	 * @param partitionValue Partition value of the required group. 
	 * @return Address register of group with particular partition value.
	 */
	public AddressRegister getRegister(Object partitionValue) {
		return this.register.get(partitionValue);
	}
	
	/**
	 * Create a new instance of grouper register storing members of communication groups.
	 * 
	 * @param address Address of node where the grouper is deployed.
	 */
	public GrouperRegister(Address address/*, GrouperRange range*/) {
		this.address = address;
		//this.range = range;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("address: %s, register: %s", address, register);
	}
}
