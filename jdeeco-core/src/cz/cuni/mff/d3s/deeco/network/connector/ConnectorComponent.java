/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.connector;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.Local;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.ip.IPTable;
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
	
	@Local public IPTable ipTable;
	@Local public IPGossipStorage storage;
	
	//public Collection<KnowledgeData> nodes;
	
	// key space range - current connector stores values for this collection of keys
	// for now suppose that key space partitioning is given
	@Local public Set<Object> range;
	
	public Collection<DicEntry> inputEntries;
	public Collection<DicEntry> outputEntries;
	
	public ConnectorComponent(String id, Collection<Object> range) {
		this.id = id;
		this.range = new HashSet<Object>(range);
	}
	
	@Process
	@PeriodicScheduling(period = 2000)
	public static void processEntries(
			@InOut("storage") ParamHolder<IPGossipStorage> storage,
			@InOut("inputEntries") ParamHolder<Collection<DicEntry>> inputEntries) {
		
		// move these entries to my local storage
		for (DicEntry entry : inputEntries.value)
			storage.value.getAndUpdate(entry.key, entry.address);
		inputEntries.value.clear();
	}
	
	
//	@Process
//	@PeriodicScheduling(period = 1000)
//	public static void updateIPTable(
//			@In("id") String id,
//			@InOut("ipTable") ParamHolder<IPTable> ipTable,
//			@In("nodes") Collection<KnowledgeData> nodes) {
//		
//	}
	
//	@Process
//	@PeriodicScheduling(period = 1000)
//	public static void paring(
//			@InOut("nodes") ParamHolder<Collection<KnowledgeData>> nodes) {
//		
//	}
}

class DicEntry implements Serializable {
	public Object key;
	public String address;
	
	public Object getKey() { return key; }
	public String getAddress() { return address; }
}
