package cz.cuni.mff.d3s.deeco.network.connector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;
import cz.cuni.mff.d3s.deeco.knowledge.ValueSet;
import cz.cuni.mff.d3s.deeco.logging.Log;
import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;
import cz.cuni.mff.d3s.deeco.network.AbstractHost;
import cz.cuni.mff.d3s.deeco.network.DataReceiver;
import cz.cuni.mff.d3s.deeco.network.DataSender;
import cz.cuni.mff.d3s.deeco.network.IPGossipStrategy;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 *
 */
public class IPGossipClient implements IPGossipStrategy, DataReceiver {
	
	private String partition;
	private Set<String> ipRegister;
	
	private Object getKnowledgeValue(String path, ValueSet data) {
		for (KnowledgePath kp : data.getKnowledgePaths()) {
			if (path.equals(kp.toString())) {
				Object key = data.getValue(kp);
				return key;
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.IPGossipStrategy#getRecipients(cz.cuni.mff.d3s.deeco.network.KnowledgeData, cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager)
	 */
	@Override
	public Collection<String> getRecipients(KnowledgeData data, KnowledgeManager sender) {
		
		// check whether this knowledge data are from a particular ensemble
		if (getKnowledgeValue(partition, data.getKnowledge()) == null)
			return new ArrayList<String>();
		
		ArrayList<String> res = new ArrayList<String>(ipRegister);
		res.remove(sender.getId());
		return res;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.DataReceiver#receiveData(java.lang.Object)
	 */
	@Override
	public void receiveData(Object data) {
		if (data instanceof ConnectorMessage) {
			processMessage((ConnectorMessage)data);
		}
	}
	
	private void processMessage(ConnectorMessage msg) {
		for (AddressEntry entry : msg.getEntries()) {
			switch(entry.getOperation()) {
			case Add:
				ipRegister.add(entry.getAddress());
				break;
			case Remove:
				ipRegister.remove(entry.getAddress());
				break;
			default:
				Log.w("Unknown operation type " + entry.getOperation() + " received from Connector registry");
				break;
			}
		}
	}
	
	public IPGossipClient(String partition, String initialHost, AbstractHost host) {
		this(partition, Arrays.asList(initialHost), host);
	}
	public IPGossipClient(String partition, Collection<String> initialHosts, AbstractHost host) {
		
		this.partition = partition;
		this.ipRegister = new HashSet<String>(initialHosts);
		
		// TODO: 
		host.addDataReceiver(this);
	}
}
