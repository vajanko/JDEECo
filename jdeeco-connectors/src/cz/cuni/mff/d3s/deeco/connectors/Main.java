package cz.cuni.mff.d3s.deeco.connectors;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Delayed;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessor;
import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.model.runtime.api.RuntimeMetadata;
import cz.cuni.mff.d3s.deeco.model.runtime.custom.RuntimeMetadataFactoryExt;
import cz.cuni.mff.d3s.deeco.network.DirectRecipientSelector;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeConfiguration;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeFramework;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeFrameworkBuilder;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeConfiguration.Distribution;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeConfiguration.Execution;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeConfiguration.Scheduling;
import cz.cuni.mff.d3s.deeco.simulation.DelayedKnowledgeDataHandler;
import cz.cuni.mff.d3s.deeco.simulation.DirectKnowledgeDataHandler;
import cz.cuni.mff.d3s.deeco.simulation.DirectSimulationHost;
import cz.cuni.mff.d3s.deeco.simulation.JDEECoSimulation;
import cz.cuni.mff.d3s.deeco.simulation.NetworkKnowledgeDataHandler;
import cz.cuni.mff.d3s.deeco.simulation.SimulationRuntimeBuilder;
import cz.cuni.mff.d3s.deeco.simulation.TimerTaskListener;

public class Main {
	
	private static void runVehicle()  throws AnnotationProcessorException {

		RuntimeMetadata model = RuntimeMetadataFactoryExt.eINSTANCE.createRuntimeMetadata();
		AnnotationProcessor processor = new AnnotationProcessor(RuntimeMetadataFactoryExt.eINSTANCE, model);
		
		processor.process(new Vehicle("V1", "Berlin", 5d, 0d), 
						  new Vehicle("V2", "Berlin", 3d, 0d),
						  new Vehicle("V3", "Berlin", 1d, 0d),
						  new Vehicle("V4", "Munich", 3d, 0d),
						  // Components 
						  DestinationAggregation.class 
						  // Ensembles
						  );
		
		RuntimeFrameworkBuilder builder = new RuntimeFrameworkBuilder(
				new RuntimeConfiguration(
						Scheduling.WALL_TIME, 
						Distribution.LOCAL, 
						Execution.SINGLE_THREADED));
		RuntimeFramework runtime = builder.build(model); 
		runtime.start();
	}
	private static void runConnector() throws AnnotationProcessorException {
		RuntimeMetadata model = RuntimeMetadataFactoryExt.eINSTANCE.createRuntimeMetadata();
		AnnotationProcessor processor = new AnnotationProcessor(RuntimeMetadataFactoryExt.eINSTANCE, model);
		
		processor.process(
						// Components 
						new Connector("C1", "Munich"), 
						new Connector("C2", "Berlin"),
						new Vehicle("V1", "Berlin"),
						new Vehicle("V2", "Berlin"),
						// Ensembles
						ConnectorEnsemble.class);
		
		RuntimeFrameworkBuilder builder = new RuntimeFrameworkBuilder(
				new RuntimeConfiguration(
						Scheduling.WALL_TIME, 
						Distribution.LOCAL, 
						Execution.SINGLE_THREADED));
		RuntimeFramework runtime = builder.build(model); 
		runtime.start();
	}
	
	private static ComponentInfo deployComponent(JDEECoSimulation sim, EnsembleComponent component, Class ensemble) throws AnnotationProcessorException {
		
		RuntimeMetadata model = RuntimeMetadataFactoryExt.eINSTANCE.createRuntimeMetadata();
		AnnotationProcessor processor = new AnnotationProcessor(RuntimeMetadataFactoryExt.eINSTANCE, model);
		
		//recipientSelector.addComponet(component);
		if (ensemble != null)
			processor.process(component, ensemble);
		else
			processor.process(component);
		
		ComponentInfo info = new ComponentInfo();
		info.host = sim.getHost(component.id);
		info.model = model;
		return info;
	}
	private static void runSimulation() throws AnnotationProcessorException {
		NetworkKnowledgeDataHandler knowledgeHandler = new DelayedKnowledgeDataHandler(100); 
				//new DelayedKnowledgeDataHandler(100);
				//new DirectKnowledgeDataHandler(); 
		JDEECoSimulation sim = new JDEECoSimulation(0, 10000, knowledgeHandler);
		
		SimulationRuntimeBuilder builder = new SimulationRuntimeBuilder();
		
		BoundaryRecipientSelector recipientSelector = new BoundaryRecipientSelector();
		Collection<DirectRecipientSelector> recipientSelectors = Arrays.asList((DirectRecipientSelector)recipientSelector);
		
		// this class is unimplemented
		SimpleGossipStrategy gossipStrategy = new SimpleGossipStrategy();
		
		ArrayList<ComponentInfo> components = new ArrayList<ComponentInfo>();
		
		components.add(deployComponent(sim, new Vehicle("V1", "Berlin", 7d, 0d), DestinationAggregation.class));
		components.add(deployComponent(sim, new Vehicle("V2", "Berlin", 4d, 0d), DestinationAggregation.class));
		components.add(deployComponent(sim, new Vehicle("V3", "Berlin", 1d, 0d), DestinationAggregation.class));
		components.add(deployComponent(sim, new Vehicle("V4", "Munich"), DestinationAggregation.class));
//		components.add(deployComponent(sim, new Connector("C1", "Berlin"), ConnectorEnsemble.class));
//		components.add(deployComponent(sim, new Connector("C2", "Berlin"), ConnectorEnsemble.class));
//		components.add(deployComponent(sim, new Connector("C3", "Berlin"), ConnectorEnsemble.class));
		
		
		Collection<TimerTaskListener> listeners = null;
		if (knowledgeHandler instanceof TimerTaskListener)
			listeners = Arrays.asList((TimerTaskListener)knowledgeHandler);
		
		for (ComponentInfo comp : components) {
			recipientSelector.addRecipient(comp.host.getHostId());
		}
		
		for (ComponentInfo comp : components) {
			RuntimeFramework runtime = builder.build(comp.host, sim, listeners, comp.model, recipientSelectors, gossipStrategy);
			runtime.start();
		}
		
		sim.run();
		
		System.out.println("Simulation finished.");
	}
	public static void main(String[] args) throws AnnotationProcessorException {
				
		runSimulation();
		//runVehicle();

	}

}

class ComponentInfo {
	public DirectSimulationHost host;
	public RuntimeMetadata model;
	
	public ComponentInfo() { }
}
