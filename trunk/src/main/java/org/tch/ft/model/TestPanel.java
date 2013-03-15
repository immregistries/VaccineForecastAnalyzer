package org.tch.ft.model;

import java.io.Serializable;

public class TestPanel implements Serializable {

  private static final long serialVersionUID = 1L;

  private int testPanelId = 0;
  private String label = "";
  private TaskGroup taskGroup = null;

  public int getTestPanelId() {
    return testPanelId;
  }

  public void setTestPanelId(int testPanelId) {
    this.testPanelId = testPanelId;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public TaskGroup getTaskGroup() {
    return taskGroup;
  }

  public void setTaskGroup(TaskGroup taskGroup) {
    this.taskGroup = taskGroup;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof TestPanel) {
      TestPanel tp = (TestPanel) obj;
      return tp.getTestPanelId() == this.getTestPanelId();
    }
    return super.equals(obj);
  }
}
