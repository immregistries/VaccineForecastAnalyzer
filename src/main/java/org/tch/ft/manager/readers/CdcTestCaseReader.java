package org.tch.ft.manager.readers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tch.fc.model.Event;
import org.tch.fc.model.VaccineGroup;
import org.tch.fc.model.ForecastResult;
import org.tch.fc.model.TestCase;
import org.tch.fc.model.TestEvent;
import org.tch.ft.model.ForecastExpected;
import org.tch.ft.model.TestCaseWithExpectations;

import static org.tch.fc.model.VaccineGroup.*;

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

  private Map<String, VaccineGroup> vaccineGroupMap = new HashMap<String, VaccineGroup>();

  public void setVaccineGroupss(Map<Integer, VaccineGroup> vaccineGroupListMap) {
    vaccineGroupMap.put("DTaP".toUpperCase(), vaccineGroupListMap.get(ID_DTAP_TDAP_TD));
    vaccineGroupMap.put("HepA".toUpperCase(), vaccineGroupListMap.get(ID_HEPA));
    vaccineGroupMap.put("HepB".toUpperCase(), vaccineGroupListMap.get(ID_HEPB));
    vaccineGroupMap.put("Hep A".toUpperCase(), vaccineGroupListMap.get(ID_HEPA));
    vaccineGroupMap.put("Hep B".toUpperCase(), vaccineGroupListMap.get(ID_HEPB));
    vaccineGroupMap.put("Flu".toUpperCase(), vaccineGroupListMap.get(ID_INFLUENZA));
    vaccineGroupMap.put("Hib".toUpperCase(), vaccineGroupListMap.get(ID_HIB));
    vaccineGroupMap.put("HPV".toUpperCase(), vaccineGroupListMap.get(ID_HPV));
    vaccineGroupMap.put("MCV".toUpperCase(), vaccineGroupListMap.get(ID_MENING));
    vaccineGroupMap.put("MMR".toUpperCase(), vaccineGroupListMap.get(ID_MMR));
    vaccineGroupMap.put("PCV".toUpperCase(), vaccineGroupListMap.get(ID_PCV));
    vaccineGroupMap.put("POL".toUpperCase(), vaccineGroupListMap.get(ID_POLIO));
    vaccineGroupMap.put("POLIO".toUpperCase(), vaccineGroupListMap.get(ID_POLIO));
    vaccineGroupMap.put("IPV".toUpperCase(), vaccineGroupListMap.get(ID_POLIO));
    vaccineGroupMap.put("Rota".toUpperCase(), vaccineGroupListMap.get(ID_ROTA));
    vaccineGroupMap.put("Var".toUpperCase(), vaccineGroupListMap.get(ID_VAR));
    vaccineGroupMap.put("Typhoid".toUpperCase(), vaccineGroupListMap.get(ID_TYPHOID));
    vaccineGroupMap.put("Td".toUpperCase(), vaccineGroupListMap.get(ID_TD_ONLY));
    vaccineGroupMap.put("Tdap".toUpperCase(), vaccineGroupListMap.get(ID_TDAP_ONLY));
    vaccineGroupMap.put("Zoster".toUpperCase(), vaccineGroupListMap.get(ID_ZOSTER));
    vaccineGroupMap.put("Japanese Encephalitis".toUpperCase(), vaccineGroupListMap.get(ID_JAPENESE_ENCEPHALITIS));
    vaccineGroupMap.put("Rabies".toUpperCase(), vaccineGroupListMap.get(ID_RABIES));
    vaccineGroupMap.put("Yellow Fever".toUpperCase(), vaccineGroupListMap.get(ID_YELLOW_FEVER));
  }

  private String[] ignoredItems = { };

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
      TestCaseWithExpectations testCaseWithExpectations = new TestCaseWithExpectations();
      TestCase testCase = testCaseWithExpectations.getTestCase();
      testCaseList.add(testCaseWithExpectations);
      testCase.setTestCaseNumber(readField(testIdPosition, testCaseFieldList));
      testCase.setCategoryName(readField(vaccineGroupPos, testCaseFieldList));
      testCase.setLabel(testCase.getTestCaseNumber() + " " + testCase.getCategoryName() + " "
          + readField(evaluationTestTypePos, testCaseFieldList));
      testCase.setDescription(readField(testCaseNamePosition, testCaseFieldList));
      testCase.setPatientDob(readDateField(birthdatePos, testCaseFieldList, testCaseWithExpectations));
      testCase.setPatientSex(readField(genderPos, testCaseFieldList).toUpperCase().startsWith("M") ? "M" : "F");
      if (referenceDate == null) {
        referenceDate = readDateField(assessmentDatePos, testCaseFieldList, testCaseWithExpectations);
      }
      testCase.setEvalDate(referenceDate);
      List<TestEvent> testEventList = new ArrayList<TestEvent>();
      testCase.setTestEventList(testEventList);
      for (int i = 1; i <= 7; i++) {
        String cvxCode = readField(shotCvxPos[i], testCaseFieldList);
        if (cvxCode.length() == 1) {
          cvxCode = "0" + cvxCode;
        }

        Date shotDate = readDateField(shotDatePos[i], testCaseFieldList, testCaseWithExpectations);
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
      VaccineGroup vaccineGroup = vaccineGroupMap.get(testCase.getCategoryName().toUpperCase());
      if (vaccineGroup == null) {
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
        forecastExpected.setVaccineGroup(vaccineGroup);
        String seriesStatus = readField(seriesStatusPos, testCaseFieldList);
        if (!seriesStatus.equals("Not Completed"))
        {
          forecastExpected.setDoseNumber(readField(forecastNumPos, testCaseFieldList));
          forecastExpected.setValidDate(readDateField(earliestDatePos, testCaseFieldList, testCaseWithExpectations));
          forecastExpected.setDueDate(readDateField(recDatePos, testCaseFieldList, testCaseWithExpectations));
          forecastExpected.setOverdueDate(readDateField(overdueDatePos, testCaseFieldList, testCaseWithExpectations));
        }
        else
        {
          forecastExpected.setDoseNumber(ForecastResult.DOSE_NUMBER_COMPLETE);
        }
        List<ForecastExpected> forecastExpectedList = testCaseWithExpectations.getForecastExpectedList();
        if (forecastExpectedList == null) {
          forecastExpectedList = new ArrayList<ForecastExpected>();
          testCaseWithExpectations.setForecastExpectedList(forecastExpectedList);
        }
        forecastExpectedList.add(forecastExpected);

      }
    }
  }

}
