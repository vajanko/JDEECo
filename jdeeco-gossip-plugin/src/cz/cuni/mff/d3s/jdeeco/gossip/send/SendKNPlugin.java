/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.send;

import cz.cuni.mff.d3s.deeco.network.KnowledgeData;
import cz.cuni.mff.d3s.deeco.network.KnowledgeMetaData;
import cz.cuni.mff.d3s.deeco.runtime.DEECoContainer;
import cz.cuni.mff.d3s.jdeeco.core.AddressHelper;
import cz.cuni.mff.d3s.jdeeco.core.LocalKnowledgeSource;
import cz.cuni.mff.d3s.jdeeco.gossip.AddressRegister;
import cz.cuni.mff.d3s.jdeeco.gossip.AddressRegisterPlugin;
import cz.cuni.mff.d3s.jdeeco.gossip.RecipientSelector;
import cz.cuni.mff.d3s.jdeeco.network.address.Address;
import cz.cuni.mff.d3s.jdeeco.network.address.IPAddress;
import cz.cuni.mff.d3s.jdeeco.network.address.MANETBroadcastAddress;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2Packet;
import cz.cuni.mff.d3s.jdeeco.network.l2.L2PacketType;
import cz.cuni.mff.d3s.jdeeco.network.l2.PacketHeader;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class SendKNPlugin extends SendBasePlugin {
	
	public static final String TASK_PERIOD = "deeco.sendKN.period";
	/**
	 * Default value of knowledge broadcasting period in milliseconds.
	 */
	public static final long TASK_PERIOD_DEFAULT = 2000;
	
	private IPAddress address;
	private AddressRegister addressRegister;
	
	/**
	 * Provides source to be published (sent).
	 */
	private LocalKnowledgeSource knowledgeSource;
	public void setKnowledgeSource(LocalKnowledgeSource knowledgeSource) {
		this.knowledgeSource = knowledgeSource;
	}
	public LocalKnowledgeSource getKnowledgeSource() {
		return this.knowledgeSource;
	}
	
	/**
	 * Provides target addresses for knowledge to be sent retrieved
	 * from {@link LocalKnowledgeSource}.
	 */
	private RecipientSelector recipientSelector;
	
	public RecipientSelector getRecipientSelector() {
		return this.recipientSelector;
	}
	public void setRecipientSelector(RecipientSelector recipientSelector) {
		this.recipientSelector = recipientSelector;
	}
	
	
	public SendKNPlugin() {
		super(Long.getLong(TASK_PERIOD, TASK_PERIOD_DEFAULT));
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.deeco.task.TimerTaskListener#at(long, java.lang.Object)
	 */
	@Override
	public void at(long time, Object triger) {		
		publish();
	}
	private void publish() {
		for(KnowledgeData data: knowledgeSource.getLocalKnowledge()) {
			PacketHeader header = new PacketHeader(L2PacketType.KNOWLEDGE);
			
			L2Packet packet = new L2Packet(header, data);
			
			// publish knowledge on MANET
			this.networkLayer.sendL2Packet(packet, MANETBroadcastAddress.BROADCAST);
			
			// publish knowledge on IP
			for (Address address : this.recipientSelector.getRecipients(data)) {
				if (this.address.equals(address))
					continue;

				// select recipients of particular knowledge
				this.networkLayer.sendL2Packet(packet, address);				
			}
			
			// ... and stores information about last knowledge version
			KnowledgeMetaData meta = data.getMetaData();
			receptionBuffer.receiveLocal(meta.componentId, meta.createdAt, meta.versionId);
		}
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.d3s.jdeeco.gossip.send.SendBasePlugin#init(cz.cuni.mff.d3s.deeco.runtime.DEECoContainer)
	 */
	@Override
	public void init(DEECoContainer container) {
		super.init(container);
		
		this.addressRegister = container.getPluginInstance(AddressRegisterPlugin.class).getRegister();
		
		this.setKnowledgeSource(this.knowledgeProvider);
		this.setRecipientSelector(new StaticRecipientSelector(this.addressRegister));
		
		this.address = AddressHelper.createIP(container.getId());
	}
}
