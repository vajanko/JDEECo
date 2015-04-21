/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.grouper;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.model.runtime.api.EnsembleDefinition;
import cz.cuni.mff.d3s.deeco.runners.DEECoSimulation;
import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.jdeeco.core.KnowledgeProvider;
import cz.cuni.mff.d3s.jdeeco.gossip.KnowledgeProviderPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.buffer.MessageBuffer;
import cz.cuni.mff.d3s.jdeeco.gossip.register.AddressRegister;
import cz.cuni.mff.d3s.jdeeco.gossip.register.AddressRegisterPlugin;
import cz.cuni.mff.d3s.jdeeco.network.Network;

/**
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class GrouperPlugin implements DEECoPlugin {

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(MessageBuffer.class, KnowledgeProviderPlugin.class);
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		
		try {
			String grouperId = "G" + container.getId();
			
			container.deployEnsemble(GrouperEnsemble.class);
			
			// TODO: how to manage grouper ranges
			GrouperRange range = new GrouperRange(Arrays.asList("Berlin", "Prague"));
			// create grouper partitions
			GrouperPartitions partitions = new GrouperPartitions();
			for (EnsembleDefinition ens : container.getRuntimeMetadata().getEnsembleDefinitions())
				partitions.addPartition(new KnowledgePartition(ens.getPartitionedBy()));
			// get dependency plugins
			KnowledgeProvider knowledgeProvider = container.getPluginInstance(KnowledgeProviderPlugin.class);
			MessageBuffer messageBuffer = container.getPluginInstance(MessageBuffer.class);
			
			// deploy grouper component
			//GrouperComponent grouper = new GrouperComponent(grouperId, range, partitions, knowledgeProvider);
			//container.deployComponent(grouper);
		
		} catch (Exception e) {
			
		}
	}

	public static void registerPlugins(DEECoSimulation sim) {
		sim.addPlugin(GrouperPlugin.class);
	}
}
