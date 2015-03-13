package cz.cuni.mff.d3s.deeco.knowledge;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import cz.cuni.mff.d3s.deeco.model.runtime.api.ComponentInstance;
import cz.cuni.mff.d3s.deeco.model.runtime.api.RuntimeMetadata;

/**
 * 
 * This container allows to deal with both local and replica knowledge managers
 * by implementing both interfaces: {@link LocalKnowledgeManagerContainer} and
 * {@link ReplicaKnowledgeManagerContainer}. It retrieves all available locals
 * and replicas knowledge managers in the container. In addition, it gives the
 * ability to register a new local listeners to listen for events caused by
 * local knowledge managers changes, and register a new replica listener to
 * listen for events caused by replica knowledge managers changes. By registered
 * listeners, the container have the ability to listen to events related to
 * adding or removing both kinds of knowledge managers (local and replica).
 * Also, the container acts as a factory for locals/replicas Knowledge managers
 * and register to them all the existing local/replica listeners in the
 * container.
 * 
 * @author Michal Kit <kit@d3s.mff.cuni.cz>
 * @author Jaroslav Keznikl <kezenikl@d3s.mff.cuni.cz>
 * 
 */
public class KnowledgeManagerContainer  {

	protected final Map<String, Map<ComponentInstance, KnowledgeManager>> replicas;
	protected final Set<ReplicaListener> replicaListeners;
	protected final Map<String, KnowledgeManager> locals;
	protected final Set<LocalListener> localListeners;
	protected final KnowledgeManagerFactory knowledgeManagerFactory;
	protected final RuntimeMetadata runtimeModel;


	public KnowledgeManagerContainer(KnowledgeManagerFactory knowledgeManagerFactory, RuntimeMetadata model) {
		this.knowledgeManagerFactory = knowledgeManagerFactory;
		this.runtimeModel = model;
		this.replicas = new HashMap<>();
		this.replicaListeners = new HashSet<>();
		this.locals = new HashMap<>();
		this.localListeners = new HashSet<>();
	}

	/**
	 * Creates a new instance of local knowledge manager with the specified id,
	 * add it to the container and register all existing local listener to it.
	 * 
	 * @param String
	 *            the identifier of the knowledge manager
	 * @return {@link KnowledgeManager} the newly created object containing
	 *         values for the specified knowledge paths.
	 */
	public KnowledgeManager createLocal(String id, ComponentInstance component) {
		if (locals.containsKey(id))
			return locals.get(id);
		
		KnowledgeManager result = new CloningKnowledgeManager(id, component);
		locals.put(id, result);
		
		for (LocalListener listener : localListeners) {
			listener.localCreated(result, this);
		}		
		return result;
	}

	/**
	 * Removes a local knowledge manager from the container and return it. This
	 * implies also informing the local listener about removing this knowledge
	 * manager.
	 * 
	 * @param {@link KnowledgeManager} local knowledge manager to be removed
	 * @return {@link KnowledgeManager} the removed local knowledge manager
	 *         object containing values for the specified knowledge paths
	 */
	public KnowledgeManager removeLocal(KnowledgeManager km) {
		KnowledgeManager kmVar = null;
		if (locals.containsValue(km)) {
			locals.remove(km);
			
			for (LocalListener listener : localListeners) {
				listener.localRemoved(km, this);
			}
			kmVar = km;
		}
		return kmVar;
	}

	/**
	 * Retrieves all the local knowledge managers in the container.
	 * 
	 * @return List<{@link KnowledgeManager}> object containing values for the
	 *         specified knowledge paths
	 */
	public Collection<KnowledgeManager> getLocals() {
		return locals.values();
	}
	
	/**
	 * Retrieves a local knowledge manager by its id.
	 */
	public KnowledgeManager getLocal(String id) {
		return locals.get(id);
	}
	
	/**
	 * Retrieves a replica knowledge manager by its id.
	 */
	public KnowledgeManager getReplica(ComponentInstance c, String id) {
		Map<ComponentInstance, KnowledgeManager> map = replicas.get(id);
		if (map == null) {
			return null;
		} else {
			return map.get(id);
		}
	}
	
	/**
	 * Returns true if the container contains a local KM for the given component id.
	 */
	public boolean hasLocal(String id) {
		return locals.containsKey(id);
	}

	/**
	 * Adds the local listener to the container
	 * 
	 * @param {@link LocalListener} listens for adding local knowledge managers
	 *        to the container or removing them.
	 */
	public void registerLocalListener(LocalListener listener) {
		if (!localListeners.contains(listener)) {
			localListeners.add(listener);			
		}
	}

	/**
	 * Retrieves all the replica knowledge managers in the container.
	 * 
	 * @return List<{@link KnowledgeManager}> object containing values for the
	 *         specified knowledge paths
	 */
	public Collection<KnowledgeManager> getReplicas() {
		Collection<KnowledgeManager> result = new LinkedList<>();
		replicas.values().stream().map(map -> map.values()).forEach(result::addAll);
		return result;
	}
	
	public Collection<KnowledgeManager> getReplicas(ComponentInstance component) {
		Collection<KnowledgeManager> result = new LinkedList<>();
		replicas
			.values()
			.stream()
			.map(map -> map
					.values()
					.stream()
					.filter(km -> km.getComponent().equals(component))
					.collect(Collectors.toList())
			).forEach(result::addAll);
		return result;
	}
	
	/**
	 * Returns true if the container contains a replica for the given component id.
	 */
	public boolean hasReplica(String id) {
		return replicas.containsKey(id);
	}

	/**
	 * Adds the replica listener to the container
	 * 
	 * @param {@link ReplicaListener} listens for adding replica knowledge
	 *        managers to the container or removing them.
	 */
	public void registerReplicaListener(ReplicaListener listener) {
		if (!replicaListeners.contains(listener)) {
			replicaListeners.add(listener);
		}
	}

	/**
	 * Adds replica knowledge manager to the container and registers all
	 * existing replica listener to it.
	 * 
	 * @param id
	 *            of the replica being registered
	 */
	public Collection<KnowledgeManager> createReplica(String id) {
		if (hasLocal(id)) {
			throw new RuntimeException("Cannot create replica for a local knowledge manager (id: " + id + ").");
		} else if (hasReplica(id)) {
			return replicas.get(id).values();
		} else {
			replicas.put(id, new HashMap<>());
			for (ComponentInstance component : runtimeModel.getComponentInstances()) {
				KnowledgeManager result = new CloningKnowledgeManager(id, component);
				replicas.get(id).put(component, result);
				for (ReplicaListener listener : replicaListeners) {
					listener.replicaRegistered(result, this);
				}
			}
			return replicas.get(id).values();
		} 
	}

	
}
