package org.tch.ft.model;

import org.tch.fc.model.TestCase;


public class ForecastActual extends org.tch.fc.model.ForecastActual {
  protected TestCase testCase = null;

  public TestCase getTestCase() {
    return testCase;
  }

  public void setTestCase(TestCase testCase) {
    this.testCase = testCase;
  }

}
