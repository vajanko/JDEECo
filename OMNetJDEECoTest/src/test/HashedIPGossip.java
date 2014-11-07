package test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;
import cz.cuni.mff.d3s.deeco.model.runtime.api.EnsembleDefinition;
import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;
import cz.cuni.mff.d3s.deeco.model.runtime.api.RuntimeMetadata;
import cz.cuni.mff.d3s.deeco.network.IPGossipStrategy;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;

public class HashedIPGossip implements IPGossipStrategy {
	HashedIPGossipStorage storage;
	Set<String> partitionedBy = new HashSet<String>();

	HashedIPGossip(RuntimeMetadata model, HashedIPGossipStorage storage) {
		this.storage = storage;

		Collection<EnsembleDefinition> ensembles = model.getEnsembleDefinitions();
		for (EnsembleDefinition ensemble : ensembles) {
			partitionedBy.add(ensemble.getPartitionedBy());
		}
	}

	public Collection<String> getRecipients(KnowledgeData data, KnowledgeManager sender) {
		Collection<String> peers = new HashSet<String>();
		Collection<KnowledgePath> paths = data.getKnowledge().getKnowledgePaths();

		// For all knowledge paths
		for (KnowledgePath p : paths) {
			String pathStr = p.toString();
			// If knowledge is used for partitioning
			if (partitionedBy.contains(pathStr)) {
				Object value = data.getKnowledge().getValue(p);

				// Update record in database
				Set<String> dests = storage.getAndUpdate(value, sender.getId());

				// Add destinations to peers
				peers.addAll(dests);
			}
		}

		// Return (peers omitting sender)
		while (peers.remove(sender.getId()))
			;
		return peers;
	}
}
