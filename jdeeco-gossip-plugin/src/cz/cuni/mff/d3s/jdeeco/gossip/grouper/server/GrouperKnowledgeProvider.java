/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.grouper.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.rits.cloning.Cloner;

import cz.cuni.mff.d3s.deeco.knowledge.ValueSet;
import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;
import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataHelper;
import cz.cuni.mff.d3s.deeco.network.KnowledgeMetaData;
import cz.cuni.mff.d3s.jdeeco.core.LocalKnowledgeSource;
import cz.cuni.mff.d3s.jdeeco.gossip.AddressRegister;
import cz.cuni.mff.d3s.jdeeco.gossip.grouper.GrouperRegister;
import cz.cuni.mff.d3s.jdeeco.gossip.grouper.GrouperRole;

/**
 * Provides knowledge data of local components on the grouper node prepared
 * for publishing on the network. This source extracts knowledge of the grouper,
 * selects individual groups and publish this data only to those groups and only
 * with relevant portion of data for that group.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class GrouperKnowledgeProvider implements LocalKnowledgeSource {

	/**
	 * The original knowledge data source as initialised on the non-grouper nodes.
	 */
	private LocalKnowledgeSource innerSource;
	/**
	 * Register of grouper holding members of particular groups.
	 */
	private GrouperRegister register;
	/**
	 * A service for creating deep copies of knowledge data.
	 */
	private Cloner cloner = new Cloner();

	/**
	 * Creates a new instance of knowledge provider designated for grouper nodes.
	 * 
	 * @param innerSource The original knowledge source as deployed on non-grouper nodes.
	 * @param register Grouper register holding information about formed communication groups.
	 */
	public GrouperKnowledgeProvider(LocalKnowledgeSource innerSource, GrouperRegister register) {
		this.innerSource = innerSource;
		this.register = register;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.gossip.KnowledgeSource#getKnowledge()
	 */
	@Override
	public Collection<KnowledgeData> getLocalKnowledge() {
		ArrayList<KnowledgeData> res = new ArrayList<KnowledgeData>();
		
		for (KnowledgeData kd : this.innerSource.getLocalKnowledge()) {
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
	/**
	 * Creates a deep copy of provided knowledge data and its metadata.
	 * 
	 * @param kd Knowledge data to be cloned.
	 * @return Deep copy of provided knowledge data.
	 */
	private KnowledgeData cloneValues(KnowledgeData kd) {
		
		ValueSet knowledge = cloneValues(kd.getKnowledge());
		ValueSet security = cloneValues(kd.getSecuritySet());
		ValueSet authors = cloneValues(kd.getAuthors());
		KnowledgeMetaData metadata = kd.getMetaData().clone();
		
		KnowledgeData res = new KnowledgeData(knowledge, security, authors, metadata);
		return res;
	}
	/**
	 * Creates a deep copy of given value set.
	 * 
	 * @param values Value set to be cloned.
	 * @return Deep copy of provided value set.
	 */
	private ValueSet cloneValues(ValueSet values) {
		ValueSet res = new ValueSet();
		
		for (KnowledgePath kp : values.getKnowledgePaths()) {
			Object val = this.cloner.deepClone(values.getValue(kp));
			res.setValue(kp, val);
		}
		
		return res;
	}
	

}
