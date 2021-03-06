package test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessor;
import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.knowledge.CloningKnowledgeManagerFactory;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManagerFactory;
import cz.cuni.mff.d3s.deeco.model.runtime.api.EnsembleDefinition;
import cz.cuni.mff.d3s.deeco.model.runtime.api.RuntimeMetadata;
import cz.cuni.mff.d3s.deeco.model.runtime.custom.RuntimeMetadataFactoryExt;
import cz.cuni.mff.d3s.deeco.network.DefaultKnowledgeDataManager;
import cz.cuni.mff.d3s.deeco.network.IPGossipStrategy;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataManager;
import cz.cuni.mff.d3s.deeco.network.connector.ConnectorComponent;
import cz.cuni.mff.d3s.deeco.network.connector.ConnectorEnsemble;
import cz.cuni.mff.d3s.deeco.network.connector.IPGossipConnectorStrategy;
import cz.cuni.mff.d3s.deeco.network.connector.KnowledgeProvider;
import cz.cuni.mff.d3s.deeco.network.ip.IPControllerImpl;
import cz.cuni.mff.d3s.deeco.network.ip.IPGossipClientStrategy;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeFramework;
import cz.cuni.mff.d3s.deeco.simulation.SimulationRuntimeBuilder;
import cz.cuni.mff.d3s.deeco.simulation.omnet.OMNetSimulation;
import cz.cuni.mff.d3s.deeco.simulation.omnet.OMNetSimulationHost;

class ConnectorInfo {
	public String id;
	public Double xCoord;
	public Double yCoord;
	public Set<Object> range;
	
	public ConnectorInfo(String id, Double xCoord, Double yCoord, Collection<Object> range) {
		this.id = id;
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.range = new HashSet<Object>(range);
	}
}


public class Launcher {
	
	static String OMNET_CONFIG_TEMPLATE = "omnetpp.ini.templ";
	static int SIMULATION_DURATION = 60; // in seconds

	static int nodeCounter = 0;

	static int getNextNodeId() {
		return nodeCounter++;
	}

	static int getNumNodes() {
		return nodeCounter;
	}

	public static void deployVehicle(OMNetSimulation sim, SimulationRuntimeBuilder builder, StringBuilder omnetCfg, Vehicle component) throws AnnotationProcessorException {
		final int nodeId = getNextNodeId();

		KnowledgeManagerFactory knowledgeManagerFactory = new CloningKnowledgeManagerFactory();
		RuntimeMetadata model = RuntimeMetadataFactoryExt.eINSTANCE.createRuntimeMetadata();
		AnnotationProcessor processor = new AnnotationProcessor(RuntimeMetadataFactoryExt.eINSTANCE, model,
				knowledgeManagerFactory, new PartitionedByProcessor());

		processor.process(component, DataExchange.class);

		omnetCfg.append(String.format("**.node[%d].mobility.initialX = %dm %n", nodeId, component.xCoord.longValue()));
		omnetCfg.append(String.format("**.node[%d].mobility.initialY = %dm %n", nodeId, component.yCoord.longValue()));
		omnetCfg.append(String.format("**.node[%d].mobility.initialZ = 0m %n", nodeId));
		omnetCfg.append(String.format("**.node[%d].appl.id = \"%s\" %n%n", nodeId, component.id));
		OMNetSimulationHost host = sim.getHost(component.id, String.format("node[%d]", nodeId));
			
		IPControllerImpl controller = new IPControllerImpl();
		// TODO: default IP should be added automatically based on current ensemble definition
		controller.getRegister(component.destination).add("C1");
		
		host.addDataReceiver(controller);
		
		Set<String> partitions = new HashSet<String>();
		for (EnsembleDefinition ens : model.getEnsembleDefinitions())
			partitions.add(ens.getPartitionedBy());
		
		IPGossipStrategy strategy = new IPGossipClientStrategy(partitions, controller);
		KnowledgeDataManager kdm = new DefaultKnowledgeDataManager(model.getEnsembleDefinitions(), strategy);
		
		/* Runtime framework */
		RuntimeFramework runtime = builder.build(host, sim, null, model, kdm, new CloningKnowledgeManagerFactory());
		runtime.start();
	}
	public static void deployConnector(OMNetSimulation sim, SimulationRuntimeBuilder builder, StringBuilder omnetCfg,
			ConnectorInfo component) throws AnnotationProcessorException {
		
		final int nodeId = getNextNodeId();
		
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
		
		OMNetSimulationHost host = sim.getHost(component.id, String.format("node[%d]", nodeId));
		
		/* Create IPController */
		IPControllerImpl controller = new IPControllerImpl();
		host.addDataReceiver(controller);	
		
		/* Create knowledge provider */
		KnowledgeProvider provider = new KnowledgeProvider();
		host.addDataReceiver(provider);
		
		/* Create Connector component */
		ConnectorComponent connector = new ConnectorComponent(component.id, partitions, component.range, 
				controller, host.getDataSender(), provider);
		processor.process(connector);
		
		// provide list of initial IPs
		controller.getRegister(connector.connector_group).add("C1"); //.add("C2", "C3");
		
		/* OmNET configuration */
		omnetCfg.append(String.format("**.node[%d].mobility.initialX = %dm %n", nodeId, component.xCoord.longValue()));
		omnetCfg.append(String.format("**.node[%d].mobility.initialY = %dm %n", nodeId, component.yCoord.longValue()));
		omnetCfg.append(String.format("**.node[%d].mobility.initialZ = 0m %n", nodeId));
		omnetCfg.append(String.format("**.node[%d].appl.id = \"%s\" %n%n", nodeId, component.id));
		
		
		IPGossipStrategy strategy = new IPGossipConnectorStrategy(partitions, controller);
		KnowledgeDataManager kdm = new DefaultKnowledgeDataManager(model.getEnsembleDefinitions(), strategy);
		
		/* Runtime framework */
		RuntimeFramework runtime = builder.build(host, sim, null, model, kdm, new CloningKnowledgeManagerFactory());
		runtime.start();
	}

	public static void main(String[] args) throws AnnotationProcessorException, IOException {
		OMNetSimulation sim = new OMNetSimulation();
		SimulationRuntimeBuilder builder = new SimulationRuntimeBuilder();

		StringBuilder omnetConfig = new StringBuilder();

		// Deploy vehicles
		deployVehicle(sim, builder, omnetConfig, new Vehicle("V0", 000.0, 000.0, "Berlin"));
		deployVehicle(sim, builder, omnetConfig, new Vehicle("V2", 300.0, 300.0, "Berlin"));
		deployVehicle(sim, builder, omnetConfig, new Vehicle("V1", 600.0, 600.0, "Prague"));
		deployVehicle(sim, builder, omnetConfig, new Vehicle("V3", 600.0, 300.0, "Prague"));
		deployVehicle(sim, builder, omnetConfig, new Vehicle("V5", 600.0, 000.0, "Prague"));
		deployVehicle(sim, builder, omnetConfig, new Vehicle("V7", 000.0, 600.0, "Prague"));
		deployVehicle(sim, builder, omnetConfig, new Vehicle("V4", 300.0, 600.0, "Drsden"));
		deployVehicle(sim, builder, omnetConfig, new Vehicle("V6", 300.0, 000.0, "Drsden"));
		deployVehicle(sim, builder, omnetConfig, new Vehicle("V8", 000.0, 300.0, "Drsden"));
		
		// Deploy connectors		
		deployConnector(sim, builder, omnetConfig, 
				new ConnectorInfo("C1", 900.0, 900.0, Arrays.asList((Object)"Berlin", "destination")));
		deployConnector(sim, builder, omnetConfig, 
				new ConnectorInfo("C2", 900.0, 000.0, Arrays.asList((Object)"Prague", "Drsden")));//, "Drsden")));
		deployConnector(sim, builder, omnetConfig, new ConnectorInfo("C3", 000.0, 900.0, Arrays.asList((Object)"Brno")));
		
		// Preparing omnetpp config
		String confName = "omnetpp";
		String confFile = confName + ".ini";
		Scanner scanner = new Scanner(new File(OMNET_CONFIG_TEMPLATE));
		String template = scanner.useDelimiter("\\Z").next();
		template = template.replace("<<<configName>>>", confName);
		scanner.close();
		PrintWriter out = new PrintWriter(Files.newOutputStream(Paths.get(confFile), StandardOpenOption.CREATE,
				StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING));
		out.println(template);
		out.println();
		out.println(String.format("**.playgroundSizeX = 1000m"));
		out.println(String.format("**.playgroundSizeY = 1000m"));
		out.println();
		out.println(String.format("**.numNodes = %d", getNumNodes()));
		out.println();
		out.println("**.node[*].appl.packet802154ByteLength = 128B");
		out.println();
		out.println();
		out.println(String.format("sim-time-limit = %ds", SIMULATION_DURATION));
		out.println();
		out.println(omnetConfig.toString());
		out.close();

		sim.run("Cmdenv", confFile);
		sim.finalize();
		System.out.println("Simulation finished.");
	}
}
