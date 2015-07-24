/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.omnet;

import cz.cuni.mff.d3s.deeco.simulation.omnet.OMNeTNative;
import cz.cuni.mff.d3s.deeco.simulation.omnet.OMNeTNativeListener;
import cz.cuni.mff.d3s.deeco.timer.TimerEventListener;
import cz.cuni.mff.d3s.jdeeco.network.omnet.OMNeTBroadcastDevice;
import cz.cuni.mff.d3s.jdeeco.network.omnet.OMNeTInfrastructureDevice;

/**
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class OmnetAgent implements OMNeTNativeListener {
	
	private final int hostId;
	
	private TimerEventListener eventListener = null;
	private OMNeTBroadcastDevice broadcastDevice = null;
	private OMNeTInfrastructureDevice infrastructureDevice = null;
	
	public int getId() {
		return hostId;
	}
	
	public OmnetAgent(int hostId, OMNeTBroadcastDevice broadcastDevice, OMNeTInfrastructureDevice infrastructureDevice) {
		this.hostId = hostId;
		this.broadcastDevice = broadcastDevice;
		this.infrastructureDevice = infrastructureDevice;
	}
	
	public void notifyAt(long time, TimerEventListener listener) {
		// TODO: this should be rather collection of callbacks which should be called at ..
		this.eventListener = listener;
		OMNeTNative.nativeCallAt(OMNeTNative.timeToOmnet(time), hostId);
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.simulation.omnet.OMNeTNativeListener#at(double)
	 */
	@Override
	public void at(double absoluteTime) {
		eventListener.at(OMNeTNative.timeFromOmnet(absoluteTime));
	}
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.simulation.omnet.OMNeTNativeListener#packetReceived(byte[], double)
	 */
	@Override
	public void packetReceived(byte[] packet, double rssi) {
		if(rssi == -1 && infrastructureDevice != null) {
			infrastructureDevice.receivePacket(packet);
		}
		
		if(rssi >= 0 && broadcastDevice != null) {
			broadcastDevice.receivePacket(packet, rssi);
		}
	}
}
