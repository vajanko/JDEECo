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
import cz.cuni.mff.d3s.jdeeco.gossip.PullKnowledgePayload;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.address.MANETBroadcastAddress;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.Layer2;
import cz.cuni.mff.d3s.jdeeco.network.l2.PacketHeader;

public class PullKnowledgePlugin implements TimerTaskListener, DEECoPlugin {

	private MessageBuffer messageBuffer;
	private Scheduler scheduler;
	private Layer2 networkLayer;
	private int period;
	private int nodeId;
		
	@Override
	public void at(long time, Object triger) {
		
		PacketHeader header = new PacketHeader(L2PacketType.PULL_REQUEST);
		
		// check whether there are some missing messages and if yes send a PULL request
		Collection<String> missingMessages = messageBuffer.getMissingMessages(time);
		if (!missingMessages.isEmpty()) {
			PullKnowledgePayload data = new PullKnowledgePayload(missingMessages);
			L2Packet packet = new L2Packet(header, data);
			
			ConsoleLog.printRequest(nodeId, time, "KN", "PULL", missingMessages);
			networkLayer.sendL2Packet(packet, MANETBroadcastAddress.BROADCAST);
		}
		
		scheduler.addTask(new CustomStepTask(scheduler, this, period));
	}
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
		this.scheduler = container.getRuntimeFramework().getScheduler();
		this.networkLayer = container.getPluginInstance(Network.class).getL2();
		this.messageBuffer = container.getPluginInstance(MessageBuffer.class);
		this.nodeId = container.getId();
		
		this.period = GossipProperties.getKnowledgePullPeriod();
	}

}
