/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.connector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.annotations.Ensemble;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.KnowledgeExchange;
import cz.cuni.mff.d3s.deeco.annotations.Membership;
import cz.cuni.mff.d3s.deeco.annotations.PartitionedBy;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
@Ensemble
@PeriodicScheduling(period = 1000)
@PartitionedBy("partition")
public class ConnectorEnsemble {
	
	@Membership
	public static boolean membership(
			@In("member.id") String mId, 
			@In("coord.id") String cId,
			@In("member.CONNECTOR_TAG") Integer mTag,
			@In("coord.CONNECTOR_TAG") Integer cTag) {
		return mTag != null && cTag != null && !mId.equals(cId);
	}
	
	@KnowledgeExchange
	public static void exchange(
			@In("coord.id") String cId, 
			@In("member.id") String mId) {
		
	}
}
