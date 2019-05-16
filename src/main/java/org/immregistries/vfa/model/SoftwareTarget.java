package org.immregistries.vfa.model;

import java.io.Serializable;

import org.immregistries.vfa.connect.model.Software;

public class SoftwareTarget implements Serializable{
  
  private int softwareTargetId = 0;
  private SoftwareCompare softwareCompare;
  private Software software = null;
  
  public int getSoftwareTargetId() {
    return softwareTargetId;
  }
  public void setSoftwareTargetId(int softwareTargetId) {
    this.softwareTargetId = softwareTargetId;
  }
  public SoftwareCompare getSoftwareCompare() {
    return softwareCompare;
  }
  public void setSoftwareCompare(SoftwareCompare softwareCompare) {
    this.softwareCompare = softwareCompare;
  }
  public Software getSoftware() {
    return software;
  }
  public void setSoftware(Software software) {
    this.software = software;
  }
}
