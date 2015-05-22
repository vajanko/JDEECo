/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.grouper.client;

import java.util.Set;

import cz.cuni.mff.d3s.deeco.annotations.Ensemble;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.KnowledgeExchange;
import cz.cuni.mff.d3s.deeco.annotations.Membership;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import cz.cuni.mff.d3s.jdeeco.grouper.GrouperRole;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
@Ensemble
@PeriodicScheduling(period = 1000)
public class GrouperClientEnsemble {
	
	@Membership
	public static boolean membership(
			@In("coord.role") GrouperRole cRole,
			@In("member.role") GrouperRole mRole) {
		
		return cRole != null && mRole != null &&
				cRole == GrouperRole.client && mRole == GrouperRole.server;
	}
	
	@KnowledgeExchange
	public static void exchange(
			//@In("coord.id") String cId,
			//@In("member.id") String mId,
			@InOut("coord.groupMembers") ParamHolder<Set<String>> cGroup,
			@In("member.groupMembers") Set<String> mGroup) {
		
		//System.out.println("coord=" + cId + " member=" + mId);
		cGroup.value.addAll(mGroup);
	}
}
