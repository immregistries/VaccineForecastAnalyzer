package org.immregistries.vfa.model;

import java.io.Serializable;
import java.util.Date;

import org.immregistries.vfa.connect.model.ForecastResult;
import org.immregistries.vfa.connect.model.RelativeRule;
import org.immregistries.vfa.connect.model.TestCase;

public class ForecastExpected extends ForecastResult implements Serializable {
  private static final long serialVersionUID = 1L;

	private int forecastExpectedId = 0;
	private TestCase testCase = null;
	private User author = null;
	private Date updatedDate = null;
	private RelativeRule validRule = null;
	private RelativeRule dueRule = null;
	private RelativeRule overdueRule = null;
	private RelativeRule finishedRule = null;

  public RelativeRule getValidRule() {
    return validRule;
  }

  public void setValidRule(RelativeRule validRule) {
    this.validRule = validRule;
  }

  public RelativeRule getDueRule() {
    return dueRule;
  }

  public void setDueRule(RelativeRule dueRule) {
    this.dueRule = dueRule;
  }

  public RelativeRule getOverdueRule() {
    return overdueRule;
  }

  public void setOverdueRule(RelativeRule overdueRule) {
    this.overdueRule = overdueRule;
  }

  public RelativeRule getFinishedRule() {
    return finishedRule;
  }

  public void setFinishedRule(RelativeRule finishedRule) {
    this.finishedRule = finishedRule;
  }

  public Date getUpdatedDate() {
    return updatedDate;
  }

  public void setUpdatedDate(Date updatedDate) {
    this.updatedDate = updatedDate;
  }

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
