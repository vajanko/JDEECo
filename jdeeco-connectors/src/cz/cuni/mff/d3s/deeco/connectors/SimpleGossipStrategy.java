package cz.cuni.mff.d3s.deeco.connectors;

import java.util.ArrayList;
import java.util.Collection;

import cz.cuni.mff.d3s.deeco.network.DirectGossipStrategy;

public final class SimpleGossipStrategy implements DirectGossipStrategy {

	public Collection<String> filterRecipients(Collection<String> recipients) {
		
		//return new ArrayList<String>();
		
		return recipients;
	}

}
