/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.core;

/**
 * Translates GPS position from one system to another.
 * 
 * @author Ondrej Kovac <info@vajanko.me>
 */
public interface PositionTranslator {
	/**
	 * Translates given position from one system to another.
	 * 
	 * @param pos Position to be translated into another positioning system.
	 * @return Translated position.
	 */
	Position translate(Position pos);
}
