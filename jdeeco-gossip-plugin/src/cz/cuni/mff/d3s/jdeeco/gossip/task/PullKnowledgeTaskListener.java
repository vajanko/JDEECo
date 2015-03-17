package cz.cuni.mff.d3s.jdeeco.gossip.task;

import java.util.Collection;

import cz.cuni.mff.d3s.deeco.runtime.RuntimeFramework;
import cz.cuni.mff.d3s.deeco.scheduler.Scheduler;
import cz.cuni.mff.d3s.deeco.task.CustomStepTask;
import cz.cuni.mff.d3s.deeco.task.TimerTask;
import cz.cuni.mff.d3s.deeco.task.TimerTaskListener;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipProperties;
import cz.cuni.mff.d3s.jdeeco.gossip.MessageBuffer;
import cz.cuni.mff.d3s.jdeeco.gossip.PullKnowledgePayload;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.address.MANETBroadcastAddress;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.Layer2;
import cz.cuni.mff.d3s.jdeeco.network.l2.PacketHeader;

public class PullKnowledgeTaskListener implements TimerTaskListener {

	private MessageBuffer messageBuffer;
	private Scheduler scheduler;
	private Layer2 networkLayer;
	private int period;
	
	public PullKnowledgeTaskListener(MessageBuffer messageBuffer, RuntimeFramework runtime, Network network) {
		this.messageBuffer = messageBuffer;
		this.scheduler = runtime.getScheduler();
		this.networkLayer = network.getL2();
		this.period = GossipProperties.getKnowledgePullPeriod();
	}
	
	@Override
	public void at(long time, Object triger) {
		
		PacketHeader header = new PacketHeader(L2PacketType.PULL_REQUEST);
		
		// check whether there are some missing messages and if yes send a PULL request
		Collection<String> missingMessages = messageBuffer.getMissingMessages(time);
		if (!missingMessages.isEmpty()) {
			PullKnowledgePayload data = new PullKnowledgePayload(missingMessages);
			L2Packet packet = new L2Packet(header, data);
			
			networkLayer.sendL2Packet(packet, MANETBroadcastAddress.BROADCAST);
		}
		
		scheduler.addTask(new CustomStepTask(scheduler, this, period));
	}
	@Override
	public TimerTask getInitialTask(Scheduler scheduler) {
		return new CustomStepTask(scheduler, this, period);
	}

}
