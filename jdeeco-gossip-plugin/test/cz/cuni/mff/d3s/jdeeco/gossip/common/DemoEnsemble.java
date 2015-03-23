/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.common;

import cz.cuni.mff.d3s.deeco.annotations.Ensemble;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.KnowledgeExchange;
import cz.cuni.mff.d3s.deeco.annotations.Membership;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;


/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
@Ensemble
@PeriodicScheduling(period=1000)
public class DemoEnsemble {
	
	@Membership
	public static boolean membership(@In("member.id") String mId, @In("coord.id") String cId) {
		return !mId.equals(cId);
	}
	
	@KnowledgeExchange
	public static void exchange(@In("member.id") String mId, @In("coord.id") String cId) {
		
	}
}
