/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.task;

import cz.cuni.mff.d3s.deeco.scheduler.Scheduler;
import cz.cuni.mff.d3s.deeco.task.CustomStepTask;
import cz.cuni.mff.d3s.deeco.task.TimerTask;
import cz.cuni.mff.d3s.deeco.task.TimerTaskListener;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipProperties;
import cz.cuni.mff.d3s.jdeeco.gossip.MessageHeaders;
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
	
	private Layer2 networkLayer;
	private Scheduler scheduler;
	private int period;
	
	private MessageHeaders getPayload() {
		// TODO: insert items
		return new MessageHeaders();
	}
	
	/**
	 * 
	 */
	public PushHeadersTaskListener() {
		this.period = GossipProperties.getHeadersPushPeriod();
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.task.TimerTaskListener#at(long, java.lang.Object)
	 */
	@Override
	public void at(long time, Object triger) {
		
		PacketHeader header = new PacketHeader(L2PacketType.MESSAGE_HEADERS);
		MessageHeaders data = getPayload();
		L2Packet packet = new L2Packet(header, data);
		
		networkLayer.sendL2Packet(packet, MANETBroadcastAddress.BROADCAST);
		
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
