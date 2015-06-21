/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.grouper.server;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.Local;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.jdeeco.core.AddressHelper;
import cz.cuni.mff.d3s.jdeeco.core.KnowledgeProvider;
import cz.cuni.mff.d3s.jdeeco.grouper.GrouperPartitions;
import cz.cuni.mff.d3s.jdeeco.grouper.GrouperRange;
import cz.cuni.mff.d3s.jdeeco.grouper.GrouperRegister;
import cz.cuni.mff.d3s.jdeeco.grouper.GrouperRole;
import cz.cuni.mff.d3s.jdeeco.grouper.KnowledgePartition;
import cz.cuni.mff.d3s.jdeeco.network.address.Address;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
@Component
public class GrouperServerComponent {
	public String id;
	public GrouperRole grouperRole = GrouperRole.server;
	public String group = "destination";
	/**
	 * This field is dynamically modified when published to particular hosts.
	 * Knowledge of this component can not be published on MANET as it doesn't
	 * have sense. MANET nodes do not have addresses so there are no communication
	 * groups.
	 */
	public Set<String> groupMembers = new HashSet<String>();
	
	// Key space range - current connector stores values for this collection of keys
	// for now suppose that key space partitioning is given.
	// In the future it can be updated by the IPService
	//@Local public GrouperRange range;
	@Local public GrouperPartitions partitions;
	@Local public KnowledgeProvider knowledgeProvider;
	@Local public GrouperRegister register;
	
	/**
	 * 
	 */
	public GrouperServerComponent(int nodeId, GrouperPartitions partitions, GrouperRegister register, KnowledgeProvider knowledgeProvider) {
		this.id = AddressHelper.encodeID("GS", nodeId);
		this.partitions = partitions;
		this.knowledgeProvider = knowledgeProvider;	
		this.register = register;
	}
	
	@Process
	@PeriodicScheduling(period = 1000)
	public static void processKnowledge(
			@In("partitions") GrouperPartitions partitions,
			@In("knowledgeProvider") KnowledgeProvider knowledgeProvider,
			@In("register") GrouperRegister register) {
		
		// process all replica knowledge received from other components
		for (KnowledgeData kd : knowledgeProvider.getReplicaKnowledgeData()) {
			Address sender = AddressHelper.decodeAddress(kd.getMetaData().componentId);
			try {
				// component knowledge may participate in multiple partitions
				for (Entry<KnowledgePartition, GrouperRange> item : partitions.getPartitions()) {
					KnowledgePartition part = item.getKey();
					GrouperRange range = item.getValue();
					
					Object partVal = part.getPartitionByValue(kd);				
					if (range.inRange(partVal)) {
						// this grouper is responsible for this component
						register.add(partVal, sender);
					} else {
						// there is another grouper responsible for this component
	
						// if this grouper was responsible for current knowledge it is not any more
						//register.remove(partVal, sender);
						register.remove(sender);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
