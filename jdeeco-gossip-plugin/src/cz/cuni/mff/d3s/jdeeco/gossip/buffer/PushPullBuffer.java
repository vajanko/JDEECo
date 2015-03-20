/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.buffer;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.GossipProperties;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class PushPullBuffer implements DEECoPlugin {
	
	private ReceptionBuffer headers;
	private ReceptionBuffer pulls;
	
	public void notifyLocalPush(String componentId, long currentTime) {
		headers.receiveLocal(componentId, currentTime);
	}
	public void notifyGlobalPush(String componentId, long currentTime) {
		headers.receiveGlobal(componentId, currentTime);
	}
	public Collection<ItemHeader> getRecentPushedMessages(long currentTime) {
		return headers.getRecentItems(currentTime);
	}
	public Collection<ItemHeader> getObsoletePushedItems(long currentTime) {
		return headers.getLocallyObsoleteItems(currentTime);
	}
	
	/*public void notifyLocalPull(String componentId, long currentTime) {
		pulls.receiveLocal(componentId, currentTime);
	}*/
	public void notifyGlobalPull(String componentId, long currentTime) {
		pulls.receiveGlobal(componentId, currentTime);
	}
	/*public Collection<ItemHeader> getRecentPulledMessages() {
		return null;
	}*/
	

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
		long localTimeout = GossipProperties.getKnowledgePullTimeout();
		long globalTimeout = GossipProperties.getComponentPullTimeout();
		
		headers = new ReceptionBuffer(localTimeout, globalTimeout);
		pulls = new ReceptionBuffer(localTimeout, globalTimeout);
	}

}
