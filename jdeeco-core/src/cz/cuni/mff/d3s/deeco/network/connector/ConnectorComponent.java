/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.connector;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.Local;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.annotations.TriggerOnChange;
import cz.cuni.mff.d3s.deeco.knowledge.ValueSet;
import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;
import cz.cuni.mff.d3s.deeco.network.DicEntry;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.ip.IPController;
import cz.cuni.mff.d3s.deeco.network.ip.KnowledgeQueue;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
@Component
public class ConnectorComponent {
	
	@Local public Double xCoord;
	@Local public Double yCoord;
	
	public String id;
	public Integer CONNECTOR_TAG = 0;
	public String partition = "destination";
	
	// Notice that in this storage are only values associated with values in the range set.
	// Storage is updated by component process - entries coming from other connectors or by
	// the IPService when knowledge is received from other nodes.
	@Local public IPGossipStorage storage;
	
	// Key space range - current connector stores values for this collection of keys
	// for now suppose that key space partitioning is given.
	// In the future it can be updated by the IPService
	@Local public Set<Object> range;
	
	@Local public KnowledgeQueue knowledgeQueue;
	@Local public IPController controller;
	
	public Set<DicEntry> inputEntries;
	public Set<DicEntry> outputEntries;
	
	public ConnectorComponent(String id, Double xCoord, Double yCoord, Collection<Object> range) {
		this.id = id;
		this.storage = new HashedIPGossipStorage();
		this.range = new HashSet<Object>(range);
		this.inputEntries = new HashSet<DicEntry>();
		this.outputEntries = new HashSet<DicEntry>();
		
		this.xCoord = xCoord;
		this.yCoord = yCoord;
	}
	
	@Process
	//@PeriodicScheduling(period = 2000)
	public static void processEntries(
			@In("id") String id,
			@In("storage") IPGossipStorage storage,
			@In("controller") IPController controller,
			@TriggerOnChange @InOut("inputEntries") ParamHolder<Collection<DicEntry>> inputEntries) {
		
		// move these entries to my local storage
		for (DicEntry entry : inputEntries.value) {
			// TODO: filter only those in my current range			
			Set<String> peers = storage.getAndUpdate(entry.getKey(), entry.getAddress());
			// these entries were sent from another connector not responsible for given partition key
			// notify back to given address of knowledge sender about all its peers
			// This should also contain current connector address
//			for (String peer : peers)
//				controller.notify(entry.getAddress(), peer, OperationType.Add);
		}
		inputEntries.value.clear();
	}
	
	@Process
	@PeriodicScheduling(period = 2000)
	public static void notifyNodes(@In("controller") IPController controller) {
		//controller.pushNotifications();
		// TODO: send IP tables
	}
	
	@Process
	@PeriodicScheduling(period = 1000)
	public static void processKnowledge(
			@In("id") String id,
			@In("knowledgeQueue") KnowledgeQueue knowledgeQueue,
			@In("controller") IPController controller,
			@In("range") Set<Object> range,
			@In("storage") IPGossipStorage storage,
			@InOut("outputEntries") ParamHolder<Set<DicEntry>> outputEntries
			) {
		
		outputEntries.value.clear();
		
		while (!knowledgeQueue.empty()) {
			KnowledgeData kd = knowledgeQueue.pop();
			
			ValueSet knowledge = kd.getKnowledge();
			String sender = kd.getMetaData().sender;
			
			for (KnowledgePath path : knowledge.getKnowledgePaths()) {
				
				if (knowledgeQueue.getPartitions().contains(path.toString())) {
					Object key = knowledge.getValue(path);
					if (range.contains(key)) {
						// current connector is responsible for this key
						Set<String> peers = storage.getAndUpdate(key, sender);
						
						// this is knowledge sent to the right connector because it is responsible for
						// partition key contained in this knowledge
						// should be performed a knowledge exchange??? (in another process) - for each pair
						// should this knowledge be kept here forever
					}
					else {
						// there is another connector responsible for this key
						outputEntries.value.add(new DicEntry(key, sender));
					}
				}
			}
		}
	}
	
//	@Process
//	@PeriodicScheduling(period = 1000)
//	public static void updateIPTable(
//			@In("id") String id,
//			@In("controller") IPController controller,
//			@In("nodes") Collection<KnowledgeData> nodes) {
//		
//	}
	
//	@Process
//	@PeriodicScheduling(period = 1000)
//	public static void paring(
//			@In("nodes") Collection<KnowledgeData> nodes) {
//		
//	}
}
