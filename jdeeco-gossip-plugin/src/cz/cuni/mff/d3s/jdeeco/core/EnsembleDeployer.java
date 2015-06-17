/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorExtensionPoint;
import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.runtime.DuplicateEnsembleDefinitionException;

/**
 * Plugin deploying ensembles on each node as specified in the configuration file 
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class EnsembleDeployer implements DEECoPlugin {
	/**
	 * Name of property in the configuration file which specifies which
	 * ensembles should be deployed. This is a list of semicolon separated
	 * class names.
	 */
	public static final String DEPLOY_ENSEMBLES = "deeco.sim.ensembles";
	/**
	 * Default collection of ensembles to be deployed.
	 */
	public static final String DEPLOY_ENSEMBLES_DEFAULT = "";
	
	private static ArrayList<AnnotationProcessorExtensionPoint> preprocessors = new ArrayList<AnnotationProcessorExtensionPoint>();
	
	/**
	 * Register custom annotation preprocessor before ensembles are deployed.
	 * 
	 * @param preprocessor Instance of custom annoatation preprocessor.
	 */
	public static void registerPreprocessor(AnnotationProcessorExtensionPoint preprocessor) {
		preprocessors.add(preprocessor);
	}

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
		
		// register annotation preprocessors before processing ensembles
		for (AnnotationProcessorExtensionPoint prep : preprocessors)
			container.getProcessor().addExtension(prep);
		
		// deploy ensembles as specified in the configuration file
		String ensembles = System.getProperty(DEPLOY_ENSEMBLES, DEPLOY_ENSEMBLES_DEFAULT);
		String[] parts = ensembles.split(";");
		for (String ens : parts) {
			try {
				container.deployEnsemble(Class.forName(ens));
			} catch (DuplicateEnsembleDefinitionException | ClassNotFoundException | AnnotationProcessorException e) {
				e.printStackTrace();
			}
		}
	}

}
