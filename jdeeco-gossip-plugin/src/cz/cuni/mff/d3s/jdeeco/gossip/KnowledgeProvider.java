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
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeFramework;
import cz.cuni.mff.d3s.deeco.timer.CurrentTimeProvider;

/**
 * Provides the ability to create a copy of knowledge data prepared for marshaling
 * and sending through the network.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class KnowledgeProvider implements DEECoPlugin {
	
	private KnowledgeManagerContainer kmContainer;
	private CurrentTimeProvider timeProvider;
	private String nodeId;
	private final List<KnowledgePath> emptyPath = new LinkedList<KnowledgePath>();
	
	/**
	 * @return Unique identifier of current DEECo node as a {@code String}
	 */
	private String getNodeId() {
		return nodeId;
	}
	/**
	 * 
	 * @return Copy of local knowledge data prepared for network transfer.
	 */
	public Collection<KnowledgeData> getLocalKnowledgeData() {
		return getKnowledgeDataCopy(kmContainer.getLocals());
	}
	/**
	 * 
	 * @return Copy of replica knowledge data prepared for network transfer.
	 */
	public Collection<KnowledgeData> getReplicaKnowledgeData() {
		return getKnowledgeDataCopy(kmContainer.getReplicas());
	}
	/**
	 * Create a copy of given knowledge data prepared for network transfer. This
	 * implies without data marked with {@link cz.cuni.mff.d3s.deeco.annotations.Local} annotation.
	 * 
	 * @param knowledgeManagers A list of {@link KnowledgeManager}s to copy data from.
	 * @return Copy of given knowledge data prepared for network transfer.
	 */
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
		String sender = getNodeId();
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

	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return new ArrayList<Class<? extends DEECoPlugin>>();
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		// no dependencies other than runtime framework
		RuntimeFramework runtime = container.getRuntimeFramework();
		
		this.kmContainer = runtime.getContainer();
		this.timeProvider = runtime.getScheduler().getTimer();
		
		this.nodeId = String.valueOf(container.getId());
		
		// initialise empty knowledge path
		RuntimeMetadataFactory factory = RuntimeMetadataFactoryExt.eINSTANCE;
		emptyPath.add(factory.createKnowledgePath());
	}

	
}
