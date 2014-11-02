package cz.cuni.mff.d3s.deeco.connectors;

public abstract class EnsembleComponent {
	
	public String id;
	public String role;
	
	public EnsembleComponent(String id) {
		this.id = id;
		this.role = this.getClass().getSimpleName();
	}
}

