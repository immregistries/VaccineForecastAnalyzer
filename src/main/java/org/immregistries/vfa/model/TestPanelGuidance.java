package org.immregistries.vfa.model;

import java.io.Serializable;

public class TestPanelGuidance implements Serializable
{

  private static final long serialVersionUID = 1L;

  private int testPanelGuidanceId = 0;
  private TestPanelCase testPanelCase = null;
  private GuidanceExpected guidanceExpected = null;

  public int getTestPanelGuidanceId() {
    return testPanelGuidanceId;
  }

  public void setTestPanelGuidanceId(int testPanelGuidanceId) {
    this.testPanelGuidanceId = testPanelGuidanceId;
  }

  public TestPanelCase getTestPanelCase() {
    return testPanelCase;
  }

  public void setTestPanelCase(TestPanelCase testPanelCase) {
    this.testPanelCase = testPanelCase;
  }

  public GuidanceExpected getGuidanceExpected() {
    return guidanceExpected;
  }

  public void setGuidanceExpected(GuidanceExpected guidanceExpected) {
    this.guidanceExpected = guidanceExpected;
  }

}
