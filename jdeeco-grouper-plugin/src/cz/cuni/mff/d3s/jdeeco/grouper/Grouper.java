/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.grouper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.model.runtime.api.EnsembleDefinition;
import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.runtime.DuplicateEnsembleDefinitionException;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.l1.DefaultDataIDSource;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class Grouper implements DEECoPlugin {

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#getDependencies()
	 */
	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(Network.class);
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		try {
			
			container.deployEnsemble(GrouperEnsemble.class);
			
			//Set<String> partitions = new HashSet();
			//for (EnsembleDefinition ens : container.getRuntimeMetadata().getEnsembleDefinitions())
			//	partitions.add(ens.getPartitionedBy());
			
		//GrouperComponent grouper = new GrouperComponent(id, partitions, range, controller, sender, knowledgeDataStore)
		//container.deployComponent(grouper)
		
		} catch (Exception e) {
			
		}
	}

}
