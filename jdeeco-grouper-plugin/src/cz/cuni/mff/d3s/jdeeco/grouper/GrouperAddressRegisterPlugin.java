/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.grouper;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.model.runtime.api.EnsembleDefinition;
import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.jdeeco.core.AddressHelper;
import cz.cuni.mff.d3s.jdeeco.core.EnsembleDeployerPlugin;
import cz.cuni.mff.d3s.jdeeco.network.address.IPAddress;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class GrouperAddressRegisterPlugin implements DEECoPlugin {

	private RangeConfigurator rangeConfig;
	
	private GrouperPartitions partitions = new GrouperPartitions();
	private GrouperRegister register;
	
	public GrouperPartitions getPartitions() {
		return partitions;
	}
	public GrouperRegister getRegister() {
		return register;
	}
	
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
