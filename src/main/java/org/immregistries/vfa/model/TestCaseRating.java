package org.immregistries.vfa.model;

import java.io.Serializable;

import org.immregistries.vfa.connect.model.TestCase;

public class TestCaseRating implements Serializable
{
  private int testCaseRatingId = 0;
  private ExpertRating expertRating = null;
  private TestCase testCase = null;

  public int getTestCaseRatingId() {
    return testCaseRatingId;
  }

  public void setTestCaseRatingId(int testCaseRatingId) {
    this.testCaseRatingId = testCaseRatingId;
  }

  public ExpertRating getExpertRating() {
    return expertRating;
  }

  public void setExpertRating(ExpertRating expertRating) {
    this.expertRating = expertRating;
  }

  public TestCase getTestCase() {
    return testCase;
  }

  public void setTestCase(TestCase testCase) {
    this.testCase = testCase;
  }

}
