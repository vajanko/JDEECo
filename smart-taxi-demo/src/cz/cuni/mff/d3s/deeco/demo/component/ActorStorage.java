/**
 * 
 */
package cz.cuni.mff.d3s.deeco.demo.component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cz.cuni.mff.d3s.jdeeco.core.Position;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class ActorStorage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8574934067056907646L;
	
	private Map<String, Position> actors = new HashMap<String, Position>();
	
	public Position getPosition(String id) {
		return this.actors.get(id);
	}
	public void putPosition(String id, Position pos) {
		this.actors.put(id, pos);
	}
	/**
	 * @return
	 */
	public Set<String> getActors() {
		return actors.keySet();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.actors.toString();
	}
}
