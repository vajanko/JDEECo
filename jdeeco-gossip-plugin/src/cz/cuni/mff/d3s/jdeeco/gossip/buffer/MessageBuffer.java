/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.buffer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;

/**
 * Plugin providing a common storage for messages received by particular node.
 * Every message can be associated with several type of values. 
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class MessageBuffer implements DEECoPlugin {
	/**
	 * Collection of message IDs and associated extensible data. Any plugin can register
	 * additional data to be stored in the buffer. 
	 */
	private Map<String, ExtensibleStorage> buffer = new HashMap<String, ExtensibleStorage>();

	/**
	 * Gets data of type {@code clazz} associated with message identified by {@code id}.
	 * @param clazz Type of value to be stored with message.
	 * @param id Unique message identifier.
	 * @return Associated message value or null if there is no such a value. 
	 */
	public <T> T getData(Class<T> clazz, String id) {
		ExtensibleStorage msgStorage = this.buffer.get(id);
		if (msgStorage == null)
			return null;
		
		return msgStorage.get(clazz);
	}
	/**
	 * Associate provided {@code data} of type {@code clazz} with message identified by {@code id}.
	 * If some value of type {@code clazz} exist it is overwritten.
	 * @param clazz Type of value to be stored with message.
	 * @param id Unique message identifier.
	 * @param data Value to be stored together with the message.
	 */
	public <T> void putData(Class<T> clazz, String id, T data) {
		ExtensibleStorage msgStorage = this.buffer.get(id);
		if (msgStorage == null) {
			msgStorage = new ExtensibleStorage();
			this.buffer.put(id, msgStorage);
		}
		
		msgStorage.put(clazz, data);
	}
	/**
	 * Removes data associated with message identified by provided {@code id}
	 * 
	 * @param id Unique message identifier.
	 */
	public void removeData(String id) {
		this.buffer.remove(id);
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList();
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		this.buffer.clear();
	}
}
