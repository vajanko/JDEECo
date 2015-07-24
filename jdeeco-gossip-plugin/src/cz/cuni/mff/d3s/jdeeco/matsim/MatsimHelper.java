/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.matsim;

/**
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public class MatsimHelper {
	/**
	 * Converts seconds to milliseconds
	 * @param s
	 * @return
	 */
	public static long sTOms(double s) {
		return Math.round(s * 1000);
	}
	/**
	 * Converts milliseconds to seconds
	 * @param ms
	 * @return
	 */
	public static double msTOs(long ms) {
		return ((double)ms) / 1000;
	}
}
