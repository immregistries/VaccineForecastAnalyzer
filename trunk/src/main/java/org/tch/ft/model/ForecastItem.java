package org.tch.ft.model;

import java.io.Serializable;

public class ForecastItem implements Serializable {
  private static final long serialVersionUID = 1L;

  private int forecastItemId = 0;
  private String label = "";

  public int getForecastItemId() {
    return forecastItemId;
  }

  public void setForecastItemId(int forecastItemId) {
    this.forecastItemId = forecastItemId;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }
  
  @Override
  public boolean equals(Object obj) {

    if (obj instanceof ForecastItem)
    {
      return ((ForecastItem) obj).getForecastItemId() == this.getForecastItemId();
    }
    return false;
  }
  
  @Override
  public int hashCode() {
    return forecastItemId == 0 ? super.hashCode() : forecastItemId;
  }
  
  @Override
  public String toString() {
    return label;
  }

}
