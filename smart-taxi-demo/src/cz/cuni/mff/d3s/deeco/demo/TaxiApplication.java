/**
 * 
 */
package cz.cuni.mff.d3s.deeco.demo;

import cz.cuni.mff.d3s.jdeeco.core.Position;

/**
 * Encapsulate the functionality of the cell phone application
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class TaxiApplication {
	private GPSDevice gps = new GPSDevice();
	
	public Position getCurrentPosition() {
		return gps.getCurrentPosition();
	}

	/**
	 * @param position
	 * @return
	 */
	public String getProximateStation(Position position) {
		// TODO Auto-generated method stub
		return null;
	}
}
