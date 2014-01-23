package org.tch.ft.model;

import java.io.Serializable;

public class TestPanelForecast  implements Serializable{

  private static final long serialVersionUID = 1L;

  private int testPanelForecastId = 0;
  private TestPanelCase testPanelCase = null;
  private ForecastExpected forecastExpected = null;

  public int getTestPanelForecastId() {
    return testPanelForecastId;
  }

  public void setTestPanelForecastId(int testPanelForecastId) {
    this.testPanelForecastId = testPanelForecastId;
  }

  public TestPanelCase getTestPanelCase() {
    return testPanelCase;
  }

  public void setTestPanelCase(TestPanelCase testPanelCase) {
    this.testPanelCase = testPanelCase;
  }

  public ForecastExpected getForecastExpected() {
    return forecastExpected;
  }

  public void setForecastExpected(ForecastExpected forecastExpected) {
    this.forecastExpected = forecastExpected;
  }
}
