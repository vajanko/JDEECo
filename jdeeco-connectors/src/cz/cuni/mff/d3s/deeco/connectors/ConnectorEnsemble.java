package cz.cuni.mff.d3s.deeco.connectors;

import java.util.HashMap;

import cz.cuni.mff.d3s.deeco.annotations.Ensemble;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.KnowledgeExchange;
import cz.cuni.mff.d3s.deeco.annotations.Membership;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

@Ensemble
@PeriodicScheduling(period = 2000)
public class ConnectorEnsemble {
	
	@Membership
	public static boolean membership(
			@In("member.role") String mRole,
			@In("coord.role") String cRole,
			
			@In("member.id") String mId,
			@In("coord.id") String cId) {
		
		Boolean res = cRole.equals("Connector") && mRole.equals("Connector") && !mId.equals(cId);
		return res;
	}
	
	
	@KnowledgeExchange
	public static void map(
			@In("member.registry") HashMap<String, ConnectorRegistry> mReg,
			@InOut("coord.registry") ParamHolder<HashMap<String, ConnectorRegistry>> cReg) {
		
		for (String key : mReg.keySet()) {
			if (!cReg.value.containsKey(key)) {
				cReg.value.put(key, mReg.get(key));
			}
		}
	}
}
