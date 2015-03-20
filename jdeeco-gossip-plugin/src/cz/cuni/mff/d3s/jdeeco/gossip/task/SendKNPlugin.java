package cz.cuni.mff.d3s.jdeeco.gossip.task;

import java.util.ArrayList;
import java.util.List;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeMetaData;
import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipProperties;
import cz.cuni.mff.d3s.jdeeco.gossip.KnowledgeProvider;
import cz.cuni.mff.d3s.jdeeco.network.address.MANETBroadcastAddress;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.PacketHeader;

/**
 * Broadcasts local knowledge data.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class SendKNPlugin extends SendBasePlugin {
	protected KnowledgeProvider knowledgeProvider; 
	
	public SendKNPlugin() {
		super(GossipProperties.getKnowledgePushPeriod());
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.task.TimerTaskListener#at(long, java.lang.Object)
	 */
	@Override
	public void at(long time, Object triger) {
		for(KnowledgeData data: knowledgeProvider.getLocalKnowledgeData()) {
			PacketHeader header = new PacketHeader(L2PacketType.KNOWLEDGE);
			L2Packet packet = new L2Packet(header, data);
			
			// broadcast knowledge ...
			networkLayer.sendL2Packet(packet, MANETBroadcastAddress.BROADCAST);
			
			// ... and stores information about last knowledge version
			KnowledgeMetaData meta = data.getMetaData();
			messageBuffer.notifyLocalPush(meta.componentId, meta.createdAt);
		}
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		ArrayList<Class<? extends DEECoPlugin>> result = new ArrayList<Class<? extends DEECoPlugin>>();
		result.addAll(super.getDependencies());
		result.add(KnowledgeProvider.class);
		
		return result;
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		// initialise dependencies
		this.knowledgeProvider = container.getPluginInstance(KnowledgeProvider.class);
		
		super.init(container);
	}

}
