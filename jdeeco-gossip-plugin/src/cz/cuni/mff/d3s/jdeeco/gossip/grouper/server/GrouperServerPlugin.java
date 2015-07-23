/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.grouper.server;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.jdeeco.core.AddressHelper;
import cz.cuni.mff.d3s.jdeeco.core.ReplicaKnowledgeSource;
import cz.cuni.mff.d3s.jdeeco.core.KnowledgeProviderPlugin;
import cz.cuni.mff.d3s.jdeeco.core.LocalKnowledgeSource;
import cz.cuni.mff.d3s.jdeeco.gossip.grouper.GrouperAddressRegisterPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.grouper.GrouperPartitions;
import cz.cuni.mff.d3s.jdeeco.gossip.grouper.GrouperRegister;
import cz.cuni.mff.d3s.jdeeco.gossip.send.SendKNPlugin;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.address.IPAddress;

/**
 * Plugin which configures the current node and transforms it into the grouper
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class GrouperServerPlugin implements DEECoPlugin {
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(Network.class, SendKNPlugin.class, GrouperAddressRegisterPlugin.class);
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		
		try {
			// get dependency plugins
			ReplicaKnowledgeSource knowledgeProvider = container.getPluginInstance(KnowledgeProviderPlugin.class);
			
			GrouperAddressRegisterPlugin register = container.getPluginInstance(GrouperAddressRegisterPlugin.class);
			GrouperRegister grouperRegister =  register.getRegister();
			GrouperPartitions partitions = register.getPartitions();
			
			// deploy grouper component
			GrouperServerComponent grouper = new GrouperServerComponent(container.getId(), partitions, grouperRegister, knowledgeProvider);
			container.deployComponent(grouper);
			
			// replace knowledge provider and recipient selector with a special grouper implementation
			SendKNPlugin sendKN = container.getPluginInstance(SendKNPlugin.class);
			LocalKnowledgeSource knowledgeSource = new GrouperKnowledgeProvider(sendKN.getKnowledgeSource(), grouperRegister);
			sendKN.setKnowledgeSource(knowledgeSource);
			
			IPAddress address = AddressHelper.createIP(container.getId());
			ServerRecipientSelector selector = new ServerRecipientSelector(address, register.getPartitions(), sendKN.getRecipientSelector());
			sendKN.setRecipientSelector(selector);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
