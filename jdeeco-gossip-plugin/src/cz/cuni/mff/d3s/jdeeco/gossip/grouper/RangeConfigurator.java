/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.grouper;

import cz.cuni.mff.d3s.deeco.model.runtime.api.EnsembleDefinition;

/**
 * A service providing for given ensemble and node the grouper range. This is 
 * especially useful when setting up the simulation.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public interface RangeConfigurator {
	/**
	 * Gets the range of grouper deployed on given node and partitioning defined
	 * in given ensemble.
	 * 
	 * @param ensemble The ensemble defining the partitioning function.
	 * @param nodeId ID of node where the grouper is about to be deplyed.
	 * @return Range for which is the grouper responsible.
	 */
	public GrouperRange getRange(EnsembleDefinition ensemble, int nodeId);
}
