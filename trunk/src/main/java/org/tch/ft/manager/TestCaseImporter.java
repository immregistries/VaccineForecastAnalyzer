package org.tch.ft.manager;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.tch.ft.manager.readers.TestCaseReader;
import org.tch.ft.model.ForecastExpected;
import org.tch.ft.model.Include;
import org.tch.ft.model.Result;
import org.tch.ft.model.TestCase;
import org.tch.ft.model.TestEvent;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.TestPanelCase;
import org.tch.ft.model.TestPanelExpected;

public class TestCaseImporter {
  public void importTestCases(TestCaseReader testCaseReader, TestPanel testPanel, Session dataSession) {
    for (TestCase testCaseImported : testCaseReader.getTestCaseList()) {
      Query query = dataSession.createQuery("from TestPanelCase where testCaseNumber = ? and testPanel = ?");
      query.setParameter(0, testCaseImported.getTestCaseNumber());
      query.setParameter(1, testPanel);
      List<TestPanelCase> testPanelCaseList = query.list();
      TestCase testCase = null;
      if (testPanelCaseList.size() > 0) {
        // found test panel case, need to update
        TestPanelCase testPanelCase = testPanelCaseList.get(0);
        testPanelCase.setCategoryName(testCaseImported.getCategoryName());
        testPanelCase.setResult(Result.RESEARCH);
        dataSession.update(testPanelCase);
        testCase = testPanelCase.getTestCase();
        testCase.setLabel(testCaseImported.getLabel());
        testCase.setDescription(testCaseImported.getDescription());
        testCase.setEvalDate(testCaseImported.getEvalDate());
        testCase.setPatientFirst(testCaseImported.getPatientFirst());
        testCase.setPatientLast(testCaseImported.getPatientLast());
        testCase.setPatientSex(testCaseImported.getPatientSex());
        testCase.setPatientDob(testCaseImported.getPatientDob());
        dataSession.update(testCase);
        List<ForecastExpected> forecastExpectedImportedList = testCase.getForecastExpectedList();
        if (forecastExpectedImportedList != null) {
          for (ForecastExpected forecastExpectedImported : forecastExpectedImportedList) {
            query = dataSession
                .createQuery("from ForecastExpected where author = ? and testCase = ? and forecastItem = ?");
            query.setParameter(0, testCaseReader.getUser());
            query.setParameter(1, testCase);
            query.setParameter(2, forecastExpectedImported.getForecastItem());
            List<ForecastExpected> forecastExpectedList = query.list();
            ForecastExpected forecastExpected = null;
            if (forecastExpectedList.size() > 0) {
              forecastExpected = forecastExpectedList.get(0);
              forecastExpected.setForecastItem(forecastExpectedImported.getForecastItem());
              forecastExpected.setDoseNumber(forecastExpectedImported.getDoseNumber());
              forecastExpected.setValidDate(forecastExpectedImported.getValidDate());
              forecastExpected.setDueDate(forecastExpectedImported.getDueDate());
              forecastExpected.setOverdueDate(forecastExpectedImported.getOverdueDate());
              forecastExpected.setFinishedDate(forecastExpectedImported.getFinishedDate());
              forecastExpected.setVaccineCvx(forecastExpectedImported.getVaccineCvx());
              dataSession.update(forecastExpected);
            } else {
              forecastExpected = forecastExpectedImported;
              forecastExpected.setTestCase(testCase);
              dataSession.save(forecastExpected);
              // now link the new expectation to the test panel
              TestPanelExpected testPanelExpected = new TestPanelExpected();
              testPanelExpected.setTestPanelCase(testPanelCase);
              testPanelExpected.setForecastExpected(forecastExpected);
              dataSession.save(testPanelExpected);
            }
          }
        }
        query = dataSession.createQuery("from TestEvent where testCase = ?");
        query.setParameter(0, testCase);
        List<TestEvent> testEventList = query.list();
        for (TestEvent testEventToDelete : testEventList) {
          dataSession.delete(testEventToDelete);
        }
        testEventList = testCase.getTestEventList();
        if (testEventList != null) {
          for (TestEvent testEvent : testEventList) {
            testEvent.setTestCase(testCase);
            dataSession.save(testEvent);
          }
        }

      } else {
        testCase = testCaseImported;
        // new test panel case, very easy
        dataSession.save(testCase);
        List<TestEvent> testEventList = testCase.getTestEventList();
        if (testEventList != null) {
          for (TestEvent testEvent : testEventList) {
            testEvent.setTestCase(testCase);
            dataSession.save(testEvent);
          }
        }
        TestPanelCase testPanelCase = new TestPanelCase();
        testPanelCase.setTestPanel(testPanel);
        testPanelCase.setTestCase(testCaseImported);
        testPanelCase.setCategoryName(testCaseImported.getCategoryName());
        testPanelCase.setInclude(Include.INCLUDED);
        testPanelCase.setResult(Result.RESEARCH);
        testPanelCase.setTestCaseNumber(testCaseImported.getTestCaseNumber());
        dataSession.save(testPanelCase);
        List<ForecastExpected> forecastExpectedListImported = testCase.getForecastExpectedList();
        if (forecastExpectedListImported != null) {
          for (ForecastExpected forecastExpectedImported : forecastExpectedListImported) {
            forecastExpectedImported.setTestCase(testCase);
            dataSession.save(forecastExpectedImported);
            TestPanelExpected testPanelExpected = new TestPanelExpected();
            testPanelExpected.setTestPanelCase(testPanelCase);
            testPanelExpected.setForecastExpected(forecastExpectedImported);
            dataSession.save(testPanelExpected);
          }
        }
      }

    }
  }
}
