package cz.cuni.mff.d3s.jdeeco.gossip;

import cz.cuni.mff.d3s.deeco.scheduler.Scheduler;
import cz.cuni.mff.d3s.deeco.task.CustomStepTask;
import cz.cuni.mff.d3s.deeco.task.TimerTask;
import cz.cuni.mff.d3s.deeco.task.TimerTaskListener;
import cz.cuni.mff.d3s.jdeeco.network.address.MANETBroadcastAddress;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.Layer2;
import cz.cuni.mff.d3s.jdeeco.network.l2.PacketHeader;

public class PullKnowledgeTaskListener implements TimerTaskListener {

	private Scheduler scheduler;
	private Layer2 networkLayer;
	private int period;
	
	public PullKnowledgeTaskListener() {
		this.period = GossipProperties.getKnowledgePullTimeout();
	}
	
	private Object getPayload() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void at(long time, Object triger) {
		
		PacketHeader header = new PacketHeader(L2PacketType.PULL_REQUEST);
		Object data = getPayload();
		L2Packet packet = new L2Packet(header, data);
		
		networkLayer.sendL2Packet(packet, MANETBroadcastAddress.BROADCAST);
		
		scheduler.addTask(new CustomStepTask(scheduler, this, period));
	}
	@Override
	public TimerTask getInitialTask(Scheduler scheduler) {
		return new CustomStepTask(scheduler, this, period);
	}

}
