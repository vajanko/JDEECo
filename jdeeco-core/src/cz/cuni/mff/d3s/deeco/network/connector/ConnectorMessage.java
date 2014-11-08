/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.connector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class ConnectorMessage implements Serializable {
	private List<AddressEntry> entries;
	
	public Collection<AddressEntry> getEntries() {
		return entries;
	}
	
	public ConnectorMessage() {
		entries = new ArrayList<AddressEntry>();
	}
}
