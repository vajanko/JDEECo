/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsim;

import java.util.HashMap;
import java.util.Map;

import org.matsim.api.core.v01.Id;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class MatsimOutputProvider {
	
	private Map<Id, MatsimOutput> outputs = new HashMap<Id, MatsimOutput>();
	
	public MatsimOutput getOutput(Id id) {
		return outputs.get(id);
	}
	
	public void updateOutputs(Map<Id, MatsimOutput> outputs) {
		this.outputs.clear();
		this.outputs.putAll(outputs);
	}
	
	public void updateOutput(Id id, MatsimOutput output) {
		this.outputs.put(id, output);
	}
}
