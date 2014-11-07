package test;

import java.util.HashSet;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

@Component
public class Vehicle {
	public String id;

	public Double xCoord;
	public Double yCoord;

	public Double myData;
	public Double othersData;
	
	public String destination;
	public Set<String> group;

	public Vehicle(String id, double xCoord, double yCoord, String destination) {
		super();
		this.id = id;
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.myData = 0.0;
		this.othersData = 0.0;
		this.destination = destination;
		this.group = new HashSet<String>();
	}

	@Process
	@PeriodicScheduling(period = 1000)
	public static void produceData(@In("id") String id, @InOut("myData") ParamHolder<Double> myData) {
		myData.value++;
		// System.out.println("Component " + id + " has produced the new data value: " + myData.value);
	}

	@Process
	@PeriodicScheduling(period = 2000)
	public static void printOthersData(@In("id") String id, @In("othersData") Double othersData,
			@In("group") Set<String> group, @In("destination") String destination) {
		// System.out.println("Component " + id + " others data is " + othersData);

		System.out.print("Component " + id + " destination: " + destination + " group members: ");
		boolean first = true;
		for (String m : group) {
			if (!first)
				System.out.print(", ");
			first = false;
			System.out.print(m);
		}
		System.out.println("");
	}
}
