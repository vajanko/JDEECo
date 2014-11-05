package cz.cuni.mff.d3s.deeco.connectors;

import java.util.Map;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;

@Component
public class VehicleRelay extends EnsembleComponent {

	public String dest;
	//public Double velocity;
	public Position position;
	
	public VehicleRelay(String id, String dest, Double xPos, Double yPos) {
		super(id);
		this.dest = dest;
		this.position = new Position(xPos, yPos);
		this.addRole("Vehicle");
	}
	
	@Process
	@PeriodicScheduling(period = 2000)
	public static void process(
			@In("id") String id, 
			@In("dest") String dest) {
		
		System.out.println(id + ": I am just a relay node ...");
	}

}
