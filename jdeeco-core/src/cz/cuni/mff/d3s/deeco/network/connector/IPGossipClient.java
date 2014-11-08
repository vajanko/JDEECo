package cz.cuni.mff.d3s.deeco.network.connector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;
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
	
	private Set<String> ipRegister;
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.IPGossipStrategy#getRecipients(cz.cuni.mff.d3s.deeco.network.KnowledgeData, cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager)
	 */
	@Override
	public Collection<String> getRecipients(KnowledgeData data, KnowledgeManager sender) {
		ArrayList<String> res = new ArrayList<String>(ipRegister);
		res.remove(sender.getId());
		return res;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.DataReceiver#receiveData(java.lang.Object)
	 */
	@Override
	public void receiveData(Object data) {
//		if (data instanceof String) {	// TODO: create some type for incoming messages
//			
//		}
	}
	
	public IPGossipClient(String initialHost, AbstractHost host) {
		this(Arrays.asList(initialHost), host);
	}
	public IPGossipClient(Collection<String> initialHosts, AbstractHost host) {

		this.ipRegister = new HashSet<String>(initialHosts);
		
		host.addDataReceiver(this);
	}
}
