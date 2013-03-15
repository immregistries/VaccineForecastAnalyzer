package org.tch.ft.connect;

import java.util.List;

import org.tch.ft.model.ForecastActual;
import org.tch.ft.model.TestCase;

public interface ConnectorInterface {
  /**
   * Given a test case, query the software to receive back a list of Forecast Actual results. 
   * @param forecastActualList
   * @return
   */
  public List<ForecastActual> queryForForecast(TestCase testCase) throws Exception;
}
