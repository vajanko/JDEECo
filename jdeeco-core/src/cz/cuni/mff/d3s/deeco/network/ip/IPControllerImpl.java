/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.ip;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import cz.cuni.mff.d3s.deeco.network.DataReceiver;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class IPControllerImpl implements IPController, DataReceiver {

	// key: partitionValue, value: partition IP table
	private Map<Object, IPRegister> ipTables;
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.ip.IPController#getIPTable(java.lang.String)
	 */
	@Override
	public IPRegister getRegister(Object partitionValue) {
		
		if (!ipTables.containsKey(partitionValue))
			ipTables.put(partitionValue, new IPRegister());
		
		return ipTables.get(partitionValue);
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.KnowledgeDataReceiver#receive(java.util.List)
	 */
	@Override
	public void receiveData(Object data) {
		if (! (data instanceof IPData))
			return;
		
		IPData ipdata = (IPData)data;
		
		IPRegister ipTable = getRegister(ipdata.getPartitionValue());
		
		ipTable.clear();
		ipTable.addAll(ipdata.getAddresses());
	}
	
	public IPControllerImpl() {
		this.ipTables = new HashMap<Object, IPRegister>();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (Entry<Object, IPRegister> item : ipTables.entrySet()) {
			str.append(item.getKey().toString() + "->" + item.getValue().toString() + "\n");
		}
		return str.toString();
	}
}
