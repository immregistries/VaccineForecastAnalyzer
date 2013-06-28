package org.tch.ft.model;

import java.io.Serializable;
import java.util.List;

import org.tch.fc.model.Software;

public class SoftwareCompare implements Serializable {

  private int softwareCompareId = 0;
  private Software software = null;
  private TestPanel testPanel = null;
  private List<SoftwareTarget> softwareTargetList = null;

  public List<SoftwareTarget> getSoftwareTargetList() {
    return softwareTargetList;
  }

  public void setSoftwareTargetList(List<SoftwareTarget> softwareTargetList) {
    this.softwareTargetList = softwareTargetList;
  }

  public int getSoftwareCompareId() {
    return softwareCompareId;
  }

  public void setSoftwareCompareId(int softwareCompareId) {
    this.softwareCompareId = softwareCompareId;
  }

  public Software getSoftware() {
    return software;
  }

  public void setSoftware(Software software) {
    this.software = software;
  }

  public TestPanel getTestPanel() {
    return testPanel;
  }

  public void setTestPanel(TestPanel testPanel) {
    this.testPanel = testPanel;
  }

  public String getComparedToLabel() {
    String s = "";
    if (softwareTargetList != null) {
      if (softwareTargetList.size() > 0)
      {
        s = softwareTargetList.get(0).getSoftware().getLabel();
      }
      for (int i = 1 ; i < softwareTargetList.size(); i++)
      {
        if ((i + 1) == softwareTargetList.size())
        {
          s += " and ";
        }
        else
        {
          s += ", ";
        }
        s += softwareTargetList.get(i).getSoftware().getLabel();
      }
    }
    return s;
  }
}
