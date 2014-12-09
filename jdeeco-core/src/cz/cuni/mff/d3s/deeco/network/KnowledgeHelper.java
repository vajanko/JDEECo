/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network;

import java.util.Arrays;

import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeNotFoundException;
import cz.cuni.mff.d3s.deeco.knowledge.ValueSet;
import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;
import cz.cuni.mff.d3s.deeco.network.ip.IPController;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class KnowledgeHelper {
	
	public static Object getValue(KnowledgeData data, String path) {
		
		return getValue(data.getKnowledge(), path);
	}
	public static Object getValue(ValueSet knowledge, String path) {
		
		for (KnowledgePath kp : knowledge.getKnowledgePaths()) {
			if (kp.toString().equals(path))
				return knowledge.getValue(kp);
		}
		
		return null;
	}
	public static KnowledgePath getPath(KnowledgeData data, String path) {
		return getPath(data.getKnowledge(), path);
	}
	public static KnowledgePath getPath(ValueSet knowledge, String path) {
		for (KnowledgePath kp : knowledge.getKnowledgePaths()) {
			if (kp.toString().equals(path))
				return kp;
		}
		return null;
	}
	
	public static Object getLocalValue(KnowledgeManager km, String path) {
		
		for (KnowledgePath kp : km.getLocalPaths()) {
			try {
				if (kp.toString().equals(path))
					return km.get(Arrays.asList(kp)).getValue(kp);
				
			} catch (KnowledgeNotFoundException e) {
				return null;
			}
		}
		
		return null;
	}
	
	public static void setValue(ValueSet knowledge, String path, Object value) {
		knowledge.setValue(getPath(knowledge, path), value);
	}
}
