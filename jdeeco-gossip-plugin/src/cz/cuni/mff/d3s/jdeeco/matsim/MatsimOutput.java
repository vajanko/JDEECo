/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsim;

import org.matsim.api.core.v01.Id;
import org.matsim.core.mobsim.framework.MobsimAgent.State;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class MatsimOutput {
	
	public Id currentLinkId;
	public Id destinationLinkId;
	public State state;
	
	/**
	 * 
	 */
	public MatsimOutput(Id currentLinkId, Id destinationLinkId, State state) {
		this.currentLinkId = currentLinkId;
		this.destinationLinkId = destinationLinkId;
		this.state = state;
	}
}
