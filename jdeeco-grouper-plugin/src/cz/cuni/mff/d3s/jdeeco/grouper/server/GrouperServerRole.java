/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.grouper.server;

import java.util.Set;

import cz.cuni.mff.d3s.deeco.annotations.Role;

/**
 * 
 * @author Ondrej Kováč <info@vajanko.me>
 */
@Role
public class GrouperServerRole {
	//public String id;
	public Set<String> groupMembers;
}
