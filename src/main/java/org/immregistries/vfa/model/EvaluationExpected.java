package org.immregistries.vfa.model;

import java.io.Serializable;
import java.util.Date;

import org.immregistries.vfa.connect.model.EvaluationResult;
import org.immregistries.vfa.connect.model.TestCase;
import org.immregistries.vfa.connect.model.TestEvent;

public class EvaluationExpected extends EvaluationResult implements Serializable
{
  private int evaluationExpectedId = 0;
  private TestCase testCase = null;
  private User author = null;
  private Date updatedDate = null;
  private TestEvent testEvent = null;
  
  public int getEvaluationExpectedId() {
    return evaluationExpectedId;
  }
  public void setEvaluationExpectedId(int evaluationExpectedId) {
    this.evaluationExpectedId = evaluationExpectedId;
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
  public Date getUpdatedDate() {
    return updatedDate;
  }
  public void setUpdatedDate(Date updatedDate) {
    this.updatedDate = updatedDate;
  }
  public TestEvent getTestEvent() {
    return testEvent;
  }
  public void setTestEvent(TestEvent testEvent) {
    this.testEvent = testEvent;
  }
}
