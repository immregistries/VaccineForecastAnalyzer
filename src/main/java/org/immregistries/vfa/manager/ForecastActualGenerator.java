package org.immregistries.vfa.manager;

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
import org.immregistries.vfa.connect.ConnectFactory;
import org.immregistries.vfa.connect.ConnectorInterface;
import org.immregistries.vfa.connect.model.Admin;
import org.immregistries.vfa.connect.model.ForecastActual;
import org.immregistries.vfa.connect.model.Software;
import org.immregistries.vfa.connect.model.SoftwareResult;
import org.immregistries.vfa.connect.model.TestCase;
import org.immregistries.vfa.connect.model.VaccineGroup;
import org.immregistries.vfa.model.ForecastExpected;
import org.immregistries.vfa.model.Result;
import org.immregistries.vfa.model.TestPanel;
import org.immregistries.vfa.model.TestPanelCase;
import org.immregistries.vfa.model.TestPanelForecast;

public class ForecastActualGenerator
{
  public static void runForecastActual(TestPanel testPanel, Software software, Session session, boolean logText)
      throws Exception {

    List<TestPanelForecast> testPanelForecastList = null;
    {
      Query query = session.createQuery("from TestPanelForecast where testPanelCase.testPanel = ?");
      query.setParameter(0, testPanel);
      testPanelForecastList = query.list();
    }

    runForecastActual(software, session, testPanelForecastList, logText);
  }

  public static void runForecastActual(TestPanel testPanel, Software software, Set<String> categoryNameSet,
      Session session, boolean logText) throws Exception {

    List<TestPanelForecast> testPanelForecastList;
    {
      Query query = session
          .createQuery("from TestPanelForecast where testPanelCase.testPanel = ? and testPanelCase.resultStatus <> 'E'");
      query.setParameter(0, testPanel);
      if (categoryNameSet == null) {
        testPanelForecastList = query.list();
      } else {
        testPanelForecastList = new ArrayList<TestPanelForecast>();
        for (TestPanelForecast testPanelForecast : (List<TestPanelForecast>) query.list()) {
          if (categoryNameSet.contains(testPanelForecast.getTestPanelCase().getCategoryName())) {
            testPanelForecastList.add(testPanelForecast);
          }
        }
      }
    }

    runForecastActual(software, session, testPanelForecastList, logText);
  }

  public static void runForecastActual(TestPanelCase testPanelCase, Software software, Session session, boolean logText)
      throws Exception {

    List<TestPanelForecast> testPanelForecastList = null;
    {
      Query query = session.createQuery("from TestPanelForecast where testPanelCase = ?");
      query.setParameter(0, testPanelCase);
      testPanelForecastList = query.list();
    }

    runForecastActual(software, session, testPanelForecastList, logText);
  }

  public static void runForecastActual(Software software, Session session,
      List<TestPanelForecast> testPanelForecastList, boolean logText) throws Exception {

    // Get list of previously run forecast results
    Query query;

    // Get list of items to forecast for
    query = session.createQuery("from VaccineGroup");
    List<VaccineGroup> vaccineGroupList = query.list();
    SoftwareManager.initSoftware(software, session);
    ConnectorInterface connector = ConnectFactory.createConnecter(software, vaccineGroupList);
    connector.setLogText(logText);

    Map<TestCase, List<TestPanelForecast>> testCaseMap = new HashMap<TestCase, List<TestPanelForecast>>();

    for (TestPanelForecast testPanelForecast : testPanelForecastList) {
      TestCase testCase = testPanelForecast.getTestPanelCase().getTestCase();

      List<TestPanelForecast> forecastExpectedList = testCaseMap.get(testCase);
      if (forecastExpectedList == null) {
        forecastExpectedList = new ArrayList<TestPanelForecast>();
        testCaseMap.put(testCase, forecastExpectedList);
      }
      forecastExpectedList.add(testPanelForecast);

    }
    List<ForecastActualExpectedCompare> forecastCompareList = new ArrayList<ForecastActualExpectedCompare>();
    String errorLog = null;
    for (TestCase testCase : testCaseMap.keySet()) {
      Transaction trans = session.beginTransaction();
      try {
        
        
        query = session.createQuery("from TestEvent where testCase = ?");
        query.setParameter(0, testCase);
        testCase.setTestEventList(query.list());
        query = session.createQuery("from TestCaseSetting where testCase = ?");
        query.setParameter(0, testCase);
        testCase.setTestCaseSettingList(query.list());
        List<ForecastActual> forecastActualList = null;

        query = session.createQuery("from SoftwareResult where software = ? and testCase = ? order by runDate DESC");
        query.setMaxResults(1);
        query.setParameter(0, software);
        query.setParameter(1, testCase);
        List<SoftwareResult> softwareResultList = query.list();
        SoftwareResult softwareResult = null;
        if (softwareResultList.size() > 0) {
          softwareResult = softwareResultList.get(0);
          int ratingCount = 0;
          // check to see if we can use this one still, if it has an associated evaluation then it needs to be preserved
          query = session.createQuery("from EvaluationActualRating where evaluationActual.softwareResult = ?");
          query.setParameter(0, softwareResult);
          ratingCount += query.list().size();
          query = session.createQuery("from ForecastActualRating where forecastActual.softwareResult = ?");
          query.setParameter(0, softwareResult);
          ratingCount += query.list().size();
          if (ratingCount > 0) {
            softwareResult = null;
          }
        }
        if (softwareResult == null) {
          softwareResult = new SoftwareResult();
          softwareResult.setSoftware(software);
          softwareResult.setTestCase(testCase);
        }
        softwareResult.setRunDate(new Date());
        session.saveOrUpdate(softwareResult);

        boolean errorOccurred = false;
        try {
          errorLog = null;
          forecastActualList = connector.queryForForecast(testCase, softwareResult);
        } catch (Exception e) {
          errorOccurred = true;
        }
        if (forecastActualList == null) {
          forecastActualList = new ArrayList<ForecastActual>();
        }
        // TODO record forecast actuals
        for (TestPanelForecast testPanelForecast : testCaseMap.get(testCase)) {
          ForecastExpected forecastExpected = testPanelForecast.getForecastExpected();
          boolean foundMatch = false;
          for (ForecastActual forecastActual : forecastActualList) {
            if (forecastActual.getVaccineGroup().equals(forecastExpected.getVaccineGroup())) {
              foundMatch = true;
              break;
            }
          }
          if (!foundMatch) {
            ForecastActual forecastActual = new ForecastActual();
            forecastActual.setSoftwareResult(softwareResult);
            forecastActual.setVaccineGroup(forecastExpected.getVaccineGroup());
            forecastActual.setAdmin(errorOccurred ? Admin.ERROR : Admin.NO_RESULTS);
            forecastActualList.add(forecastActual);
          }
        }

        query = session.createQuery("from ForecastActual where softwareResult = ?");
        query.setParameter(0, softwareResult);
        List<ForecastActual> forecastActualCurrentList = query.list();
        // delete forecastActuals that didn't come back this time
        for (ForecastActual forecastActualCurrent : forecastActualCurrentList) {
          boolean foundMatch = false;
          for (ForecastActual forecastActual : forecastActualList) {
            if (forecastActual.getVaccineGroup().equals(forecastActualCurrent.getVaccineGroup())) {
              foundMatch = true;
              break;
            }
          }
          if (!foundMatch) {
            session.delete(forecastActualCurrent);
          }
        }

        for (ForecastActual forecastActual : forecastActualList) {
          boolean foundMatch = false;
          for (ForecastActual forecastActualCurrent : forecastActualCurrentList) {
            if (forecastActual.getVaccineGroup().equals(forecastActualCurrent.getVaccineGroup())) {
              forecastActualCurrent.setScheduleName(forecastActual.getScheduleName());
              forecastActualCurrent.setAdmin(forecastActual.getAdmin());
              forecastActualCurrent.setDoseNumber(forecastActual.getDoseNumber());
              forecastActualCurrent.setValidDate(forecastActual.getValidDate());
              forecastActualCurrent.setDueDate(forecastActual.getDueDate());
              forecastActualCurrent.setOverdueDate(forecastActual.getOverdueDate());
              forecastActualCurrent.setFinishedDate(forecastActual.getFinishedDate());
              forecastActualCurrent.setVaccineCvx(forecastActual.getVaccineCvx());
              forecastActualCurrent.setForecastReason(forecastActual.getForecastReason());
              session.update(forecastActualCurrent);
              foundMatch = true;
              break;
            }
          }
          if (!foundMatch) {
            session.save(forecastActual);
          }
        }

      } finally {
        trans.commit();
      }
      // TODO record evaluation actuals

    }

  }

  public static List<ForecastActualExpectedCompare> createForecastComparison(TestPanel testPanel, Software software,
      Set<String> categoryNameSet, Session session) {
    List<ForecastActualExpectedCompare> forecastCompareList = new ArrayList<ForecastActualExpectedCompare>();
    Query query = session.createQuery("from TestPanelForecast where testPanelCase.testPanel = ?");
    query.setParameter(0, testPanel);
    List<TestPanelForecast> testPanelForecastList;
    if (categoryNameSet == null) {
      testPanelForecastList = query.list();
    } else {
      testPanelForecastList = new ArrayList<TestPanelForecast>();
      for (TestPanelForecast testPanelForecast : (List<TestPanelForecast>) query.list()) {
        if (categoryNameSet.contains(testPanelForecast.getTestPanelCase().getCategoryName())) {
          testPanelForecastList.add(testPanelForecast);
        }
      }
    }
    for (TestPanelForecast testPanelForecast : testPanelForecastList) {
      ForecastExpected forecastExpected = testPanelForecast.getForecastExpected();
      TestCase testCase = forecastExpected.getTestCase();
      ForecastActualExpectedCompare forecastCompare = new ForecastActualExpectedCompare();
      forecastCompare.setForecastResultA(forecastExpected);
      forecastCompare.setTestPanelCase(testPanelForecast.getTestPanelCase());
      forecastCompare.setTestCase(testCase);
      // Get forecast results that were run for this software
      query = session
          .createQuery("from ForecastActual where softwareResult.software = ? and softwareResult.testCase = ? and vaccineGroup = ? order by softwareResult.runDate desc");
      query.setParameter(0, software);
      query.setParameter(1, testCase);
      query.setParameter(2, forecastExpected.getVaccineGroup());
      List<ForecastActual> forecastActualList = query.list();
      if (forecastActualList.size() > 0) {
        forecastCompare.setForecastResultB(forecastActualList.get(0));
      }
      forecastCompareList.add(forecastCompare);
    }

    return forecastCompareList;
  }

  public static Set<TestPanelCase> updateStatusOfTestPanel(Session session,
      List<ForecastActualExpectedCompare> forecastCompareList) {
    Set<TestPanelCase> testPanelCaseList = new HashSet<TestPanelCase>();
    Transaction transaction = session.beginTransaction();
    Set<TestPanelCase> testPanelCaseSetPass = new HashSet<TestPanelCase>();
    Set<TestPanelCase> testPanelCaseSetFail = new HashSet<TestPanelCase>();
    for (ForecastActualExpectedCompare forecastCompare : forecastCompareList) {
      if (forecastCompare.matchExactly()) {
        testPanelCaseSetPass.add(forecastCompare.getTestPanelCase());
      } else {
        testPanelCaseSetFail.add(forecastCompare.getTestPanelCase());
      }
    }
    testPanelCaseSetPass.remove(testPanelCaseSetFail);
    for (TestPanelCase testPanelCase : testPanelCaseSetPass) {
      if (testPanelCase.getResult() == null) {
        testPanelCase.setResult(Result.PASS);
        session.update(testPanelCase);
        testPanelCaseList.add(testPanelCase);
      } else if (testPanelCase.getResult() == Result.FAIL) {
        testPanelCase.setResult(Result.FIXED);
        session.update(testPanelCase);
        testPanelCaseList.add(testPanelCase);
      }
    }
    for (TestPanelCase testPanelCase : testPanelCaseSetFail) {
      if (testPanelCase.getResult() == null) {
        testPanelCase.setResult(Result.FAIL);
        session.update(testPanelCase);
        testPanelCaseList.add(testPanelCase);
      } else if (testPanelCase.getResult() == Result.PASS) {
        testPanelCase.setResult(Result.FAIL);
        session.update(testPanelCase);
        testPanelCaseList.add(testPanelCase);
      }
    }
    transaction.commit();
    return testPanelCaseList;
  }

}
