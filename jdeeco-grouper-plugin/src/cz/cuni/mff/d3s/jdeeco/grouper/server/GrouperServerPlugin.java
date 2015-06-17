/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.grouper.server;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.model.runtime.api.EnsembleDefinition;
import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.jdeeco.core.AddressHelper;
import cz.cuni.mff.d3s.jdeeco.core.EnsembleDeployer;
import cz.cuni.mff.d3s.jdeeco.core.KnowledgeProvider;
import cz.cuni.mff.d3s.jdeeco.gossip.KnowledgeProviderPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.KnowledgeSource;
import cz.cuni.mff.d3s.jdeeco.gossip.RecipientSelector;
import cz.cuni.mff.d3s.jdeeco.gossip.register.AddressRegisterPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.send.SendKNPlugin;
import cz.cuni.mff.d3s.jdeeco.grouper.FieldKnowledgePartition;
import cz.cuni.mff.d3s.jdeeco.grouper.GrouperPartitions;
import cz.cuni.mff.d3s.jdeeco.grouper.GrouperRange;
import cz.cuni.mff.d3s.jdeeco.grouper.GrouperRegister;
import cz.cuni.mff.d3s.jdeeco.grouper.GrouperSetRange;
import cz.cuni.mff.d3s.jdeeco.grouper.KnowledgePartition;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.address.IPAddress;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class GrouperServerPlugin implements DEECoPlugin {

	private GrouperRegister grouperRegister;
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(Network.class, AddressRegisterPlugin.class, SendKNPlugin.class, EnsembleDeployer.class);
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		
		try {		
			// TODO: how to manage grouper ranges
			GrouperRange range = new GrouperSetRange(Arrays.asList("Berlin", "Prague"));
			
			// create grouper partitions
			GrouperPartitions partitions = new GrouperPartitions();
			for (EnsembleDefinition ens : container.getRuntimeMetadata().getEnsembleDefinitions())
				partitions.addPartition(new FieldKnowledgePartition(ens.getPartitionedBy()));
			
			// get dependency plugins
			KnowledgeProvider knowledgeProvider = container.getPluginInstance(KnowledgeProviderPlugin.class);

			IPAddress grouperAddr = AddressHelper.createIP(container.getId());
			this.grouperRegister =  new GrouperRegister(grouperAddr, range);
			
			// deploy grouper component
			GrouperServerComponent grouper = new GrouperServerComponent(container.getId(), range, partitions, grouperRegister, knowledgeProvider);
			container.deployComponent(grouper);
			
			// replace knowledge provider and recipient selector with a special grouper implementation
			SendKNPlugin sendKN = container.getPluginInstance(SendKNPlugin.class);
			KnowledgeSource knowledgeSource = new GrouperKnowledgeProvider(sendKN.getKnowledgeSource(), grouperRegister);
			sendKN.setKnowledgeSource(knowledgeSource);
			RecipientSelector recipinetSelector = new GrouperRecipientSelector(sendKN.getRecipientSelector());
			sendKN.setRecipientSelector(recipinetSelector);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
