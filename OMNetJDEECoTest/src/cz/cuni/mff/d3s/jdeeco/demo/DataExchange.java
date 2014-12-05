package cz.cuni.mff.d3s.jdeeco.demo;

import java.util.Set;

import cz.cuni.mff.d3s.deeco.annotations.Ensemble;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.KnowledgeExchange;
import cz.cuni.mff.d3s.deeco.annotations.Membership;
import cz.cuni.mff.d3s.deeco.annotations.PartitionedBy;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

@Ensemble
@PeriodicScheduling(period = 1000)
@PartitionedBy("destination")
public class DataExchange {

	@Membership
	public static boolean membership(@In("member.id") String mId, @In("coord.id") String cId) {
		return !mId.equals(cId);
	}

	@KnowledgeExchange
	public static void exchange(@In("coord.id") String coordId, @In("member.id") String memberId,
			@In("coord.myData") Double cMyData, @InOut("member.othersData") ParamHolder<Double> mOthersData,
			@InOut("member.group") ParamHolder<Set<String>> mGroup) {
		mOthersData.value = cMyData;
		mGroup.value.add(coordId);
		mGroup.value.add(memberId);
	}
}
