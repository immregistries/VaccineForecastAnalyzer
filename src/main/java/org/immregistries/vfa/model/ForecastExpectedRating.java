package org.immregistries.vfa.model;

import java.io.Serializable;

public class ForecastExpectedRating implements Serializable
{
  private int forecastExpectedRatingId = 0;
  private ForecastExpected forecastExpected = null;
  private ExpertRating expertRating = null;

  public int getForecastExpectedRatingId() {
    return forecastExpectedRatingId;
  }

  public void setForecastExpectedRatingId(int forecastExpectedRatingId) {
    this.forecastExpectedRatingId = forecastExpectedRatingId;
  }

  public ForecastExpected getForecastExpected() {
    return forecastExpected;
  }

  public void setForecastExpected(ForecastExpected forecastExpected) {
    this.forecastExpected = forecastExpected;
  }

  public ExpertRating getExpertRating() {
    return expertRating;
  }

  public void setExpertRating(ExpertRating expertRating) {
    this.expertRating = expertRating;
  }

}
