/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.strategy;

import java.util.Random;

import cz.cuni.mff.d3s.jdeeco.gossip.GossipProperties;
import cz.cuni.mff.d3s.jdeeco.network.address.MANETBroadcastAddress;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy;
import cz.cuni.mff.d3s.jdeeco.network.l2.Layer2;

/**
 * Rebroadcast received knowledge over MANET with certain probability.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class KnowledgeRebroadcastStrategy implements L2Strategy {

	private Random generator;
	private double probability;
	private Layer2 networkLayer;
	
	/**
	 * Creates a new instance of network Layer2 strategy which rebroadcasts
	 * received packets with certain probability.
	 */
	public KnowledgeRebroadcastStrategy(Layer2 networkLayer) {
		this.generator = new Random();
		this.probability = GossipProperties.getKnowledgePushProbability();
		this.networkLayer = networkLayer;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy#processL2Packet(cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet)
	 */
	@Override
	public void processL2Packet(L2Packet packet) {
		
		if (packet.header.type.equals(L2PacketType.KNOWLEDGE)) {
			if (generator.nextDouble() < probability) {
				networkLayer.sendL2Packet(packet, MANETBroadcastAddress.BROADCAST);
			}
		}
	}

}
