package register;

import cz.cuni.mff.d3s.deeco.network.PacketSender;

public class CommunicationPort {
	
	private PacketSender sender;
	
	public void sendMessage(RequestMessage msg, String recipient) {
		sender.sendData(msg, recipient);
	}
	public ResponseMessage receiveMessage() {
		return null;
	}
	
	public CommunicationPort(PacketSender sender) {
		this.sender = sender;
	}
}
