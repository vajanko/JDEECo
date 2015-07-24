/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsim;

import org.matsim.core.controler.Controler;
import org.matsim.core.facilities.ActivityFacilitiesImpl;
import org.matsim.core.scenario.ScenarioImpl;
import org.matsim.population.algorithms.XY2Links;

/**
 * Extension of default MATSim {@link Controler}. Loads network, population and facilities 
 * data upon creation and not just before run as the default {@link Controler} does.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class PreloadingControler extends Controler {

	/**
	 * Creates a new instance of matsim controler and loads config data.
	 * 
	 * @param configFileName Absolute or relative path to matsim config.xml file.
	 */
	public PreloadingControler(String configFileName) {
		super(configFileName);
		
		this.loadData();
		
		ActivityFacilitiesImpl facilities = ((ScenarioImpl) this.getScenario()).getActivityFacilities();
		
		XY2Links xy2Links = new XY2Links(this.getNetwork(), facilities);
		xy2Links.run(this.getPopulation());
	}
}
