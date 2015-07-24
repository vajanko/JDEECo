/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.core;

import java.util.HashMap;

/**
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class ExtensibleStorage {
	
	private HashMap<Class<?>, Object> data = new HashMap<Class<?>, Object>();
	
	public <T> void put(Class<T> clazz, T value) {
		this.data.put(clazz, value);
	}
	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> clazz) {
		return (T)this.data.get(clazz);
	}
}
