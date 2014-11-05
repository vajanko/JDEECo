package cz.cuni.mff.d3s.deeco.connectors;

import java.util.List;
import java.util.Map;

import cz.cuni.mff.d3s.deeco.annotations.CommunicationBoundary;
import cz.cuni.mff.d3s.deeco.annotations.Ensemble;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.KnowledgeExchange;
import cz.cuni.mff.d3s.deeco.annotations.Membership;
import cz.cuni.mff.d3s.deeco.annotations.PartitionKey;
import cz.cuni.mff.d3s.deeco.annotations.TriggerOnChange;
import cz.cuni.mff.d3s.deeco.knowledge.ReadOnlyKnowledgeManager;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

@Ensemble
public class DestinationEnsemble extends EnsembleBase {
	
	public static Boolean matchDest(String cDest, String mDest) {
		return mDest.equals(cDest);
	}
	
	public static Boolean isBefore(Position cPos, Position mPos) {
		// this is not a good solution but for illustration is enough
		return cPos.x >= mPos.x && cPos.y >= mPos.y;
	}
	
	/**
	 * There is a membership coord-member between two components when 
	 * both are vehicles, both are going to the same destination
	 * and coord is before member.
	 */
	@Membership
	public static boolean membership(
			@In("member.roles") List<String> mRoles,
			@In("coord.roles") List<String> cRoles, 
			@In("member.dest") String mDest,
			@In("coord.dest") String cDest,
			@In("member.position") Position mPos,
			@In("coord.position") Position cPos,
			
			// these fields are informative only
			@In("member.id") String mId,
			@In("coord.id") String cId
			) {
		
		Boolean mem = matchRole("Vehicle", cRoles, mRoles) && 
					  matchDest(cDest, mDest) &&
					  isBefore(cPos, mPos) &&
					  // this condition is necessary because membership condition is also called
					  // for one component in role of member and coordinator at once
					  !mId.equals(cId);
		
		return mem;
	}
	
	@KnowledgeExchange
	public static void map(
			// these fields are informative only
			@In("member.id") String mId,
			@In("coord.id") String cId,
			@In("member.dest") String mDest,
			@In("coord.dest") String cDest,
			
			// @In("member.velocity") Integer mVelocity,
			// when coordinator's position change member will adjust it's velocity accordingly
			@TriggerOnChange @In("coord.position") Position cPosition,
			@InOut("member.leaders") ParamHolder<Map<String, Position>> mLeaders
			//@In("coord.velocity") Double cVelocity
			) {
		
		// knowledge exchange is performed only when coord is before member
		
		// exclude relay nodes
		if (!cId.startsWith("R")) {
			// update member's knowledge about coordiantor's position
			mLeaders.value.put(cId, cPosition);
		}
	}
	
	
	// TODO: this must be implemented as in the membership method
	@CommunicationBoundary
	public static boolean boundary(KnowledgeData data, ReadOnlyKnowledgeManager sender) {
		
//		KnowledgePath kpPosition = KnowledgePathBuilder.buildSimplePath("position");
//		KnowledgePath kpTeam = KnowledgePathBuilder.buildSimplePath("teamId");
//		//Position ownerPos = (Position) data.getKnowledge().getValue(kpPosition);
//		Position senderPos = (Position) sender.get(Arrays.asList(kpPosition)).getValue(kpPosition);
//		String ownerTeam = (String) data.getKnowledge().getValue(kpTeam);
//	
//		// if the current component does not hawe a position or the owner does
//		// not belong to a team then we do not propagate the knowledge (includes
//		// all O*)
//		if (senderPos == null || ownerTeam == null || !data.getMetaData().componentId.startsWith("M"))
//			return false;
//		return AreaNetworkRegistry.INSTANCE.isAtTheTeamsSite(ownerTeam, senderPos);
		
		return true;
	}
	
	@PartitionKey
	public static String partition(
			@In("dest") String dest,
			@In("position") Position pos) {
		
		return dest;
	}
}
