/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.task;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeMetaData;
import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.scheduler.Scheduler;
import cz.cuni.mff.d3s.deeco.task.TimerTask;
import cz.cuni.mff.d3s.deeco.task.TimerTaskListener;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipProperties;
import cz.cuni.mff.d3s.jdeeco.gossip.KnowledgeProvider;
import cz.cuni.mff.d3s.jdeeco.gossip.MessageBuffer;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.address.MANETBroadcastAddress;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.Layer2;
import cz.cuni.mff.d3s.jdeeco.network.l2.PacketHeader;

/**
 * Implements functionality of task responsible for regularly broadcasting local 
 * knowledge data into the network.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class PushKnowledgePlugin implements TimerTaskListener, DEECoPlugin {

	private KnowledgeProvider knowledgeProvider; 
	private Layer2 networkLayer;
	private MessageBuffer messageBuffer;
	
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
			messageBuffer.localUpdate(meta.componentId, meta.createdAt);
		}
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.task.TimerTaskListener#getInitialTask(cz.cuni.mff.d3s.deeco.scheduler.Scheduler)
	 */
	@Override
	public TimerTask getInitialTask(Scheduler scheduler) {
		return null;
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
		// initialise dependencies
		this.knowledgeProvider = container.getPluginInstance(KnowledgeProvider.class);
		this.networkLayer = container.getPluginInstance(Network.class).getL2();
		this.messageBuffer = container.getPluginInstance(MessageBuffer.class);
		
		// run PUSH knowledge gossip task 
		Scheduler scheduler = container.getRuntimeFramework().getScheduler();
		long period = GossipProperties.getKnowledgePushPeriod();
		scheduler.addTask(new PeriodicTask(scheduler, this, period));
	}

}
