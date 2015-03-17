/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class MessageBuffer {
	/**
	 * A collection of message IDs and information about time of message received
	 * locally and globally by any node in the network.
	 */
	private Map<String, MessageInfo> buffer = new HashMap<String, MessageInfo>();
	/**
	 * After this timeout a message is considered to be uninteresting for any node
	 * in the network and therefore can be removed from the buffer.
	 */
	private int globalTimeout;
	/**
	 * After this timeout a message is considered to be outdated on the current node
	 * and an update is required. This update could result in a PULL request for all
	 * outdated messages.
	 */
	private int localTimeout;
	
	public MessageBuffer() {
		this.globalTimeout = 100000;
	}

	/**
	 * Stores information about receiving a message with given {@code id} locally.
	 * Receiving a message locally also implies global message update.
	 * Notice that the local update time is only modified if given {@code time} is 
	 * larger than the current value.
	 * 
	 * @see #globalUpdate(String, long)
	 * 
	 * @param id Unique message ID
	 * @param time System or simulation time of message received by current node.
	 */
	public void localUpdate(String id, long time) {
		if (!buffer.containsKey(id)) {
			buffer.put(id, new MessageInfo(time, time));
		}
		else {
			MessageInfo info = buffer.get(id); 
			if (info.LocalUpdate < time) {
				info.LocalUpdate = time;
				info.GlobalUpdate = time;
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
	public void globalUpdate(String id, long time) {
		if (!buffer.containsKey(id)) {
			buffer.put(id, new MessageInfo(0,  time));
		}
		else {
			MessageInfo info = buffer.get(id);
			if (info.GlobalUpdate < time)
				info.GlobalUpdate = time;
		}
	}
	
	/**
	 * Removes all timeout messages which weren't received by any node in the system for
	 * a long time. 
	 */
	private void removeTimeoutMessages(long currentTime) {
		Iterator<Entry<String, MessageInfo>> it = buffer.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, MessageInfo> entry = it.next();
			if (currentTime - entry.getValue().GlobalUpdate >= globalTimeout) {
				it.remove();
			}
		}
	}

	/**
	 * Gets a list of message IDs which weren't received by current node for longer
	 * time specified in @{link GossipProperties}
	 * 
	 * @param currentTime Current system or simulation time
	 * @return List of outdated messaged which need to be PULLed.
	 */
	public Collection<String> getMissingMessages(long currentTime) {
		
		// remove old messages - no need to PULL them
		removeTimeoutMessages(currentTime);
		
		ArrayList<String> result = new ArrayList<String>(); 
		// get a list of outdated messages but which are still visible in the network
		for (Entry<String, MessageInfo> entry : buffer.entrySet()) {
			if (currentTime - entry.getValue().LocalUpdate >= localTimeout) {
				result.add(entry.getKey());
			}
		}
		
		return result;
	}
	
	public Collection<MessageHeader> getKnownMessages(long currentTime) {
		
		// remove old messages - no need sent them to other nodes
		removeTimeoutMessages(currentTime);
		// get a list of messages and the time of lastly received by any node in the network 
		ArrayList<MessageHeader> result = new ArrayList<MessageHeader>();
		for (Entry<String, MessageInfo> entry : buffer.entrySet()) {
			result.add(new MessageHeader(entry.getKey(), entry.getValue().GlobalUpdate));
		}
		
		return result;
	}
		
	class MessageInfo {
		/**
		 * The last time when the message was received by current node.
		 */
		public long LocalUpdate;
		/**
		 * The last time when the message has been detected in the network.
		 * It can be either notification from other node or when message was
		 * received by current node.
		 */
		public long GlobalUpdate;
		
		public MessageInfo(long localUpdate, long globalUpdate) {
			this.LocalUpdate = localUpdate;
			this.GlobalUpdate = globalUpdate;
		}
	}
}
