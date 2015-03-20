/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.io.IOException;
import java.io.InputStream;

/**
 * For correct behaviour these values should be constrained as follows:
 * HEADERS_PUSH_PERIOD < HEADERS_PUSH_TIMEOUT < KNOWLEDGE_PULL_TIMEOUT
 * 
 * @author Ondrej Kováè <info@vajanko.me>
 */
public final class GossipProperties {
	
	/**
	 * Loads user properties specified by the user in the config.properties file.
	 */
	public static void initialize() {
		InputStream input = null;
		
		try {
			input = GossipProperties.class.getClassLoader().getResourceAsStream("config.properties");
			
			System.getProperties().load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static final String KNOWLEDGE_PUSH_PERIOD = "deeco.push.knowledge_period";
	/**
	 * Default value of knowledge publishing period in milliseconds.
	 */
	public static final long KNOWLEDGE_PUSH_PERIOD_DEFAULT = 1000;
	/**
	 * See {@link #KNOWLEDGE_PUSH_PERIOD_DEFAULT} for default value.
	 * @return Knowledge publishing period in milliseconds.
	 */
	public static long getKnowledgePushPeriod() {
		return Long.getLong(KNOWLEDGE_PUSH_PERIOD, KNOWLEDGE_PUSH_PERIOD_DEFAULT);
	}
	
	public static final String KONWLEDGE_PUSH_PROBABILITY = "deeco.push.knowledge_probability";
	public static final double KONWLEDGE_PUSH_PROBABILITY_DEFAULT = 0.5;
	/**
	 * @return Probability of knowledge rebroadcast when received by current node.
	 */
	public static double getKnowledgePushProbability() {
		return getDouble(KONWLEDGE_PUSH_PROBABILITY, KONWLEDGE_PUSH_PROBABILITY_DEFAULT);
	}
	
	public static final String HEADERS_PUSH_PERIOD = "deeco.push.headers_period";
	public static final long HEADERS_PUSH_PERIOD_DEFAULT = 2000;
	/**
	 * 
	 * @return Message headers publishing period in milliseconds.
	 */
	public static long getHeadersPushPeriod() {
		return Long.getLong(HEADERS_PUSH_PERIOD, HEADERS_PUSH_PERIOD_DEFAULT);
	}
	
	public static final String HEADERS_PUSH_PROBABILITY = "deeco.push.headers_probability";
	public static final double HEADERS_PUSH_PROBABILITY_DEFAULT = 0.0;
	/**
	 * 
	 * @return Probability of message headers rebroadcast when received by current node.
	 */
	public static double getHeadersPushProbability() {
		return getDouble(HEADERS_PUSH_PROBABILITY, HEADERS_PUSH_PROBABILITY_DEFAULT);
	}
	
	/*public static final String HEADERS_PUSH_TIMEOUT = "deeco.push_headers_timeout";
	public static final int HEADERS_PUSH_TIMEOUT_DEFAULT = 10000;
	public static int getHeadersPushTimeout() {
		return Integer.getInteger(HEADERS_PUSH_TIMEOUT, HEADERS_PUSH_TIMEOUT_DEFAULT);
	}*/
	
	public static final String KNOWLEDGE_PULL_PERIOD = "deeco.pull.knowledge_period";
	public static final long KNOWLEDGE_PULL_PERIOD_DEFAULT = 4000;
	/**
	 * 
	 * @return Knowledge pulling period in milliseconds.
	 */
	public static final long getKnowledgePullPeriod() {
		return Long.getLong(KNOWLEDGE_PULL_PERIOD, KNOWLEDGE_PULL_PERIOD_DEFAULT);
	}
	
	public static final String KNOWLEDGE_PULL_TIMEOUT = "deeco.pull.knowledge_timeout";
	public static final long KNOWLEDGE_PULL_TIMEOUT_DEFAULT = 4000;
	/**
	 * See {@link #KNOWLEDGE_PULL_TIMEOUT_DEFAULT} for default value.
	 * @return Time after which knowledge is considered to be outdated and PULL request
	 * is necessary.
	 */
	public static final long getKnowledgePullTimeout() {
		return Long.getLong(KNOWLEDGE_PULL_TIMEOUT, KNOWLEDGE_PULL_TIMEOUT_DEFAULT);
	}
	
	public static final String COMPONENT_PULL_TIMEOUT = "deeco.pull.component_timeout";
	public static final int COMPONENT_PULL_TIMEOUT_DEFAULT = 20000;
	/**
	 * See {@link #COMPONENT_PULL_TIMEOUT_DEFAULT} for default value.
	 * @return Time after which component is considered to be decommissioned and
	 * can be removed from all buffers.
	 */
	public static final long getComponentPullTimeout() {
		return Long.getLong(COMPONENT_PULL_TIMEOUT, COMPONENT_PULL_TIMEOUT_DEFAULT);
	}
	
	private static double getDouble(String name, double def) {
		String prop = System.getProperty(name, Double.toString(def));
		
		double value = Double.parseDouble(prop);
		
		return value;
	}
}
