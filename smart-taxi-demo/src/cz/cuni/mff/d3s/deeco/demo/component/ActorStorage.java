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
 * A class encapsulating a collection of actors (drivers or passengers) together with
 * their's current position.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class ActorStorage implements Serializable {
	/**
	 * Generated serialisation ID
	 */
	private static final long serialVersionUID = -8574934067056907646L;
	
	/**
	 * Unique actor ID and its position.
	 */
	private Map<String, Position> actors = new HashMap<String, Position>();
	/**
	 * Gets current position of actor with given ID.
	 * 
	 * @param id Unique ID of the actor.
	 * @return Actor position
	 */
	public Position getPosition(String id) {
		return this.actors.get(id);
	}
	/**
	 * Stores or updates current position of actor with given ID.
	 * 
	 * @param id Unique ID of the actor.
	 * @param pos Current actor's position
	 */
	public void putPosition(String id, Position pos) {
		this.actors.put(id, pos);
	}
	/**
	 * Gets the collection of all actor's IDs
	 * 
	 * @return Collection of IDs
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
