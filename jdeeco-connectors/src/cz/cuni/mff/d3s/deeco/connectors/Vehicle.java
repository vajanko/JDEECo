package cz.cuni.mff.d3s.deeco.connectors;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.Out;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.annotations.TriggerOnChange;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

@Component
public class Vehicle {
	
	public String id;
	public String dest;
	public Boolean inDanger;

	public Vehicle(String id, String dest) {
		this.id = id;
		this.dest = dest;
		this.inDanger = false;
	}
	
	@Process
	@PeriodicScheduling(period = 2000)
	public static void process(@In("id") String id, @In("dest") String dest, @InOut("inDanger") ParamHolder<Boolean> inDanger) {
		
		// check whether we are in danger (randomly)

		if (id == "V1")
			inDanger.value = true;
		
		if (inDanger.value)
			System.out.println(id + " going to " + dest + " is in danger");
		//else if (prevDanger && !inDanger.value)
		//	System.out.println(id + " going to " + dest + " is no longer in danger");
	}
}
