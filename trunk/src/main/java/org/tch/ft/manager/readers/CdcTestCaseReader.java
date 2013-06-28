package org.tch.ft.manager.readers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tch.fc.model.Event;
import org.tch.fc.model.ForecastItem;
import org.tch.fc.model.ForecastResult;
import org.tch.fc.model.TestEvent;
import org.tch.ft.model.ForecastExpected;
import org.tch.ft.model.TestCaseWithExpectations;

public class CdcTestCaseReader extends CsvTestCaseReader implements TestCaseReader {

  private static final String FIELD_CDC_TEST_ID = "CDC_Test_ID";
  private static final String FIELD_TEST_CASE_NAME = "Test_Case_Name";
  private static final String FIELD_DOB = "DOB";
  private static final String FIELD_GENDER = "Gender";
//  private static final String FIELD_MED_HISTORY_TEXT = "Med_History_Text";
//  private static final String FIELD_MED_HISTORY_CODE = "Med_History_Code";
//  private static final String FIELD_MED_HISTORY_CODE_SYS = "Med_History_Code_Sys";
  private static final String FIELD_SERIES_STATUS = "Series_Status";

  private static final String FIELD_DATE_ADMINISTERED_ = "Date_Administered_";
//   private static final String FIELD_VACCINE_NAME_ = "Vaccine_Name_";
  private static final String FIELD_CVX_ = "CVX_";
  private static final String FIELD_MVX_ = "MVX_";
//  private static final String FIELD_EVALUATION_STATUS_ = "Evaluation_Status_";
//  private static final String FIELD_EVALUATION_REASON_ = "Evaluation_Reason_";

  private static final String FIELD_FORECAST_NUM = "Forecast_#";
  private static final String FIELD_EARLIEST_DATE = "Earliest_Date";
  private static final String FIELD_RECOMMENDED_DATE = "Recommended_Date";
  private static final String FIELD_PAST_DUE_DATE = "Past_Due_Date";
  private static final String FIELD_VACCINE_GROUP = "Vaccine_Group";
  private static final String FIELD_ASSESSMENT_DATE = "Assessment_Date";
  private static final String FIELD_EVALUATION_TEST_TYPE = "Evaluation_Test_Type";
//  private static final String FIELD_DATE_ADDED = "Date_added";
//  private static final String FIELD_DATE_UPDATED = "Date_updated";
//  private static final String FIELD_FORECAST_TEST_TYPE = "Forecast_Test_Type";

  private Map<String, ForecastItem> forecastItemMap = new HashMap<String, ForecastItem>();

  public void setForecastItems(Map<Integer, ForecastItem> forecastItemListMap) {
    forecastItemMap.put("DTaP", forecastItemListMap.get(2));
    forecastItemMap.put("HepA", forecastItemListMap.get(4));
    forecastItemMap.put("HepB", forecastItemListMap.get(5));
    forecastItemMap.put("Flu", forecastItemListMap.get(3));
    forecastItemMap.put("Hib", forecastItemListMap.get(6));
    forecastItemMap.put("HPV", forecastItemListMap.get(7));
    forecastItemMap.put("MCV", forecastItemListMap.get(8));
    forecastItemMap.put("MMR", forecastItemListMap.get(9));
    forecastItemMap.put("PCV", forecastItemListMap.get(10));
    forecastItemMap.put("POL", forecastItemListMap.get(11));
    forecastItemMap.put("Rota", forecastItemListMap.get(12));
    forecastItemMap.put("Var", forecastItemListMap.get(13));
  }

  private String[] ignoredItems = { "Typhoid" };

  public void read(InputStream in) throws IOException {
    readInputStream(in);
    if (testCaseFieldListList.size() <= 1) {
      throw new IllegalArgumentException("No test cases found");
    }
    headerFields = testCaseFieldListList.get(0);
    testCaseFieldListList.remove(0);

    int testIdPosition = findFieldPos(FIELD_CDC_TEST_ID);
    int testCaseNamePosition = findFieldPos(FIELD_TEST_CASE_NAME);
    int birthdatePos = findFieldPos(FIELD_DOB);
    int genderPos = findFieldPos(FIELD_GENDER);
    int vaccineGroupPos = findFieldPos(FIELD_VACCINE_GROUP);
    int shotCvxPos[] = new int[10];
    int shotDatePos[] = new int[10];
    int shotMvxPos[] = new int[8];
    for (int i = 1; i <= 7; i++) {
      shotCvxPos[i] = findFieldPos(FIELD_CVX_ + i);
      shotDatePos[i] = findFieldPos(FIELD_DATE_ADMINISTERED_ + i);
      shotMvxPos[i] = findFieldPos(FIELD_MVX_ + i);
    }
    int forecastNumPos = findFieldPos(FIELD_FORECAST_NUM);
    int earliestDatePos = findFieldPos(FIELD_EARLIEST_DATE);
    int recDatePos = findFieldPos(FIELD_RECOMMENDED_DATE);
    int overdueDatePos = findFieldPos(FIELD_PAST_DUE_DATE);
    int assessmentDatePos = findFieldPos(FIELD_ASSESSMENT_DATE);
    int evaluationTestTypePos = findFieldPos(FIELD_EVALUATION_TEST_TYPE);
    int seriesStatusPos = findFieldPos(FIELD_SERIES_STATUS);
    
    Date referenceDate = null;
    for (List<String> testCaseFieldList : testCaseFieldListList) {
      TestCaseWithExpectations testCase = new TestCaseWithExpectations();
      testCaseList.add(testCase);
      testCase.setTestCaseNumber(readField(testIdPosition, testCaseFieldList));
      testCase.setCategoryName(readField(vaccineGroupPos, testCaseFieldList));
      testCase.setLabel(testCase.getTestCaseNumber() + " " + testCase.getCategoryName() + " "
          + readField(evaluationTestTypePos, testCaseFieldList));
      testCase.setDescription(readField(testCaseNamePosition, testCaseFieldList));
      testCase.setPatientDob(readDateField(birthdatePos, testCaseFieldList, testCase));
      testCase.setPatientSex(readField(genderPos, testCaseFieldList).toUpperCase().startsWith("M") ? "M" : "F");
      if (referenceDate == null) {
        referenceDate = readDateField(assessmentDatePos, testCaseFieldList, testCase);
      }
      testCase.setEvalDate(referenceDate);
      List<TestEvent> testEventList = new ArrayList<TestEvent>();
      testCase.setTestEventList(testEventList);
      for (int i = 1; i <= 7; i++) {
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
        String seriesStatus = readField(seriesStatusPos, testCaseFieldList);
        if (!seriesStatus.equals("Not Completed"))
        {
          forecastExpected.setDoseNumber(readField(forecastNumPos, testCaseFieldList));
          forecastExpected.setValidDate(readDateField(earliestDatePos, testCaseFieldList, testCase));
          forecastExpected.setDueDate(readDateField(recDatePos, testCaseFieldList, testCase));
          forecastExpected.setOverdueDate(readDateField(overdueDatePos, testCaseFieldList, testCase));
        }
        else
        {
          forecastExpected.setDoseNumber(ForecastResult.DOSE_NUMBER_COMPLETE);
        }
        List<ForecastExpected> forecastExpectedList = testCase.getForecastExpectedList();
        if (forecastExpectedList == null) {
          forecastExpectedList = new ArrayList<ForecastExpected>();
          testCase.setForecastExpectedList(forecastExpectedList);
        }
        forecastExpectedList.add(forecastExpected);

      }
    }
  }

}
