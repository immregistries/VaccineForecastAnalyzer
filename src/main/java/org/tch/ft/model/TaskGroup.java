package org.tch.ft.model;

import java.io.Serializable;

import org.tch.fc.model.Software;

public class TaskGroup implements Serializable {

  private static final long serialVersionUID = 1L;

	private int taskGroupId = 0;
	private String label = "";
	private Software primarySoftware = null;

	public int getTaskGroupId() {
		return taskGroupId;
	}

	public void setTaskGroupId(int taskGroupId) {
		this.taskGroupId = taskGroupId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Software getPrimarySoftware() {
		return primarySoftware;
	}

	public void setPrimarySoftware(Software primarySoftware) {
		this.primarySoftware = primarySoftware;
	}
	
	@Override
	public boolean equals(Object obj) {
	  if (obj instanceof TaskGroup)
	  {
	    TaskGroup tg = (TaskGroup) obj;
	    return tg.getTaskGroupId() == this.getTaskGroupId();
	  }
	  return super.equals(obj);
	}
	
	@Override
	public String toString() {
	  return label;
	}
}
