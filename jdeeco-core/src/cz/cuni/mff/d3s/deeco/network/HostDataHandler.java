package cz.cuni.mff.d3s.deeco.network;

public interface HostDataHandler {
	
	public KnowledgeDataSender getKnowledgeDataSender();
	
	public DataSender getDataSender();
	public void addDataReceiver(DataReceiver dataReceiver);
}
