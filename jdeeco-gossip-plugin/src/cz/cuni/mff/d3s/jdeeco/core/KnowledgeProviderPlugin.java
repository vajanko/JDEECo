/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.core;

import java.util.ArrayList;
import java.util.Arrays;
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
import cz.cuni.mff.d3s.jdeeco.gossip.ReceptionBuffer;

/**
 * Provides the ability to create a copy of knowledge data prepared for marshaling
 * and sending through the network.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class KnowledgeProviderPlugin implements DEECoPlugin, KnowledgeProvider {
	
	private ReceptionBuffer receptionBuffer;
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
		return getKnowledgeDataCopy(kmContainer.getLocals(), true);
	}
	/**
	 * 
	 * @return Copy of replica knowledge data prepared for network transfer.
	 */
	public Collection<KnowledgeData> getReplicaKnowledgeData() {
		return getKnowledgeDataCopy(kmContainer.getReplicas(), false);
	}
	/**
	 * Create a copy of given knowledge data prepared for network transfer. This
	 * implies without data marked with {@link cz.cuni.mff.d3s.deeco.annotations.Local} annotation.
	 * 
	 * @param knowledgeManagers A list of {@link KnowledgeManager}s to copy data from.
	 * @return Copy of given knowledge data prepared for network transfer.
	 */
	private Collection<KnowledgeData> getKnowledgeDataCopy(Collection<KnowledgeManager> knowledgeManagers, boolean isLocalKnowledge) {
		Collection<KnowledgeData> result = new ArrayList<KnowledgeData>();
		
		for (KnowledgeManager km : knowledgeManagers) {
			try {
				
				KnowledgeData kd = prepareKnowledgeData(km, isLocalKnowledge); 
				result.add(kd);
				
			} catch (Exception e) {
				Log.e("prepareKnowledgeData error", e);
			}
		}
		
		return result;
	}
	
	public KnowledgeData getComponentKnowledge(String id) {
		try {
			if (kmContainer.hasLocal(id)) {
				return prepareKnowledgeData(kmContainer.getLocal(id), true);
			}
			else if (kmContainer.hasReplica(id)) {
				for (KnowledgeManager r : kmContainer.getReplicas()) {
					if (id.equals(r.getId())) {
						return prepareKnowledgeData(r, false);
					}
				}
			}
			
		} catch (KnowledgeNotFoundException e) { }
		
		// null if KnowledgeManager or Knowledge not found
		return null;
	}
	/**
	 * Gets value indicating whether there is knowledge data from component identified
	 * by given component ID which comes from current (local) node component instance.
	 * 
	 * @param componentId Unique component identifier.
	 * @return True if there is a local knowledge data otherwise false.
	 */
	public boolean hasLocal(String componentId) {
		return kmContainer.hasLocal(componentId);
	}
	
	/*public boolean hasKnowledgeManager(String componentId) {
		return kmContainer.hasLocal(componentId) || kmContainer.hasReplica(componentId);
	}*/
	
	private KnowledgeData prepareKnowledgeData(KnowledgeManager km, boolean isLocalKnowledge) throws KnowledgeNotFoundException {
		// TODO: version is implemented by current time
		// TODO: We are ignoring security, and host
		
		String componentId = km.getId();
		long versionId = timeProvider.getCurrentMilliseconds();
		String sender = getNodeId();
		long createdAt = 0;
		if (isLocalKnowledge) {
			// local knowledge is always up-to-date therefore we use current time
			createdAt = timeProvider.getCurrentMilliseconds();
			versionId = timeProvider.getCurrentMilliseconds();
		}
		else {
			// get time when replica knowledge data was last time received
			createdAt = receptionBuffer.getLocalReceptionTime(componentId);
			versionId = receptionBuffer.getVersion(componentId);
		}
		int hopCount = 1;
		
		KnowledgeMetaData metadata = new KnowledgeMetaData(componentId, versionId, sender, createdAt, hopCount);
		
		ValueSet toFilter = km.get(emptyPath);
		ValueSet knowledge = getTransferableKnowledge(toFilter, km);
		
		ValueSet security = new ValueSet();
		ValueSet authors = new ValueSet();
		
		KnowledgeData data = new KnowledgeData(knowledge, security, authors, metadata);
		return data;
	}
	
	/**
	 * Gets knowledge that can be send via network (is not @Local annotated)
	 * 
	 * @param source
	 *            Knowledge values to be stripped of non-transferable knowledge
	 * @param knowledgeManager
	 *            Knowledge manager containing the source knowledge
	 * @return Source knowledge stripped of @Local annotated knowledge
	 */
	private static ValueSet getTransferableKnowledge(ValueSet source, KnowledgeManager knowledgeManager) {
		ValueSet result = new ValueSet();
		for (KnowledgePath kp : source.getKnowledgePaths()) {
			if (!knowledgeManager.isLocal(kp)) {
				result.setValue(kp, source.getValue(kp));
			}
		}
		return result;
	}

	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(ReceptionBuffer.class);
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		this.receptionBuffer = container.getPluginInstance(ReceptionBuffer.class);
		RuntimeFramework runtime = container.getRuntimeFramework();
		this.kmContainer = runtime.getContainer();
		this.timeProvider = runtime.getScheduler().getTimer();
		
		this.nodeId = String.valueOf(container.getId());
		
		// initialise empty knowledge path
		RuntimeMetadataFactory factory = RuntimeMetadataFactoryExt.eINSTANCE;
		emptyPath.add(factory.createKnowledgePath());
	}

	
}
