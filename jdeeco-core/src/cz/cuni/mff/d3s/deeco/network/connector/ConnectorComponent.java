/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.connector;

import java.util.Set;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
@Component
public class ConnectorComponent {
	
	public Integer CONNECTOR_TAG = 0;
	
	public String id;
	public Double xCoord;
	public Double yCoord;
	public String partition = "destination";
	
	public Set<String> range;
	
	public ConnectorComponent(String id, Double xCoord, Double yCoord) {
		this.id = id;
		this.xCoord = xCoord;
		this.yCoord = yCoord;
	}
	
	@Process
	@PeriodicScheduling(period = 1000)
	public static void produceData(@In("id") String id) {
		
	}
}
