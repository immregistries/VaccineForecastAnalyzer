package org.tch.ft.model;

import java.io.Serializable;

import org.tch.fc.model.EvaluationActual;
import org.tch.fc.model.ForecastActual;

public class ForecastActualRating implements Serializable
{
  private int forecastActualRatingId = 0;
  private ForecastActual forecastActual = null;
  private ExpertRating expertRating = null;

  public ForecastActual getForecastActual() {
    return forecastActual;
  }
  
  public void setForecastActual(ForecastActual forecastActual) {
    this.forecastActual = forecastActual;
  }
  
  public int getForecastActualRatingId() {
    return forecastActualRatingId;
  }

  public void setForecastActualRatingId(int forecastActualRatingId) {
    this.forecastActualRatingId = forecastActualRatingId;
  }

  public ExpertRating getExpertRating() {
    return expertRating;
  }

  public void setExpertRating(ExpertRating expertRating) {
    this.expertRating = expertRating;
  }
}
