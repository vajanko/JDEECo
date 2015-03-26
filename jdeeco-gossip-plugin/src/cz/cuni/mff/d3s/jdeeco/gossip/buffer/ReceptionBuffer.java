/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.buffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeMetaData;
import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy;
import cz.cuni.mff.d3s.jdeeco.network.l2.Layer2;

/**
 * Buffered collection of items which are passed between multiple nodes across
 * a network or simulation environment. This buffer stores information about
 * last reception of an item locally by current node and a belief of the most
 * recent reception of the item globally by any node in the network.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class ReceptionBuffer implements L2Strategy, DEECoPlugin {
	
	/**
	 * Minimal possible value of item reception.
	 */
	public static final long MINUS_INFINITE = Long.MIN_VALUE;
	
	public static final String GLOBAL_TIMEOUT = "deeco.receptionBuffer.globalTimeout";
	public static final long GLOBAL_TIMEOUT_DEFAULT = 20000;
	
	public static final String LOCAL_TIMEOUT = "deeco.receptionBuffer.localTimeout";
	public static final long LOCAL_TIMEOUT_DEFAULT = 3000;
	
	/**
	 * A collection of item IDs and associated information about item reception
	 * by the current node and globally by nodes in the network.
	 */
	private Map<String, ItemInfo> buffer = new HashMap<String, ItemInfo>();
	/**
	 * After this timeout a message is considered to be uninteresting for any node
	 * in the network and therefore can be removed from the buffer.
	 */
	private long globalTimeout;
	/**
	 * After this timeout a message is considered to be outdated on the current node
	 * and an update (request for reception) is required.
	 */
	private long localTimeout;
	
	/**
	 * 
	 */
	public ReceptionBuffer() {
		localTimeout = Long.getLong(LOCAL_TIMEOUT, LOCAL_TIMEOUT_DEFAULT);
		globalTimeout = Long.getLong(GLOBAL_TIMEOUT, GLOBAL_TIMEOUT_DEFAULT);
	}
	
	/**
	 * Stores information about receiving a message with given {@code id} locally.
	 * Receiving a message locally also implies global message update.
	 * Notice that the local update time is only modified if given {@code time} is 
	 * larger than the current value.
	 * 
	 * @see #receiveGlobal(String, long)
	 * 
	 * @param id Unique message ID
	 * @param time System or simulation time of message received by current node.
	 * @param version Current message version
	 */
	public void receiveLocal(String id, long time, long version) {
		if (!buffer.containsKey(id)) {
			buffer.put(id, new ItemInfo(time, time, version));
		}
		else {
			ItemInfo info = buffer.get(id); 
			if (info.localReception < time)
				info.localReception = time;
			// We separately test also global reception time, but it is probably
			// impossible that globalReception >= time, because we call this method
			// upon local reception of an item. It is not possible that it was 
			// received somewhere in a future time.
			if (info.globalReception < time)
				info.globalReception = time;
			if (info.version < version)
				info.version = version;
		}
	}
	/**
	 * Stores information about receiving a message with given {@code id} globally -
	 * meaning by any node in the network. Notice that the global update time is only 
	 * modified if {@code time} is larger than the current value.
	 * 
	 * @param id Unique message ID
	 * @param time System or simulation time of message received by current node
	 * or by any node in the network
	 */
	public void receiveGlobal(String id, long time) {
		if (!buffer.containsKey(id)) {
			// Item was never seen on the current node. The very first idea is
			// to add something similar: new ItemInfo(MINUS_INFINITE,  time). The problem of
			// this approach is that the item is immediately obsolete and will be PULLed.
			//buffer.put(id, new ItemInfo(MINUS_INFINITE,  time));
			
			// Our approach is that when a header of item never seen before is received
			// we are waiting for PULL timeout and only then we sent the PULL request.
			// It is probable that the item will be received meantime.
			buffer.put(id, new ItemInfo(time, time, MINUS_INFINITE));
		}
		else {
			ItemInfo info = buffer.get(id);
			if (info.globalReception < time)
				info.globalReception = time;
		}
	}
	
	/**
	 * Stores information about receiving  a pull request for a message with
	 * given {@code id}.
	 * 
	 * @param id Unique message identifier
	 * @param time System or simulation time of last message received by remote node.
	 */
	public void receivePull(String id, long time) {
		if (!buffer.containsKey(id)) {
			ItemInfo info = new ItemInfo(MINUS_INFINITE, time, MINUS_INFINITE);
			info.pulled = true;
			buffer.put(id, info);
		}
		else {
			ItemInfo info = buffer.get(id);
			info.pulled = true;
			//info.localReception = MINUS_INFINITE;
			if (info.globalReception < time)
				info.globalReception = time;
		}
	}
	/**
	 * Gets indication whether particular message was pulled on current node.
	 * 
	 * @param id Unique message identifier.
	 * @return True if message was pulled, otherwise false.
	 */
	public boolean getPulledTag(String id) {
		ItemInfo info = buffer.get(id);
		return info != null && info.pulled;
	}
	/**
	 * Clear pull indication for particular message.
	 * 
	 * @param id Unique message identifier.
	 */
	public void clearPulledTag(String id) {
		ItemInfo info = buffer.get(id);
		if (info != null) {
			info.pulled = false;
		}
	}
	/**
	 * Gets collection of IDs of currently pulled knowledge data which
	 * should be broadcasted.
	 * 
	 * @return Collection of component IDs
	 */
	public Collection<String> getPulledItems() {
		ArrayList<String> result = new ArrayList<String>();
		for (Entry<String, ItemInfo> item : buffer.entrySet())
			if (item.getValue().pulled)
				result.add(item.getKey());
		return result;
	}
	
	/**
	 * Gets reception time of locally received item identified by given {@code id}
	 * 
	 * @param id Unique identifier of received item
	 * @return System or simulation time of item received locally or {@link #MINUS_INFINITE}
	 * if the item was never received locally.
	 */
	public long getLocalReceptionTime(String id) {
		ItemInfo info = buffer.get(id);
		if (info == null)
			return MINUS_INFINITE;
		return info.localReception;
	}
	/**
	 * Gets reception time of globally received item identified by given {@code id}
	 * 
	 * @param id Unique identifier of received item
	 * @return System or simulation time of item received globally or {@link #MINUS_INFINITE}
	 * if the item was never received globally.
	 */
	public long getGlobalReceptionTime(String id) {
		ItemInfo info = buffer.get(id);
		if (info == null)
			return MINUS_INFINITE;
		return info.localReception;
	}
	/**
	 * Gets last message version received locally identified by {@code id}
	 * 
	 * @param id Unique identifier of received message
	 * @return Last message version of {@link #MINUS_INFINITE} if the message
	 * was never received locally.
	 */
	public long getVersion(String id) {
		ItemInfo info = buffer.get(id);
		if (info == null)
			return MINUS_INFINITE;
		return info.version;
	}
	
	/**
	 * Removes all items from the buffer which are globally expired at 
	 * the time {@link currentTime}.
	 * 
	 * @param currentTime Current system or simulation time.
	 */
	public void removeGloballyObsoleteItems(long currentTime) {
		Iterator<Entry<String, ItemInfo>> it = buffer.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, ItemInfo> entry = it.next();
			if (currentTime - entry.getValue().globalReception >= globalTimeout) {
				// at "currentTime" the globally known reception time exceeds the timeout
				it.remove();
			}
		}
	}
	/**
	 * Creates a collection of items from the buffer which locally expired at
	 * the time {@link currentTime}
	 * 
	 * @param currentTime Current system or simulation time.
	 * @return Collection of locally expired items.
	 */
	public Collection<ItemHeader> getLocallyObsoleteItems(long currentTime) {
		// remove too old items ...
		removeGloballyObsoleteItems(currentTime);
		// ... then filter only outdated items
		ArrayList<ItemHeader> result = new ArrayList<ItemHeader>(); 
		// get a list of outdated messages but which are still visible in the network
		for (Entry<String, ItemInfo> entry : buffer.entrySet()) {
			if (currentTime - entry.getValue().localReception >= localTimeout) {
				// TODO: which reception time should be used ???
				result.add(new ItemHeader(entry.getKey(), entry.getValue().localReception));
			}
		}
		
		return result;
	}
	
	/**
	 * Gets valid content of the buffer. This includes items not globally
	 * expired.
	 * 
	 * @param currentTime Current system or simulation time
	 * @return Collection of {@link ItemHeader} representing valid items 
	 * currently present in the buffer.
	 */
	public Collection<ItemHeader> getRecentItems(long currentTime) {
		// remove old items ...
		removeGloballyObsoleteItems(currentTime);
		// ... then take all left items
		ArrayList<ItemHeader> result = new ArrayList<ItemHeader>();
		for (Entry<String, ItemInfo> entry : buffer.entrySet()) {
			// TODO: could locally obsolete items be omitted?
			// obsolete items will be sent in the PULL request as well
			result.add(new ItemHeader(entry.getKey(), entry.getValue().globalReception));
		}
		return result;
	}
	
	/**
	 * Gets indication whether message identified by given ID with provided
	 * version can be received by the application, that is that the message
	 * wasn't received already before.
	 * 
	 * @param id Unique message identifier.
	 * @param version Current message version.
	 * @return True if message can be received otherwise false.
	 */
	public boolean canReceive(String id, long version) {
		ItemInfo info = buffer.get(id);
		return info == null || info.version <= version;
	}
	
	/**
	 * A helper structure for holding information about item reception times.
	 * Notice that this invariant should hold:
	 * {@link #localReception} <= {@link #globalReception}, because when an item
	 * is received locally it is also considered as global reception in the network.
	 * 
	 * @author Ondrej Kov·Ë <info@vajanko.me>
	 */
	class ItemInfo {
		/**
		 * The last time when an item was received locally by the current node.
		 * Invariant: {@link #localReception} <= {@link #globalReception}
		 */
		public long localReception;
		/**
		 * The last time when an item was received globally by any node in the network
		 * including the current node.
		 * Invariant: {@link #localReception} <= {@link #globalReception}
		 */
		public long globalReception;
		/**
		 * Last message version received by the current node.
		 */
		public long version = 0;
		
		public boolean pulled = false;
		
		public ItemInfo(long localReception, long globalReception, long version) {
			this.localReception = localReception;
			this.globalReception = globalReception;
			this.version = version;
		}
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy#processL2Packet(cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet)
	 */
	@Override
	public void processL2Packet(L2Packet packet) {
		if (packet.header.type.equals(L2PacketType.KNOWLEDGE)) {			
			KnowledgeData kd = (KnowledgeData)packet.getObject();
			KnowledgeMetaData meta = kd.getMetaData();
			
			receiveLocal(meta.componentId, meta.createdAt, meta.versionId);
		}
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(Network.class);
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		// register L2 strategy
		Layer2 networkLayer = container.getPluginInstance(Network.class).getL2();
		networkLayer.registerL2Strategy(this);
	}
}
