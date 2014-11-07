package register;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sound.midi.Receiver;

import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;
import cz.cuni.mff.d3s.deeco.model.runtime.api.EnsembleDefinition;
import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;
import cz.cuni.mff.d3s.deeco.model.runtime.api.RuntimeMetadata;
import cz.cuni.mff.d3s.deeco.network.IPGossipStrategy;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.NetworkProvider;
import cz.cuni.mff.d3s.deeco.network.PacketSender;

public class IPGossipClient implements IPGossipStrategy {
	
	private Set<String> connectors;
	private Set<String> partitionedBy;
	private Set<String> peers;
	private CommunicationPort port;
	
	private Collection<Object> getParitionByValues(KnowledgeData data) {
		
		ArrayList<Object> values = new ArrayList<Object>();
		Collection<KnowledgePath> paths = data.getKnowledge().getKnowledgePaths();

		// For all knowledge paths
		for (KnowledgePath p : paths) {
			String pathStr = p.toString();
			// If knowledge is used for partitioning
			if (partitionedBy.contains(pathStr)) {
				Object value = data.getKnowledge().getValue(p);
				values.add(value);
			}
		}
		
		return values;
	}
	private RequestMessage createMessage(String senderId, Collection<Object> keyValues) {
		RequestMessage msg = new RequestMessage(senderId, keyValues);
		
		return msg;
	}
	private void processMessage(ResponseMessage msg) {
		for (String p : msg.addPeers)
			peers.add(p);
		for (String p : msg.removePeers)
			peers.remove(p);		
	}
	private Collection<String> getPeers(String senderId, Collection<Object> keyValues) {
		
		// TODO: implement some strategy for choosing the recipient
		String recipient = connectors.iterator().next();
		RequestMessage request = createMessage(senderId, keyValues);
		
		port.sendMessage(request, recipient);
		
		// this is a blocking call
		ResponseMessage response = port.receiveMessage();
		if (response != null)
			processMessage(response);
		
		// notice that in the received list of peers sender ID is omitted
		return peers;
	}
	
	public Collection<String> getRecipients(KnowledgeData data, KnowledgeManager sender) {
		
		Collection<Object> partitionValues = getParitionByValues(data);
		
		Collection<String> peers = getPeers(sender.getId(), partitionValues);

		return peers;
	}
	
	public IPGossipClient(String initialHost, RuntimeMetadata model, PacketSender packetSender) {
		this(Arrays.asList(initialHost), model, packetSender);
	}
	public IPGossipClient(Collection<String> initialHosts, RuntimeMetadata model, PacketSender packetSender) {
		
		this.partitionedBy = new HashSet<String>();
		this.connectors = new HashSet<String>(initialHosts);
		this.peers = new HashSet<String>();
		
		Collection<EnsembleDefinition> ensembles = model.getEnsembleDefinitions();
		for (EnsembleDefinition ensemble : ensembles) {
			partitionedBy.add(ensemble.getPartitionedBy());
		}

		this.port = new CommunicationPort(packetSender);
	}
}
