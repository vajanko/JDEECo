package cz.cuni.mff.d3s.deeco.connectors;

import java.util.List;

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
public class RelayEnsemble extends EnsembleBase {
	
	@Membership
	public static Boolean member(
			// these fields are informative only
			@In("member.id") String mId,
			@In("coord.id") String cId,
			@In("member.roles") List<String> mRoles,
			@In("coord.roles") List<String> cRoles 
			) {
		
		Boolean res = matchRole("Connector", cRoles) && matchRole("Vehicle", mRoles);
		
		return res;
	}
	
	@KnowledgeExchange
	public static void map(
		@InOut("member.relays") ParamHolder<List<String>> mRelays,
		@In("coord.relays") List<String> cRelays) {
		
		for (String rel : cRelays) {
			if (!mRelays.value.contains(rel))
				mRelays.value.add(rel);
		}
	}
}
