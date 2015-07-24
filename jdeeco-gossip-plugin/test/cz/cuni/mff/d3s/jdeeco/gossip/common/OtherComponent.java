/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.common;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;

/**
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
@Component
public class OtherComponent {
	public String id;
	
	
	public OtherComponent(String id) {
		this.id = id;
	}
	
	@Process
	@PeriodicScheduling(period=1000)
	public static void process(@In("id") String id) {
		
	}
}
