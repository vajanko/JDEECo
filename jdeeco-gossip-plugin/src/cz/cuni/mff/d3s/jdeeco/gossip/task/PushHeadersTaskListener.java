/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.task;

import java.util.Collection;

import cz.cuni.mff.d3s.deeco.runtime.RuntimeFramework;
import cz.cuni.mff.d3s.deeco.scheduler.Scheduler;
import cz.cuni.mff.d3s.deeco.task.CustomStepTask;
import cz.cuni.mff.d3s.deeco.task.TimerTask;
import cz.cuni.mff.d3s.deeco.task.TimerTaskListener;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipProperties;
import cz.cuni.mff.d3s.jdeeco.gossip.MessageBuffer;
import cz.cuni.mff.d3s.jdeeco.gossip.MessageHeader;
import cz.cuni.mff.d3s.jdeeco.gossip.PushHeadersPayload;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.address.MANETBroadcastAddress;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.Layer2;
import cz.cuni.mff.d3s.jdeeco.network.l2.PacketHeader;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class PushHeadersTaskListener implements TimerTaskListener {
	
	private MessageBuffer messageBuffer;
	private Layer2 networkLayer;
	private Scheduler scheduler;
	private int period;
	
	/**
	 * 
	 */
	public PushHeadersTaskListener(RuntimeFramework runtime, Network network, GossipPlugin gossip) {
		this.messageBuffer = gossip.getMessageBuffer();
		this.scheduler = runtime.getScheduler();
		this.networkLayer = network.getL2();
		this.period = GossipProperties.getHeadersPushPeriod();
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.task.TimerTaskListener#at(long, java.lang.Object)
	 */
	@Override
	public void at(long time, Object triger) {
		
		PacketHeader header = new PacketHeader(L2PacketType.MESSAGE_HEADERS);
		
		Collection<MessageHeader> headers = messageBuffer.getKnownMessages(time);
		if (!headers.isEmpty()) {
			PushHeadersPayload data = new PushHeadersPayload(headers);
			L2Packet packet = new L2Packet(header, data);

			System.out.println(String.format("[%s] %4d: PUSH headers", "??", time));
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
