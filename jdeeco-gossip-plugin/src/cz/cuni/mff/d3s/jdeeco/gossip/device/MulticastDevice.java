package cz.cuni.mff.d3s.jdeeco.gossip.device;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.deeco.runtime.DEECoPlugin;
import cz.cuni.mff.d3s.deeco.scheduler.Scheduler;
import cz.cuni.mff.d3s.deeco.task.CustomStepTask;
import cz.cuni.mff.d3s.deeco.task.TimerTask;
import cz.cuni.mff.d3s.deeco.task.TimerTaskListener;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.address.Address;
import cz.cuni.mff.d3s.jdeeco.network.address.IPAddress;
import cz.cuni.mff.d3s.jdeeco.network.address.MANETBroadcastAddress;
import cz.cuni.mff.d3s.jdeeco.network.device.Device;
import cz.cuni.mff.d3s.jdeeco.network.l1.Layer1;
import cz.cuni.mff.d3s.jdeeco.network.l1.ReceivedInfo;

/**
 * Infrastructure loop-back plug-in
 * 
 * Can be initialized by more DEECo run-times at the same time. Packets send are then delivered (instantly or with
 * delay) to destination loop device.
 * 
 * @author Vladimir Matena <matena@d3s.mff.cuni.cz>
 *
 */
public class MulticastDevice implements DEECoPlugin {
	/**
	 * Loop device used to provide broadcast device to layer 1
	 */
	private class LoopDevice extends Device {
		public Layer1 layer1;
		public IPAddress address;

		private String id;

		public LoopDevice(String id, IPAddress address, Layer1 layer1) {
			this.id = id;
			this.address = address;
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
		public void send(byte[] data, Address notUsed) {
			// Schedule packet delivery
			PacketWrapper packet = new PacketWrapper(data, this);
			Scheduler scheduler = MulticastDevice.this.scheduler;
			scheduler.addTask(new CustomStepTask(scheduler, new DeliveryListener(packet)));
		}
	}

	/**
	 * Packet source and destination wrapper
	 */
	private final class PacketWrapper {
		public final byte[] data;
		public final LoopDevice sender;

		PacketWrapper(byte[] data, LoopDevice sender) {
			this.data = data;
			this.sender = sender;
		}
	}

	/**
	 * Delayed delivery listener
	 */
	private class DeliveryListener implements TimerTaskListener {
		private final PacketWrapper packet;

		public DeliveryListener(PacketWrapper packet) {
			this.packet = packet;
		}

		@Override
		public void at(long time, Object triger) {
			MulticastDevice.this.multicastSend(packet);
		}

		@Override
		public TimerTask getInitialTask(Scheduler scheduler) {
			return null;
		}
	}

	final int PACKET_SIZE = 128;

	final long constantDelay;

	Scheduler scheduler;

	
	private Map<Integer, LoopDevice> devices = new HashMap<>();
	
	// source IP address -> collection of destination devices
	private Map<IPAddress, Collection<LoopDevice>> loops = new HashMap<>();
	
	private Collection<NetworkLink> links;

	/**
	 * Constructs infrastructure loop-back to maintain constant delay
	 * 
	 * @param constantDelay
	 *            Packet delivery delay
	 */
	public MulticastDevice(long constantDelay, Collection<NetworkLink> links) {
		this.constantDelay = constantDelay;
		this.links = links;
	}

	/**
	 * Constructs infrastructure loop-back with zero delay
	 */
	public MulticastDevice(Collection<NetworkLink> links) {
		this(0, links);
	}

	/**
	 * Routes packet to matching loop devices
	 * 
	 * @param data
	 *            Packet data
	 * @param source
	 *            Source loop device
	 * @param destination
	 *            Destination IP address
	 */
	public void multicastSend(PacketWrapper packet) {
		Collection<LoopDevice> loops = this.loops.get(packet.sender.address); 
		
		for (LoopDevice loop : loops) {
			loop.layer1.processL0Packet(packet.data, packet.sender, new ReceivedInfo(packet.sender.address));
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
		String name = String.valueOf(container.getId());
		IPAddress address = new IPAddress(name);
		LoopDevice dev = new LoopDevice(name, address, l1);
		l1.registerDevice(dev);
		
		devices.put(container.getId(), dev);
		
		if (!loops.containsKey(address))
			loops.put(address, new ArrayList<LoopDevice>());
		
		LoopDevice other;
		for (NetworkLink link : links) {
			other = null;
			
			if (link.node1 == container.getId()) {
				other = devices.get(link.node2);
			}
			else if (link.node2 == container.getId()) {
				other = devices.get(link.node1);
			}
			
			if (other == null)
				continue;
			
			loops.get(address).add(other);
			loops.get(other.address).add(dev);
		}
	}
}
