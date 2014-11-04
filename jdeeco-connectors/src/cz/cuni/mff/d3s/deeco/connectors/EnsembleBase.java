package cz.cuni.mff.d3s.deeco.connectors;

import java.util.Collection;
import java.util.List;

public abstract class EnsembleBase {
	
	public static Boolean matchRole(String role, Collection<String> cRoles, Collection<String> mRoles) {
		return matchRole(role, cRoles) && matchRole(role, mRoles);
	}
	public static Boolean matchRole(String role, Collection<String> roles) {
		return roles != null && roles.contains(role);
	}
}
