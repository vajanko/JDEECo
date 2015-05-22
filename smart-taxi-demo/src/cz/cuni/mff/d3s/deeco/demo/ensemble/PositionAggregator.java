/**
 * 
 */
package cz.cuni.mff.d3s.deeco.demo.ensemble;

import java.util.Arrays;

import cz.cuni.mff.d3s.deeco.annotations.CommunicationBoundary;
import cz.cuni.mff.d3s.deeco.annotations.Ensemble;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.KnowledgeExchange;
import cz.cuni.mff.d3s.deeco.annotations.Membership;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.demo.component.ActorRole;
import cz.cuni.mff.d3s.deeco.demo.component.ActorStorage;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeNotFoundException;
import cz.cuni.mff.d3s.deeco.knowledge.ReadOnlyKnowledgeManager;
import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataHelper;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import cz.cuni.mff.d3s.jdeeco.core.Position;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
@Ensemble
@PeriodicScheduling(period = 5000)
public class PositionAggregator {
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
		//System.out.println("Current " + cId + " actors: " + cActors.value);
	}
	
	@CommunicationBoundary
	public static boolean boundary(KnowledgeData data, ReadOnlyKnowledgeManager sender) throws KnowledgeNotFoundException {
		KnowledgePath posPath = KnowledgeDataHelper.getPath(data, "position");
		Position senderPos = (Position)sender.get(Arrays.asList(posPath)).getValue(posPath);
		Position compPos = (Position) data.getKnowledge().getValue(posPath);
		
		// less than 2 km
		boolean rebroadcast = distance(senderPos, compPos) < 2000;
		return rebroadcast;
	}
	
	private static double distance(Position a, Position b) {
		return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}
}
