package cz.cuni.mff.d3s.jdeeco.simulation.test;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;

@Component
public final class NodeComponent {
	
	public String id;
	public String data;
	
	public NodeComponent(String id, String data) {
		this.id = id;
		this.data = data;
	}

	@Process
	@PeriodicScheduling(period = 1000)
	public static void process(@In("data") String data)
	{
		System.out.println(data);
	}
}
