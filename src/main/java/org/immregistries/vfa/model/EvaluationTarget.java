package org.immregistries.vfa.model;

import org.immregistries.vfa.connect.model.EvaluationActual;

public class EvaluationTarget
{

  private int evaluationTargetId = 0;
  private EvaluationCompare evaluationCompare = null;
  private EvaluationActual evaluationActual = null;

  public int getEvaluationTargetId() {
    return evaluationTargetId;
  }

  public void setEvaluationTargetId(int evaluationTargetId) {
    this.evaluationTargetId = evaluationTargetId;
  }

  public EvaluationCompare getEvaluationCompare() {
    return evaluationCompare;
  }

  public void setEvaluationCompare(EvaluationCompare evaluationCompare) {
    this.evaluationCompare = evaluationCompare;
  }

  public EvaluationActual getEvaluationActual() {
    return evaluationActual;
  }

  public void setEvaluationActual(EvaluationActual evaluationActual) {
    this.evaluationActual = evaluationActual;
  }
}
