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
			@In("member.id") String mId,
			@InOut("coord.storage") ParamHolder<Map<String, Set<String>>> storage,
			@In("member.range") Set<String> range) {
		
		// TODO: divide all ranges fairly
		
		Set<String> all = new HashSet<String>(range);
		if (storage.value.containsKey(cId))
			all.addAll(storage.value.get(mId));
	
		Set<String> newSet = new HashSet<String>();
		ArrayList<String> allList = new ArrayList<String>(all);
		
		if (cId.compareTo(mId) < 0) {
			for (int i = 0; i < all.size() / 2; i++)
				newSet.add(allList.get(i));
		}
		else {
			for (int i = all.size() / 2; i < all.size(); i++)
				newSet.add(allList.get(i));
		}
		storage.value.put(cId, newSet);
	}
}
