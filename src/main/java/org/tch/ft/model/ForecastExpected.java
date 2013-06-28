package org.tch.ft.model;

import java.io.Serializable;

import org.tch.fc.model.ForecastResult;
import org.tch.fc.model.TestCase;

public class ForecastExpected extends ForecastResult implements Serializable {
  private static final long serialVersionUID = 1L;

	private int forecastExpectedId = 0;
	private TestCase testCase = null;
	private User author = null;

	public int getForecastExpectedId() {
		return forecastExpectedId;
	}

	public void setForecastExpectedId(int forecastExpectedId) {
		this.forecastExpectedId = forecastExpectedId;
	}

	public TestCase getTestCase() {
		return testCase;
	}

	public void setTestCase(TestCase testCase) {
		this.testCase = testCase;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

}
