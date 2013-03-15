package org.tch.ft.model;

import java.io.Serializable;
import java.util.Date;

public class ForecastActual extends ForecastResult implements Serializable {
  private static final long serialVersionUID = 1L;

	private int forecastActualId = 0;
	private Software software = null;
	private String scheduleName = "";
	private Date runDate = null;
	private String logText = null;

  public String getLogText() {
    return logText;
  }

  public void setLogText(String logText) {
    this.logText = logText;
  }

  public int getForecastActualId() {
		return forecastActualId;
	}

	public void setForecastActualId(int forecastActualId) {
		this.forecastActualId = forecastActualId;
	}

	public Software getSoftware() {
		return software;
	}

	public void setSoftware(Software software) {
		this.software = software;
	}

	public String getScheduleName() {
		return scheduleName;
	}

	public void setScheduleName(String softwareVersion) {
		this.scheduleName = softwareVersion;
	}

	public Date getRunDate() {
		return runDate;
	}

	public void setRunDate(Date runDate) {
		this.runDate = runDate;
	}

}
