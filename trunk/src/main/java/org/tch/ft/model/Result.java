package org.tch.ft.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public enum Result implements Serializable {
  PASS("P", "Pass"), ACCEPT("A", "Accept"), FAIL("F", "Fail"), RESEARCH("R", "Research"), FIXED("I", "Fixed");
  
  private static final long serialVersionUID = 1L;
  
  private String resultStatus = "";
  private String label = "";

  Result(String resultStatus, String label) {
    this.resultStatus = resultStatus;
    this.label = label;
  }

  public String getResultStatus() {
    return resultStatus;
  }

  public void setResultStatus(String resultStatus) {
    this.resultStatus = resultStatus;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }
  
  public static Result getResult(String resultStatus)
  {
    for (Result result : values())
    {
      if (result.getResultStatus().equals(resultStatus))
      {
        return result;
      }
    }
    return null;
  }
  
  public static List<Result> valueList()
  {
    return Arrays.asList(values());
  }

  @Override
  public String toString() {
    return label;
  }
}
