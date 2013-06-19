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
import org.tch.ft.model.Software;
import org.tch.ft.model.TestCase;
import org.tch.ft.model.TestEvent;

public class StcTestCaseReader extends CsvTestCaseReader implements TestCaseReader {

  private static final String FIELD_SCHEDULE_DESCRIPTION = "Schedule Description";
  private static final String FIELD_EVAL_DATE = "Eval Date";
  private static final String FIELD_CONFIGURATION = "Configuration";
  private static final String FIELD_TEST_CASE_ID = "Test Case ID";
  private static final String FIELD_DOB = "DOB";
  private static final String FIELD_GENDER = "Gender";
  private static final String FIELD_ANTIGEN = "Antigen";
  private static final String FIELD_ASIIS = "ASIIS";
  private static final String FIELD_CPT = "CPT";
  private static final String FIELD_CVX = "CVX";
  private static final String FIELD_VACC_DATE = "Vacc Date";
  private static final String FIELD_CONTRAINDICATED = "Contraindicated (Y/N)";
  private static final String FIELD_VALID = "Valid (Y/N)";
  private static final String FIELD_VACCINATION_MESSAGES = "Vaccination Messages";
  private static final String FIELD_FAMILY_CODE = "Family Code";
  private static final String FIELD_DOSE_NUMBER = "Dose #";
  private static final String FIELD_REC_DATE = "Rec Date";
  private static final String FIELD_MIN_DATE = "Min Date";
  private static final String FIELD_PAST_DUE = "Past Due";
  private static final String FIELD_MAX_DATE = "Max Date";
  private static final String FIELD_COMMENTS = "Comments";
  private static final String FIELD_EVALUATE = "Evaluate (Y/N)";
  private static final String FIELD_TEST_RESULTS = "Test Results";
  private static final String FIELD_SOAP_REQUEST = "SOAP Request";
  private static final String FIELD_SOAP_RESPONSE = "SOAP Response";

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

    int scheduleDescriptionPosition = findFieldPos(FIELD_SCHEDULE_DESCRIPTION);
    
    int testCaseIdPosition = findFieldPos(FIELD_TEST_CASE_ID);
    int birthdatePos = findFieldPos(FIELD_DOB);
    int genderPos = findFieldPos(FIELD_GENDER);
    
    int forecastNumPos = findFieldPos(FIELD_DOSE_NUMBER);
    int earliestDatePos = findFieldPos(FIELD_MIN_DATE);
    int recDatePos = findFieldPos(FIELD_REC_DATE);
    int overdueDatePos = findFieldPos(FIELD_PAST_DUE);
    int referenceDatePos = findFieldPos(FIELD_EVAL_DATE);

    Date referenceDate = null;
    TestCase testCase = null;
    for (List<String> testCaseFieldList : testCaseFieldListList) {
      String testCaseId = readField(testCaseIdPosition, testCaseFieldList);
      if (!testCaseId.equals(""))
      {
        
      }
      else
      {
        
      }
      testCaseList.add(testCase);
      testCase.setTestCaseNumber(readField(testCaseIdPosition, testCaseFieldList));
      testCase.setCategoryName(readCategoryName(testCase.getTestCaseNumber()));
      testCase.setLabel("Test Case " + testCase.getTestCaseNumber());
      testCase.setDescription(readField(0, testCaseFieldList));
      testCase.setPatientDob(readDateField(birthdatePos, testCaseFieldList, testCase));
      testCase.setPatientSex(readField(genderPos, testCaseFieldList).toUpperCase().startsWith("M") ? "M" : "F");
      if (referenceDate == null) {
        referenceDate = readDateField(referenceDatePos, testCaseFieldList, testCase);
      }
      testCase.setEvalDate(referenceDate);
      List<TestEvent> testEventList = new ArrayList<TestEvent>();
      testCase.setTestEventList(testEventList);
        String cvxCode = readField(0, testCaseFieldList);
        if (cvxCode.length() == 1) {
          cvxCode = "0" + cvxCode;
        }

       // Date shotDate = readDateField(shotDatePos[i], testCaseFieldList, testCase);
        // if (!cvxCode.equals("") && shotDate != null) 
        {
          TestEvent testEvent = new TestEvent();
          Event event = cvxToEventMap.get(cvxCode);
          if (event == null) {
            throw new IllegalArgumentException("Unrecognized CVX code '" + cvxCode + "' for Shot test case "
                + testCase.getTestCaseNumber() + "");
          }
          testEvent.setEvent(event);
         // testEvent.setEventDate(shotDate);
          testEvent.setTestCase(testCase);
          testEventList.add(testEvent);
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
        List<ForecastExpected> forecastExpectedList = testCase.getForecastExpectedList();
        if (forecastExpectedList == null) {
          forecastExpectedList = new ArrayList<ForecastExpected>();
          testCase.setForecastExpectedList(forecastExpectedList);
        }
        forecastExpectedList.add(forecastExpected);

      }
    }
  }
  private static String readCategoryName(String testCaseId)
  {
    testCaseId = testCaseId.trim();
    for (int i = 0; i < testCaseId.length(); i++)
    {
      if (testCaseId.charAt(i) < 'A')
      {
        return testCaseId.substring(0, i);
      }
    }
    return testCaseId;
  }

}
