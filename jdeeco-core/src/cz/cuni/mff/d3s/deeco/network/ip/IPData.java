/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.ip;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class IPData implements Serializable {
	
	private Set<String> addresses; 
	private Object partitionValue;
	
	public Object getPartitionValue() {
		return partitionValue;
	}
	public Collection<String> getAddresses() {
		return addresses;
	}
	public void addAddress(String address) {
		addresses.add(address);
	}
	
	public IPData(Object partitionValue) {
		this.partitionValue = partitionValue;
		this.addresses = new HashSet<String>();
	}
	public IPData(Object partitionValue, Collection<String> addresses) {
		this.partitionValue = partitionValue;
		this.addresses = new HashSet<String>(addresses);
	}
}
