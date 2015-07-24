/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.receive;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManagerContainer;
import cz.cuni.mff.d3s.deeco.model.runtime.api.EnsembleDefinition;
import cz.cuni.mff.d3s.deeco.model.runtime.api.RuntimeMetadata;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeMetaData;
import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.jdeeco.core.AddressHelper;
import cz.cuni.mff.d3s.jdeeco.core.ConfigHelper;
import cz.cuni.mff.d3s.jdeeco.core.KnowledgeProviderPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipHelper;
import cz.cuni.mff.d3s.jdeeco.gossip.ReceptionBuffer;
import cz.cuni.mff.d3s.jdeeco.gossip.send.SendKNPlugin;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.address.Address;
import cz.cuni.mff.d3s.jdeeco.network.address.IPAddress;
import cz.cuni.mff.d3s.jdeeco.network.address.MANETBroadcastAddress;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy;
import cz.cuni.mff.d3s.jdeeco.network.l2.Layer2;

/**
 * Rebroadcast received L2 packets over MANET with certain probability.
 * Different probabilities are used for knowledge and other types of data.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class GossipRebroadcastPlugin implements L2Strategy, DEECoPlugin {

	/**
	 * Name of the configuration property expressing probability of gossip rebroadcast
	 */
	public static final String REBROADCAST_PROBABILITY = "deeco.gossipRebroadcast.probability";
	/**
	 * Default value of knowledge rebroadcast probability when received by current node.
	 */
	public static final double REBROADCAST_PROBABILITY_DEFAULT = 0.5;
	
	/**
	 * Probability of gossip rebroadcast.
	 */
	private double probability;
	/**
	 * Network layer allowing message sending.
	 */
	private Layer2 networkLayer;
	private ReceptionBuffer receptionBuffer;
	private KnowledgeProviderPlugin knowledgeProvider;
	private RuntimeMetadata runtimeMetadata;
	private String nodeId;
	private KnowledgeManagerContainer kmContainer;
	
	private SendKNPlugin knowledgeSender;
	/**
	 * IP address of the current node.
	 */
	private IPAddress address;
	
	/**
	 * Modify given knowledge data for rebroadcast. In particular the hop count is incremented
	 * and the sender is set to the current node.
	 * @param kd Knowledge data to be rebroadcasted.
	 * @return Modified knowledge data prepared for rebroadcast.
	 */
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
			
			if (!withinBoundary(kd)) {
				//System.out.println("out of boundary");
				return;
			}
			
			// do not rebroadcast when receive own local knowledge
			if (knowledgeProvider.hasLocal(meta.componentId))
				return;
			
			// do not rebroadcast older versions than currently available
			// because the newer version of this knowledge already has had possibility
			// to be rebroadcasted
			if (!receptionBuffer.canReceive(meta.componentId, meta.versionId))
				return;
			
			L2Packet pck = new L2Packet(packet.header, prepareForRebroadcast(kd));
			
			if (receptionBuffer.getPulledTag(meta.componentId)) {
				// pulled knowledge is rebroadcasted
				networkLayer.sendL2Packet(pck, MANETBroadcastAddress.BROADCAST);
				receptionBuffer.clearPulledTag(meta.componentId);
			}
			else if (GossipHelper.generator.nextDouble() < probability) {
				// rebroadcast with certain probability on MANET
				networkLayer.sendL2Packet(pck, MANETBroadcastAddress.BROADCAST);
			}
			
			// rebroadcast to random subset of known nodes
			for (Address adr : this.knowledgeSender.getRecipientSelector().getRecipients(kd)) {
				if (adr.equals(this.address))
					continue;
				
				// rebroadcast with certain probability on IP
				if (GossipHelper.generator.nextDouble() < probability) {
					networkLayer.sendL2Packet(pck, adr);
				}
			}
		}
	}
	/**
	 * Gets value indicating whether given knowledge data is within MANET communication boundary.
	 * @param data Received knowledge data.
	 * @return True if given knowledge is within the communication boundary.
	 */
	protected boolean withinBoundary(KnowledgeData data) {
		
		// FIXME: create a KM which will contain union of the entire node knowledge
		KnowledgeManager sender = this.kmContainer.getLocals().iterator().next();
		
		for (EnsembleDefinition ens: runtimeMetadata.getEnsembleDefinitions()) {
			// null boundary condition counts as a satisfied one
			if (ens.getCommunicationBoundary() == null)
				return true;
			if (ens.getCommunicationBoundary().eval(data, sender))
				return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(Network.class, ReceptionBuffer.class, KnowledgeProviderPlugin.class, SendKNPlugin.class);
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		// initialise dependencies
		this.runtimeMetadata = container.getRuntimeMetadata();
		this.kmContainer = container.getRuntimeFramework().getContainer();
		this.knowledgeProvider = container.getPluginInstance(KnowledgeProviderPlugin.class);
		this.receptionBuffer = container.getPluginInstance(ReceptionBuffer.class);
		this.networkLayer = container.getPluginInstance(Network.class).getL2();
		// register L2 strategy
		this.networkLayer.registerL2Strategy(this);
		this.nodeId = String.valueOf(container.getId());
		this.address = AddressHelper.createIP(container.getId());
		
		// config parameters
		this.probability = ConfigHelper.getDouble(REBROADCAST_PROBABILITY, REBROADCAST_PROBABILITY_DEFAULT);
		this.knowledgeSender = container.getPluginInstance(SendKNPlugin.class);
	}

}
