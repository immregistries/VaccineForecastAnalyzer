package org.tch.ft.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public enum Available implements Serializable {
  PUBLIC("A", "Public"), PRIVATE("P", "Private"), DELETED("D", "Deleted");
  
  private String availableCode = "";
  private String label = "";

  public String getAvailableCode() {
    return availableCode;
  }

  public String getLabel() {
    return label;
  }

  private Available(String availableCode, String label) {
    this.availableCode = availableCode;
    this.label = label;
  }

  @Override
  public String toString() {
    return label;
  }

  public static Available getAvailable(String availableCode) {
    for (Available available : Available.values()) {
      if (available.getAvailableCode().equalsIgnoreCase(availableCode)) {
        return available;
      }
    }
    return null;
  }
  
  public static List<Available> valueList() {
    return Arrays.asList(values());
  }

}
