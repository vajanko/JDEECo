/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.connector;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class ConnectorMessage implements Serializable {
	public List<AddressEntry> entries;
	
	public Collection<AddressEntry> getEntries() {
		return entries;
	}
}
