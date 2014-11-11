package cz.cuni.mff.d3s.deeco.simulation;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cz.cuni.mff.d3s.deeco.network.AbstractHost;
import cz.cuni.mff.d3s.deeco.network.DataReceiver;
import cz.cuni.mff.d3s.deeco.network.DataSender;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataReceiver;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataSender;

public abstract class NetworkKnowledgeDataHandler implements KnowledgeDataReceiversHolder {

	//private final Map<AbstractHost, KnowledgeDataReceiver> receivers;
	private final Map<AbstractHost, DataReceiver> receivers;
	private final Map<AbstractHost, KnowledgeDataSender> knowledgeDataSenders;

	public NetworkKnowledgeDataHandler() {
		this.receivers = new HashMap<>();
		this.knowledgeDataSenders = new HashMap<>();
	}
	
	@Override
	public void addKnowledgeDataReceiver(AbstractHost host,
			KnowledgeDataReceiver receiver) {
		//receivers.put(host, receiver);
	}
	public void addDataReceiver(AbstractHost host, DataReceiver receiver) {
		this.receivers.put(host, receiver);
	}
	public DataSender getDataSender(AbstractHost host) {
		return (DataSender)this.knowledgeDataSenders.get(host);
	}
	
	public KnowledgeDataSender getKnowledgeDataSender(AbstractHost host) {
		KnowledgeDataSender result = knowledgeDataSenders.get(host);
		if (result == null) {
			result = new NetworkKnowledgeDataSender(host);
			knowledgeDataSenders.put(host, result);
		}
		return result;
	}
	
	protected abstract void networkBroadcast(AbstractHost from, List<? extends KnowledgeData> knowledgeData, Collection<KnowledgeDataReceiver> receivers);
	protected abstract void networkSend(AbstractHost from, List<? extends KnowledgeData> knowledgeData, KnowledgeDataReceiver recipient);
	
	private class NetworkKnowledgeDataSender implements KnowledgeDataSender {
		
		private final AbstractHost host;

		public NetworkKnowledgeDataSender(AbstractHost host) {
			this.host = host;
		}
		
		@Override
		public void broadcastData(List<? extends KnowledgeData> knowledgeData) {
			//Collection<KnowledgeDataReceiver> sendTo = new LinkedList<KnowledgeDataReceiver>(receivers.values());
			Collection<KnowledgeDataReceiver> sendTo = new LinkedList<KnowledgeDataReceiver>();
			for (DataReceiver receiver : receivers.values()) {
				if (receiver instanceof KnowledgeDataReceiver) {
					sendTo.add((KnowledgeDataReceiver)receiver);
				}
			}
			sendTo.remove(receivers.get(host));
			networkBroadcast(host, knowledgeData, sendTo);
			
		}

		@Override
		public void sendData(List<? extends KnowledgeData> knowledgeData, String recipient) {
			for (AbstractHost host : receivers.keySet()) {
				if (host.getHostId().equals(recipient)) {
					DataReceiver receiver = receivers.get(host);
					if (receiver instanceof KnowledgeDataReceiver)
						networkSend(host, knowledgeData, (KnowledgeDataReceiver)receivers.get(host));
					//networkSend(host, knowledgeData, receivers.get(host));
					break;
				}
			}
			
		}		
	}

}
