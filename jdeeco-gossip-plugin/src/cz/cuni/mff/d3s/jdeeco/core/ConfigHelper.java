/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Manages system properties specified by user in *.properties file.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class ConfigHelper {
	
	/**
	 * Loads user properties specified in the given {@code config} stream
	 * 
	 * @param config Input stream with configuration.
	 */
	public static void loadProperties(InputStream config) throws IOException {
		System.getProperties().load(config);
		config.close();
	}
	/**
	 * Load user configuration from given {@code config} file.
	 * 
	 * @param file Absolute or relative path configuration file.
	 */
	public static void loadProperties(String file) {
		try {
			InputStream input = new FileInputStream(file);
			loadProperties(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get system property with given name of {@link Double} value or default
	 * value if specified property can not be found.
	 *  
	 * @param name Name of system property 
	 * @param def Default value of specified property
	 * @return Double value of specified property or default value if not found.
	 */
	public static double getDouble(String name, double def) {
		String prop = System.getProperty(name, Double.toString(def));
		
		double value = Double.parseDouble(prop);
		
		return value;
	}
}
