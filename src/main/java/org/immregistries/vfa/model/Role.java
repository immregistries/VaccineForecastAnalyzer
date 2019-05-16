package org.immregistries.vfa.model;

import java.io.Serializable;

public enum Role implements Serializable {
	ADMIN("A", "Admin"), EXPERT("E", "Expert"), VIEW("V", "View"), ;
	
	private static final long serialVersionUID = 1L;
	
	private String roleStatus = "";
	private String label = "";

	Role(String roleStatus, String label) {
		this.roleStatus = roleStatus;

		this.label = label;
	}
	
	public boolean canEdit()
	{
	  return this == ADMIN || this == EXPERT;
	}

	public String getRoleStatus() {
		return roleStatus;
	}

	public void setRoleStatus(String roleStatus) {
		this.roleStatus = roleStatus;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
  public static Role getRole(String roleStatus) {
    for (Role role : values()) {
      if (role.getRoleStatus().equalsIgnoreCase(roleStatus)) {
        return role;
      }
    }
    return null;
  }
  
  @Override
  public String toString() {
    return label;
  }

}
