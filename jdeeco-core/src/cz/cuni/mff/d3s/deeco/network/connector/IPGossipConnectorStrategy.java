/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.connector;

import java.util.ArrayList;
import java.util.Collection;

import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;
import cz.cuni.mff.d3s.deeco.network.IPGossipStrategy;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.ip.IPController;
import cz.cuni.mff.d3s.deeco.network.ip.IPTable;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class IPGossipConnectorStrategy implements IPGossipStrategy {
	
	private IPTable ipTable;
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.IPGossipStrategy#getRecipients(cz.cuni.mff.d3s.deeco.network.KnowledgeData, cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager)
	 */
	@Override
	public Collection<String> getRecipients(KnowledgeData data, KnowledgeManager sender) {
		
		// Notice that recipients are always the same for any kind of data.
		
		if (!data.getMetaData().componentId.startsWith("C"))
			return new ArrayList<String>();
		
		ArrayList<String> res = new ArrayList<String>(ipTable.getAddresses());
		res.remove(sender.getId());
		return res;
	}
	
	public IPGossipConnectorStrategy(IPController controller) {
		ipTable = controller.getIPTable();
	}
}
