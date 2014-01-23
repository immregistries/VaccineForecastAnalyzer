package org.tch.ft.model;

import org.tch.fc.model.EvaluationActual;

public class EvaluationCompare
{
  private int evaluationCompareId = 0;
  private SoftwareCompare softwareCompare = null;
  private EvaluationActual evaluationActual = null;
  private String compareLabel = "";
  private Result result = null;

  public int getEvaluationCompareId() {
    return evaluationCompareId;
  }

  public void setEvaluationCompareId(int evaluationCompareId) {
    this.evaluationCompareId = evaluationCompareId;
  }

  public SoftwareCompare getSoftwareCompare() {
    return softwareCompare;
  }

  public void setSoftwareCompare(SoftwareCompare softwareCompare) {
    this.softwareCompare = softwareCompare;
  }

  public EvaluationActual getEvaluationActual() {
    return evaluationActual;
  }

  public void setEvaluationActual(EvaluationActual evaluationActual) {
    this.evaluationActual = evaluationActual;
  }

  public String getCompareLabel() {
    return compareLabel;
  }

  public void setCompareLabel(String compareLabel) {
    this.compareLabel = compareLabel;
  }

  public Result getResult() {
    return result;
  }

  public void setResult(Result result) {
    this.result = result;
  }

  public String getResultStatus() {
    return result == null ? null : result.getResultStatus();
  }

  public void setResultStatus(String resultStatus) {
    result = Result.getResult(resultStatus);
  }
}
