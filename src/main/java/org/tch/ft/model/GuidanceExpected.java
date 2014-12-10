package org.tch.ft.model;

import java.io.Serializable;
import java.util.Date;

import org.tch.fc.model.Guidance;
import org.tch.fc.model.RelativeRule;
import org.tch.fc.model.TestCase;

public class GuidanceExpected implements Serializable
{
  private static final long serialVersionUID = 1L;

  private int guidanceExpectedId = 0;
  private Guidance guidance = null;
  private TestCase testCase = null;
  private User author = null;
  private Date updatedDate = null;
  private RelativeRule effectiveRule = null;
  private RelativeRule expirationRule = null;

  public int getGuidanceExpectedId() {
    return guidanceExpectedId;
  }

  public void setGuidanceExpectedId(int guidanceExpectedId) {
    this.guidanceExpectedId = guidanceExpectedId;
  }

  public Guidance getGuidance() {
    return guidance;
  }

  public void setGuidance(Guidance guidance) {
    this.guidance = guidance;
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

  public RelativeRule getEffectiveRule() {
    return effectiveRule;
  }

  public void setEffectiveRule(RelativeRule effectiveRule) {
    this.effectiveRule = effectiveRule;
  }

  public RelativeRule getExpirationRule() {
    return expirationRule;
  }

  public void setExpirationRule(RelativeRule expirationRule) {
    this.expirationRule = expirationRule;
  }

}
