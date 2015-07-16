/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.grouper.client;

import java.util.HashSet;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.Local;
import cz.cuni.mff.d3s.deeco.annotations.TriggerOnChange;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.jdeeco.core.AddressHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.AddressRegister;
import cz.cuni.mff.d3s.jdeeco.grouper.GrouperRole;
import cz.cuni.mff.d3s.jdeeco.network.address.IPAddress;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
@Component
public class GrouperClientComponent {
	public String id;
	/**
	 * Component role
	 */
	public GrouperRole grouperRole = GrouperRole.client;
	/**
	 * Collection of nodes in the communication group.
	 */
	public Set<String> groupMembers;
	/**
	 * Register of addresses known by current node.
	 */
	@Local public AddressRegister register;
	
	public GrouperClientComponent(int nodeId, AddressRegister register) {
		this.id = AddressHelper.encodeID("GC", nodeId);
		this.groupMembers = new HashSet<String>();
		this.register = register;
	}
	
	/**
	 * Process addresses of group members received from grouper component
	 * 
	 * @param groupMembers Collection of group member to be added to the local register.
	 * @param register Local register of known addresses.
	 */
	@Process
	public static void processMembers(
			@In("id") String id,
			@TriggerOnChange @In("groupMembers") Set<String> groupMembers,
			@In("register") AddressRegister register) {
		
		register.clear();
		for (String address : groupMembers)
			register.add(new IPAddress(address));
				
		groupMembers.clear();
		
		//System.out.println(id + " - register: " + register);
	}
}
