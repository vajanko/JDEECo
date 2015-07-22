/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.grouper.server;

import java.lang.annotation.Annotation;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import cz.cuni.mff.d3s.deeco.annotations.PartitionedBy;
import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorExtensionPoint;
import cz.cuni.mff.d3s.deeco.model.runtime.api.EnsembleDefinition;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
public class PartitionedByProcessor extends AnnotationProcessorExtensionPoint {
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorExtensionPoint#onEnsembleDefinitionCreation(cz.cuni.mff.d3s.deeco.model.runtime.api.EnsembleDefinition, java.lang.annotation.Annotation)
	 */
	@Override
	public void onEnsembleDefinitionCreation(EnsembleDefinition ensembleDefinition, Annotation unknownAnnotation) {
		if (unknownAnnotation instanceof PartitionedBy) {
			PartitionedBy partBy = (PartitionedBy)unknownAnnotation;
			
			if(ensembleDefinition.getPartitionedBy() != null) {
				throw new NotImplementedException();
			}
			
			ensembleDefinition.setPartitionedBy(partBy.value());
		}
	}
}
