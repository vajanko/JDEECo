package cz.cuni.mff.d3s.deeco.network;

public interface HostDataHandler {
	// TODO: this need to be changed to something more general
	public KnowledgeDataSender getKnowledgeDataSender();
	public void addDataReceiver(DataReceiver dataReceiver);
}
