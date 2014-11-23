package cz.cuni.mff.d3s.deeco.connectors;

import cz.cuni.mff.d3s.deeco.annotations.Ensemble;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.KnowledgeExchange;
import cz.cuni.mff.d3s.deeco.annotations.Membership;

@Ensemble
public class ConnectorEnsemble {
	
	@Membership
	public static boolean membership(
			@In("member.id") String mId,
			@In("coord.id") String cId) {
		return !mId.equals(cId);
	}
	
	
	@KnowledgeExchange
	public static void map() {
		
	}
}
