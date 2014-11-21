/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.connector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.Local;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.annotations.TriggerOnChange;
import cz.cuni.mff.d3s.deeco.network.DicEntry;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeHelper;
import cz.cuni.mff.d3s.deeco.network.ip.IPController;
import cz.cuni.mff.d3s.deeco.network.ip.IPData;
import cz.cuni.mff.d3s.deeco.network.ip.IPDataSender;
import cz.cuni.mff.d3s.deeco.network.ip.IPTable;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
@Component
public class ConnectorComponent {

	public String id;
	public Integer CONNECTOR_TAG = 0;
	public String group = "destination";
	
	// Key space range - current connector stores values for this collection of keys
	// for now suppose that key space partitioning is given.
	// In the future it can be updated by the IPService
	@Local public Set<Object> range;
	
	@Local public Set<String> partitions;
	
	@Local public IPController controller;
	@Local public IPDataSender sender;
	@Local public KnowledgeProvider provider;
	
	public Set<DicEntry> inputEntries;
	public Set<DicEntry> outputEntries;
	
	public ConnectorComponent(String id, Collection<Object> range, IPController controller, IPDataSender sender) {
		this.id = id;
		this.range = new HashSet<Object>(range);
		this.inputEntries = new HashSet<DicEntry>();
		this.outputEntries = new HashSet<DicEntry>();
		this.partitions = new HashSet<String>();
		
		this.controller = controller;
		this.sender = sender;
	}
	
	@Process
	//@PeriodicScheduling(period = 2000)
	public static void processEntries(
			@In("id") String id,
			@In("range") Set<Object> range,
			@In("controller") IPController controller,
			@TriggerOnChange @InOut("inputEntries") ParamHolder<Collection<DicEntry>> inputEntries) {
		
		// move these entries to my local storage
		for (DicEntry entry : inputEntries.value) {
			if (range.contains(entry.getKey())) {
				controller.getIPTable(entry.getKey()).add(entry.getAddress());
			}
		}
		inputEntries.value.clear();
	}
	
	@Process
	@PeriodicScheduling(period = 5000)
	public static void notifyNodes(
			@In("sender") IPDataSender sender,
			@In("controller") IPController controller,
			@In("range") Set<Object> range) {
		
		// FIXME: ? send by parts, not all at once ?
		for (Object partVal : range) {
			IPTable tab = controller.getIPTable(partVal);
			if (tab != null) {
				IPData data = new IPData(partVal, tab.getAddresses());
				for (String recipient : tab.getAddresses()) {
					sender.sendData(data, recipient);
				}
			}
		}
	}
	
	@Process
	@PeriodicScheduling(period = 1000)
	public static void processKnowledge(
			@In("id") String id,
			@In("controller") IPController controller,
			@In("provider") KnowledgeProvider provider,
			@In("range") Set<Object> range,
			@In("partitions") Set<String> partitions,
			@InOut("outputEntries") ParamHolder<Set<DicEntry>> outputEntries
			) {
		
		outputEntries.value.clear();
		
		for (Entry<Object, KnowledgeData> item : provider.getKnowledge().entrySet()) {
			
		}
		
		// TODO: get the knowledge
		List<KnowledgeData> knowledge = new ArrayList<KnowledgeData>();
		
		for (KnowledgeData kd : knowledge) {
			String sender = kd.getMetaData().sender;
			
			for (String part : partitions) {
				Object val = KnowledgeHelper.getValue(kd, part);
				if (val != null && range.contains(val)) {
					
					// current connector is responsible for this value
					controller.getIPTable(val).add(sender);
				}
				else {
					// there is another connector responsible for this key
					outputEntries.value.add(new DicEntry(val, sender));
				}
			}
		}
	}
}
