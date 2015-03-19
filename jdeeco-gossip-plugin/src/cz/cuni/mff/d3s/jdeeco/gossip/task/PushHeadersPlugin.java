/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.task;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.scheduler.Scheduler;
import cz.cuni.mff.d3s.deeco.task.CustomStepTask;
import cz.cuni.mff.d3s.deeco.task.TimerTask;
import cz.cuni.mff.d3s.deeco.task.TimerTaskListener;
import cz.cuni.mff.d3s.jdeeco.gossip.ConsoleLog;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipProperties;
import cz.cuni.mff.d3s.jdeeco.gossip.MessageBuffer;
import cz.cuni.mff.d3s.jdeeco.gossip.MessageHeader;
import cz.cuni.mff.d3s.jdeeco.gossip.PushHeadersPayload;
import cz.cuni.mff.d3s.jdeeco.gossip.ConsoleLog.ActType;
import cz.cuni.mff.d3s.jdeeco.gossip.ConsoleLog.MsgType;
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
public class PushHeadersPlugin implements TimerTaskListener, DEECoPlugin {
	
	private MessageBuffer messageBuffer;
	private Layer2 networkLayer;
	private Scheduler scheduler;
	private int period;
	private int nodeId;
	
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

			ConsoleLog.printRequest(nodeId, time, MsgType.HD, ActType.SEND, headers);
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

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(Network.class, MessageBuffer.class);
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		// initialise dependencies
		this.scheduler = container.getRuntimeFramework().getScheduler();
		this.networkLayer = container.getPluginInstance(Network.class).getL2();
		this.messageBuffer = container.getPluginInstance(MessageBuffer.class);
		this.nodeId = container.getId();
		
		this.period = GossipProperties.getHeadersPushPeriod();
		
		// run PUSH message headers gossip task
		scheduler.addTask(new CustomStepTask(scheduler, this));
	}

}
