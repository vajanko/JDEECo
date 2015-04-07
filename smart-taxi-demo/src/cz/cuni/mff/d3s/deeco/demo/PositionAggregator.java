/**
 * 
 */
package cz.cuni.mff.d3s.deeco.demo;

import java.util.Collection;

import cz.cuni.mff.d3s.deeco.annotations.Ensemble;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.KnowledgeExchange;
import cz.cuni.mff.d3s.deeco.annotations.Membership;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
@Ensemble
@PeriodicScheduling(period = 2000)
public class PositionAggregator {
	@Membership
	public static boolean membership(
			@In("member.currentStation") String mCurrentStation,
			@In("member.nextStation") String mNextStation,
			@In("coord.currentStation") String cCurrentStation,
			@In("coord.passingStations") Collection<String>  cPassingStations) {
		
		return
			// implements same interface
			(mCurrentStation != null && cCurrentStation != null) &&
			// both in a nearby position (same station)
			(mCurrentStation.equals(cCurrentStation)) &&
			// member is going somewhere on my way
			(cPassingStations.contains(mNextStation));
	}

	@KnowledgeExchange
	public static void map(
			@In("member.position") Position mPosition,
			@InOut("coord.application") TaxiApplication cApplication) {
		
	}
}
