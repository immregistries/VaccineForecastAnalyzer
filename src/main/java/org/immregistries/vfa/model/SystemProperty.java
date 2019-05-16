package org.immregistries.vfa.model;

public class SystemProperty {

  private int systemPropertyId = 0;
  private String propertyName = "";
  private String propertyValue = "";

  public int getSystemPropertyId() {
    return systemPropertyId;
  }

  public void setSystemPropertyId(int systemPropertyId) {
    this.systemPropertyId = systemPropertyId;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  public String getPropertyValue() {
    return propertyValue;
  }

  public void setPropertyValue(String propertyValue) {
    this.propertyValue = propertyValue;
  }

}
