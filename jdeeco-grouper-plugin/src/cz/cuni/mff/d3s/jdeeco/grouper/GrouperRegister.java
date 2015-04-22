/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.grouper;

import java.util.Collection;
import java.util.HashMap;

import cz.cuni.mff.d3s.jdeeco.gossip.register.AddressRegister;
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
	private GrouperRange range;
	
	private HashMap<Object, AddressRegister> register = new HashMap<Object, AddressRegister>();
	
	public void add(Object partitionValue, Address address) {
		AddressRegister reg = this.register.get(partitionValue);
		if (reg == null) {
			reg = new AddressRegister();
			this.register.put(partitionValue, reg);
			
			// grouper address is automatically added to the partition key belongs to its range
			if (this.range.inRange(partitionValue))
				reg.add(this.address);
		}
		
		reg.add(address);
	}
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
	public Collection<AddressRegister> getRegisters() {
		return this.register.values();
	}
	
	public GrouperRegister(Address address, GrouperRange range) {
		this.address = address;
		this.range = range;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("address: %s, %s, register: %s", address, range, register);
	}
}
