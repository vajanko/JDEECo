package cz.cuni.mff.d3s.jdeeco.gossip.strategy;

import cz.cuni.mff.d3s.deeco.knowledge.ChangeSet;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManagerContainer;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeUpdateException;
import cz.cuni.mff.d3s.deeco.knowledge.ValueSet;
import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeMetaData;
import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;

/**
 * Strategy for processing received knowledge message. Message header is extracted
 * and stored to local buffer.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class ReceiveKNStrategy extends ReceiveBaseStrategy {
	
	protected KnowledgeManagerContainer kmContainer;
	
	/**
	 * Converts value set to change set
	 * 
	 * @param valueSet {@link ValueSet} to be converted
	 * @return {@link ChangeSet} set composed from input knowledge
	 */
	private static ChangeSet toChangeSet(ValueSet valueSet) {
		ChangeSet result = new ChangeSet();

		for (KnowledgePath kp : valueSet.getKnowledgePaths()) {
			result.setValue(kp, valueSet.getValue(kp));
		}

		return result;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy#processL2Packet(cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet)
	 */
	@Override
	public void processL2Packet(L2Packet packet) {
				
		if (packet.header.type.equals(L2PacketType.KNOWLEDGE)) {
			// Collect all IDs of component which are coming from the network.
			// Remember also the time so that we can recognise when some component
			// is lost and does not participate in the communication any more.
			KnowledgeData kd = (KnowledgeData)packet.getObject();
			KnowledgeMetaData meta = kd.getMetaData();
			
			// check whether not received more than once
			if (!messageBuffer.canReceive(meta.componentId, meta.versionId))
				return;
			
			if (!kmContainer.hasLocal(meta.componentId)) {
				// not local knowledge add to replica manager
				for (KnowledgeManager replica : kmContainer.createReplica(meta.componentId)) {
					try {
						replica.update(toChangeSet(kd.getKnowledge()));
					} catch (KnowledgeUpdateException e) {
						e.printStackTrace();
					}
				}
			}
			
			// update current version (as well as time) of message received locally
			messageBuffer.receiveLocal(meta.componentId, meta.createdAt, meta.versionId);
		}
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.gossip.strategy.ReceiveBaseStrategy#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		super.init(container);
		
		this.kmContainer = container.getRuntimeFramework().getContainer();
	}
}