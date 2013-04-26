package org.tch.ft.manager.readers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tch.ft.model.Event;
import org.tch.ft.model.ForecastExpected;
import org.tch.ft.model.ForecastItem;
import org.tch.ft.model.TestCase;
import org.tch.ft.model.TestEvent;

public class MiisTestCaseReader extends CsvTestCaseReader implements TestCaseReader {

  private static final String FIELD_CASE = "Case";
  private static final String FIELD_IZ_SERIES = "IZ Series";
  private static final String FIELD_BIRTHDATE = "birthdate";
  private static final String FIELD_GENDER = "Gender";
  private static final String FIELD_VACCINE_NAMES = "Dosage History";
  private static final String FIELD_SHOT_ = "Shot";
  private static final String FIELD_SHOT_CVX = " CVX";
  private static final String FIELD_SHOT_DATE = " Date";
  private static final String FIELD_FORECAST_NUM = "Forecast#";
  private static final String FIELD_EARLIEST_DATE = "Earliest Date";
  private static final String FIELD_REC_DATE = "Rec Date";
  private static final String FIELD_OVERDUE_DATE = "Overdue Date";
  private static final String FIELD_REFERENCE_DATE = "Reference Date";

  private Map<String, ForecastItem> forecastItemMap = new HashMap<String, ForecastItem>();

  public void setForecastItems(Map<Integer, ForecastItem> forecastItemListMap) {
    forecastItemMap.put("DTP", forecastItemListMap.get(2));
    forecastItemMap.put("DTaP", forecastItemListMap.get(2));
    forecastItemMap.put("H1N1", forecastItemListMap.get(3));
    forecastItemMap.put("HepA", forecastItemListMap.get(4));
    forecastItemMap.put("HepB", forecastItemListMap.get(5));
    forecastItemMap.put("HerpesZoster", forecastItemListMap.get(14));
    forecastItemMap.put("Hib", forecastItemListMap.get(6));
    forecastItemMap.put("HPV", forecastItemListMap.get(7));
    forecastItemMap.put("Influenza", forecastItemListMap.get(3));
    forecastItemMap.put("Flu", forecastItemListMap.get(3));
    forecastItemMap.put("MCV4", forecastItemListMap.get(8));
    forecastItemMap.put("MCV", forecastItemListMap.get(8));
    forecastItemMap.put("MMR", forecastItemListMap.get(9));
    forecastItemMap.put("MPSV", forecastItemListMap.get(8));
    forecastItemMap.put("Pneumo-Poly", forecastItemListMap.get(10));
    forecastItemMap.put("Pneumonia", forecastItemListMap.get(10));
    forecastItemMap.put("PCV", forecastItemListMap.get(10));
    forecastItemMap.put("Polio", forecastItemListMap.get(11));
    forecastItemMap.put("POL", forecastItemListMap.get(11));
    forecastItemMap.put("Rotavirus", forecastItemListMap.get(12));
    forecastItemMap.put("Rota", forecastItemListMap.get(12));
    forecastItemMap.put("Td/Tdap", forecastItemListMap.get(15));
    forecastItemMap.put("Varicella", forecastItemListMap.get(13));
    forecastItemMap.put("Var", forecastItemListMap.get(13));
    forecastItemMap.put("Typhoid", forecastItemListMap.get(0));
  }

  private String[] ignoredItems = { "Typhoid" };

  public void read(InputStream in) throws IOException {
    readInputStream(in);
    if (testCaseFieldListList.size() <= 1) {
      throw new IllegalArgumentException("No test cases found");
    }
    headerFields = testCaseFieldListList.get(0);
    testCaseFieldListList.remove(0);

    int caseNumberPosition = findFieldPos(FIELD_CASE);
    int izSeriesPos = findFieldPos(FIELD_IZ_SERIES);
    int birthdatePos = findFieldPos(FIELD_BIRTHDATE);
    int genderPos = findFieldPos(FIELD_GENDER);
    int vaccineNamesPos = findFieldPos(FIELD_VACCINE_NAMES);
    int shotCvxPos[] = new int[10];
    int shotDatePos[] = new int[10];
    for (int i = 1; i <= 9; i++) {
      shotCvxPos[i] = findFieldPos(FIELD_SHOT_ + i + FIELD_SHOT_CVX);
      shotDatePos[i] = findFieldPos(FIELD_SHOT_ + i + FIELD_SHOT_DATE);
    }
    int forecastNumPos = findFieldPos(FIELD_FORECAST_NUM);
    int earliestDatePos = findFieldPos(FIELD_EARLIEST_DATE);
    int recDatePos = findFieldPos(FIELD_REC_DATE);
    int overdueDatePos = findFieldPos(FIELD_OVERDUE_DATE);
    int referenceDatePos = findFieldPos(FIELD_REFERENCE_DATE);

    Date referenceDate = null;
    for (List<String> testCaseFieldList : testCaseFieldListList) {
      TestCase testCase = new TestCase();
      testCaseList.add(testCase);
      testCase.setTestCaseNumber(readField(caseNumberPosition, testCaseFieldList));
      testCase.setCategoryName(readField(izSeriesPos, testCaseFieldList));
      testCase.setLabel("Test Case " + testCase.getTestCaseNumber());
      testCase.setDescription(readField(vaccineNamesPos, testCaseFieldList));
      testCase.setPatientDob(readDateField(birthdatePos, testCaseFieldList, testCase));
      testCase.setPatientSex(readField(genderPos, testCaseFieldList).toUpperCase().startsWith("M") ? "M" : "F");
      if (referenceDate == null) {
        referenceDate = readDateField(referenceDatePos, testCaseFieldList, testCase);
      }
      testCase.setEvalDate(referenceDate);
      List<TestEvent> testEventList = new ArrayList<TestEvent>();
      testEventListMap.put(testCase, testEventList);
      for (int i = 1; i <= 9; i++) {
        String cvxCode = readField(shotCvxPos[i], testCaseFieldList);
        if (cvxCode.length() == 1) {
          cvxCode = "0" + cvxCode;
        }

        Date shotDate = readDateField(shotDatePos[i], testCaseFieldList, testCase);
        if (!cvxCode.equals("") && shotDate != null) {
          TestEvent testEvent = new TestEvent();
          Event event = cvxToEventMap.get(cvxCode);
          if (event == null) {
            throw new IllegalArgumentException("Unrecognized CVX code '" + cvxCode + "' for Shot" + i + " test case "
                + testCase.getTestCaseNumber() + "");
          }
          testEvent.setEvent(event);
          testEvent.setEventDate(shotDate);
          testEvent.setTestCase(testCase);
          testEventList.add(testEvent);
        }
      }
      ForecastItem forecastItem = forecastItemMap.get(testCase.getCategoryName());
      if (forecastItem == null) {
        boolean found = false;
        for (String ignoredItem : ignoredItems) {
          if (testCase.getCategoryName().equals(ignoredItem)) {
            found = true;
            continue;
          }
        }
        if (!found) {
          throw new IllegalArgumentException("Unrecognized category name '" + testCase.getCategoryName() + "'");
        }
      } else {
        ForecastExpected forecastExpected = new ForecastExpected();
        forecastExpected.setTestCase(testCase);
        forecastExpected.setAuthor(user);
        forecastExpected.setForecastItem(forecastItem);
        forecastExpected.setDoseNumber(readField(forecastNumPos, testCaseFieldList));
        forecastExpected.setValidDate(readDateField(earliestDatePos, testCaseFieldList, testCase));
        forecastExpected.setDueDate(readDateField(recDatePos, testCaseFieldList, testCase));
        forecastExpected.setOverdueDate(readDateField(overdueDatePos, testCaseFieldList, testCase));
        List<ForecastExpected> forecastExpectedList = forecastExpectedListMap.get(testCase);
        if (forecastExpectedList == null)
        {
          forecastExpectedList = new ArrayList<ForecastExpected>();
          forecastExpectedListMap.put(testCase, forecastExpectedList);
        }
        forecastExpectedList.add(forecastExpected);
        
      }
    }
  }


}
