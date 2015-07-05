package cz.cuni.mff.d3s.jdeeco.network.omnet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class OMNeTConfigGenerator {
	class Node {
		public final int id;
		public int ordinal;
		public final String ipAddress;
		
		Node(int id, String ipAddress) {
			this.id = id;
			this.ipAddress = ipAddress;
		}
	}
	
	static final String DEFAULT_CONTENT = "omnetpp.ini";
	
	private final long limit;
	private final String configFile;
	
	private Set<Node> nodes = new HashSet<>();
	
	
	public OMNeTConfigGenerator(long limit) {
		this(limit, DEFAULT_CONTENT);
	}
	public OMNeTConfigGenerator(long limit, String configFile) {
		this.limit = limit;
		this.configFile = configFile;
	}
	
	public void addNode(Node node) {
		nodes.add(node);
	}
	
	public void addNode(int id, String ipAddress) {
		addNode(new Node(id, ipAddress));
	}
	
	public void addNode(OMNeTSimulation.OMNeTHost host) {
		String ip = null;
		if(host.infrastructureDevice != null) {
			ip = host.infrastructureDevice.address.ipAddress;
		}
		
		addNode(new Node(host.id, ip));
	}
	
	public String getContent() throws IOException {
		StringBuilder content = new StringBuilder();
		
		// Load default configuration content
		content.append(new String(Files.readAllBytes(Paths.get(configFile))));
		content.append(String.format("%n%n%n# CONTENT GENERATED BY %s %n%n%n", getClass().getName()));
		
		// Add num nodes
		content.append(String.format("**.numNodes = %d%n", nodes.size()));
				
		// Add time limit
		content.append(String.format(Locale.getDefault(), "sim-time-limit = %fs%n", (double)limit / 1000));
		
		int run = Integer.getInteger("deeco.sim.run", 0);
		content.append(String.format("output-vector-file = ./results/omnetpp-%d.vec%n", run));
		content.append(String.format("output-scalar-file = ./results/omnetpp-%d.vci%n", run));
		
		// Add nodes
		int counter = 0;
		for(Node node: nodes) {
			node.ordinal = counter++;
			content.append(String.format("%n%n# Node %d definition%n", node.id));
			content.append(String.format("**.node[%d].mobility.initialX = 100m%n", node.ordinal));
			content.append(String.format("**.node[%d].mobility.initialY = 100m%n", node.ordinal));
			content.append(String.format("**.node[%d].mobility.initialZ = 0m%n", node.ordinal));
			content.append(String.format("**.node[%d].appl.id = %d", node.ordinal, node.id));
		}
		
		// Add IP config
//		content.append(String.format("%n%n%n# IP Static configuration%n"));
//		content.append("*.configurator.config = xml(\"");
//		content.append(String.format("<config>\\%n"));
//		for(Node node: nodes) {
//			if(node.ipAddress != null) {
//				content.append(String.format("\t<interface hosts='**.node[%d]' name='eth' address='%s' netmask='255.255.x.x'/>\\%n", node.ordinal, node.ipAddress));
//			}
//		}
//		content.append("</config>\")");
		
		content.append(String.format("%n%n%n# IP Static configuration%n"));
		for(Node node: nodes) {
			if(node.ipAddress != null) {
				content.append(String.format("**.node[%d].configurator.config = xml(<config><interface hosts='**.node[%d]' name='eth' address='%s' netmask='255.255.x.x'/></config>%n", node.id, node.id, node.ipAddress));
			}
		}
		
		return content.toString();
	}
	
	public File writeToTemp() throws IOException {
		// Note: OMNeT finds its parts relative to configuration file
		File temp = new File(String.format("omnentpp-%d.ini", System.currentTimeMillis()));
		temp.deleteOnExit();
		
		FileWriter writer = new FileWriter(temp);
		writer.write(getContent());
		writer.close();
		
		return temp;
	}
}
