/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.receive;

import cz.cuni.mff.d3s.jdeeco.network.l2.L1DataProcessor;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy;
import cz.cuni.mff.d3s.jdeeco.network.l2.Layer2;

/**
 * Decorator for the standard L1 data processor allowing the logger plugin
 * to intercept the sending of each message and log it into a file or console.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class L2Wrapper implements L1DataProcessor {

	/**
	 * L2 layer having strategies which should be applied upon message reception.
	 */
	private Layer2 layer2;
	
	/**
	 * L2 strategy which should be executed as the last one.
	 */
	private L2Strategy strategy;
	
	/**
	 * Creates a new instance of L2 wrapper from already existing L2 layer
	 * which is the default L1 data processor.
	 * 
	 * @param layer2 Network layer 2
	 * @param strategy Last L2 strategy to be applied.
	 */
	public L2Wrapper(Layer2 layer2, L2Strategy strategy) {
		this.layer2 = layer2;
		this.strategy = strategy;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.network.l2.L1DataProcessor#processL2Packet(cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet)
	 */
	@Override
	public void processL2Packet(L2Packet packet) {
		layer2.processL2Packet(packet);
		strategy.processL2Packet(packet);
	}

}
