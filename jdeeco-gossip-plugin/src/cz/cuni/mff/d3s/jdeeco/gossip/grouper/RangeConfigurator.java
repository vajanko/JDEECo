/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.grouper;

import cz.cuni.mff.d3s.deeco.model.runtime.api.EnsembleDefinition;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public interface RangeConfigurator {
	public GrouperRange getRange(EnsembleDefinition ensemble, int nodeId);
}
