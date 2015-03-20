/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.buffer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Buffered collection of items which are passed between multiple nodes across
 * a network or simulation environment. This buffer stores information about
 * last reception of an item locally by current node and a belief of the most
 * recent reception of the item globally by any node in the network.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class ReceptionBuffer {
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
	 * Create a new instance of buffer for received messages with default timeouts.
	 */
	public ReceptionBuffer(long localTimeout, long globalTimeout) {
		this.localTimeout = localTimeout;
		this.globalTimeout = globalTimeout;
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
	 */
	public void receiveLocal(String id, long time) {
		if (!buffer.containsKey(id)) {
			buffer.put(id, new ItemInfo(time, time));
		}
		else {
			ItemInfo info = buffer.get(id); 
			if (info.localReception < time) {
				info.localReception = time;
				info.globalReception = time;
			}
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
			buffer.put(id, new ItemInfo(Long.MIN_VALUE,  time));
		}
		else {
			ItemInfo info = buffer.get(id);
			if (info.globalReception < time)
				info.globalReception = time;
		}
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
	
	public Collection<ItemHeader> getRecentItems(long currentTime) {
		// remove old items ...
		removeGloballyObsoleteItems(currentTime);
		// ... then take all left items
		ArrayList<ItemHeader> result = new ArrayList<ItemHeader>();
		for (Entry<String, ItemInfo> entry : buffer.entrySet()) {
			result.add(new ItemHeader(entry.getKey(), entry.getValue().globalReception));
		}
		return result;
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
		
		public ItemInfo(long localReception, long globalReception) {
			this.localReception = localReception;
			this.globalReception = globalReception;
		}
	}
}
