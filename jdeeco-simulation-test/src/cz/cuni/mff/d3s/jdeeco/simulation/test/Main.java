package cz.cuni.mff.d3s.jdeeco.simulation.test;


import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;

public class Main {
	
	private static void DirectSimulation() throws AnnotationProcessorException
	{
		/*NetworkKnowledgeDataHandler knowledgeHandler = new DirectKnowledgeDataHandler(); 
				//new DelayedKnowledgeDataHandler(100);
		JDEECoSimulation sim = new JDEECoSimulation(0, 1000, knowledgeHandler);
		
		SimulationRuntimeBuilder builder = new SimulationRuntimeBuilder();
		
		BroadcastRecipientSelector recipientSelector = new BroadcastRecipientSelector();
		Collection<DirectRecipientSelector> recipientSelectors = Arrays.asList((DirectRecipientSelector)recipientSelector);
		
		SimpleGossipStrategy gossipStrategy = new SimpleGossipStrategy();
		
		for (int i = 0; i < 2; i++) {
			RuntimeMetadata model = RuntimeMetadataFactoryExt.eINSTANCE.createRuntimeMetadata();
			AnnotationProcessor processor = new AnnotationProcessor(RuntimeMetadataFactoryExt.eINSTANCE, model);
			
			// component id is the same as OmNET simulation host
			NodeComponent component = new NodeComponent("N" + i, "data" + i);
			recipientSelector.addComponet(component);
			processor.process(component, NodeAggregation.class);
			
			DirectSimulationHost host = sim.getHost(component.id);
			
			RuntimeFramework runtime = builder.build(host, sim, null, model, recipientSelectors, gossipStrategy);
			
			runtime.start();
		}
		
		sim.run();
		
		System.out.println("Simulation finished.");*/
	}
	private static void OmnetSimulation() throws AnnotationProcessorException
	{
		/*OMNetSimulation sim = new OMNetSimulation();
		
		SimulationRuntimeBuilder builder = new SimulationRuntimeBuilder();
		
		BroadcastRecipientSelector recipientSelector = new BroadcastRecipientSelector();
		Collection<DirectRecipientSelector> recipientSelectors = Arrays.asList((DirectRecipientSelector)recipientSelector);
		
		SimpleGossipStrategy gossipStrategy = new SimpleGossipStrategy();
		
		for (int i = 0; i < 2; i++) {
			RuntimeMetadata model = RuntimeMetadataFactoryExt.eINSTANCE.createRuntimeMetadata();
			AnnotationProcessor processor = new AnnotationProcessor(RuntimeMetadataFactoryExt.eINSTANCE, model);
			
			// component id is the same as OmNET simulation host
			NodeComponent component = new NodeComponent("N" + i, "data" + i);
			recipientSelector.addComponet(component);
			processor.process(component, NodeAggregation.class);
			
			// OMNetSimulationHost id must be the same as id of the omnet simple module hosting OMNetSimulationHost
			OMNetSimulationHost host = sim.getHost(component.id, "node[" + i + "]");
			
			RuntimeFramework runtime = builder.build(host, sim, null, model, recipientSelectors, gossipStrategy);
			runtime.start();
		}
		
		sim.run("Cmdenv", "omnetpp.ini");
		//sim.run("Tkenv", "omnetpp.ini");
		
		sim.finalize();
		System.out.println("Simulation finished.");*/
	}

	public static void main(String[] args) throws AnnotationProcessorException {
		
		//OmnetSimulation();
		DirectSimulation();
		
	}
}
