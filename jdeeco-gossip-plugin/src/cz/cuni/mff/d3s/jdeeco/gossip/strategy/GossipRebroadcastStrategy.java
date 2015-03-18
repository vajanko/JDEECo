/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.strategy;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.timer.CurrentTimeProvider;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipProperties;
import cz.cuni.mff.d3s.jdeeco.gossip.KnowledgeProvider;
import cz.cuni.mff.d3s.jdeeco.gossip.PullKnowledgePayload;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.address.MANETBroadcastAddress;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy;
import cz.cuni.mff.d3s.jdeeco.network.l2.Layer2;
import cz.cuni.mff.d3s.jdeeco.network.l2.PacketHeader;

/**
 * Rebroadcast received L2 packets over MANET with certain probability.
 * Different probabilities are used for knowledge and other types of data.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class GossipRebroadcastStrategy implements L2Strategy, DEECoPlugin {

	private final Random generator = new Random();
	private double knowledgeProbability;
	private double headersProbability;
	private Layer2 networkLayer;
	private KnowledgeProvider knowledgeProvider;
	private CurrentTimeProvider timeProvider;
	private int nodeId;
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy#processL2Packet(cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet)
	 */
	@Override
	public void processL2Packet(L2Packet packet) {
		
		if (packet.header.type.equals(L2PacketType.KNOWLEDGE)) {
			if (generator.nextDouble() < knowledgeProbability) {
				networkLayer.sendL2Packet(packet, MANETBroadcastAddress.BROADCAST);
			}
		}
		else if (packet.header.type.equals(L2PacketType.MESSAGE_HEADERS)) {
			if (generator.nextDouble() < headersProbability) {
				networkLayer.sendL2Packet(packet, MANETBroadcastAddress.BROADCAST);
			}
		}
		else if (packet.header.type.equals(L2PacketType.PULL_REQUEST)) {
			PullKnowledgePayload pullRequest = (PullKnowledgePayload)packet.getObject();
			for (String id : pullRequest.getMessages()) {
				
				KnowledgeData kd = knowledgeProvider.getKnowledgeByComponentId(id);
				if (kd != null) {
					PacketHeader hdr = new PacketHeader(L2PacketType.KNOWLEDGE);
					L2Packet pck = new L2Packet(hdr, kd);
					
					System.out.println(String.format("[%2d] %4d KN PUSH [%s]", nodeId, timeProvider.getCurrentMilliseconds(), kd.getMetaData().componentId));
					networkLayer.sendL2Packet(pck, MANETBroadcastAddress.BROADCAST);
				}
				else {
					// TODO: remember missing and rebroadcast this packet
					
					// knowledge is missing on this node - rebroadcast
					// networkLayer.sendL2Packet(packet, MANETBroadcastAddress.BROADCAST);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(Network.class, KnowledgeProvider.class);
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		this.knowledgeProvider = container.getPluginInstance(KnowledgeProvider.class);
		this.networkLayer = container.getPluginInstance(Network.class).getL2();
		this.timeProvider = container.getRuntimeFramework().getScheduler().getTimer();
		this.nodeId = container.getId();
		// register L2 strategy
		this.networkLayer.registerL2Strategy(this);
		
		this.knowledgeProbability = GossipProperties.getKnowledgePushProbability();
		this.headersProbability = GossipProperties.getHeadersPushProbability();
	}

}
