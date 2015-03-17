/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManagerContainer;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeNotFoundException;
import cz.cuni.mff.d3s.deeco.knowledge.ValueSet;
import cz.cuni.mff.d3s.deeco.logging.Log;
import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;
import cz.cuni.mff.d3s.deeco.model.runtime.custom.RuntimeMetadataFactoryExt;
import cz.cuni.mff.d3s.deeco.model.runtime.meta.RuntimeMetadataFactory;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeMetaData;
import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeFramework;
import cz.cuni.mff.d3s.deeco.timer.CurrentTimeProvider;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class KnowledgeProvider {
	
	private KnowledgeManagerContainer kmContainer;
	private CurrentTimeProvider timeProvider;
	private String currentNodeId;
	// NOTE: Taken from DefaultKnowledgeDataManager
	private final List<KnowledgePath> emptyPath = new LinkedList<KnowledgePath>();
	
	public KnowledgeProvider(DEECoContainer container) {
		
		RuntimeFramework runtime = container.getRuntimeFramework();
		
		this.kmContainer = runtime.getContainer();
		this.timeProvider = runtime.getScheduler().getTimer();
		this.currentNodeId = String.valueOf(container.getId());
		
		// initialise empty knowledge path
		RuntimeMetadataFactory factory = RuntimeMetadataFactoryExt.eINSTANCE;
		emptyPath.add(factory.createKnowledgePath());
	}
	
	public Collection<KnowledgeData> getLocalKnowledgeData() {
		
		return getKnowledgeDataCopy(kmContainer.getLocals());
	}
	public Collection<KnowledgeData> getReplicaKnowledgeData() {
		
		return getKnowledgeDataCopy(kmContainer.getReplicas());
	}
	private Collection<KnowledgeData> getKnowledgeDataCopy(Collection<KnowledgeManager> knowledgeManagers) {
		Collection<KnowledgeData> result = new ArrayList<KnowledgeData>();
		
		for (KnowledgeManager km : knowledgeManagers) {
			try {
				
				KnowledgeData kd = prepareLocalKnowledgeData(km); 
				result.add(kd);
				
			} catch (Exception e) {
				Log.e("prepareKnowledgeData error", e);
			}
		}
		
		return result;
	}
	
	// NOTE: Taken from DefaultKnowledgeDataManager
	private KnowledgeData prepareLocalKnowledgeData(KnowledgeManager km) throws KnowledgeNotFoundException {
		String componentId = km.getId();
		// TODO: version is implemented by current time
		long versionId = timeProvider.getCurrentMilliseconds();
		String sender = currentNodeId;
		long createdAt = timeProvider.getCurrentMilliseconds();
		int hopCount = 1;
		KnowledgeMetaData metadata = new KnowledgeMetaData(componentId, versionId, sender, createdAt, hopCount);
		
		ValueSet toFilter = km.get(emptyPath);
		ValueSet knowledge = filterNonSerializablePaths(toFilter, km);
		// TODO: We are ignoring security, and host
		ValueSet security = new ValueSet();
		ValueSet authors = new ValueSet();
		
		KnowledgeData data = new KnowledgeData(knowledge, security, authors, metadata);
		return data;
	}
	// NOTE: Taken from DefaultKnowledgeDataManager
	private ValueSet filterNonSerializablePaths(ValueSet toFilter, KnowledgeManager km) {
		ValueSet result = new ValueSet();
		for (KnowledgePath kp : toFilter.getKnowledgePaths()) {
			if (!km.isLocal(kp)) {
				result.setValue(kp, toFilter.getValue(kp));
			}
		}
		return result;
	}

	
}
