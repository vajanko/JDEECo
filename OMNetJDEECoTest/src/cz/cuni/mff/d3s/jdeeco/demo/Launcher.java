package cz.cuni.mff.d3s.jdeeco.demo;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessor;
import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.knowledge.CloningKnowledgeManagerFactory;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManagerFactory;
import cz.cuni.mff.d3s.deeco.model.runtime.api.EnsembleDefinition;
import cz.cuni.mff.d3s.deeco.model.runtime.api.RuntimeMetadata;
import cz.cuni.mff.d3s.deeco.model.runtime.custom.RuntimeMetadataFactoryExt;
import cz.cuni.mff.d3s.deeco.network.DataSender;
import cz.cuni.mff.d3s.deeco.network.DefaultKnowledgeDataManager;
import cz.cuni.mff.d3s.deeco.network.IPGossipStrategy;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataManager;
import cz.cuni.mff.d3s.deeco.network.connector.ConnectorAwareKnowledgeDataManager;
import cz.cuni.mff.d3s.deeco.network.connector.ConnectorComponent;
import cz.cuni.mff.d3s.deeco.network.connector.ConnectorEnsemble;
import cz.cuni.mff.d3s.deeco.network.connector.IPGossipConnectorStrategy;
import cz.cuni.mff.d3s.deeco.network.ip.IPControllerImpl;
import cz.cuni.mff.d3s.deeco.network.ip.IPDataSender;
import cz.cuni.mff.d3s.deeco.network.ip.IPGossipClientStrategy;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeFramework;
import cz.cuni.mff.d3s.deeco.simulation.DirectSimulationHost;
import cz.cuni.mff.d3s.deeco.simulation.JDEECoSimulation;
import cz.cuni.mff.d3s.deeco.simulation.NetworkDataHandler;
import cz.cuni.mff.d3s.deeco.simulation.SimulationRuntimeBuilder;
import cz.cuni.mff.d3s.jdeeco.demo.custom.KnowledgeProvider;
import cz.cuni.mff.d3s.jdeeco.demo.custom.RealisticKnowledgeDataHandler;

public class Launcher {
	
	static int SIMULATION_DURATION = 60; // in seconds
	static JDEECoSimulation simulation;
	static SimulationRuntimeBuilder builder;
	static CloningKnowledgeManagerFactory kmFactory;
	static PositionActuator positionActuator;

	public static void deployVehicle(Vehicle component) throws AnnotationProcessorException {

		RuntimeMetadata model = RuntimeMetadataFactoryExt.eINSTANCE.createRuntimeMetadata();
		AnnotationProcessor processor = new AnnotationProcessor(RuntimeMetadataFactoryExt.eINSTANCE, model,
				kmFactory, new PartitionedByProcessor());
		processor.process(component, DataExchange.class);

		DirectSimulationHost host = simulation.getHost(component.id);
		IPControllerImpl controller = new IPControllerImpl();
		
		// TODO: default IP should be added automatically based on current ensemble definition
		controller.getRegister(component.destination).add("C1");
		host.addDataReceiver(controller);
		
		Set<String> partitions = new HashSet<String>();
		for (EnsembleDefinition ens : model.getEnsembleDefinitions())
			partitions.add(ens.getPartitionedBy());
		
		IPGossipStrategy strategy = new IPGossipClientStrategy(partitions, controller);
		
		KnowledgeDataManager kdm = new DefaultKnowledgeDataManager(model.getEnsembleDefinitions(), strategy);
		
		RuntimeFramework runtime = builder.build(host, simulation, null, model, kdm, new CloningKnowledgeManagerFactory());
		runtime.start();
	}
	
	public static void deployConnector(String id, Position position, Collection<Object> range) throws AnnotationProcessorException {
		/* Model */
		KnowledgeManagerFactory knowledgeManagerFactory = new CloningKnowledgeManagerFactory();
		RuntimeMetadata model = RuntimeMetadataFactoryExt.eINSTANCE.createRuntimeMetadata();
		AnnotationProcessor processor = new AnnotationProcessor(RuntimeMetadataFactoryExt.eINSTANCE, model,
				knowledgeManagerFactory, new PartitionedByProcessor());
		
		processor.process(DataExchange.class, ConnectorEnsemble.class);
		
		/* Available partitions */
		Set<String> partitions = new HashSet<String>();
		for (EnsembleDefinition ens : model.getEnsembleDefinitions())
			partitions.add(ens.getPartitionedBy());
		
		DirectSimulationHost host = simulation.getHost(id);
		
		/* Create IPController */
		IPControllerImpl controller = new IPControllerImpl();
		host.addDataReceiver(controller);	
		
		/* Create knowledge provider */
		KnowledgeProvider provider = new KnowledgeProvider();
		host.addDataReceiver(provider);
		
		/* Create Connector component */
		IPDataSender ipSender = new IPDataSenderWrapper(host.getDataSender());
		ConnectorComponent connector = new ConnectorComponent(id, partitions, range, 
				controller, ipSender, provider);
		processor.process(connector);
		
		// provide list of initial IPs
		controller.getRegister(connector.connector_group).add("C1"); //.add("C2", "C3");
		
		IPGossipStrategy strategy = new IPGossipConnectorStrategy(partitions, controller);	
		KnowledgeDataManager kdm = new ConnectorAwareKnowledgeDataManager(model.getEnsembleDefinitions(), strategy); 
				//new DefaultKnowledgeDataManager(model.getEnsembleDefinitions(), strategy);
		RuntimeFramework runtime = builder.build(host, simulation, null, model, kdm, new CloningKnowledgeManagerFactory());
		runtime.start();
	}

	public static void main(String[] args) throws AnnotationProcessorException, IOException {
		positionActuator = new PositionActuator();
		NetworkDataHandler ndh = new RealisticKnowledgeDataHandler(positionActuator);
		simulation = new JDEECoSimulation(0L, 60000L, ndh);
		builder = new SimulationRuntimeBuilder();
		kmFactory = new CloningKnowledgeManagerFactory();

		// Deploy vehicles
		deployVehicle(new Vehicle("V0", new Position(0.0, 0.0), "Berlin", positionActuator));
		deployVehicle(new Vehicle("V2", new Position(300.0, 300.0), "Berlin", positionActuator));
		deployVehicle(new Vehicle("V1", new Position(600.0, 600.0), "Prague", positionActuator));
		deployVehicle(new Vehicle("V3", new Position(600.0, 300.0), "Prague", positionActuator));
		deployVehicle(new Vehicle("V5", new Position(600.0, 0.0), "Prague", positionActuator));
		deployVehicle(new Vehicle("V7", new Position(0.0, 600.0), "Prague", positionActuator));
		deployVehicle(new Vehicle("V4", new Position(300.0, 600.0), "Dresden", positionActuator));
		deployVehicle(new Vehicle("V6", new Position(300.0, 0.0), "Dresden", positionActuator));
		
		// Deploy connectors		
		deployConnector("C1", new Position(900.0, 900.0), Arrays.asList((Object) "Berlin"));
		deployConnector("C2", new Position(900.0, 0.0), Arrays.asList((Object) "Prague", "Dresden"));
		deployConnector("C3", new Position(0.0, 900.0), Arrays.asList((Object) "Brno"));
		
		// Run the simulation
		simulation.run();
		System.out.println("Simulation finished.");
	}
	
	private static class IPDataSenderWrapper implements IPDataSender {

		private final DataSender sender;
		
		public IPDataSenderWrapper(DataSender sender) {
			this.sender = sender;
		}
		
		public void sendData(Object data, String recipient) {
			sender.sendData(data, recipient);
		}
		
		public void broadcastData(Object data) {
			sender.broadcastData(data);
		}

	}
}
