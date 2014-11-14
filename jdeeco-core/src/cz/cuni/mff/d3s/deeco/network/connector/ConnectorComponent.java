/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.connector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.Local;
import cz.cuni.mff.d3s.deeco.annotations.Out;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.annotations.TriggerOnChange;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

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
	
	@Local
	public Map<String, Set<String>> storage;	// this will be a range of values
	
	public Set<String> range;
	
	public ConnectorComponent(String id, Double xCoord, Double yCoord) {
		this.id = id;
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		storage = new HashMap<String, Set<String>>();
		range = new HashSet<String>();
		
		// TODO: this is a hack
		if (id == "C1") {
			//storage.put("C2", new HashSet<String>())
		}
		
		storage.put(id, range);
	}
	
	@Process
	//@PeriodicScheduling(period = 1000)
	public static void produceData(@In("id") String id, 
			@TriggerOnChange @In("storage") Map<String, Set<String>> storage,
			@Out("range") ParamHolder<Set<String>> range) {
		
		range.value = storage.get(id);
		
	}
}
