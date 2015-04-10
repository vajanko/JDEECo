/**
 * 
 */
package cz.cuni.mff.d3s.deeco.demo.ensemble;

import org.matsim.api.core.v01.Coord;

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
			/*@In("member.currentStation") String mCurrentStation,
			@In("member.nextStation") String mNextStation,
			@In("coord.currentStation") String cCurrentStation,
			@In("coord.passingStations") Collection<String>  cPassingStations) {*/
			@In("member.position") Coord mPosition,
			@In("coord.position") Coord cPosition) {
		
		return !mPosition.equals(cPosition);
		/*return
			// implements same interface
			(mCurrentStation != null && cCurrentStation != null) &&
			// both in a nearby position (same station)
			(mCurrentStation.equals(cCurrentStation)) &&
			// member is going somewhere on my way
			(cPassingStations.contains(mNextStation));*/
	}

	@KnowledgeExchange
	public static void map(
			/*@In("member.position") Position mPosition,
			@InOut("coord.application") TaxiApplication cApplication) {*/
			@In("member.position") Coord mPosition,
			@InOut("coord.position") ParamHolder<Coord> cPosition) {
		
	}
}
