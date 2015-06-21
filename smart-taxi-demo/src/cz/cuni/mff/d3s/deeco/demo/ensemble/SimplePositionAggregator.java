/**
 * 
 */
package cz.cuni.mff.d3s.deeco.demo.ensemble;

import cz.cuni.mff.d3s.deeco.annotations.Ensemble;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.KnowledgeExchange;
import cz.cuni.mff.d3s.deeco.annotations.Membership;
import cz.cuni.mff.d3s.deeco.annotations.PartitionedBy;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.demo.component.ActorRole;
import cz.cuni.mff.d3s.deeco.demo.component.ActorStorage;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import cz.cuni.mff.d3s.jdeeco.core.Position;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
@Ensemble
@PeriodicScheduling(period = 5000)
@PartitionedBy(value = "sector")
public class SimplePositionAggregator {
	@Membership
	public static boolean membership(
			@In("member.role") ActorRole mRole,
			@In("coord.role") ActorRole cRole) {
		
		// check whether component implement desired interface (has role field)
		boolean hasInterface = mRole != null && cRole != null; 
		// only driver -> passenger ..
		boolean driverPassenger = cRole == ActorRole.driver && mRole == ActorRole.passenger;
		// .. or passenger -> driver
		boolean passengerDriver = cRole == ActorRole.passenger && mRole == ActorRole.driver;
		
		return hasInterface && (driverPassenger || passengerDriver);
	}

	@KnowledgeExchange
	public static void map(
			@In("coord.id") String cId,
			@InOut("coord.actors") ParamHolder<ActorStorage> cActors, 
			
			@In("member.id") String mId,
			@In("member.position") Position mPosition) {
		
		cActors.value.putPosition(mId, mPosition);
	}
}
