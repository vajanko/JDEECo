/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
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
	
	public static final String PUBLISH_KN_PERIOD = "deeco.publish.kn_period";
	/**
	 * Default value of knowledge broadcasting period in milliseconds.
	 */
	public static final long PUBLISH_KN_PERIOD_DEFAULT = 2000;
	/**
	 * See {@link #PUBLISH_KN_PERIOD_DEFAULT} for default value.
	 * 
	 * @return Knowledge broadcasting period in milliseconds.
	 */
	public static long getPublishKNPeriod() {
		return Long.getLong(PUBLISH_KN_PERIOD, PUBLISH_KN_PERIOD_DEFAULT);
	}
	
	public static final String PUBLISH_HD_PERIOD = "deeco.publish.hd_period";
	/**
	 * Default value of message headers broadcasting period in milliseconds.
	 */
	public static final long PUBLISH_HD_PERIOD_DEFAULT = 2000;
	/**
	 * See {@link #PUBLISH_HD_PERIOD_DEFAULT} for default value.
	 * 
	 * @return Message headers broadcasting period in milliseconds.
	 */
	public static long getPublishHDPeriod() {
		return Long.getLong(PUBLISH_HD_PERIOD, PUBLISH_HD_PERIOD_DEFAULT);
	}
	
	public static final String PUBLISH_PL_PERIOD = "deeco.publish.pl_period";
	public static final long PUBLISH_PL_PERIOD_DEFAULT = 1000;
	/**
	 * 
	 * @return Knowledge pulling period in milliseconds.
	 */
	public static final long getPublishPLPeriod() {
		return Long.getLong(PUBLISH_PL_PERIOD, PUBLISH_PL_PERIOD_DEFAULT);
	}
	
	public static final String PUBLISH_PROBABILITY = "deeco.publish.probability";
	/**
	 * Default value of knowledge rebroadcast probability when received by current node.
	 */
	public static final double PUBLISH_PROBABILITY_DEFAULT = 0.5;
	/**
	 * See {@link #PUBLISH_PROBABILITY_DEFAULT} for default value.
	 * 
	 * @return Probability of knowledge rebroadcast when received by current node.
	 */
	public static double getPublishProbability() {
		return getDouble(PUBLISH_PROBABILITY, PUBLISH_PROBABILITY_DEFAULT);
	}
		
	public static final String PUBLISH_KN_TIMEOUT = "deeco.publish.kn_timeout";
	/**
	 * Default value of knowledge timeout. After this time knowledge is considered 
	 * to be outdated.
	 */
	public static final long PUBLISH_KN_TIMEOUT_DEFAULT = 4000;
	/**
	 * See {@link #PUBLISH_KN_TIMEOUT_DEFAULT} for default value.
	 * 
	 * @return Knowledge timeout after which it is considered to be outdated.
	 */
	public static final long getPublishKNTimeout() {
		return Long.getLong(PUBLISH_KN_TIMEOUT, PUBLISH_KN_TIMEOUT_DEFAULT);
	}
	
	public static final String PUBLISH_PL_TIMEOUT = "deeco.publish.pl_timeout";
	/**
	 * Default value of pull request timeout. After this time component is considered
	 * to be outdated. 
	 */
	public static final int PUBLISH_PL_TIMEOUT_DEFAULT = 20000;
	/**
	 * See {@link #PUBLISH_PL_TIMEOUT_DEFAULT} for default value.
	 * 
	 * @return Pull request timeout after which component is considered to be outdated.
	 */
	public static final long getPushlishPLTimeout() {
		return Long.getLong(PUBLISH_PL_TIMEOUT, PUBLISH_PL_TIMEOUT_DEFAULT);
	}
	
	private static double getDouble(String name, double def) {
		String prop = System.getProperty(name, Double.toString(def));
		
		double value = Double.parseDouble(prop);
		
		return value;
	}
}
