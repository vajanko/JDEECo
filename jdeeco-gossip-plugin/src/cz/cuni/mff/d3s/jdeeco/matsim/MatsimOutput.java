/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsim;

import org.matsim.api.core.v01.Id;
import org.matsim.core.mobsim.framework.MobsimAgent.State;

/**
 * 
 * @author Ondrej Kov�� <info@vajanko.me>
 */
public class MatsimOutput {
	
	public Id currentLinkId;
	public State state;
	
	/**
	 * 
	 */
	public MatsimOutput(Id currentLinkId, State state) {
		this.currentLinkId = currentLinkId;
		this.state = state;
	}
}