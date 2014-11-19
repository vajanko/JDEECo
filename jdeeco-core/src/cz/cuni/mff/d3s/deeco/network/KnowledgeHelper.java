/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network;

import cz.cuni.mff.d3s.deeco.knowledge.ValueSet;
import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class KnowledgeHelper {
	
	public static Object getValue(KnowledgeData data, String path) {
		
		ValueSet knowledge = data.getKnowledge();
		for (KnowledgePath kp : knowledge.getKnowledgePaths()) {
			if (kp.toString() == path)
				return knowledge.getValue(kp);
		}
		
		return null;
	}
}
