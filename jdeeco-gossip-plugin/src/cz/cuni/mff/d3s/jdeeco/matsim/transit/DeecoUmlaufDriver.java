/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsim.transit;

import org.matsim.core.mobsim.qsim.InternalInterface;
import org.matsim.core.mobsim.qsim.pt.TransitStopAgentTracker;
import org.matsim.core.mobsim.qsim.pt.UmlaufDriver;
import org.matsim.pt.Umlauf;

import cz.cuni.mff.d3s.jdeeco.matsim.DeecoAgent;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class DeecoUmlaufDriver extends UmlaufDriver implements DeecoAgent {

	/**
	 * @param umlauf
	 * @param transportMode
	 * @param thisAgentTracker
	 * @param internalInterface
	 */
	public DeecoUmlaufDriver(Umlauf umlauf, String transportMode,
			TransitStopAgentTracker thisAgentTracker,
			InternalInterface internalInterface) {
		super(umlauf, transportMode, thisAgentTracker, internalInterface);
	}


}
