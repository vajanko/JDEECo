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
	private String address;
	private OperationType operation;
	
	public OperationType getOperation() {
		return operation;
	}
	public String getAddress() {
		return address;
	}
	
	public AddressEntry(String address, OperationType operation) {
		this.address = address;
		this.operation = operation;
	}
	
	public enum OperationType {
		None,
		Add,
		Remove
	}
	
}
