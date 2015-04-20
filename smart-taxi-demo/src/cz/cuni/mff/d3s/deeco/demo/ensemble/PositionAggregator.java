/**
 * 
 */
package cz.cuni.mff.d3s.deeco.demo.ensemble;

import cz.cuni.mff.d3s.deeco.annotations.CoordinatorRole;
import cz.cuni.mff.d3s.deeco.annotations.Ensemble;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.KnowledgeExchange;
import cz.cuni.mff.d3s.deeco.annotations.MemberRole;
import cz.cuni.mff.d3s.deeco.annotations.Membership;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.demo.component.PositionAware;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import cz.cuni.mff.d3s.jdeeco.core.Position;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
@Ensemble
@PeriodicScheduling(period = 2000)
@CoordinatorRole(PositionAware.class)
@MemberRole(PositionAware.class)
public class PositionAggregator {
	@Membership
	public static boolean membership(
			@In("member.position") Position mPosition,
			@In("coord.position") Position cPosition) {
		
		return !mPosition.equals(cPosition);
	}

	@KnowledgeExchange
	public static void map(
			@In("member.position") Position mPosition,
			@InOut("coord.position") ParamHolder<Position> cPosition) {
		
	}
}
