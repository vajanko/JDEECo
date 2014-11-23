package cz.cuni.mff.d3s.deeco.connectors;

import cz.cuni.mff.d3s.deeco.annotations.Ensemble;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.KnowledgeExchange;
import cz.cuni.mff.d3s.deeco.annotations.Membership;
import cz.cuni.mff.d3s.deeco.annotations.TriggerOnChange;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

@Ensemble
public class DestinationAggregation {
	
	@Membership
	public static boolean membership(
			@In("member.id") String mId,
			@In("member.dest") String mDest,
			@In("coord.id") String cId,
			@In("coord.dest") String cDest) {
		
		return mDest.equals(cDest) && !mId.equals(cId);
	}
	
	@KnowledgeExchange
	public static void map(
			@In("member.id") String mId,
			@In("member.dest") String mDest,
			@TriggerOnChange @In("member.inDanger") Boolean mInDanger,
			@In("coord.id") String cId,
			@In("coord.dest") String cDest,
			@InOut("coord.inDanger") ParamHolder<Boolean> cInDanger) {
		
		if (mInDanger && !cInDanger.value) {
			System.out.println(mId + " going to " + mDest + " notifies " + cId + " (" + cDest + ") about danger.");
		}
		else if (!mInDanger && cInDanger.value) {
			
		}
		
		cInDanger.value = mInDanger;
	}
}
