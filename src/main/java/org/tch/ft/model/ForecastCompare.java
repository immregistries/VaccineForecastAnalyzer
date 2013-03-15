package org.tch.ft.model;

import java.io.Serializable;

public class ForecastCompare implements Serializable{
  /*
   * CREATE TABLE forecast_compare (
  forecast_compare_id  INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  software_compare_id  INTEGER NOT NULL,
  forecast_actual_id   INTEGER NOT NULL,
  compare_label        VARCHAR(120) NOT NULL,
  result_status        VARCHAR(1)
);
   */
  private int forecastCompareId = 0;
  private SoftwareCompare softwareCompare = null;
  private ForecastActual forecastActual = null;
  private String compareLabel = "";
  private Result result = null;
  
  public int getForecastCompareId() {
    return forecastCompareId;
  }
  public void setForecastCompareId(int forecastCompareId) {
    this.forecastCompareId = forecastCompareId;
  }
  public SoftwareCompare getSoftwareCompare() {
    return softwareCompare;
  }
  public void setSoftwareCompare(SoftwareCompare softwareCompare) {
    this.softwareCompare = softwareCompare;
  }
  public ForecastActual getForecastActual() {
    return forecastActual;
  }
  public void setForecastActual(ForecastActual forecastActual) {
    this.forecastActual = forecastActual;
  }
  public String getCompareLabel() {
    return compareLabel;
  }
  public void setCompareLabel(String compareLabel) {
    this.compareLabel = compareLabel;
  }
  public Result getResult() {
    return result;
  }
  public void setResult(Result result) {
    this.result = result;
  }
  public String getResultStatus() {
    return result == null ? null : result.getResultStatus();
  }

  public void setResultStatus(String resultStatus) {
    result = Result.getResult(resultStatus);
  }

}
