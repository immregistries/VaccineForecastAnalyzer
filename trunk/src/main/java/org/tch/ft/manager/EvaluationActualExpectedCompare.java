package org.tch.ft.manager;

import org.tch.fc.model.EvaluationResult;
import org.tch.fc.model.ForecastResult;
import org.tch.fc.model.TestCase;
import org.tch.fc.model.VaccineGroup;
import org.tch.ft.model.TestPanelCase;

public class EvaluationActualExpectedCompare
{

  private EvaluationResult evaluationResultA = null;
  private EvaluationResult evaluationResultB = null;
  private TestCase testCase = null;
  private VaccineGroup vaccineGroup = null;
  private TestPanelCase testPanelCase = null;

  public EvaluationResult getEvaluationResultA() {
    return evaluationResultA;
  }

  public void setEvaluationResultA(EvaluationResult evaluationResultA) {
    this.evaluationResultA = evaluationResultA;
  }

  public EvaluationResult getEvaluationResultB() {
    return evaluationResultB;
  }

  public void setEvaluationResultB(EvaluationResult evaluationResultB) {
    this.evaluationResultB = evaluationResultB;
  }

  public TestCase getTestCase() {
    return testCase;
  }

  public void setTestCase(TestCase testCase) {
    this.testCase = testCase;
  }

  public VaccineGroup getVaccineGroup() {
    return vaccineGroup;
  }

  public void setVaccineGroup(VaccineGroup vaccineGroup) {
    this.vaccineGroup = vaccineGroup;
  }

  public TestPanelCase getTestPanelCase() {
    return testPanelCase;
  }

  public void setTestPanelCase(TestPanelCase testPanelCase) {
    this.testPanelCase = testPanelCase;
  }

}
