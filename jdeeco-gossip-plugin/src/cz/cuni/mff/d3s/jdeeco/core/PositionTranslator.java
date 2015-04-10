/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.core;

/**
 * Translates GPS position from one system to another.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public interface PositionTranslator {
	Position translate(Position pos);
}
