/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsim;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class MatsimOutputProvider {
	
	private Map<Integer, MatsimOutput> outputs = new HashMap<Integer, MatsimOutput>();
	
	public MatsimOutput getOutput(Integer nodeId) {
		return outputs.get(nodeId);
	}
	
	public void updateOutputs(Map<Integer, MatsimOutput> outputs) {
		this.outputs.clear();
		this.outputs.putAll(outputs);
	}
	
	public void updateOutput(Integer nodeId, MatsimOutput output) {
		this.outputs.put(nodeId, output);
	}
}
