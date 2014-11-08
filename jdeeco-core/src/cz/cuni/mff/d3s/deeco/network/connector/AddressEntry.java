/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.connector;

import java.io.Serializable;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class AddressEntry implements Serializable {
	public String address;
	public OperationType operation;
	
	public OperationType getOperation() {
		return operation;
	}
	public String getAddress() {
		return address;
	}
	
	public enum OperationType {
		None,
		Add,
		Remove
	}
	
}
