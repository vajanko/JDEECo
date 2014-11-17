/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.ip;

import java.io.Serializable;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class IPEntry implements Serializable {
	private String address;
	private OperationType operation;
	
	public OperationType getOperation() {
		return operation;
	}
	public String getAddress() {
		return address;
	}
	@Override
	public String toString() {
		return address + (operation == OperationType.Add ? "+" : "-");
	}
	
	public IPEntry(String address, OperationType operation) {
		this.address = address;
		this.operation = operation;
	}
	
	public enum OperationType {
		None,
		Add,
		Remove
	}
	
}
