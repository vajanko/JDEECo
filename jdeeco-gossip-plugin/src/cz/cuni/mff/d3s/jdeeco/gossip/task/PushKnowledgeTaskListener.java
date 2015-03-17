/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.task;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeFramework;
import cz.cuni.mff.d3s.deeco.scheduler.Scheduler;
import cz.cuni.mff.d3s.deeco.task.CustomStepTask;
import cz.cuni.mff.d3s.deeco.task.TimerTask;
import cz.cuni.mff.d3s.deeco.task.TimerTaskListener;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipProperties;
import cz.cuni.mff.d3s.jdeeco.gossip.KnowledgeProvider;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.address.MANETBroadcastAddress;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.Layer2;
import cz.cuni.mff.d3s.jdeeco.network.l2.PacketHeader;

/**
 * Task responsible for regularly broadcasting local knowledge data into the network.
 * 
 * @author Ondrej Kov�� <info@vajanko.me>
 */
public class PushKnowledgeTaskListener implements TimerTaskListener {

	private KnowledgeProvider knowledgeProvider; 
	private Layer2 networkLayer;
	private Scheduler scheduler;
	private int period;
	
	/**
	 * Creates a new instance of {@link TimerTaskListener} responsible for regularly broadcasting
	 * local knowledge data. The period of the broadcast is a configurable property.
	 */
	public PushKnowledgeTaskListener(RuntimeFramework runtime, Network network, GossipPlugin gossip) {
		this.knowledgeProvider = gossip.getKnowledgeProvider();
		this.networkLayer = network.getL2();
		this.scheduler = runtime.getScheduler();
		this.period = GossipProperties.getKnowledgePushPeriod();
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.task.TimerTaskListener#at(long, java.lang.Object)
	 */
	@Override
	public void at(long time, Object triger) {
		for(KnowledgeData data: knowledgeProvider.getLocalKnowledgeData()) {
			PacketHeader header = new PacketHeader(L2PacketType.KNOWLEDGE);
			L2Packet packet = new L2Packet(header, data);
			
			System.out.println(String.format("[%s] %4d: PUSH knowledge", data.getMetaData().componentId, time));
			networkLayer.sendL2Packet(packet, MANETBroadcastAddress.BROADCAST);
		}
		
		scheduler.addTask(new CustomStepTask(scheduler, this, period));
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.task.TimerTaskListener#getInitialTask(cz.cuni.mff.d3s.deeco.scheduler.Scheduler)
	 */
	@Override
	public TimerTask getInitialTask(Scheduler scheduler) {
		return new CustomStepTask(scheduler, this, period);
	}

}
