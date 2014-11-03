package cz.cuni.mff.d3s.deeco.connectors;

public abstract class EnsembleBase {
	
	public static Boolean matchRole(String role, String cRole, String mRole) {
		return cRole.equals(role) && mRole.equals(role);
	}
}
