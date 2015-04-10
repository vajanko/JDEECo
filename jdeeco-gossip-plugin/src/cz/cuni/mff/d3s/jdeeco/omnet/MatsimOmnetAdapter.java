/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.omnet;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.simulation.matsim.MATSimOMNetCoordinatesTranslator;
import cz.cuni.mff.d3s.deeco.simulation.matsim.MATSimOutput;
import cz.cuni.mff.d3s.deeco.simulation.matsim.MATSimRouter;
import cz.cuni.mff.d3s.deeco.simulation.matsim.MatsimOutputStrategy;
import cz.cuni.mff.d3s.deeco.simulation.omnet.OMNeTNative;
import cz.cuni.mff.d3s.jdeeco.matsim.MATSimSimulation;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class MatsimOmnetAdapter implements DEECoPlugin, MatsimOutputStrategy {

	private MATSimOMNetCoordinatesTranslator translator;
	private MATSimRouter router;
	private int nodeId;
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.simulation.matsim.MatsimReceiverStrategy#processMatsimData(java.util.Map)
	 */
	@Override
	public void processMatsimData(Map<Id, MATSimOutput> data) {
		for (MATSimOutput output : data.values()) {
			
			// get current matsim node position ...
			Coord matsimPos = router.getLink(output.currentLinkId).getCoord();
			// ... and translates to omnet coordinates
			Coord omnetPos = translator.fromMATSimToOMNet(matsimPos);
			
			OMNeTNative.nativeSetPosition(nodeId, omnetPos.getX(), omnetPos.getY(), 0);
		}
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(MATSimSimulation.class);
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		MATSimSimulation sim = container.getPluginInstance(MATSimSimulation.class);
		sim.getMatsimOutputProvider().register(this);
		
		this.translator = new MATSimOMNetCoordinatesTranslator(sim.getController().getNetwork());
		this.router = sim.getRouter();
		this.nodeId = container.getId();		
	}
}
