package org.tch.ft.model;

import java.io.Serializable;

import org.tch.fc.model.ForecastItem;

public class ForecastCvx implements Serializable
{
  private int forecastCvxId = 0;
  private ForecastItem forecastItem = null;
  private String vaccineCvx = "";

  public int getForecastCvxId() {
    return forecastCvxId;
  }

  public void setForecastCvxId(int forecastCvxId) {
    this.forecastCvxId = forecastCvxId;
  }

  public ForecastItem getForecastItem() {
    return forecastItem;
  }

  public void setForecastItem(ForecastItem forecastItem) {
    this.forecastItem = forecastItem;
  }

  public String getVaccineCvx() {
    return vaccineCvx;
  }

  public void setVaccineCvx(String vaccineCvx) {
    this.vaccineCvx = vaccineCvx;
  }

}
