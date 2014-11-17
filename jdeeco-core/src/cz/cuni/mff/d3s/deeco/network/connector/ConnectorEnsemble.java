/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.connector;

import java.util.Collection;
import java.util.Iterator;
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
@PeriodicScheduling(period = 2000)
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
			@InOut("coord.outputEntries") ParamHolder<Collection<DicEntry>> cOutput,
			@InOut("coord.inputEntries") ParamHolder<Collection<DicEntry>> mInput,
			@In("member.range") Set<Object> mRange
			) {
		
		Iterator<DicEntry> it = cOutput.value.iterator();
		while (it.hasNext()) {
			DicEntry entry = it.next();
			if (mRange.contains(entry.getKey())) {
				mInput.value.add(entry);
				it.remove();
			}
		}
	}
}
