/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.grouper;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.model.runtime.api.EnsembleDefinition;
import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.jdeeco.core.AddressHelper;
import cz.cuni.mff.d3s.jdeeco.core.EnsembleDeployerPlugin;
import cz.cuni.mff.d3s.jdeeco.network.address.IPAddress;

/**
 * Plugin providing a register used by the grouper to store group memberships 
 * and partition functions.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class GrouperAddressRegisterPlugin implements DEECoPlugin {

	// service assigning ranges to groupers
	private RangeConfigurator rangeConfig;
	// partitions of the grouper deployed on the same node
	private GrouperPartitions partitions = new GrouperPartitions();
	// grouper storage of group members
	private GrouperRegister register;
	
	/**
	 * Gets partitions of the current grouper.
	 * 
	 * @return Partition collection.
	 */
	public GrouperPartitions getPartitions() {
		return partitions;
	}
	/**
	 * Gets register of the current grouper containing members of already formed groups.
	 * 
	 * @return Grouper register.
	 */
	public GrouperRegister getRegister() {
		return register;
	}
	/**
	 * Creates a new instance of plugin providing register and partitions of grouper
	 * deployed on the same node.
	 * 
	 * @param rangeConfig Configuration service assigning ranges to individual groupers.
	 */
	public GrouperAddressRegisterPlugin(RangeConfigurator rangeConfig) {
		this.rangeConfig = rangeConfig;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(EnsembleDeployerPlugin.class);
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		
		for (EnsembleDefinition ens : container.getRuntimeMetadata().getEnsembleDefinitions()) {
			KnowledgePartition part = new FieldKnowledgePartition(ens.getPartitionedBy());
			GrouperRange range = this.rangeConfig.getRange(ens, container.getId());
			
			this.partitions.addPartition(part, range);
		}
		
		IPAddress addr = AddressHelper.createIP(container.getId());
		this.register =  new GrouperRegister(addr);
	}

}
