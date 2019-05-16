package org.immregistries.vfa.manager;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.immregistries.vfa.connect.model.DateSet;
import org.immregistries.vfa.connect.model.TestEvent;
import org.immregistries.vfa.manager.readers.TestCaseReader;
import org.immregistries.vfa.model.ForecastExpected;
import org.immregistries.vfa.model.Include;
import org.immregistries.vfa.model.Result;
import org.immregistries.vfa.model.TestCaseWithExpectations;
import org.immregistries.vfa.model.TestPanel;
import org.immregistries.vfa.model.TestPanelCase;
import org.immregistries.vfa.model.TestPanelForecast;
import org.immregistries.vfa.connect.model.TestCase;

public class TestCaseImporter {
  public void importTestCases(TestCaseReader testCaseReader, TestPanel testPanel, Session dataSession) {
    for (TestCaseWithExpectations testCaseWithExpectations : testCaseReader.getTestCaseList()) {
      TestCase testCaseImported = testCaseWithExpectations.getTestCase();
      Query query = dataSession.createQuery("from TestPanelCase where testCaseNumber = ? and testPanel = ?");
      query.setParameter(0, testCaseImported.getTestCaseNumber());
      query.setParameter(1, testPanel);
      List<TestPanelCase> testPanelCaseList = query.list();
      if (testPanelCaseList.size() > 0) {
        // found test panel case, need to update
        TestPanelCase testPanelCase = testPanelCaseList.get(0);
        testPanelCase.setCategoryName(testCaseImported.getCategoryName());
        testPanelCase.setResult(Result.RESEARCH);
        dataSession.update(testPanelCase);
        TestCase testCase = testPanelCase.getTestCase();
        testCase.setLabel(testCaseImported.getLabel());
        testCase.setDescription(testCaseImported.getDescription());
        testCase.setEvalDate(testCaseImported.getEvalDate());
        testCase.setPatientFirst(testCaseImported.getPatientFirst());
        testCase.setPatientLast(testCaseImported.getPatientLast());
        testCase.setPatientSex(testCaseImported.getPatientSex());
        testCase.setPatientDob(testCaseImported.getPatientDob());
        testCase.setDateSet(DateSet.FIXED);
        dataSession.update(testCase);
        List<ForecastExpected> forecastExpectedImportedList = testCaseWithExpectations.getForecastExpectedList();
        if (forecastExpectedImportedList != null) {
          for (ForecastExpected forecastExpectedImported : forecastExpectedImportedList) {
            query = dataSession
                .createQuery("from ForecastExpected where author = ? and testCase = ? and vaccineGroup = ?");
            query.setParameter(0, testCaseReader.getUser());
            query.setParameter(1, testCase);
            query.setParameter(2, forecastExpectedImported.getVaccineGroup());
            List<ForecastExpected> forecastExpectedList = query.list();
            ForecastExpected forecastExpected = null;
            if (forecastExpectedList.size() > 0) {
              forecastExpected = forecastExpectedList.get(0);
              forecastExpected.setAdmin(forecastExpectedImported.getAdmin());
              forecastExpected.setVaccineGroup(forecastExpectedImported.getVaccineGroup());
              forecastExpected.setDoseNumber(forecastExpectedImported.getDoseNumber());
              forecastExpected.setValidDate(forecastExpectedImported.getValidDate());
              forecastExpected.setDueDate(forecastExpectedImported.getDueDate());
              forecastExpected.setOverdueDate(forecastExpectedImported.getOverdueDate());
              forecastExpected.setFinishedDate(forecastExpectedImported.getFinishedDate());
              forecastExpected.setVaccineCvx(forecastExpectedImported.getVaccineCvx());
              forecastExpected.setForecastReason(forecastExpectedImported.getForecastReason());
              forecastExpected.setUpdatedDate(new Date());
              dataSession.update(forecastExpected);
            } else {
              forecastExpected = forecastExpectedImported;
              forecastExpected.setTestCase(testCase);
              dataSession.save(forecastExpected);
              // now link the new expectation to the test panel
              TestPanelForecast testPanelForecast = new TestPanelForecast();
              testPanelForecast.setTestPanelCase(testPanelCase);
              testPanelForecast.setForecastExpected(forecastExpected);
              dataSession.save(testPanelForecast);
            }
          }
        }
        query = dataSession.createQuery("from TestEvent where testCase = ?");
        query.setParameter(0, testCase);
        List<TestEvent> testEventList = query.list();
        for (TestEvent testEventToDelete : testEventList) {
          dataSession.delete(testEventToDelete);
        }
        testEventList = testCaseImported.getTestEventList();
        if (testEventList != null) {
          for (TestEvent testEvent : testEventList) {
            testEvent.setTestCase(testCase);
            dataSession.save(testEvent);
          }
        }
        testCase.setTestEventList(testEventList);
        testCaseWithExpectations.setTestCase(testCase);
        
      } else {
        
        // new test panel case, very easy
        dataSession.save(testCaseWithExpectations.getTestCase());
        List<TestEvent> testEventList = testCaseImported.getTestEventList();
        if (testEventList != null) {
          for (TestEvent testEvent : testEventList) {
            testEvent.setTestCase(testCaseImported);
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
        List<ForecastExpected> forecastExpectedListImported = testCaseWithExpectations.getForecastExpectedList();
        if (forecastExpectedListImported != null) {
          for (ForecastExpected forecastExpectedImported : forecastExpectedListImported) {
            forecastExpectedImported.setTestCase(testCaseImported);
            dataSession.save(forecastExpectedImported);
            TestPanelForecast testPanelForecast = new TestPanelForecast();
            testPanelForecast.setTestPanelCase(testPanelCase);
            testPanelForecast.setForecastExpected(forecastExpectedImported);
            dataSession.save(testPanelForecast);
          }
        }
      }
    }
  }
}
