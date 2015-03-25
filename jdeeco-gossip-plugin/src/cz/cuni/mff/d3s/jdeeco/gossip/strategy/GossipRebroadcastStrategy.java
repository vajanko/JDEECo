/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.strategy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeMetaData;
import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.KnowledgeProvider;
import cz.cuni.mff.d3s.jdeeco.gossip.buffer.ReceptionBuffer;
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
public class GossipRebroadcastStrategy implements L2Strategy, DEECoPlugin {

	public static final String REBROADCAST_PROBABILITY = "deeco.gossipRebroadcast.probability";
	/**
	 * Default value of knowledge rebroadcast probability when received by current node.
	 */
	public static final double REBROADCAST_PROBABILITY_DEFAULT = 0.5;
	
	private final Random generator = new Random();
	private double probability;
	private Layer2 networkLayer;
	private ReceptionBuffer messageBuffer;
	private KnowledgeProvider knowledgeProvider;
	
	private Map<String, Long> currentVersions = new HashMap<String, Long>();
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy#processL2Packet(cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet)
	 */
	@Override
	public void processL2Packet(L2Packet packet) {
		
		if (packet.header.type.equals(L2PacketType.KNOWLEDGE)) {
			KnowledgeData kd = (KnowledgeData)packet.getObject();
			KnowledgeMetaData meta = kd.getMetaData();
			if (messageBuffer.getPulledTag(meta.componentId)) {
				// if knowledge was pulled it will be rebroadcasted
				networkLayer.sendL2Packet(packet, MANETBroadcastAddress.BROADCAST);
				messageBuffer.clearPulledTag(meta.componentId);
			}
			else {
				// do not rebroadcast when receive own local knowledge
				if (knowledgeProvider.hasLocal(meta.componentId))
					return;
				
				// do not rebroadcast older versions than currently available
				Long curVer = currentVersions.get(meta.componentId);
				if (curVer != null && curVer >= meta.versionId)
					return;
				
				currentVersions.put(meta.componentId, meta.versionId);
				
				if (generator.nextDouble() < probability) {
					networkLayer.sendL2Packet(packet, MANETBroadcastAddress.BROADCAST);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(Network.class, ReceptionBuffer.class, KnowledgeProvider.class);
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		// initialise dependencies
		this.knowledgeProvider = container.getPluginInstance(KnowledgeProvider.class);
		this.messageBuffer = container.getPluginInstance(ReceptionBuffer.class);
		this.networkLayer = container.getPluginInstance(Network.class).getL2();
		// register L2 strategy
		this.networkLayer.registerL2Strategy(this);
		
		// config parameters
		this.probability = GossipHelper.getDouble(REBROADCAST_PROBABILITY, REBROADCAST_PROBABILITY_DEFAULT);
	}

}
