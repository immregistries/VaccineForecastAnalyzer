package org.tch.ft.model;

import java.util.List;

public class TestCaseWithExpectations extends org.tch.fc.model.TestCase {

  public TestCaseWithExpectations()
  {
    // default;
  }
  
  public TestCaseWithExpectations(TestCaseWithExpectations parentTestCase)
  {
    super(parentTestCase);
  }

  private List<ForecastExpected> forecastExpectedList = null;

  public List<ForecastExpected> getForecastExpectedList() {
    return forecastExpectedList;
  }

  public void setForecastExpectedList(List<ForecastExpected> forecastExpectedList) {
    this.forecastExpectedList = forecastExpectedList;
  }


}
