package org.immregistries.vfa.manager;

import org.immregistries.vfa.connect.model.EvaluationResult;
import org.immregistries.vfa.connect.model.ForecastResult;
import org.immregistries.vfa.connect.model.TestCase;
import org.immregistries.vfa.connect.model.VaccineGroup;
import org.immregistries.vfa.model.TestPanelCase;

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
