package org.tch.ft.manager.readers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.tch.ft.connect.ConnectFactory;
import org.tch.ft.connect.ConnectorInterface;
import org.tch.ft.model.Event;
import org.tch.ft.model.ForecastActual;
import org.tch.ft.model.ForecastExpected;
import org.tch.ft.model.ForecastItem;
import org.tch.ft.model.ForecastResult;
import org.tch.ft.model.TestCase;
import org.tch.ft.model.TestEvent;
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

    List<ForecastItem> forecastItemList = new ArrayList<ForecastItem>(forecastItemListMap.values());

    {
      Date referenceDate = new Date();
      String lastMrn = "";
      TestCase testCase = null;
      List<TestEvent> testEventList = new ArrayList<TestEvent>();
      for (List<String> testCaseFieldList : testCaseFieldListList) {
        String mrn = readField(caseNumberPosition, testCaseFieldList);
        if (!mrn.equals(lastMrn)) {
          lastMrn = mrn;
          testCase = new TestCase();
          testCaseList.add(testCase);
          testCase.setTestCaseNumber("IHS-" + mrn);
          testCase.setLabel("Test Case " + testCase.getTestCaseNumber());
          testCase.setDescription(testCase.getLabel());
          testCase.setPatientDob(readDateField(birthdatePos, testCaseFieldList, testCase));
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
        Date shotDate = readDateField(shotDatePos, testCaseFieldList, testCase);
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
    for (TestCase testCase : testCaseList) {
      List<ForecastActual> forecastActualList = null;
      if (loadExpectationsSoftware != null) {
        try {
          ConnectorInterface connector = ConnectFactory.createConnecter(loadExpectationsSoftware, forecastItemList);
          forecastActualList = connector.queryForForecast(testCase);

        } catch (Exception e) {
          loadExpectationsSoftware = null;
          e.printStackTrace();
          errorMessage = "Unable to query software to load expectations: " + e.getMessage();
        }

      }
      int ageInYears = getAgeInYears(testCase.getPatientDob(), testCase.getEvalDate());
      for (ForecastItem forecastItem : forecastItemList) {
        if (ageInYears < (forecastItem.getTypicallyGivenYearStart() - 1)
            || ageInYears > (forecastItem.getTypicallyGivenYearEnd() + 1)) {
          continue;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(testCase.getPatientDob());
        calendar.add(Calendar.YEAR, forecastItem.getTypicallyGivenYearStart());
        Date typicalStartDate = calendar.getTime();

        ForecastExpected forecastExpected = null;
        if (forecastActualList != null) {
          for (ForecastActual forecastActual : forecastActualList) {
            if (forecastActual.getForecastItem().equals(forecastItem)) {
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
        forecastExpected.setTestCase(testCase);
        forecastExpected.setAuthor(user);
        forecastExpected.setForecastItem(forecastItem);
        List<ForecastExpected> forecastExpectedList = testCase.getForecastExpectedList();
        if (forecastExpectedList == null) {
          forecastExpectedList = new ArrayList<ForecastExpected>();
          testCase.setForecastExpectedList(forecastExpectedList);
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

  private Map<Integer, ForecastItem> forecastItemListMap = null;

  public void setForecastItems(Map<Integer, ForecastItem> forecastItemListMap) {
    this.forecastItemListMap = forecastItemListMap;
  }

}
