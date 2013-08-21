package org.tch.ft.manager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.tch.fc.ConnectFactory;
import org.tch.fc.ConnectorInterface;
import org.tch.fc.model.ForecastActual;
import org.tch.fc.model.ForecastItem;
import org.tch.fc.model.ForecastResult;
import org.tch.fc.model.Software;
import org.tch.fc.model.TestCase;
import org.tch.ft.model.ForecastExpected;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.TestPanelCase;
import org.tch.ft.model.TestPanelExpected;

public class ForecastActualGenerator {
  public static List<ForecastActualExpectedCompare> runForecastActual(TestPanel testPanel, Software software,
      Session session) throws Exception {

    List<TestPanelExpected> testPanelExpectedList = null;
    {
      Query query = session.createQuery("from TestPanelExpected where testPanelCase.testPanel = ?");
      query.setParameter(0, testPanel);
      testPanelExpectedList = query.list();
    }

    return runForecastActual(software, session, testPanelExpectedList);
  }

  public static List<ForecastActualExpectedCompare> runForecastActual(TestPanelCase testPanelCase, Software software,
      Session session) throws Exception {

    List<TestPanelExpected> testPanelExpectedList = null;
    {
      Query query = session.createQuery("from TestPanelExpected where testPanelCase = ?");
      query.setParameter(0, testPanelCase);
      testPanelExpectedList = query.list();
    }

    return runForecastActual(software, session, testPanelExpectedList);
  }

  public static List<ForecastActualExpectedCompare> runForecastActual(Software software, Session session,
      List<TestPanelExpected> testPanelExpectedList) throws Exception {
    // Get list of previously run forecast results
    Query query;

    // Get list of items to forecast for
    query = session.createQuery("from ForecastItem");
    List<ForecastItem> forecastItemList = query.list();
    SoftwareManager.initSoftware(software, session);
    ConnectorInterface connector = ConnectFactory.createConnecter(software, forecastItemList);

    Map<TestCase, List<ForecastExpected>> testCaseMap = new HashMap<TestCase, List<ForecastExpected>>();

    for (TestPanelExpected testPanelExpected : testPanelExpectedList) {
      TestCase testCase = testPanelExpected.getTestPanelCase().getTestCase();

      List<ForecastExpected> forecastExpectedList = testCaseMap.get(testCase);
      if (forecastExpectedList == null) {
        forecastExpectedList = new ArrayList<ForecastExpected>();
        testCaseMap.put(testCase, forecastExpectedList);
      }
      forecastExpectedList.add(testPanelExpected.getForecastExpected());
    }
    Transaction trans = session.beginTransaction();
    List<ForecastActualExpectedCompare> forecastCompareList = new ArrayList<ForecastActualExpectedCompare>();
    String errorLog = null;
    try {
      for (TestCase testCase : testCaseMap.keySet()) {
        query = session.createQuery("from TestEvent where testCase = ?");
        query.setParameter(0, testCase);
        testCase.setTestEventList(query.list());
        List<ForecastActual> forecastActualList = null;
        try {
          errorLog = null;
          forecastActualList = connector.queryForForecast(testCase);
        } catch (Exception e) {
          e.printStackTrace();
          Writer writer = new StringWriter();
          PrintWriter printWriter = new PrintWriter(writer);
          printWriter.println("Unable to run forecast, exception ocurred:");
          e.printStackTrace(printWriter);
          printWriter.close();
          errorLog = writer.toString();
        }
        for (ForecastExpected forecastExpected : testCaseMap.get(testCase)) {
          query = session.createQuery("from ForecastActual where testCase = ? and software = ? and forecastItem = ?");
          query.setParameter(0, forecastExpected.getTestCase());
          query.setParameter(1, software);
          query.setParameter(2, forecastExpected.getForecastItem());
          List<ForecastActual> forecastActualListOriginal = query.list();
          ForecastActual forecastActual;
          if (forecastActualListOriginal.size() > 0) {
            forecastActual = forecastActualListOriginal.get(0);
          } else {
            forecastActual = new ForecastActual();
            forecastActual.setSoftware(software);
            forecastActual.setTestCase(testCase);
            forecastActual.setForecastItem(forecastExpected.getForecastItem());
          }
          forecastActual.setScheduleName(software.getScheduleName());
          forecastActual.setRunDate(new Date());
          if (errorLog == null) {
            boolean found = false;

            for (ForecastActual result : forecastActualList) {
              if (result.getForecastItem().equals(forecastActual.getForecastItem())) {
                forecastActual.setDoseNumber(result.getDoseNumber());
                forecastActual.setValidDate(result.getValidDate());
                forecastActual.setDueDate(result.getDueDate());
                forecastActual.setOverdueDate(result.getOverdueDate());
                forecastActual.setFinishedDate(result.getFinishedDate());
                forecastActual.setVaccineCvx(result.getVaccineCvx());
                System.out.println(forecastActual.getLogText());
                forecastActual.setLogText(result.getLogText());
                found = true;
                break;
              }
            }

            if (!found) {
              String logText = "";
              if (forecastActualList.size() > 0) {
                logText = forecastActualList.get(0).getLogText();
              }
              forecastActual.setDoseNumber(ForecastResult.DOSE_NUMBER_COMPLETE);
              forecastActual.setValidDate(null);
              forecastActual.setOverdueDate(null);
              forecastActual.setDueDate(null);
              forecastActual.setFinishedDate(null);
              forecastActual.setVaccineCvx(null);
              forecastActual.setLogText("No results found, returning log for first results \n" + logText);
            }
          } else {
            forecastActual.setDoseNumber(ForecastResult.DOSE_NUMBER_ERROR);
            forecastActual.setValidDate(null);
            forecastActual.setOverdueDate(null);
            forecastActual.setDueDate(null);
            forecastActual.setFinishedDate(null);
            forecastActual.setVaccineCvx(null);
            forecastActual.setLogText(errorLog);
          }

          session.saveOrUpdate(forecastActual);
          ForecastActualExpectedCompare forecastCompare = new ForecastActualExpectedCompare();
          forecastCompare.setTestCase(testCase);
          forecastCompare.setForecastResultA(forecastExpected);
          forecastCompare.setForecastResultB(forecastActual);
          forecastCompare.setRunStatus(ForecastActualExpectedCompare.RunStatus.QUERIED);
          forecastCompareList.add(forecastCompare);
        }
      }
    } finally {
      trans.commit();
    }
    return forecastCompareList;
  }

  public static List<ForecastActualExpectedCompare> createForecastComparison(TestPanel testPanel, Software software,
      Session session) {
    List<ForecastActualExpectedCompare> forecastCompareList = new ArrayList<ForecastActualExpectedCompare>();
    Query query = session.createQuery("from TestPanelExpected where testPanelCase.testPanel = ?");
    query.setParameter(0, testPanel);
    List<TestPanelExpected> testPanelExpectedList = query.list();
    for (TestPanelExpected testPanelExpected : testPanelExpectedList) {
      ForecastExpected forecastExpected = testPanelExpected.getForecastExpected();
      TestCase testCase = forecastExpected.getTestCase();
      ForecastActualExpectedCompare forecastCompare = new ForecastActualExpectedCompare();
      forecastCompare.setForecastResultA(forecastExpected);
      forecastCompare.setTestCase(testCase);
      // Get forecast results that were run for this software
      query = session.createQuery("from ForecastActual where software = ? and testCase = ? and forecastItem = ?");
      query.setParameter(0, software);
      query.setParameter(1, testCase);
      query.setParameter(2, forecastExpected.getForecastItem());
      List<ForecastActual> forecastActualList = query.list();
      if (forecastActualList.size() > 0) {
        forecastCompare.setForecastResultB(forecastActualList.get(0));
      }
      forecastCompareList.add(forecastCompare);
    }
    return forecastCompareList;
  }

}
