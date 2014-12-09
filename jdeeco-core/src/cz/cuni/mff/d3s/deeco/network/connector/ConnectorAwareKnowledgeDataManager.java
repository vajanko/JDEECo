/**
 * 
 */
package cz.cuni.mff.d3s.deeco.network.connector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeNotFoundException;
import cz.cuni.mff.d3s.deeco.knowledge.ValueSet;
import cz.cuni.mff.d3s.deeco.model.runtime.api.EnsembleDefinition;
import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;
import cz.cuni.mff.d3s.deeco.network.DefaultKnowledgeDataManager;
import cz.cuni.mff.d3s.deeco.network.IPGossipStrategy;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeHelper;
import cz.cuni.mff.d3s.deeco.network.ip.IPController;
import cz.cuni.mff.d3s.deeco.network.ip.IPData;
import cz.cuni.mff.d3s.deeco.network.ip.IPRegister;


/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class ConnectorAwareKnowledgeDataManager extends DefaultKnowledgeDataManager {

	/**
	 * @param ensembleDefinitions
	 * @param ipGossipStrategy
	 */
	public ConnectorAwareKnowledgeDataManager(List<EnsembleDefinition> ensembleDefinitions,
			IPGossipStrategy ipGossipStrategy) {
		super(ensembleDefinitions, ipGossipStrategy);
	}
	
	@Override
	protected void sendDirect(List<KnowledgeData> data) {
		
		// !!! this approach is limited by the fact that IPController data will be sent
		// if and only if the connector data are sent
		
		List<KnowledgeData> newData = new ArrayList<KnowledgeData>();
		
		for (KnowledgeData kd : data) {
			String id = kd.getMetaData().componentId;
			
			// decide whether this is connector knowledge
			//if (!id.endsWith("#connector"))
			if (!id.startsWith("C")) {
				newData.add(kd);
				continue;
			}
			
			KnowledgeManager km = this.kmContainer.getLocal(id);
			IPController ctrl = (IPController)KnowledgeHelper.getLocalValue(km, "controller");
			Set<Object> range = (Set<Object>)KnowledgeHelper.getLocalValue(km, "range");
			
			ValueSet values = kd.getKnowledge();
			
			for (Object key : range) {
				ValueSet newValues = cloneValues(values);
				
				//KnowledgeHelper.setValue(newValues, "", value);
				//newValues.setValue(KnowledgeHelper.getPath(kd, "connector_group"), key);
				 
				//ctrl.getRegister(key);
				
				newData.add(new KnowledgeData(newValues, kd.getMetaData().clone()));
			}
			
			/*KnowledgePath kp = KnowledgeHelper.getPath(kd, "connectorRegisters");
			ValueSet values = kd.getKnowledge();
			Set<IPRegister> regs = (Set<IPRegister>)values.getValue(kp);
			
			for (IPRegister reg : regs) {
				ValueSet newValues = cloneValues(values);
				
				Set<IPRegister> singleReg = new HashSet<IPRegister>(Arrays.asList(reg));
				
				newValues.setValue(kp, singleReg);
				
				newData.add(new KnowledgeData(newValues, kd.getMetaData().clone()));
			}*/
		}
		
		super.sendDirect(newData);
	}
	
	private ValueSet cloneValues(ValueSet values) {
		ValueSet newValues = new ValueSet();
		
		for (KnowledgePath kp: values.getKnowledgePaths()) {
			newValues.setValue(kp, values.getValue(kp));
		}
		
		return newValues;
	}
}
