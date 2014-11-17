/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.ip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cz.cuni.mff.d3s.deeco.network.ip.IPEntry.OperationType;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class IPData implements Serializable {
	private List<IPEntry> entries;
	
	public Collection<IPEntry> getEntries() {
		return entries;
	}
	public void addEntry(String address, OperationType op) {
		entries.add(new IPEntry(address, op));
	}
	
	public IPData() {
		this.entries = new ArrayList<IPEntry>();
	}
	public IPData(Collection<IPEntry> entries) {
		this.entries = new ArrayList<IPEntry>(entries);
	}
}
