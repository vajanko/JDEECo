/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.common;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.jdeeco.core.AddressHelper;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
@Component
public class DemoComponent {
	public String id;
	public String dest;
	
	
	public DemoComponent(int nodeId, String dest) {
		this.id = AddressHelper.encodeID("D", nodeId);
		this.dest = dest;
	}
	public DemoComponent(int nodeId) {
		this(nodeId, "Berlin");
	}
	
	@Process
	@PeriodicScheduling(period=1000)
	public static void process(@In("id") String id) {
		//System.out.println("I am " + id);
	}
}
