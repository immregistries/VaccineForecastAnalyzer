package org.tch.ft.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public enum Include implements Serializable {
  PROPOSED("P", "Proposed"), INCLUDED("I", "Included"), EXCLUDED("E", "Excluded");
  private static final long serialVersionUID = 1L;

  private String includeStatus = "";
  private String label = "";

  Include(String includeStatus, String label) {
    this.includeStatus = includeStatus;
    this.label = label;
  }

  public String getIncludeStatus() {
    return includeStatus;
  }

  public void setIncludeStatus(String includeStatus) {
    this.includeStatus = includeStatus;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public static Include getInclude(String includeStatus) {
    for (Include include : values()) {
      if (include.getIncludeStatus().equals(includeStatus)) {
        return include;
      }
    }
    return null;
  }
  
  public static List<Include> valueList()
  {
    return Arrays.asList(values());
  }
  
  @Override
  public String toString() {
    return label;
  }
  
}
