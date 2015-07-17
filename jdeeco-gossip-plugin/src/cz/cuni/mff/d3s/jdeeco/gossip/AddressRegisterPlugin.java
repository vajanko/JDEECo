/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;

/**
 * Plugin providing a singleton instance of address register for particular node.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class AddressRegisterPlugin implements DEECoPlugin {
	
	// single instance of address register for a node
	private AddressRegister register = new AddressRegister();
	
	/**
	 * Gets the shared instance of address register of known peer nodes.
	 * 
	 * @return Address register of known peer nodes.
	 */
	public AddressRegister getRegister() {
		return register;
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList();
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		this.register.clear();
	}

}
