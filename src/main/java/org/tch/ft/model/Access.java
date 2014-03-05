package org.tch.ft.model;

import java.io.Serializable;

public enum Access implements Serializable {
  REQUESTED("R", "Requested"), GRANTED("G", "Granted"), CONFIRMED("C", "Confirmed"), REVOKED("X", "Revoked");
  
  private String accessStatus = "";
  private String label = "";

  public String getAccessStatus() {
    return accessStatus;
  }

  public void setAccessStatus(String accessStatus) {
    this.accessStatus = accessStatus;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  private Access(String accessStatus, String label) {
    this.accessStatus = accessStatus;
    this.label = label;
  }

  public static Access getAccess(String accessStatus) {
    for (Access access : Access.values()) {
      if (access.getAccessStatus().equalsIgnoreCase(accessStatus)) {
        return access;
      }
    }
    return null;
  }
}
