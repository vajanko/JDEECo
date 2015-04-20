/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.register;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.jdeeco.network.address.MANETBroadcastAddress;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class AddressRegisterPlugin implements DEECoPlugin {
	
	private AddressRegister register = new AddressRegister();
	
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
		// this address won't be used if there is no boradcast device
		this.register.add(MANETBroadcastAddress.BROADCAST);
	}

}
