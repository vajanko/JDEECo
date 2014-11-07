package test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessor;
import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.knowledge.CloningKnowledgeManagerFactory;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManagerFactory;
import cz.cuni.mff.d3s.deeco.model.runtime.api.RuntimeMetadata;
import cz.cuni.mff.d3s.deeco.model.runtime.custom.RuntimeMetadataFactoryExt;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeFramework;
import cz.cuni.mff.d3s.deeco.simulation.SimulationRuntimeBuilder;
import cz.cuni.mff.d3s.deeco.simulation.omnet.OMNetSimulation;
import cz.cuni.mff.d3s.deeco.simulation.omnet.OMNetSimulationHost;

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

	public static void deployVehicle(OMNetSimulation sim, SimulationRuntimeBuilder builder, StringBuilder omnetCfg,
			Vehicle component, HashedIPGossipStorage storage) throws AnnotationProcessorException {
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
		HashedIPGossip strategy = new HashedIPGossip(model, storage);
		RuntimeFramework runtime = builder.build(host, sim, null, model, strategy, null);
		runtime.start();
	}

	public static void main(String[] args) throws AnnotationProcessorException, IOException {
		OMNetSimulation sim = new OMNetSimulation();
		SimulationRuntimeBuilder builder = new SimulationRuntimeBuilder();

		StringBuilder omnetConfig = new StringBuilder();

		// IP gossip
		HashedIPGossipStorage storage = new HashedIPGossipStorage();

		// Deploy vehicles
		deployVehicle(sim, builder, omnetConfig, new Vehicle("V0", 100.0, 100.0, "Berlin"), storage);
		deployVehicle(sim, builder, omnetConfig, new Vehicle("V2", 400.0, 400.0, "Berlin"), storage);
		deployVehicle(sim, builder, omnetConfig, new Vehicle("V1", 900.0, 900.0, "Prague"), storage);
		deployVehicle(sim, builder, omnetConfig, new Vehicle("V3", 900.0, 400.0, "Prague"), storage);
		deployVehicle(sim, builder, omnetConfig, new Vehicle("V5", 900.0, 100.0, "Prague"), storage);
		deployVehicle(sim, builder, omnetConfig, new Vehicle("V7", 100.0, 900.0, "Prague"), storage);
		deployVehicle(sim, builder, omnetConfig, new Vehicle("V4", 400.0, 900.0, "Drsden"), storage);
		deployVehicle(sim, builder, omnetConfig, new Vehicle("V6", 400.0, 100.0, "Drsden"), storage);
		deployVehicle(sim, builder, omnetConfig, new Vehicle("V8", 100.0, 400.0, "Drsden"), storage);

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
