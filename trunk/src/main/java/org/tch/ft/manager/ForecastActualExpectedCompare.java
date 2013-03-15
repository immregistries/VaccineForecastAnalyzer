package org.tch.ft.manager;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import org.tch.ft.model.ForecastItem;
import org.tch.ft.model.ForecastResult;
import org.tch.ft.model.TestCase;

public class ForecastActualExpectedCompare implements Serializable {

  public static class ForecastCompareComparator implements Comparator<ForecastActualExpectedCompare> {

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
  private ForecastItem forecastItem = null;

  public ForecastItem getForecastItem() {
    return forecastItem;
  }

  public void setForecastItem(ForecastItem forecastItem) {
    this.forecastItem = forecastItem;
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
    if (forecastResultA.isComplete() && forecastResultB.isComplete()) {
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
    if (forecastResultA.isComplete() && forecastResultB.isComplete()) {
      return true;
    }
    if (same(forecastResultA.getDoseNumber(), forecastResultB.getDoseNumber())
        && same(forecastResultA.getValidDate(), forecastResultB.getValidDate())
        && same(forecastResultA.getDueDate(), forecastResultB.getDueDate())
        && same(forecastResultA.getOverdueDate(), forecastResultB.getOverdueDate())) {
      return true;
    }
    return false;
  }

  public boolean matchExactlyExlcudeOverdue() {
    if (forecastResultA == null || forecastResultB == null) {
      return false;
    }
    if (forecastResultA.isComplete() && forecastResultB.isComplete()) {
      return true;
    }
    if (same(forecastResultA.getDoseNumber(), forecastResultB.getDoseNumber())
        && same(forecastResultA.getValidDate(), forecastResultB.getValidDate())
        && same(forecastResultA.getDueDate(), forecastResultB.getDueDate())) {
      return true;
    }
    return false;
  }

  public static boolean same(String a, String b) {
    return (a == null && b == null)
        || (a != null && b != null && a.equals(b) || (a != null && b != null && (a.equals("*") || b.equals("*"))));
  }

  public static boolean same(Date a, Date b) {
    return (a == null && b == null) || (a != null && b != null && a.equals(b));
  }

}
