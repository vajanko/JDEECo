package cz.cuni.mff.d3s.deeco.connectors;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.model.architecture.api.EnsembleInstance;

@Component
public class Connector {
	
	// TODO: data
	public String id;
	
	
	
	public Connector() {
		
	}

	@Process
	@PeriodicScheduling(period = 5000)
	public static void process() {
		
	}
}
