/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.connector;

import java.util.Collection;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.Local;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.ip.IPTable;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
@Component
public class ConnectorComponent {
	
	@Local public Double xCoord;
	@Local public Double yCoord;
	
	public String id;
	public Integer CONNECTOR_TAG = 0;
	public String partition = "destination";
	
	@Local public IPTable ipTable;
	
	public Collection<KnowledgeData> nodes;
	
	public ConnectorComponent(String id) {
		this(id, 0.0, 0.0);
	}
	public ConnectorComponent(String id, Double xCoord, Double yCoord) {
		this.id = id;
		this.xCoord = xCoord;
		this.yCoord = yCoord;
	}
	
	@Process
	@PeriodicScheduling(period = 1000)
	public static void updateIPTable(@In("id") String id,
			@InOut("ipTable") ParamHolder<IPTable> ipTable,
			@In("nodes") Collection<KnowledgeData> nodes
			) {
		
		
		
	}
}
