/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.sim;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.scheduler.Scheduler;
import cz.cuni.mff.d3s.deeco.task.CustomStepTask;
import cz.cuni.mff.d3s.deeco.task.Task;
import cz.cuni.mff.d3s.deeco.task.TimerTask;
import cz.cuni.mff.d3s.deeco.task.TimerTaskListener;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.address.Address;
import cz.cuni.mff.d3s.jdeeco.network.address.MANETBroadcastAddress;
import cz.cuni.mff.d3s.jdeeco.network.device.Device;
import cz.cuni.mff.d3s.jdeeco.network.l1.Layer1;
import cz.cuni.mff.d3s.jdeeco.network.l1.ReceivedInfo;

/**
 * This is a copy of {@link cz.cuni.mff.d3s.jdeeco.network.device.BroadcastLoopback}
 * for special purpose.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class BroadcastDevice implements DEECoPlugin {
	
	public static final String BROADCAST_DELAY = "deeco.broadcast.delay";
	public static final int BROADCAST_DELAY_DEFAULT = 0;
	
	final int PACKET_SIZE = 128;
	final long constantDelay;

	private Scheduler scheduler;

	// Loop devices this loop-back network is registered with
	private Set<LoopDevice> loops = new HashSet<>();

	/**
	 * Loop device used to provide broadcast device to layer 1
	 */
	private class LoopDevice extends Device {
		public Layer1 layer1;
		public MANETBroadcastAddress address;

		private String id;

		public LoopDevice(String id, Layer1 layer1) {
			this.id = id;
			this.address = new MANETBroadcastAddress(getId());
			this.layer1 = layer1;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public int getMTU() {
			return PACKET_SIZE;
		}

		@Override
		public boolean canSend(Address address) {
			return address instanceof MANETBroadcastAddress;
		}

		@Override
		public void send(byte[] data, Address addressNotUsed) {
			Task task = new CustomStepTask(scheduler,
					new DeliveryListener(constantDelay, new PacketWrapper(data, this)));
			BroadcastDevice.this.scheduler.addTask(task);
		}
	}

	/**
	 * Packet package used to carry information about packet
	 */
	private final class PacketWrapper {
		public final byte[] data;
		public final LoopDevice source;

		PacketWrapper(byte[] data, LoopDevice source) {
			this.data = data;
			this.source = source;
		}
	}

	/**
	 * Listener used to delayed delivery of data
	 */
	private class DeliveryListener implements TimerTaskListener {
		final private PacketWrapper packet;

		public DeliveryListener(long delay, PacketWrapper packet) {
			this.packet = packet;
		}

		@Override
		public void at(long time, Object triger) {
			BroadcastDevice.this.sendToAll(packet);
		}

		@Override
		public TimerTask getInitialTask(Scheduler scheduler) {
			return null;
		}
	}

	/**
	 * Constructs loop-back broadcast
	 * 
	 * @param constantDelay
	 *            Delay between sending and delivering the packets
	 */
	public BroadcastDevice(long constantDelay) {
		this.constantDelay = constantDelay;
	}

	/**
	 * Constructs loop-back broadcast
	 * 
	 * Delivers packets immediately
	 */
	public BroadcastDevice() {
		this(Integer.getInteger(BROADCAST_DELAY, BROADCAST_DELAY_DEFAULT));
	}

	/**
	 * Sends packet to all registered loop devices except the sender
	 * 
	 * @param packet
	 *            Container containing packet data and sender information
	 */
	public void sendToAll(PacketWrapper packet) {
		for (LoopDevice loop : loops) {
			if (!packet.source.equals(loop))
				loop.layer1.processL0Packet(packet.data, packet.source, new ReceivedInfo(packet.source.address));
		}
	}

	@Override
	public List<Class<? extends DEECoPlugin>> getDependencies() {
		return Arrays.asList(Network.class);
	}

	@Override
	public void init(DEECoContainer container) {
		scheduler = container.getRuntimeFramework().getScheduler();
		Layer1 l1 = container.getPluginInstance(Network.class).getL1();
		LoopDevice loop = new LoopDevice(String.valueOf(container.getId()), l1);
		l1.registerDevice(loop);
		loops.add(loop);
	}
}
