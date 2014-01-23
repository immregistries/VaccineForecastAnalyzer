package org.tch.ft.manager.readers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.tch.fc.ConnectFactory;
import org.tch.fc.ConnectorInterface;
import org.tch.fc.model.Event;
import org.tch.fc.model.ForecastActual;
import org.tch.fc.model.VaccineGroup;
import org.tch.fc.model.ForecastResult;
import org.tch.fc.model.TestCase;
import org.tch.fc.model.TestEvent;
import org.tch.ft.model.ForecastExpected;
import org.tch.ft.model.TestCaseWithExpectations;
import org.tch.ft.web.testCase.RandomNames;

public class IhsTestCaseReader extends CsvTestCaseReader implements TestCaseReader {

  public void read(InputStream in) throws IOException {
    readInputStream(in);
    if (testCaseFieldListList.size() <= 1) {
      throw new IllegalArgumentException("No test cases found");
    }
    headerFields = testCaseFieldListList.get(0);
    testCaseFieldListList.remove(0);

    int vaccineNamesPos = 0;
    int birthdatePos = 1;
    int caseNumberPosition = 2;
    int shotDatePos = 3;
    int shotCvxPos = 4;

    List<VaccineGroup> vaccineGroupList = new ArrayList<VaccineGroup>(vaccineGroupListMap.values());

    {
      Date referenceDate = new Date();
      String lastMrn = "";
      TestCaseWithExpectations testCaseWithExpectations = null;
      TestCase testCase = null;
      List<TestEvent> testEventList = new ArrayList<TestEvent>();
      for (List<String> testCaseFieldList : testCaseFieldListList) {
        String mrn = readField(caseNumberPosition, testCaseFieldList);
        if (!mrn.equals(lastMrn)) {
          lastMrn = mrn;
          testCaseWithExpectations = new TestCaseWithExpectations();
          testCase = testCaseWithExpectations.getTestCase();
          testCaseList.add(testCaseWithExpectations);
          testCase.setTestCaseNumber("IHS-" + mrn);
          testCase.setLabel("Test Case " + testCase.getTestCaseNumber());
          testCase.setDescription(testCase.getLabel());
          testCase.setPatientDob(readDateField(birthdatePos, testCaseFieldList, testCaseWithExpectations));
          testCase.setCategoryName(createAgeCategoryName(testCase.getPatientDob(), referenceDate));
          testCase.setPatientSex("F");
          testCase.setPatientFirst(RandomNames.getRandomFirstName());
          testCase.setPatientLast(RandomNames.getRandomLastName());
          testCase.setEvalDate(referenceDate);
          testCase.setTestEventList(testEventList);
          testEventList = new ArrayList<TestEvent>();
          testCase.setTestEventList(testEventList);
        }
        String vaccineName = readField(vaccineNamesPos, testCaseFieldList);
        String cvxCode = readField(shotCvxPos, testCaseFieldList);
        if (cvxCode.length() == 1) {
          cvxCode = "0" + cvxCode;
        }
        Date shotDate = readDateField(shotDatePos, testCaseFieldList, testCaseWithExpectations);
        if (!cvxCode.equals("") && shotDate != null) {
          TestEvent testEvent = new TestEvent();
          Event event = cvxToEventMap.get(cvxCode);
          if (event == null) {
            throw new IllegalArgumentException("Unrecognized CVX code '" + cvxCode + "' for shot " + vaccineName
                + " test case " + testCase.getTestCaseNumber() + "");
          }
          testEvent.setEvent(event);
          testEvent.setEventDate(shotDate);
          testEvent.setTestCase(testCase);
          testEventList.add(testEvent);
        }
      }
    }
    for (TestCaseWithExpectations testCaseWithExpectations : testCaseList) {
      List<ForecastActual> forecastActualList = null;
      if (loadExpectationsSoftware != null) {
        try {
          ConnectorInterface connector = ConnectFactory.createConnecter(loadExpectationsSoftware, vaccineGroupList);
          forecastActualList = connector.queryForForecast(testCaseWithExpectations.getTestCase());

        } catch (Exception e) {
          loadExpectationsSoftware = null;
          e.printStackTrace();
          errorMessage = "Unable to query software to load expectations: " + e.getMessage();
        }

      }
      int ageInYears = getAgeInYears(testCaseWithExpectations.getTestCase().getPatientDob(), testCaseWithExpectations.getTestCase().getEvalDate());
      for (VaccineGroup vaccineGroupItem : vaccineGroupList) {
        if (ageInYears < (vaccineGroupItem.getTypicallyGivenYearStart() - 1)
            || ageInYears > (vaccineGroupItem.getTypicallyGivenYearEnd() + 1)) {
          continue;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(testCaseWithExpectations.getTestCase().getPatientDob());
        calendar.add(Calendar.YEAR, vaccineGroupItem.getTypicallyGivenYearStart());
        Date typicalStartDate = calendar.getTime();

        ForecastExpected forecastExpected = null;
        if (forecastActualList != null) {
          for (ForecastActual forecastActual : forecastActualList) {
            if (forecastActual.getVaccineGroup().equals(vaccineGroupItem)) {
              forecastExpected = new ForecastExpected();
              forecastExpected.setDoseNumber(forecastActual.getDoseNumber());
              forecastExpected.setValidDate(forecastActual.getValidDate());
              forecastExpected.setDueDate(forecastActual.getDueDate());
              forecastExpected.setOverdueDate(forecastActual.getOverdueDate());
              forecastExpected.setFinishedDate(forecastActual.getFinishedDate());
              forecastExpected.setVaccineCvx(forecastActual.getVaccineCvx());
              break;
            }
          }
        }
        if (forecastExpected == null) {
          forecastExpected = new ForecastExpected();
          forecastExpected.setDoseNumber(ForecastResult.DOSE_NUMBER_COMPLETE);
        }
        forecastExpected.setTestCase(testCaseWithExpectations.getTestCase());
        forecastExpected.setAuthor(user);
        forecastExpected.setVaccineGroup(vaccineGroupItem);
        List<ForecastExpected> forecastExpectedList = testCaseWithExpectations.getForecastExpectedList();
        if (forecastExpectedList == null) {
          forecastExpectedList = new ArrayList<ForecastExpected>();
          testCaseWithExpectations.setForecastExpectedList(forecastExpectedList);
        }
        forecastExpectedList.add(forecastExpected);
      }
    }
  }

  private int getAgeInYears(Date date, Date referenceDate) {
    Calendar calDate = Calendar.getInstance();
    Calendar refDate = Calendar.getInstance();
    calDate.setTime(date);
    refDate.setTime(referenceDate);
    int calYear = calDate.get(Calendar.YEAR);
    int refYear = refDate.get(Calendar.YEAR);
    int diff = refYear - calYear;
    calDate.add(Calendar.YEAR, diff);
    if (referenceDate.before(calDate.getTime())) {
      diff--;
    }
    return diff;
  }

  private String createAgeCategoryName(Date date, Date referenceDate) {

    if (beforeCutoff(date, referenceDate, 1)) {
      return "0-11 Months";
    } else if (beforeCutoff(date, referenceDate, 4)) {
      return "1-3 Years";
    } else if (beforeCutoff(date, referenceDate, 7)) {
      return "4-6 Years";
    } else if (beforeCutoff(date, referenceDate, 11)) {
      return "7-10 Years";
    } else if (beforeCutoff(date, referenceDate, 19)) {
      return "11-18 Years";
    } else if (beforeCutoff(date, referenceDate, 50)) {
      return "19-49 Years";
    } else if (beforeCutoff(date, referenceDate, 65)) {
      return "50-64 Years";
    }
    return "65 and Older";
  }

  public boolean beforeCutoff(Date date, Date referenceDate, int cutoffYears) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.YEAR, cutoffYears);
    boolean beforeCutoff = referenceDate.before(calendar.getTime());
    return beforeCutoff;
  }

  private Map<Integer, VaccineGroup> vaccineGroupListMap = null;

  public void setVaccineGroupss(Map<Integer, VaccineGroup> vaccineGroupListMap) {
    this.vaccineGroupListMap = vaccineGroupListMap;
  }

}
