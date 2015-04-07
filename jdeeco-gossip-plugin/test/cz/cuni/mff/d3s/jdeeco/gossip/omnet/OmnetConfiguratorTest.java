/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.omnet;

import java.io.IOException;

import cz.cuni.mff.d3s.jdeeco.omnet.OmnetConfigurator;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class OmnetConfiguratorTest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		OmnetConfigurator config = new OmnetConfigurator();
		config.setEthernet(true);
		for (int i =0; i < 3; ++i) {
			config.addNode(i, i * 10, i * 5);
		}

		config.createConfig("test/cz/cuni/mff/d3s/jdeeco/gossip/omnet/omnetpp.ini.templ", "bin/omnetpp.ini");
	}

}
