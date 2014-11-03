package cz.cuni.mff.d3s.deeco.connectors;

import java.util.ArrayList;
import java.util.List;

public abstract class EnsembleComponent {
	
	public String id;
	public List<String> roles;
	
	public Boolean hasRole(String role) {
		return this.roles.contains(role);
	}
	
	public EnsembleComponent(String id) {
		this.roles = new ArrayList<String>();
		this.id = id;
		this.roles.add(this.getClass().getSimpleName());
	}
}

