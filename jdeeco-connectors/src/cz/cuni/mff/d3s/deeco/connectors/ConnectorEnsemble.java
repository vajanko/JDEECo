package cz.cuni.mff.d3s.deeco.connectors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.mff.d3s.deeco.annotations.Ensemble;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.KnowledgeExchange;
import cz.cuni.mff.d3s.deeco.annotations.Membership;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

@Ensemble
@PeriodicScheduling(period = 2000)
public class ConnectorEnsemble extends EnsembleBase {
	
	@Membership
	public static boolean membership(
			@In("member.roles") List<String> mRoles,
			@In("coord.roles") List<String> cRoles,
			
			@In("member.id") String mId,
			@In("coord.id") String cId) {
		
		Boolean mem = matchRole("Connector", mRoles, cRoles) &&
					  !mId.equals(cId);
		return mem;
	}
	
	
	@KnowledgeExchange
	public static void map(
			@In("member.registry") HashMap<String, ConnectorRegistry> mReg,
			@InOut("coord.registry") ParamHolder<HashMap<String, ConnectorRegistry>> cReg) {
		
		// copy registry of member to coordinator
		for (Map.Entry<String, ConnectorRegistry> entry : mReg.entrySet()) {
			cReg.value.put(entry.getKey(), entry.getValue());
		}
	}
}
