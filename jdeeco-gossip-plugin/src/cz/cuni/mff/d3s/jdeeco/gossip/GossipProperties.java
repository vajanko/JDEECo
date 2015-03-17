/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

/**
 * For correct behaviour these values should be constrained as follows:
 * HEADERS_PUSH_PERIOD < HEADERS_PUSH_TIMEOUT < KNOWLEDGE_PULL_TIMEOUT
 * 
 * @author Ondrej Kováè <info@vajanko.me>
 */
public final class GossipProperties {
	
	public static final String KNOWLEDGE_PUSH_PERIOD = "deeco.push.knowledge_period";
	public static final int KNOWLEDGE_PUSH_PERIOD_DEFAULT = 1000;
	public static int getKnowledgePushPeriod() {
		return Integer.getInteger(KNOWLEDGE_PUSH_PERIOD, KNOWLEDGE_PUSH_PERIOD_DEFAULT);
	}
	
	public static final String KONWLEDGE_PUSH_PROBABILITY = "deeco.push.knowledge_probability";
	public static final double KONWLEDGE_PUSH_PROBABILITY_DEFAULT = 0.5;
	public static double getKnowledgePushProbability() {
		return getDouble(KONWLEDGE_PUSH_PROBABILITY, KONWLEDGE_PUSH_PROBABILITY_DEFAULT);
	}
	
	public static final String HEADERS_PUSH_PERIOD = "deeco.push.headers_period";
	public static final int HEADERS_PUSH_PERIOD_DEFAULT = 1000;
	public static int getHeadersPushPeriod() {
		return Integer.getInteger(HEADERS_PUSH_PERIOD, HEADERS_PUSH_PERIOD_DEFAULT);
	}
	
	public static final String HEADERS_PUSH_PROBABILITY = "deeco.push.headers_probability";
	public static final double HEADERS_PUSH_PROBABILITY_DEFAULT = 0.5;
	public static double getHeadersPushProbability() {
		return getDouble(HEADERS_PUSH_PROBABILITY, HEADERS_PUSH_PROBABILITY_DEFAULT);
	}
	
	public static final String HEADERS_PUSH_TIMEOUT = "deeco.push_headers_timeout";
	public static final int HEADERS_PUSH_TIMEOUT_DEFAULT = 10000;
	public static int getHeadersPushTimeout() {
		return Integer.getInteger(HEADERS_PUSH_TIMEOUT, HEADERS_PUSH_TIMEOUT_DEFAULT);
	}
	
	public static final String KNOWLEDGE_PULL_PERIOD = "deeco.pull.knowledge_period";
	public static final int KNOWLEDGE_PULL_PERIOD_DEFAULT = 3000;
	public static final int getKnowledgePullPeriod() {
		return Integer.getInteger(KNOWLEDGE_PULL_PERIOD, KNOWLEDGE_PULL_PERIOD_DEFAULT);
	}
	

	
	private static double getDouble(String name, double def) {
		String prop = System.getProperty(name, Double.toString(def));
		
		double value = Double.parseDouble(prop);
		
		return value;
	}
}
