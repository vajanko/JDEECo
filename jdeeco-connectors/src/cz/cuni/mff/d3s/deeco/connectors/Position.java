package cz.cuni.mff.d3s.deeco.connectors;

public class Position {
	
	// GPS coordinates
	public Double x;
	public Double y;
	
	public Double distance(Position pos) {
		Double a = x - pos.x;
		Double b = y - pos.y;
		return Math.sqrt(a * a + b * b);
	}
	
	public Boolean equals(Position other) {
		return x.equals(other.x) && y.equals(other.y);
	}
	public String toString() {
		return "[" + x + ", " + y + "]";
	}
	
	public Position(Double x, Double y) {
		this.x = x;
		this.y = y;
	}
	public Position() {
		this(0d, 0d);
	}
	
}
