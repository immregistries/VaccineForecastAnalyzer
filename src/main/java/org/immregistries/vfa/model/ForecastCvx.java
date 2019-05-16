package org.immregistries.vfa.model;

import java.io.Serializable;

import org.immregistries.vfa.connect.model.VaccineGroup;

public class ForecastCvx implements Serializable
{
  private int forecastCvxId = 0;
  private VaccineGroup vaccineGroup = null;
  private String vaccineCvx = "";

  public int getForecastCvxId() {
    return forecastCvxId;
  }

  public void setForecastCvxId(int forecastCvxId) {
    this.forecastCvxId = forecastCvxId;
  }

  public VaccineGroup getVaccineGroup() {
    return vaccineGroup;
  }

  public void setVaccineGroup(VaccineGroup vaccineGroup) {
    this.vaccineGroup = vaccineGroup;
  }

  public String getVaccineCvx() {
    return vaccineCvx;
  }

  public void setVaccineCvx(String vaccineCvx) {
    this.vaccineCvx = vaccineCvx;
  }

}
