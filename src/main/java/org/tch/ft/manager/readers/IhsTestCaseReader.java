package org.tch.ft.manager.readers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.tch.ft.model.Event;
import org.tch.ft.model.ForecastExpected;
import org.tch.ft.model.ForecastItem;
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

    Date referenceDate = new Date();
    String lastMrn = "";
    TestCase testCase = null;
    List<TestEvent> testEventList = null;
    for (List<String> testCaseFieldList : testCaseFieldListList) {
      String mrn = readField(caseNumberPosition, testCaseFieldList);
      if (!mrn.equals(lastMrn)) {
        lastMrn = mrn;
        testCase = new TestCase();
        testCaseList.add(testCase);
        testCase.setTestCaseNumber("IHS-" + mrn);
        testCase.setCategoryName("General");
        testCase.setLabel("Test Case " + testCase.getTestCaseNumber());
        testCase.setDescription(testCase.getLabel());
        testCase.setPatientDob(readDateField(birthdatePos, testCaseFieldList, testCase));
        testCase.setPatientSex("F");
        testCase.setPatientFirst(RandomNames.getRandomFirstName());
        testCase.setPatientLast(RandomNames.getRandomLastName());
        testCase.setEvalDate(referenceDate);
        testEventList = new ArrayList<TestEvent>();
        testEventListMap.put(testCase, testEventList);
      }
      if (testEventList != null) {
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
        for (ForecastItem forecastItem : forecastItemListMap.values()) {
          ForecastExpected forecastExpected = new ForecastExpected();
          forecastExpected.setTestCase(testCase);
          forecastExpected.setAuthor(user);
          forecastExpected.setForecastItem(forecastItem);
          forecastExpected.setDoseNumber("1");
          forecastExpected.setValidDate(referenceDate);
          forecastExpected.setDueDate(referenceDate);
          forecastExpected.setOverdueDate(referenceDate);
          List<ForecastExpected> forecastExpectedList = forecastExpectedListMap.get(testCase);
          if (forecastExpectedList == null) {
            forecastExpectedList = new ArrayList<ForecastExpected>();
            forecastExpectedListMap.put(testCase, forecastExpectedList);
          }
          forecastExpectedList.add(forecastExpected);
        }
      }
    }
  }

  private Map<Integer, ForecastItem> forecastItemListMap = null;

  public void setForecastItems(Map<Integer, ForecastItem> forecastItemListMap) {
    this.forecastItemListMap = forecastItemListMap;
  }

}
