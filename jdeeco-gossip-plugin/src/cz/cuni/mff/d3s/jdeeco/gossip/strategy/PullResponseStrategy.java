/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.timer.CurrentTimeProvider;
import cz.cuni.mff.d3s.jdeeco.gossip.ConsoleLog;
import cz.cuni.mff.d3s.jdeeco.gossip.KnowledgeProvider;
import cz.cuni.mff.d3s.jdeeco.gossip.MessageBuffer;
import cz.cuni.mff.d3s.jdeeco.gossip.PullKnowledgePayload;
import cz.cuni.mff.d3s.jdeeco.gossip.ConsoleLog.ActType;
import cz.cuni.mff.d3s.jdeeco.gossip.ConsoleLog.MsgType;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.address.MANETBroadcastAddress;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy;
import cz.cuni.mff.d3s.jdeeco.network.l2.Layer2;
import cz.cuni.mff.d3s.jdeeco.network.l2.PacketHeader;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class PullResponseStrategy implements L2Strategy, DEECoPlugin {
	
	private Layer2 networkLayer;
	private KnowledgeProvider knowledgeProvider;
	private MessageBuffer messageBuffer;
	private CurrentTimeProvider timeProvider;
	private int nodeId;
	
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy#processL2Packet(cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet)
	 */
	@Override
	public void processL2Packet(L2Packet packet) {
		
		// only process PULL_REQUEST packets
		if (!packet.header.type.equals(L2PacketType.PULL_REQUEST))
			return;
		
		PullKnowledgePayload pullRequest = (PullKnowledgePayload)packet.getObject();
		ArrayList<String> missingMessages = new ArrayList<String>();
		long time = timeProvider.getCurrentMilliseconds();
		
		ConsoleLog.printRequest(nodeId, time, MsgType.PL, ActType.RECV, pullRequest.getMessages());
		
		for (String id : pullRequest.getMessages()) {
			
			// check whether message requested by the PULL is present on current node
			if (messageBuffer.hasRecentMessage(id, time)) {
				
				KnowledgeData kd = knowledgeProvider.getComponentKnowledge(id);
				PacketHeader hdr = new PacketHeader(L2PacketType.KNOWLEDGE);
				L2Packet pck = new L2Packet(hdr, kd);
				
				ConsoleLog.printRequest(nodeId, time, MsgType.KN, ActType.SEND, kd.getMetaData().componentId);
				networkLayer.sendL2Packet(pck, MANETBroadcastAddress.BROADCAST);
			}
			else {
				// may be there is the required message but too old
				//missingMessages.add(id);
			}
		}
		
		if (!missingMessages.isEmpty()) {
			// rebroadcast list of missing messages requested by the PULL
			PacketHeader hdr = new PacketHeader(L2PacketType.PULL_REQUEST);
			L2Packet pck = new L2Packet(hdr, new PullKnowledgePayload(missingMessages));
			networkLayer.sendL2Packet(pck, MANETBroadcastAddress.BROADCAST);
			
			// TODO: it would be nice if also messages missing by current node are included
		}
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(Network.class, KnowledgeProvider.class, MessageBuffer.class);
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		this.knowledgeProvider = container.getPluginInstance(KnowledgeProvider.class);
		this.networkLayer = container.getPluginInstance(Network.class).getL2();
		this.messageBuffer = container.getPluginInstance(MessageBuffer.class);
		this.timeProvider = container.getRuntimeFramework().getScheduler().getTimer();
		this.nodeId = container.getId();
		// register L2 strategy
		this.networkLayer.registerL2Strategy(this);
	}

	

}
