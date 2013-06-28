package org.tch.ft.model;

import java.io.Serializable;
import java.util.Comparator;

import org.tch.fc.model.TestCase;

public class TestPanelCase implements Serializable {

  public static class TestPanelCaseComparator implements Comparator<TestPanelCase> {

    public int compare(TestPanelCase c1, TestPanelCase c2) {
      if (c1.getCategoryName().equals(c2.getCategoryName())) {
        String labelA = c1.getTestCase().getLabel().trim().toUpperCase();
        String labelB = c2.getTestCase().getLabel().trim().toUpperCase();

        int posA = labelA.lastIndexOf(" ");
        int numberA = 0;
        if (posA != -1 && posA < labelA.length()) {
          try {
            numberA = Integer.parseInt(labelA.substring(posA).trim());
          } catch (NumberFormatException nfe) {
            numberA = 0;
          }
          if (numberA > 0) {
            labelA = labelA.substring(0, posA).trim();
          }
        }
        int posB = labelB.lastIndexOf(" ");
        int numberB = 0;
        if (posB != -1 && posB < labelB.length()) {
          try {
            numberB = Integer.parseInt(labelB.substring(posB).trim());
          } catch (NumberFormatException nfe) {
            numberB = 0;
          }
          if (numberB > 0) {
            labelB = labelB.substring(0, posB).trim();
          }
        }
        if (labelA.equals(labelB)) {
          return new Integer(numberA).compareTo(numberB);
        }
        return labelA.compareTo(labelB);
      } else {
        return c1.getCategoryName().compareTo(c2.getCategoryName());
      }
    }
  }

  private static final long serialVersionUID = 1L;

  private int testPanelCaseId = 0;
  private TestPanel testPanel = null;
  private TestCase testCase = null;
  private String categoryName = null;
  private Include include = null;
  private Result result = null;
  private String testCaseNumber = "";

  public String getTestCaseNumber() {
    return testCaseNumber;
  }

  public void setTestCaseNumber(String testCaseNumber) {
    this.testCaseNumber = testCaseNumber;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public int getTestPanelCaseId() {
    return testPanelCaseId;
  }

  public void setTestPanelCaseId(int testPanelCaseId) {
    this.testPanelCaseId = testPanelCaseId;
  }

  public TestPanel getTestPanel() {
    return testPanel;
  }

  public void setTestPanel(TestPanel testPanel) {
    this.testPanel = testPanel;
  }

  public TestCase getTestCase() {
    return testCase;
  }

  public void setTestCase(TestCase testCase) {
    this.testCase = testCase;
  }

  public Include getInclude() {
    return include;
  }

  public void setInclude(Include include) {
    this.include = include;
  }

  public Result getResult() {
    return result;
  }

  public void setResult(Result result) {
    this.result = result;
  }

  public String getIncludeStatus() {
    return include == null ? null : include.getIncludeStatus();
  }

  public void setIncludeStatus(String includeStatus) {
    include = Include.getInclude(includeStatus);
  }

  public String getResultStatus() {
    return result == null ? null : result.getResultStatus();
  }

  public void setResultStatus(String resultStatus) {
    result = Result.getResult(resultStatus);
  }
}
