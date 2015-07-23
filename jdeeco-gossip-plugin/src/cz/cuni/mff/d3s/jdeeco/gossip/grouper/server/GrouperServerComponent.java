/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.grouper.server;

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
import cz.cuni.mff.d3s.jdeeco.core.ReplicaKnowledgeSource;
import cz.cuni.mff.d3s.jdeeco.gossip.grouper.GrouperPartitions;
import cz.cuni.mff.d3s.jdeeco.gossip.grouper.GrouperRange;
import cz.cuni.mff.d3s.jdeeco.gossip.grouper.GrouperRegister;
import cz.cuni.mff.d3s.jdeeco.gossip.grouper.GrouperRole;
import cz.cuni.mff.d3s.jdeeco.gossip.grouper.KnowledgePartition;
import cz.cuni.mff.d3s.jdeeco.network.address.Address;

/**
 * Deeco component deployed on grouper nodes evaluating the received knowledge and
 * forming the communication groups.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
@Component
public class GrouperServerComponent {
	/**
	 * Unique component ID
	 */
	public String id;
	/**
	 * Component role
	 */
	public GrouperRole grouperRole = GrouperRole.server;
	
	//public String group = "destination";
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
	@Local public ReplicaKnowledgeSource knowledgeProvider;
	@Local public GrouperRegister register;
	
	/**
	 * Creates a new instance of Deeco component deployed on grouper node evaluating received knowledge
	 * and forming communication groups.
	 * 
	 * @param nodeId ID of node where this component is about to be deployed.
	 * @param partitions Partitions of the grouper.
	 * @param register Grouper register holding formed communication groups.
	 * @param knowledgeProvider Service providing deep copy of replica knowledge data.
	 */
	public GrouperServerComponent(int nodeId, GrouperPartitions partitions, GrouperRegister register, ReplicaKnowledgeSource knowledgeProvider) {
		this.id = AddressHelper.encodeID("GS", nodeId);
		this.partitions = partitions;
		this.knowledgeProvider = knowledgeProvider;	
		this.register = register;
	}
	
	@Process
	@PeriodicScheduling(period = 1000)
	public static void processKnowledge(
			@In("partitions") GrouperPartitions partitions,
			@In("knowledgeProvider") ReplicaKnowledgeSource knowledgeProvider,
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
