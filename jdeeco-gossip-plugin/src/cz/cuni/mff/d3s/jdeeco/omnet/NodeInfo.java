/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.omnet;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class NodeInfo {
	public double x;
	public double y;
	public double z;
	public int id;
	
	public NodeInfo(int id, double x, double y, double z) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public NodeInfo(int id, double x, double y) {
		this(id, x, y, 0);
	}
}
