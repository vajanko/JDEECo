package cz.cuni.mff.d3s.deeco.connectors;

import java.util.ArrayList;
import java.util.List;

public abstract class EnsembleComponent {
	
	public String id;
	public List<String> roles;
	public List<String> relays;
	
	public Boolean hasRole(String role) {
		return this.roles.contains(role);
	}
	
	protected void addRole(String role) {
		this.roles.add(role);
	}
	
	public EnsembleComponent(String id) {
		this.roles = new ArrayList<String>();
		this.relays = new ArrayList<String>();
		this.id = id;
		addRole(this.getClass().getSimpleName());
	
		// TODO: add implemented interfaces of this instance
	}
}

