/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.receive;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeMetaData;
import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.jdeeco.core.ConfigHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.KnowledgeProviderPlugin;
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
	private ReceptionBuffer receptionBuffer;
	private KnowledgeProviderPlugin knowledgeProvider;
	private String nodeId;
	
	protected KnowledgeData prepareForRebroadcast(KnowledgeData kd) {		
		KnowledgeMetaData meta = kd.getMetaData().clone();
		meta.sender = nodeId;
		meta.hopCount++;
		return new KnowledgeData(kd.getKnowledge(), kd.getSecuritySet(), kd.getAuthors(), meta);
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy#processL2Packet(cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet)
	 */
	@Override
	public void processL2Packet(L2Packet packet) {
		
		if (packet.header.type.equals(L2PacketType.KNOWLEDGE)) {
			KnowledgeData kd = (KnowledgeData)packet.getObject();
			KnowledgeMetaData meta = kd.getMetaData();
			
			if (receptionBuffer.getPulledTag(meta.componentId)) {
				// if knowledge was pulled it will be rebroadcasted
				L2Packet pck = new L2Packet(packet.header, prepareForRebroadcast(kd));
				networkLayer.sendL2Packet(pck, MANETBroadcastAddress.BROADCAST);
				receptionBuffer.clearPulledTag(meta.componentId);
			}
			else {
				// do not rebroadcast when receive own local knowledge
				if (knowledgeProvider.hasLocal(meta.componentId))
					return;
				
				// do not rebroadcast older versions than currently available
				if (!receptionBuffer.canReceive(meta.componentId, meta.versionId))
					return;
				
				if (generator.nextDouble() < probability) {
					L2Packet pck = new L2Packet(packet.header, prepareForRebroadcast(kd));
					networkLayer.sendL2Packet(pck, MANETBroadcastAddress.BROADCAST);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(Network.class, ReceptionBuffer.class, KnowledgeProviderPlugin.class);
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		// initialise dependencies
		this.knowledgeProvider = container.getPluginInstance(KnowledgeProviderPlugin.class);
		this.receptionBuffer = container.getPluginInstance(ReceptionBuffer.class);
		this.networkLayer = container.getPluginInstance(Network.class).getL2();
		// register L2 strategy
		this.networkLayer.registerL2Strategy(this);
		this.nodeId = String.valueOf(container.getId());
		
		// config parameters
		this.probability = ConfigHelper.getDouble(REBROADCAST_PROBABILITY, REBROADCAST_PROBABILITY_DEFAULT);
	}

}
