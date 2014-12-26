package org.tch.ft.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.tch.fc.model.ForecastActual;
import org.tch.fc.model.Software;
import org.tch.ft.CentralControl;
import org.tch.ft.manager.ForecastActualExpectedCompare.CompareCriteria;
import org.tch.ft.model.ForecastCompare;
import org.tch.ft.model.ForecastTarget;
import org.tch.ft.model.SoftwareCompare;
import org.tch.ft.model.SoftwareTarget;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.TestPanelCase;
import org.tch.ft.web.softwareCompare.CompareResults;

public class SoftwareCompareManager extends Thread
{

  private Map<String, List<ForecastCompare>> compareResultMap = new HashMap<String, List<ForecastCompare>>();
  private TestPanel testPanel = null;
  private ArrayList<Software> compareSoftwareSelect;
  private Software software = null;

  public SoftwareCompareManager(TestPanel testPanel, Software software, ArrayList<Software> compareSoftwareSelect) {
    this.testPanel = testPanel;
    this.software = software;
    this.compareSoftwareSelect = compareSoftwareSelect;
  }

  private String status = "Initialized";
  private boolean finished = false;
  private Exception exception = null;

  public Map<String, List<ForecastCompare>> getCompareResultMap() {
    return compareResultMap;
  }

  public Exception getException() {
    return exception;
  }

  public void setException(Exception exception) {
    this.exception = exception;
  }

  public boolean isFinished() {
    return finished;
  }

  public void setFinished(boolean finished) {
    this.finished = finished;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public void run() {
    SessionFactory factory = CentralControl.getSessionFactory();
    Session dataSession = factory.openSession();
    try {
      status = "Creating software comparison";

      Transaction trans = dataSession.beginTransaction();
      SoftwareCompare softwareCompare = new SoftwareCompare();
      try {

        softwareCompare.setSoftware(software);
        softwareCompare.setTestPanel(testPanel);
        dataSession.save(softwareCompare);
        for (Software compareSoftware : compareSoftwareSelect) {
          SoftwareTarget softwareTarget = new SoftwareTarget();
          softwareTarget.setSoftwareCompare(softwareCompare);
          softwareTarget.setSoftware(compareSoftware);
          dataSession.save(softwareTarget);
        }
      } finally {
        trans.commit();
      }

      trans = dataSession.beginTransaction();

      Query query = dataSession.createQuery("from TestPanelCase where testPanel = ?");
      query.setParameter(0, testPanel);
      List<TestPanelCase> testPanelCaseList = query.list();
      int count = 0;
      for (TestPanelCase testPanelCase : testPanelCaseList) {
        count++;
        status = "Looking at test panel " + count + " of " + testPanelCaseList.size();
        query = dataSession
            .createQuery("from ForecastActual where softwareResult.software = ? and softwareResult.testCase = ? order by softwareResult.runDate desc");
        query.setParameter(0, software);
        query.setParameter(1, testPanelCase.getTestCase());
        List<ForecastActual> forecastActualList = query.list();
        if (forecastActualList.size() != 0) {
          for (ForecastActual forecastActual : forecastActualList) {
            ForecastCompare forecastCompare = new ForecastCompare();
            forecastCompare.setSoftwareCompare(softwareCompare);
            forecastCompare.setForecastActual(forecastActual);

            boolean allFound = compareSoftwareSelect.size() > 0;
            List<ForecastActual> compareWithList = new ArrayList<ForecastActual>();
            List<ForecastActualExpectedCompare> forecastActualExpectedCompareList = new ArrayList<ForecastActualExpectedCompare>();
            String compareLabel = "";
            for (Software compareSoftware : compareSoftwareSelect) {
              query = dataSession
                  .createQuery("from ForecastActual where softwareResult.software = ? and softwareResult.testCase = ? and vaccineGroup = ? order by softwareResult.runDate desc");
              query.setParameter(0, compareSoftware);
              query.setParameter(1, testPanelCase.getTestCase());
              query.setParameter(2, forecastActual.getVaccineGroup());
              List<ForecastActual> forecastActualCompareList = query.list();
              if (forecastActualCompareList.size() == 0) {
                allFound = false;
              } else {
                ForecastTarget forecastTarget = new ForecastTarget();
                forecastTarget.setForecastCompare(forecastCompare);
                forecastTarget.setForecastActual(forecastActual);

                compareWithList.add(forecastActualCompareList.get(0));
                ForecastActualExpectedCompare forecastActualExcpectedCompare = new ForecastActualExpectedCompare();
                forecastActualExcpectedCompare.setTestCase(testPanelCase.getTestCase());
                forecastActualExcpectedCompare.setForecastResultA(forecastActual);
                forecastActualExcpectedCompare.setForecastResultB(forecastActualCompareList.get(0));
                forecastActualExcpectedCompare.setVaccineGroup(forecastActual.getVaccineGroup());
                forecastActualExpectedCompareList.add(forecastActualExcpectedCompare);
              }
            }
            if (!allFound) {
              compareLabel = "X: Not all forecasts run";
            } else {
              boolean matchAll = true;
              int matchCount = 0;
              ForecastActualExpectedCompare.CompareCriteria compareCriteria = new ForecastActualExpectedCompare.CompareCriteria();
              compareCriteria.setVerifyForecastOverdueDate(false);
              for (ForecastActualExpectedCompare forecastActualExcpectedCompare : forecastActualExpectedCompareList) {
                if (forecastActualExcpectedCompare.matchExactly(compareCriteria)) {
                  matchCount++;
                } else {
                  matchAll = false;
                }
              }
              if (matchAll) {
                compareLabel = "A: Same as all others";
              } else if (matchCount > 0) {
                compareLabel = "B: Same as at least " + matchCount + " other" + (matchCount == 1 ? "" : "s");
              } else {
                boolean completelyDisagree = true;
                boolean completelyAgree = true;
                for (int i = 1; i < compareWithList.size(); i++) {
                  for (int j = 0; j < i; j++) {
                    ForecastActualExpectedCompare forecastActualExcpectedCompare = new ForecastActualExpectedCompare();
                    forecastActualExcpectedCompare.setForecastResultA(compareWithList.get(j));
                    forecastActualExcpectedCompare.setForecastResultB(compareWithList.get(i));
                    if (forecastActualExcpectedCompare.matchExactly(compareCriteria)) {
                      completelyDisagree = false;
                    } else {
                      completelyAgree = false;
                    }
                  }
                }
                if (completelyDisagree) {
                  compareLabel = "C: Different than all others and others don't agree";
                } else if (completelyAgree) {
                  compareLabel = "E: Different than all others and others agree";
                } else {
                  compareLabel = "D: Different than all others and others have mixed agreement";
                }
              }
              forecastCompare.setCompareLabel(compareLabel);
              dataSession.save(forecastCompare);
              List<ForecastCompare> compareResultsList = compareResultMap.get(compareLabel);
              if (compareResultsList == null) {
                compareResultsList = new ArrayList<ForecastCompare>();
                compareResultMap.put(compareLabel, compareResultsList);
              }
              compareResultsList.add(forecastCompare);
              for (ForecastActual forecastActualToCompareWith : compareWithList) {
                ForecastTarget forecastTarget = new ForecastTarget();
                forecastTarget.setForecastCompare(forecastCompare);
                forecastTarget.setForecastActual(forecastActualToCompareWith);
                dataSession.save(forecastTarget);
              }
            }
            dataSession.save(forecastCompare);
          }
        }
      }
      trans.commit();
      status = "Completed normally";
    } catch (Exception e) {
      status = "Exception occurred: " + e.getMessage();
      exception = e;
      e.printStackTrace();
    } finally {
      finished = true;
    }
  }
}
