/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.d3s.deeco.DeecoProperties;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManagerContainer;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeNotFoundException;
import cz.cuni.mff.d3s.deeco.knowledge.ValueSet;
import cz.cuni.mff.d3s.deeco.logging.Log;
import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeMetaData;
import cz.cuni.mff.d3s.deeco.network.PublisherTask;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeFramework;
import cz.cuni.mff.d3s.deeco.scheduler.Scheduler;
import cz.cuni.mff.d3s.deeco.task.CustomStepTask;
import cz.cuni.mff.d3s.deeco.task.TimerTask;
import cz.cuni.mff.d3s.deeco.task.TimerTaskListener;
import cz.cuni.mff.d3s.deeco.timer.CurrentTimeProvider;
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
public class PushGossipTaskListener implements TimerTaskListener {

	private String nodeId;
	private KnowledgeManagerContainer kmContainer;
	private CurrentTimeProvider timeProvider;
	private Layer2 networkLayer;
	private Scheduler scheduler;
	private Integer publishingPeriod;
	
	/**
	 * 
	 */
	public PushGossipTaskListener(String nodeId, RuntimeFramework runtime, Network network) {
		this.nodeId = nodeId;
		this.networkLayer = network.getL2();
		this.kmContainer = runtime.getContainer();
		this.scheduler = runtime.getScheduler();
		this.timeProvider = scheduler.getTimer();
		this.publishingPeriod = Integer.getInteger(DeecoProperties.PUBLISHING_PERIOD, 
				PublisherTask.DEFAULT_PUBLISHING_PERIOD);
	}
	
	// NOTE: Taken from DefaultKnowledgeDataManager
	protected final List<KnowledgePath> emptyPath = null;
	
	// NOTE: Taken from DefaultKnowledgeDataManager
	protected List<KnowledgeData> prepareLocalKnowledgeData() {
		List<KnowledgeData> result = new LinkedList<>();
		
		for (KnowledgeManager km : kmContainer.getLocals()) {
			try {
				result.add(prepareLocalKnowledgeData(km));
			} catch (Exception e) {
				Log.e("prepareKnowledgeData error", e);
			}
		}
		return result;
	}
	// NOTE: Taken from DefaultKnowledgeDataManager
	protected KnowledgeData prepareLocalKnowledgeData(KnowledgeManager km) throws KnowledgeNotFoundException {
		String componentId = km.getId();
		// TODO: version is implemented by current time
		long versionId = timeProvider.getCurrentMilliseconds();
		String sender = nodeId;
		long createdAt = timeProvider.getCurrentMilliseconds();
		int hopCount = 1;
		KnowledgeMetaData metadata = new KnowledgeMetaData(componentId, versionId, sender, createdAt, hopCount);
		
		ValueSet toFilter = km.get(emptyPath);
		ValueSet knowledge = getNonLocalKnowledge(toFilter, km);
		// TODO: We are ignoring security, and host
		ValueSet security = new ValueSet();
		ValueSet authors = new ValueSet();
		KnowledgeData data = new KnowledgeData(knowledge, security, authors, metadata);
		
		return data;
	}
	// NOTE: Taken from DefaultKnowledgeDataManager
	protected ValueSet getNonLocalKnowledge(ValueSet toFilter, KnowledgeManager km) {
		ValueSet result = new ValueSet();
		for (KnowledgePath kp : toFilter.getKnowledgePaths()) {
			if (!km.isLocal(kp)) {
				result.setValue(kp, toFilter.getValue(kp));
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.task.TimerTaskListener#at(long, java.lang.Object)
	 */
	@Override
	public void at(long time, Object triger) {
		for(KnowledgeData data: prepareLocalKnowledgeData()) {
			PacketHeader header = new PacketHeader(L2PacketType.KNOWLEDGE);
			L2Packet packet = new L2Packet(header, data);
			
			networkLayer.sendL2Packet(packet, MANETBroadcastAddress.BROADCAST);
		}
		
		scheduler.addTask(new CustomStepTask(scheduler, this, publishingPeriod));
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.task.TimerTaskListener#getInitialTask(cz.cuni.mff.d3s.deeco.scheduler.Scheduler)
	 */
	@Override
	public TimerTask getInitialTask(Scheduler scheduler) {
		
		TimerTask task = new CustomStepTask(scheduler, this, publishingPeriod);
		
		return task;
	}

}
