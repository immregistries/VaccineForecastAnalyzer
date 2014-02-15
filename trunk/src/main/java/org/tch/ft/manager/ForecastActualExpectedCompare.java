package org.tch.ft.manager;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import org.tch.fc.model.Admin;
import org.tch.fc.model.VaccineGroup;
import org.tch.fc.model.ForecastResult;
import org.tch.fc.model.TestCase;
import org.tch.ft.model.TestPanelCase;

public class ForecastActualExpectedCompare implements Serializable
{

  public static class ForecastCompareComparator implements Comparator<ForecastActualExpectedCompare>
  {

    public int compare(ForecastActualExpectedCompare c1, ForecastActualExpectedCompare c2) {
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
        return new Integer(numberA).compareTo(numberB);
      }
      return labelA.compareTo(labelB);
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
    if (forecastResultA == null || forecastResultB == null) {
      return 0;
    }
    if (same(forecastResultA.getAdmin(), forecastResultB.getAdmin())) {
      return 100;
    }
    int score = 0;
    if (same(forecastResultA.getDoseNumber(), forecastResultB.getDoseNumber())) {
      score += 40;
    }
    if (same(forecastResultA.getValidDate(), forecastResultB.getValidDate())) {
      score += 30;
    }
    if (same(forecastResultA.getDueDate(), forecastResultB.getDueDate())) {
      score += 20;
    }
    if (same(forecastResultA.getOverdueDate(), forecastResultB.getOverdueDate())) {
      score += 10;
    }
    return score;
  }

  public String getMatchStatus() {
    if (forecastResultA == null || forecastResultB == null) {
      return "Not Run";
    }
    if (matchExactly()) {
      return "Same";
    }
    return "Different";
  }

  public String getMatchSimilarity() {
    return similarity() + "%";
  }

  public boolean matchExactly() {
    if (forecastResultA == null || forecastResultB == null) {
      return false;
    }
    if (same(forecastResultA.getAdmin(), forecastResultB.getAdmin())
        && same(forecastResultA.getDoseNumber(), forecastResultB.getDoseNumber())
        && same(forecastResultA.getValidDate(), forecastResultB.getValidDate())
        && same(forecastResultA.getDueDate(), forecastResultB.getDueDate())
        && same(forecastResultA.getOverdueDate(), forecastResultB.getOverdueDate())) {
      return true;
    }
    return false;
  }

  public boolean matchExactlyExcludeOverdue() {
    if (forecastResultA == null || forecastResultB == null) {
      return false;
    }
    if (same(forecastResultA.getAdmin(), forecastResultB.getAdmin())
        && same(forecastResultA.getDoseNumber(), forecastResultB.getDoseNumber())
        && same(forecastResultA.getValidDate(), forecastResultB.getValidDate())
        && same(forecastResultA.getDueDate(), forecastResultB.getDueDate())) {
      return true;
    }
    return false;
  }

  public static boolean same(Admin a, Admin b) {
    return a != null && b != null && a == b;
  }

  public static boolean same(String a, String b) {
    if (a != null && (a.equals("-") || a.equals(""))) {
      a = null;
    }
    if (b != null && (b.equals("-") || b.equals(""))) {
      b = null;
    }
    if (a == null && b == null) {
      return true;
    }
    if (a == null || b == null) {
      return false;
    }
    return a.equals(b) || a.equals("*") || b.equals("*");
  }

  public static boolean same(Date a, Date b) {
    return (a == null && b == null) || (a != null && b != null && a.equals(b));
  }

}
