package org.tch.ft.model;

import java.io.Serializable;

public class Expert implements Serializable {
  private static final long serialVersionUID = 1L;

	private int expertId = 0;
	private User user = null;
	private TaskGroup taskGroup = null;
	private Role role = null;

	public int getExpertId() {
		return expertId;
	}

	public void setExpertId(int expertId) {
		this.expertId = expertId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public TaskGroup getTaskGroup() {
		return taskGroup;
	}

	public void setTaskGroup(TaskGroup taskGroup) {
		this.taskGroup = taskGroup;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public void setRole(String roleStatus) {
		this.role = Role.valueOf(roleStatus);
	}
	
	public String getRoleStatus()
	{
	  return role == null ? null : role.getRoleStatus();
	}
	
	public void setRoleStatus(String roleStatus) 
	{
	  role = Role.getRole(roleStatus);
	}
	    
}
