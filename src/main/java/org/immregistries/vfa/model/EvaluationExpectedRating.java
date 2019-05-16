package org.immregistries.vfa.model;

import java.io.Serializable;

public class EvaluationExpectedRating implements Serializable
{
  private int evaluationExpectedRatingId = 0;
  private EvaluationExpected evaluationExpected = null;
  private ExpertRating expertRating = null;

  public int getEvaluationExpectedRatingId() {
    return evaluationExpectedRatingId;
  }

  public void setEvaluationExpectedRatingId(int evaluationExpectedRatingId) {
    this.evaluationExpectedRatingId = evaluationExpectedRatingId;
  }

  public EvaluationExpected getEvaluationExpected() {
    return evaluationExpected;
  }

  public void setEvaluationExpected(EvaluationExpected evaluationExpected) {
    this.evaluationExpected = evaluationExpected;
  }

  public ExpertRating getExpertRating() {
    return expertRating;
  }

  public void setExpertRating(ExpertRating expertRating) {
    this.expertRating = expertRating;
  }

}
