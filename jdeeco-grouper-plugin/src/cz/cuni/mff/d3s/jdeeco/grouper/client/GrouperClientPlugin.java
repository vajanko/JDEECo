/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.grouper.client;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.runtime.DuplicateEnsembleDefinitionException;
import cz.cuni.mff.d3s.jdeeco.gossip.register.AddressRegister;
import cz.cuni.mff.d3s.jdeeco.gossip.register.AddressRegisterPlugin;
import cz.cuni.mff.d3s.jdeeco.network.Network;

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
		return Arrays.asList(Network.class, AddressRegisterPlugin.class);
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {	
		AddressRegister addressRegister = container.getPluginInstance(AddressRegisterPlugin.class).getRegister();
		
		try {
			
			container.deployComponent(new GrouperClientComponent(container.getId(), addressRegister));
			container.deployEnsemble(GrouperClientEnsemble.class);
			
		} catch (AnnotationProcessorException | DuplicateEnsembleDefinitionException e) {
			e.printStackTrace();
		}
	}
	

}
