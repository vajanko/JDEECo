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
 * Deeco ensemble providing the exchange of current positions between drivers and
 * passengers.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
@Ensemble
@PeriodicScheduling(period = 5000)
public class BoundaryPositionAggregator {

	@Membership
	public static boolean membership(
			@In("member.role") ActorRole mRole,
			@In("coord.role") ActorRole cRole) {
		
		return SimplePositionAggregator.membership(mRole, cRole);
	}
	
	@KnowledgeExchange
	public static void map(
			@In("coord.id") String cId,
			@InOut("coord.actors") ParamHolder<ActorStorage> cActors, 
			
			@In("member.id") String mId,
			@In("member.position") Position mPosition) {
		
		SimplePositionAggregator.map(cId, cActors, mId, mPosition);
	}
	
	@CommunicationBoundary
	public static boolean boundary(KnowledgeData data, ReadOnlyKnowledgeManager sender) throws KnowledgeNotFoundException {
		try {
		KnowledgePath posPath = KnowledgeDataHelper.getPath(data, "position");
		Position senderPos = (Position)sender.get(Arrays.asList(posPath)).getValue(posPath);
		Position compPos = (Position) data.getKnowledge().getValue(posPath);
		
		// less than 500 m
		boolean rebroadcast = distance(senderPos, compPos) < 500;
		return rebroadcast;
		} catch (KnowledgeNotFoundException e) {
			throw e;
		}
	}
	/**
	 * Calculates distance between to points.
	 * 
	 * @param a Begin position
	 * @param b End position
	 * @return Distance between two points.
	 */
	private static double distance(Position a, Position b) {
		return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}
}
