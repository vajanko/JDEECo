/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.grouper.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.rits.cloning.Cloner;

import cz.cuni.mff.d3s.deeco.knowledge.ValueSet;
import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataHelper;
import cz.cuni.mff.d3s.deeco.network.KnowledgeMetaData;
import cz.cuni.mff.d3s.jdeeco.core.KnowledgeSource;
import cz.cuni.mff.d3s.jdeeco.gossip.AddressRegister;
import cz.cuni.mff.d3s.jdeeco.grouper.GrouperRegister;
import cz.cuni.mff.d3s.jdeeco.grouper.GrouperRole;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class GrouperKnowledgeProvider implements KnowledgeSource {

	private KnowledgeSource innerSource;
	private GrouperRegister register;
	private Cloner cloner = new Cloner();

	public GrouperKnowledgeProvider(KnowledgeSource innerSource, GrouperRegister register) {
		this.innerSource = innerSource;
		this.register = register;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.gossip.KnowledgeSource#getKnowledge()
	 */
	@Override
	public Collection<KnowledgeData> getKnowledge() {
		ArrayList<KnowledgeData> res = new ArrayList<KnowledgeData>();
		
		for (KnowledgeData kd : this.innerSource.getKnowledge()) {
			// check whether this is gouper
			KnowledgePath rolePath = KnowledgeDataHelper.getPath(kd, "grouperRole");
			GrouperRole role = (GrouperRole)kd.getKnowledge().getValue(rolePath);
			if (role == null || role != GrouperRole.server) {
				res.add(kd);
				continue;
			}
			
			for (AddressRegister reg : this.register.getRegisters()) {
			
				// clone knowledge
				KnowledgeData copy = cloneValues(kd);
				// copy group data
				HashSet<String> group = new HashSet<String>(reg.getStrings());
				
				// do not publish small groups with one member
				if (group.size() <= 1)
					continue;
			
				// set group data
				KnowledgePath groupPath = KnowledgeDataHelper.getPath(kd, "groupMembers");
				copy.getKnowledge().setValue(groupPath, group);
				
				res.add(copy);
			}
		}
		
		return res;
	}

	private KnowledgeData cloneValues(KnowledgeData kd) {
		
		ValueSet knowledge = cloneValues(kd.getKnowledge());
		ValueSet security = cloneValues(kd.getSecuritySet());
		ValueSet authors = cloneValues(kd.getAuthors());
		KnowledgeMetaData metadata = kd.getMetaData().clone();
		
		KnowledgeData res = new KnowledgeData(knowledge, security, authors, metadata);
		return res;
	}
	private ValueSet cloneValues(ValueSet values) {
		ValueSet res = new ValueSet();
		
		for (KnowledgePath kp : values.getKnowledgePaths()) {
			Object val = this.cloner.deepClone(values.getValue(kp));
			res.setValue(kp, val);
		}
		
		return res;
	}
	

}
