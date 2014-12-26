package org.tch.ft.manager;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import org.tch.fc.model.Admin;
import org.tch.fc.model.ForecastResult;
import org.tch.fc.model.TestCase;
import org.tch.fc.model.VaccineGroup;
import org.tch.ft.model.TestPanelCase;

public class ForecastActualExpectedCompare implements Serializable
{

  public static class CompareCriteria
  {
    private boolean verifyEvaluationStatus = false;
    private boolean verifyForecastStatus = true;
    private boolean verifyForecastDose = true;
    private boolean verifyForecastValidDate = true;
    private boolean verifyForecastDueDate = true;
    private boolean verifyForecastOverdueDate = true;
    private boolean verifyForecastFinishedDate = false;

    public boolean isVerifyEvaluationStatus() {
      return verifyEvaluationStatus;
    }

    public void setVerifyEvaluationStatus(boolean verifyEvaluation) {
      this.verifyEvaluationStatus = verifyEvaluation;
    }

    public boolean isVerifyForecastStatus() {
      return verifyForecastStatus;
    }

    public void setVerifyForecastStatus(boolean verifyStatus) {
      this.verifyForecastStatus = verifyStatus;
    }

    public boolean isVerifyForecastDose() {
      return verifyForecastDose;
    }

    public void setVerifyForecastDose(boolean verifyDose) {
      this.verifyForecastDose = verifyDose;
    }

    public boolean isVerifyForecastValidDate() {
      return verifyForecastValidDate;
    }

    public void setVerifyForecastValidDate(boolean verifyValidDate) {
      this.verifyForecastValidDate = verifyValidDate;
    }

    public boolean isVerifyForecastDueDate() {
      return verifyForecastDueDate;
    }

    public void setVerifyForecastDueDate(boolean verifyDueDate) {
      this.verifyForecastDueDate = verifyDueDate;
    }

    public boolean isVerifyForecastOverdueDate() {
      return verifyForecastOverdueDate;
    }

    public void setVerifyForecastOverdueDate(boolean verifyOverdueDate) {
      this.verifyForecastOverdueDate = verifyOverdueDate;
    }

    public boolean isVerifyForecastFinishedDate() {
      return verifyForecastFinishedDate;
    }

    public void setVerifyForecastFinishedDate(boolean verifyFinishedDate) {
      this.verifyForecastFinishedDate = verifyFinishedDate;
    }
  }

  public static class ForecastCompareComparator implements Comparator<ForecastActualExpectedCompare>
  {

    public int compare(ForecastActualExpectedCompare c1, ForecastActualExpectedCompare c2) {
      String categoryA = c1.getTestPanelCase().getCategoryName();
      String categoryB = c2.getTestPanelCase().getCategoryName();
      if (!categoryA.equals(categoryB)) {
        return categoryA.compareTo(categoryB);
      } else {
        String labelA = c1.getForecastResultA().getTestCase().getLabel().trim().toUpperCase();
        String labelB = c2.getForecastResultA().getTestCase().getLabel().trim().toUpperCase();
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
          if (numberA == numberB) {
            return new Integer(c1.getTestPanelCase().getTestPanelCaseId()).compareTo(c2.getTestPanelCase()
                .getTestPanelCaseId());
          }
          return new Integer(numberA).compareTo(numberB);
        }
        return labelA.compareTo(labelB);
      }
    }
  }

  public enum RunStatus {
    QUERIED, ERRORED
  }

  private Throwable runException = null;
  private RunStatus runStatus = null;

  public Throwable getRunException() {
    return runException;
  }

  public void setRunException(Throwable runException) {
    this.runException = runException;
  }

  public RunStatus getRunStatus() {
    return runStatus;
  }

  public void setRunStatus(RunStatus runStatus) {
    this.runStatus = runStatus;
  }

  private ForecastResult forecastResultA = null;
  private ForecastResult forecastResultB = null;
  private TestCase testCase = null;
  private VaccineGroup vaccineGroup = null;
  private TestPanelCase testPanelCase = null;
  private CompareCriteria compareCriteria = new CompareCriteria();
  private Boolean matchExactly = null;
  private Integer simularity = null;

  public CompareCriteria getCompareCriteria() {
    return compareCriteria;
  }

  public void setCompareCriteria(CompareCriteria compareCriteria) {
    this.compareCriteria = compareCriteria;
  }

  public TestPanelCase getTestPanelCase() {
    return testPanelCase;
  }

  public void setTestPanelCase(TestPanelCase testPanelCase) {
    this.testPanelCase = testPanelCase;
  }

  public VaccineGroup getVaccineGroup() {
    return vaccineGroup;
  }

  public void setVaccineGroup(VaccineGroup vaccineGroup) {
    this.vaccineGroup = vaccineGroup;
  }

  public TestCase getTestCase() {
    return testCase;
  }

  public void setTestCase(TestCase testCase) {
    this.testCase = testCase;
  }

  public ForecastResult getForecastResultA() {
    return forecastResultA;
  }

  public void setForecastResultA(ForecastResult forecastResultA) {
    this.forecastResultA = forecastResultA;
  }

  public ForecastResult getForecastResultB() {
    return forecastResultB;
  }

  public void setForecastResultB(ForecastResult forecastResultB) {
    this.forecastResultB = forecastResultB;
  }

  public int similarity() {
    return similarity(compareCriteria);
  }

  public int similarity(CompareCriteria compareCriteria) {
    if (simularity != null) {
      return simularity;
    }
    if (forecastResultA == null || forecastResultB == null) {
      return 0;
    }
    int numerator = 0;
    int denominator = 0;
    if (compareCriteria.verifyForecastStatus) {
      if (same(forecastResultA.getAdmin(), forecastResultB.getAdmin())) {
        numerator += 27;
      }
      denominator += 27;
    }
    if (compareCriteria.verifyForecastDose) {
      if (same(forecastResultA.getDoseNumber(), forecastResultB.getDoseNumber())) {
        numerator += 33;
      }
      denominator += 33;
    }
    if (compareCriteria.verifyForecastValidDate) {
      if (same(forecastResultA.getValidDate(), forecastResultB.getValidDate())) {
        numerator += 14;
      }
      denominator += 14;
    }
    if (compareCriteria.verifyForecastDueDate) {
      if (same(forecastResultA.getDueDate(), forecastResultB.getDueDate())) {
        numerator += 21;
      }
      denominator += 21;
    }
    if (compareCriteria.verifyForecastOverdueDate) {
      if (same(forecastResultA.getOverdueDate(), forecastResultB.getOverdueDate())) {
        numerator += 5;
      }
      denominator += 5;
    }
    if (compareCriteria.verifyForecastFinishedDate) {
      if (same(forecastResultA.getFinishedDate(), forecastResultB.getFinishedDate())) {
        numerator += 5;
      }
      denominator += 5;
    }
    simularity = (int) (100.0 * numerator / denominator); // not rounding up, so 100% means exactly that
    return simularity;
  }

  public String getMatchStatus() {
    return getMatchStatus(compareCriteria);
  }

  public String getMatchStatus(CompareCriteria compareCriteria) {
    if (forecastResultA == null || forecastResultB == null) {
      return "Not Run";
    }
    if (matchExactly(compareCriteria)) {
      return "Same";
    }
    return "Different";
  }

  public String getMatchSimilarity(CompareCriteria compareCriteria) {
    return similarity() + "%";
  }

  public String getMatchSimilarity() {
    return similarity() + "%";
  }

  public String getMatchDifference(CompareCriteria compareCriteria) {
    return (100 - similarity()) + "%";
  }

  public String getMatchDifference() {
    return (100 - similarity()) + "%";
  }

  public boolean matchExactly() {
    return matchExactly(compareCriteria);
  }

  public boolean matchExactly(CompareCriteria compareCriteria) {
    if (matchExactly != null) {
      return matchExactly;
    }
    if (forecastResultA == null || forecastResultB == null) {
      matchExactly = false;
      return false;
    }
    if (compareCriteria.isVerifyForecastStatus()) {
      if (!same(forecastResultA.getAdmin(), forecastResultB.getAdmin())) {
        matchExactly = false;
        return false;
      }
    }
    if (compareCriteria.isVerifyForecastDose()) {
      if (!same(forecastResultA.getDoseNumber(), forecastResultB.getDoseNumber())) {
        matchExactly = false;
        return false;
      }
    }
    if (compareCriteria.isVerifyForecastValidDate()) {
      if (!same(forecastResultA.getValidDate(), forecastResultB.getValidDate())) {
        matchExactly = false;
        return false;
      }
    }
    if (compareCriteria.isVerifyForecastDueDate()) {
      if (!same(forecastResultA.getDueDate(), forecastResultB.getDueDate())) {
        matchExactly = false;
        return false;
      }
    }
    if (compareCriteria.isVerifyForecastOverdueDate()) {
      if (!same(forecastResultA.getOverdueDate(), forecastResultB.getOverdueDate())) {
        matchExactly = false;
        return false;
      }
    }
    if (compareCriteria.isVerifyForecastFinishedDate()) {
      if (!same(forecastResultA.getFinishedDate(), forecastResultB.getFinishedDate())) {
        matchExactly = false;
        return false;
      }
    }
    matchExactly = true;
    return true;
  }

  public static boolean same(Admin a, Admin b) {
    if (a == null || b == null) {
      return false;
    }
    if (a == Admin.NOT_COMPLETE) {
      return b == Admin.NOT_COMPLETE || b == Admin.DUE || b == Admin.DUE_LATER || b == Admin.OVERDUE;
    }
    return a == b;
  }

  public static boolean same(String a, String b) {
    if (a == null || a.equals("-")) {
      a = "";
    }
    if (b == null || b.equals("-")) {
      b = "";
    }
    return a.equalsIgnoreCase(b) || a.equals("*") || b.equals("*");
  }

  public static boolean same(Date a, Date b) {
    return (a == null && b == null) || (a != null && b != null && a.equals(b));
  }

}
