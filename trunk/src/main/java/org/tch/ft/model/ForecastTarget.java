package org.tch.ft.model;

import java.io.Serializable;

public class ForecastTarget implements Serializable {

  /*
   * CREATE TABLE forecast_target (
  forecast_target_id   INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  forecast_compare_id  INTEGER NOT NULL,
  forecast_actual_id   INTEGER NOT NULL
);
   */
  private int forecastTargetId = 0;
  private ForecastCompare forecastCompare = null;
  private ForecastActual forecastActual = null;
  
  public int getForecastTargetId() {
    return forecastTargetId;
  }
  public void setForecastTargetId(int forecastTargetId) {
    this.forecastTargetId = forecastTargetId;
  }
  public ForecastCompare getForecastCompare() {
    return forecastCompare;
  }
  public void setForecastCompare(ForecastCompare forecastCompare) {
    this.forecastCompare = forecastCompare;
  }
  public ForecastActual getForecastActual() {
    return forecastActual;
  }
  public void setForecastActual(ForecastActual forecastActual) {
    this.forecastActual = forecastActual;
  }
  
}
