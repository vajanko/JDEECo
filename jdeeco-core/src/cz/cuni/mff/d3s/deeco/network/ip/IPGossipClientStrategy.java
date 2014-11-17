package cz.cuni.mff.d3s.deeco.network.ip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;
import cz.cuni.mff.d3s.deeco.knowledge.ValueSet;
import cz.cuni.mff.d3s.deeco.logging.Log;
import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;
import cz.cuni.mff.d3s.deeco.network.AbstractHost;
import cz.cuni.mff.d3s.deeco.network.DataReceiver;
import cz.cuni.mff.d3s.deeco.network.DataSender;
import cz.cuni.mff.d3s.deeco.network.IPGossipStrategy;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;

/**
 * Provides list of recipient IP addresses for given knowledge data and sender.
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class IPGossipClientStrategy implements IPGossipStrategy {
	
	private IPTable ipTable;
	private Map<String, IPTable> partitions;
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.network.IPGossipStrategy#getRecipients(cz.cuni.mff.d3s.deeco.network.KnowledgeData, cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager)
	 */
	@Override
	public Collection<String> getRecipients(KnowledgeData data, KnowledgeManager sender) {
		
		
		
		ArrayList<String> res = new ArrayList<String>(ipTable.getAddresses());
		res.remove(sender.getId());
		return res;
	}
	
	public IPGossipClientStrategy(IPController controller) {
		ipTable = controller.getIPTable();
		this.partitions = new HashMap<String, IPTable>();
	}
}
