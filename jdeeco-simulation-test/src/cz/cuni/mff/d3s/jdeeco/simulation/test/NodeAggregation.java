package cz.cuni.mff.d3s.jdeeco.simulation.test;

import cz.cuni.mff.d3s.deeco.annotations.CommunicationBoundary;
import cz.cuni.mff.d3s.deeco.annotations.Ensemble;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.KnowledgeExchange;
import cz.cuni.mff.d3s.deeco.annotations.Membership;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.TriggerOnChange;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

@Ensemble
@PeriodicScheduling(period = 1000)
public final class NodeAggregation {

	@Membership
	public static boolean membership(@In("member.id") String mId, @In("coord.id") String cId)
	{
		return mId != cId;
	}
	
	@KnowledgeExchange
	public static void map(
			@TriggerOnChange @In("member.data") String mData, 
			@InOut("coord.data") ParamHolder<String> cData)
	{
		cData.value = mData;
	}
	
	/*@CommunicationBoundary
	public static boolean boundary()
	{
		return true;
	}*/
}
