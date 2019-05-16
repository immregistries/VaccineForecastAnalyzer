package org.immregistries.vfa.model;

public class TestPanelEvaluation
{
  private int testPanelEvaluationId = 0;
  private TestPanelCase testPanelCase = null;
  private EvaluationExpected evaluationExpected = null;
  public int getTestPanelEvaluationId() {
    return testPanelEvaluationId;
  }
  public void setTestPanelEvaluationId(int testPanelEvaluationId) {
    this.testPanelEvaluationId = testPanelEvaluationId;
  }
  public TestPanelCase getTestPanelCase() {
    return testPanelCase;
  }
  public void setTestPanelCase(TestPanelCase testPanelCase) {
    this.testPanelCase = testPanelCase;
  }
  public EvaluationExpected getEvaluationExpected() {
    return evaluationExpected;
  }
  public void setEvaluationExpected(EvaluationExpected evaluationExpected) {
    this.evaluationExpected = evaluationExpected;
  }

}
