package org.tch.ft.model;

import java.io.Serializable;

import org.tch.fc.model.EvaluationActual;

public class EvaluationActualRating implements Serializable
{
  private int evaluationActualRatingId = 0;
  private EvaluationActual evaluationActual = null;
  private ExpertRating expertRating = null;
  
  public int getEvaluationActualRatingId() {
    return evaluationActualRatingId;
  }
  public void setEvaluationActualRatingId(int evaluationActualRatingId) {
    this.evaluationActualRatingId = evaluationActualRatingId;
  }
  public EvaluationActual getEvaluationActual() {
    return evaluationActual;
  }
  public void setEvaluationActual(EvaluationActual evaluationActual) {
    this.evaluationActual = evaluationActual;
  }
  public ExpertRating getExpertRating() {
    return expertRating;
  }
  public void setExpertRating(ExpertRating expertRating) {
    this.expertRating = expertRating;
  }
}
