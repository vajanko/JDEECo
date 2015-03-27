/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.strategy;

import cz.cuni.mff.d3s.jdeeco.network.l2.L1DataProcessor;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Strategy;
import cz.cuni.mff.d3s.jdeeco.network.l2.Layer2;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class L2Wrapper implements L1DataProcessor {

	private Layer2 layer2;
	
	private L2Strategy strategy;
	
	/**
	 * 
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
