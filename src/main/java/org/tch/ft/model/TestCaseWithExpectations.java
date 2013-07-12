package org.tch.ft.model;

import java.io.Serializable;
import java.util.List;

import org.tch.fc.model.TestCase;

public class TestCaseWithExpectations implements Serializable {

  private TestCase testCase = new TestCase();
  
  public TestCaseWithExpectations()
  {
    this.testCase = new TestCase();
  }
  
  public TestCaseWithExpectations(TestCase testCase)
  {
    this.testCase = new TestCase(testCase);
    
  }

  public TestCase getTestCase() {
    return testCase;
  }

  public void setTestCase(TestCase testCase) {
    this.testCase = testCase;
  }

  private List<ForecastExpected> forecastExpectedList = null;

  public List<ForecastExpected> getForecastExpectedList() {
    return forecastExpectedList;
  }

  public void setForecastExpectedList(List<ForecastExpected> forecastExpectedList) {
    this.forecastExpectedList = forecastExpectedList;
  }

}
