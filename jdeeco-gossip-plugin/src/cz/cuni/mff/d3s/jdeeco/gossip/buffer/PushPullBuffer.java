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
	
	private ReceptionBuffer push;
	private ReceptionBuffer pull;
	
	public void notifyLocalPush(String componentId, long currentTime) {
		push.receiveLocal(componentId, currentTime);
	}
	public void notifyGlobalPush(String componentId, long currentTime) {
		push.receiveGlobal(componentId, currentTime);
	}
	public Collection<ItemHeader> getRecentPushedMessages(long currentTime) {
		return push.getRecentItems(currentTime);
	}
	public Collection<ItemHeader> getObsoletePushedItems(long currentTime) {
		return push.getLocallyObsoleteItems(currentTime);
	}
	public long getLocalPushTime(String componentId) {
		return push.getLocalReceptionTime(componentId);
	}
	
	/*public void notifyLocalPull(String componentId, long currentTime) {
		pulls.receiveLocal(componentId, currentTime);
	}*/
	public void notifyGlobalPull(String componentId, long currentTime) {
		pull.receiveGlobal(componentId, currentTime);
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
		
		push = new ReceptionBuffer(localTimeout, globalTimeout);
		pull = new ReceptionBuffer(localTimeout, globalTimeout);
	}

}
