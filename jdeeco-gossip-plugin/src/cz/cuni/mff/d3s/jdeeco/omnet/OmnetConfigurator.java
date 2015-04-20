/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.omnet;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class OmnetConfigurator {
	
	private boolean ethernet;
	public boolean getEthernet() { return ethernet; }
	public void setEthernet(boolean value) { ethernet = value; }
	
	private List<NodeInfo> nodes = new ArrayList<NodeInfo>();
	public void addNode(int id, double x, double y) {
		nodes.add(new NodeInfo(id, x, y));
	}
	
	public void createConfig(String inputFile, String outputFile) throws IOException {
		
		String config = new String(Files.readAllBytes(Paths.get(inputFile)));
		PrintWriter out = new PrintWriter(outputFile);
		
		out.println(config);
		
		out.println(String.format("**.ethernet=%b", getEthernet()));
		out.println(String.format("**.playgroundSizeX = %dm", 1000));
		out.println(String.format("**.playgroundSizeX = %dm", 1000));
		
		out.println();
		out.println(String.format("**.numNodes = %d", nodes.size()));
		for (NodeInfo node : nodes) {
			out.println(String.format("**.node[%d].mobility.initialX = %fm", node.id, node.x));
			
			out.println(String.format("**.node[%d].mobility.initialY = %fm", node.id, node.y));
			out.println(String.format("**.node[%d].mobility.initialZ = 0m", node.id));
			out.println(String.format("**.node[%d].appl.id = %d", node.id, node.id));
			out.println();
		}
		out.close();		
	}
}
