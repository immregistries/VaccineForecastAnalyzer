package org.tch.ft.model;

import java.io.Serializable;

public class TestPanelExpected  implements Serializable{

  private static final long serialVersionUID = 1L;

  private int testPanelExpectedId = 0;
  private TestPanelCase testPanelCase = null;
  private ForecastExpected forecastExpected = null;

  public int getTestPanelExpectedId() {
    return testPanelExpectedId;
  }

  public void setTestPanelExpectedId(int testPanelExpectedId) {
    this.testPanelExpectedId = testPanelExpectedId;
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
