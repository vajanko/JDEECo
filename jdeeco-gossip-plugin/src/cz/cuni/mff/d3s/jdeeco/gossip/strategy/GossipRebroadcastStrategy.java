/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.strategy;

import java.util.Random;

import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManagerContainer;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeFramework;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipProperties;
import cz.cuni.mff.d3s.jdeeco.gossip.PullKnowledgePayload;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.address.MANETBroadcastAddress;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy;
import cz.cuni.mff.d3s.jdeeco.network.l2.Layer2;

/**
 * Rebroadcast received L2 packets over MANET with certain probability.
 * Different probabilities are used for knowledge and other types of data.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class GossipRebroadcastStrategy implements L2Strategy {

	private Random generator;
	private double knowledgeProbability;
	private double headersProbability;
	private Layer2 networkLayer;
	private KnowledgeManagerContainer kmContainer;
	
	/**
	 * Creates a new instance of network Layer2 strategy which rebroadcasts
	 * received packets with certain probability.
	 */
	public GossipRebroadcastStrategy(RuntimeFramework runtime, Network network) {
		this.generator = new Random();
		this.knowledgeProbability = GossipProperties.getKnowledgePushProbability();
		this.headersProbability = GossipProperties.getHeadersPushProbability();
		this.networkLayer = network.getL2();
		this.kmContainer = runtime.getContainer();
	}
	
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
			// TODO: somehow get the knowledge and PUSH it
			PullKnowledgePayload pullRequest = (PullKnowledgePayload)packet.getObject();
			for (String msgId : pullRequest.getMessages()) {
				//kmContainer.getLocal(msgId)
			}
			//pullRequest.getMessages()
		}
	}

}
