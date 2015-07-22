/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.grouper.client;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.runtime.DuplicateEnsembleDefinitionException;
import cz.cuni.mff.d3s.jdeeco.core.AddressHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.AddressRegister;
import cz.cuni.mff.d3s.jdeeco.gossip.AddressRegisterPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.send.SendKNPlugin;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.address.IPAddress;

/**
 * Plugin allows to receive notifications from grouper about members of communication group.
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class GrouperClientPlugin implements DEECoPlugin {
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(Network.class, AddressRegisterPlugin.class, SendKNPlugin.class);
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {	
		AddressRegister register = container.getPluginInstance(AddressRegisterPlugin.class).getRegister();
		
		try {
			
			container.deployComponent(new GrouperClientComponent(container.getId(), register));
			container.deployEnsemble(GrouperClientEnsemble.class);
			
		} catch (AnnotationProcessorException | DuplicateEnsembleDefinitionException e) {
			e.printStackTrace();
		}
		
		SendKNPlugin sendKN = container.getPluginInstance(SendKNPlugin.class);
		IPAddress address = AddressHelper.createIP(container.getId());
		ClientRecipinetSelector selector = new ClientRecipinetSelector(address, sendKN.getRecipientSelector());
		sendKN.setRecipientSelector(selector);
	}
	

}
