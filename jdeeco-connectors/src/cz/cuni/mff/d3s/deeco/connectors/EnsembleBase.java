package cz.cuni.mff.d3s.deeco.connectors;

import java.util.List;

public abstract class EnsembleBase {
	
	public static Boolean matchRole(String role, List<String> cRoles, List<String> mRoles) {
		return cRoles != null && mRoles != null && cRoles.contains(role) && mRoles.contains(role);
	}
}
