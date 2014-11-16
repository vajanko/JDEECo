/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.ip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class IPData implements Serializable {
	private List<IPEntry> entries;
	
	public Collection<IPEntry> getEntries() {
		return entries;
	}
	
	public IPData() {
		entries = new ArrayList<IPEntry>();
	}
}
