/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.connector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
import cz.cuni.mff.d3s.deeco.network.KnowledgeHelper;
import cz.cuni.mff.d3s.deeco.network.ip.IPController;
import cz.cuni.mff.d3s.deeco.network.ip.IPTable;
import cz.cuni.mff.d3s.deeco.network.ip.KnowledgeQueue;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
@Component
public class ConnectorComponent {

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
	
	@Local public Set<String> partitions;
	
	@Local public IPController controller;
	
	public Set<DicEntry> inputEntries;
	public Set<DicEntry> outputEntries;
	
	public ConnectorComponent(String id, Collection<Object> range, IPController controller) {
		this.id = id;
		this.storage = new HashedIPGossipStorage();
		this.range = new HashSet<Object>(range);
		this.inputEntries = new HashSet<DicEntry>();
		this.outputEntries = new HashSet<DicEntry>();
		this.partitions = new HashSet<String>();
		
		this.controller = controller;
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
		}
		inputEntries.value.clear();
	}
	
	@Process
	@PeriodicScheduling(period = 5000)
	public static void notifyNodes(
			@In("controller") IPController controller,
			@In("partitinos") Set<String> partitions) {
		
		for (String part : partitions) {
			IPTable tab = controller.getIPTable(part);
			if (tab != null) {
				// TODO: send notifications to all members in this table
			}
		}
	}
	
	@Process
	@PeriodicScheduling(period = 1000)
	public static void processKnowledge(
			@In("id") String id,
			@In("controller") IPController controller,
			@In("range") Set<Object> range,
			@In("partitinos") Set<String> partitions,
			@In("storage") IPGossipStorage storage,
			@InOut("outputEntries") ParamHolder<Set<DicEntry>> outputEntries
			) {
		
		outputEntries.value.clear();
		
		// TODO: get the knowledge
		List<KnowledgeData> knowledge = new ArrayList<KnowledgeData>();
		
		for (KnowledgeData kd : knowledge) {
			String sender = kd.getMetaData().sender;
			
			for (String part : partitions) {
				Object val = KnowledgeHelper.getValue(kd, part);
				if (val != null && range.contains(val)) {
					
					// current connector is responsible for this value
					// TODO: add these to the local tables
					Set<String> peers = storage.getAndUpdate(val, sender);
					
					// this is knowledge sent to the right connector because it is responsible for
					// partition key contained in this knowledge
					// should be performed a knowledge exchange??? (in another process) - for each pair
					// should this knowledge be kept here forever
				}
				else {
					// there is another connector responsible for this key
					outputEntries.value.add(new DicEntry(val, sender));
				}
			}
		}
	}
}
