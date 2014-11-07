package cz.cuni.mff.d3s.deeco.connectors;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Delayed;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessor;
import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.knowledge.CloningKnowledgeManagerFactory;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManagerFactory;
import cz.cuni.mff.d3s.deeco.model.runtime.api.RuntimeMetadata;
import cz.cuni.mff.d3s.deeco.model.runtime.custom.RuntimeMetadataFactoryExt;
import cz.cuni.mff.d3s.deeco.network.DirectRecipientSelector;
import cz.cuni.mff.d3s.deeco.network.IPGossipStrategy;
import cz.cuni.mff.d3s.deeco.network.connector.DummyGossipStrategy;
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
import cz.cuni.mff.d3s.jdeeco.simulation.connector.PartitionedByProcessor;

public class Main {
	
	private static void runVehicle()  throws AnnotationProcessorException {

		RuntimeMetadata model = RuntimeMetadataFactoryExt.eINSTANCE.createRuntimeMetadata();
		KnowledgeManagerFactory knowledgeManagerFactory = new CloningKnowledgeManagerFactory();
		PartitionedByProcessor partionProc = new PartitionedByProcessor();
		AnnotationProcessor processor = new AnnotationProcessor(RuntimeMetadataFactoryExt.eINSTANCE, model, knowledgeManagerFactory, partionProc);
		
		processor.process(new Vehicle("V1", "Berlin", 5d, 0d), 
						  new Vehicle("V2", "Berlin", 3d, 0d),
						  new Vehicle("V3", "Berlin", 1d, 0d),
						  new Vehicle("V4", "Munich", 3d, 0d),
						  // Components 
						  DestinationEnsemble.class 
						  // Ensembles
						  );
		
		RuntimeFrameworkBuilder builder = new RuntimeFrameworkBuilder(
				new RuntimeConfiguration(
						Scheduling.WALL_TIME, 
						Distribution.LOCAL, 
						Execution.SINGLE_THREADED),
				knowledgeManagerFactory);
		RuntimeFramework runtime = builder.build(model); 
		runtime.start();
	}
	private static void runConnector() throws AnnotationProcessorException {
		RuntimeMetadata model = RuntimeMetadataFactoryExt.eINSTANCE.createRuntimeMetadata();
		KnowledgeManagerFactory knowledgeManagerFactory = new CloningKnowledgeManagerFactory();
		PartitionedByProcessor partionProc = new PartitionedByProcessor();
		AnnotationProcessor processor = new AnnotationProcessor(RuntimeMetadataFactoryExt.eINSTANCE, model, knowledgeManagerFactory, partionProc);
		
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
						Execution.SINGLE_THREADED),
				knowledgeManagerFactory);
		RuntimeFramework runtime = builder.build(model); 
		runtime.start();
	}
	
	private static ComponentInfo deployComponent(JDEECoSimulation sim, EnsembleComponent component, Class ... ensemble) throws AnnotationProcessorException {
		
		RuntimeMetadata model = RuntimeMetadataFactoryExt.eINSTANCE.createRuntimeMetadata();
		KnowledgeManagerFactory knowledgeManagerFactory = new CloningKnowledgeManagerFactory();
		PartitionedByProcessor partionProc = new PartitionedByProcessor();
		AnnotationProcessor processor = new AnnotationProcessor(RuntimeMetadataFactoryExt.eINSTANCE, model, knowledgeManagerFactory, partionProc);
		
		processor.process(component, ensemble);
		
		ComponentInfo info = new ComponentInfo();
		info.host = sim.getHost(component.id);
		info.model = model;
		info.knowledgeManagerFactory = knowledgeManagerFactory;
		return info;
	}
	private static void runSimulation() throws AnnotationProcessorException {
		NetworkKnowledgeDataHandler knowledgeHandler = new DelayedKnowledgeDataHandler(100); 
				//new DelayedKnowledgeDataHandler(100);
				//new DirectKnowledgeDataHandler(); 
		JDEECoSimulation sim = new JDEECoSimulation(0, 10000, knowledgeHandler);
		
		SimulationRuntimeBuilder builder = new SimulationRuntimeBuilder();
		
//		BoundaryRecipientSelector recipientSelector = new BoundaryRecipientSelector();
//		Collection<DirectRecipientSelector> recipientSelectors = Arrays.asList((DirectRecipientSelector)recipientSelector);
		
		// selects and filter recipients
		IPGossipStrategy gossipStrategy = new DummyGossipStrategy();
		
		ArrayList<ComponentInfo> components = new ArrayList<ComponentInfo>();
		
		// vehicles
		components.add(deployComponent(sim, new Vehicle("V1", "Berlin", 7d, 0d), RelayEnsemble.class, DestinationEnsemble.class));
		components.add(deployComponent(sim, new Vehicle("V2", "Berlin", 4d, 0d), DestinationEnsemble.class));
		components.add(deployComponent(sim, new Vehicle("V3", "Berlin", 1d, 0d), DestinationEnsemble.class));
//		for (int i = 4; i < 20; i++) {
//			components.add(deployComponent(sim, new Vehicle("V" + i, "Munich", (double)i, 0d), DestinationAggregation.class));
//		}
		//components.add(deployComponent(sim, new Vehicle("V4", "Munich"), DestinationAggregation.class));
		
		// relays
		components.add(deployComponent(sim, new VehicleRelay("R1", "Berlin", 9d, 0d), DestinationEnsemble.class));
		
		components.add(deployComponent(sim, new Connector("C1", "Berlin"), ConnectorEnsemble.class));
//		components.add(deployComponent(sim, new Connector("C2", "Berlin"), ConnectorEnsemble.class));
//		components.add(deployComponent(sim, new Connector("C3", "Berlin"), ConnectorEnsemble.class));
		
		
		Collection<TimerTaskListener> listeners = null;
		if (knowledgeHandler instanceof TimerTaskListener)
			listeners = Arrays.asList((TimerTaskListener)knowledgeHandler);
		
//		for (ComponentInfo comp : components) {
//			recipientSelector.addRecipient(comp.host.getHostId());
//		}
		
		for (ComponentInfo comp : components) {
			RuntimeFramework runtime = builder.build(comp.host, sim, listeners, comp.model, gossipStrategy, comp.knowledgeManagerFactory);
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
	public KnowledgeManagerFactory knowledgeManagerFactory;
	public DirectSimulationHost host;
	public RuntimeMetadata model;
	
	public ComponentInfo() { }
}
