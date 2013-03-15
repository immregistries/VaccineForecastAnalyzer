package org.tch.ft.manager;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.mapping.Array;
import org.tch.ft.model.Event;
import org.tch.ft.model.EventType;
import org.tch.ft.model.ForecastExpected;
import org.tch.ft.model.ForecastItem;
import org.tch.ft.model.TestCase;
import org.tch.ft.model.TestEvent;
import org.tch.ft.model.User;

public class StcTestCaseReader {

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

  private SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
  private List<List<String>> testCaseFieldListList = new ArrayList<List<String>>();
  private List<TestCase> testCaseList = new ArrayList<TestCase>();
  private List<String> headerFields = null;
  private Map<TestCase, ForecastExpected> forecastExpectedMap = new HashMap<TestCase, ForecastExpected>();
  private Map<TestCase, List<TestEvent>> testEventListMap = new HashMap<TestCase, List<TestEvent>>();
  private Map<String, Event> cvxToEventMap = new HashMap<String, Event>();
  private User user = null;

  public Map<TestCase, List<TestEvent>> getTestEventListMap() {
    return testEventListMap;
  }

  public User getUser() {
    return user;
  }

  private String problemText = null;

  public List<TestCase> getTestCaseList() {
    return testCaseList;
  }

  public Map<TestCase, ForecastExpected> getForecastExpectedMap() {
    return forecastExpectedMap;
  }

  public void setEventList(List<Event> eventList) {
    for (Event event : eventList) {
      if (event.getEventType() == EventType.VACCINE) {
        cvxToEventMap.put(event.getVaccineCvx(), event);
      }
    }
  }

  public void setUser(User user) {
    this.user = user;
  }

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
        forecastExpectedMap.put(testCase, forecastExpected);
      }
    }
  }

  private String readField(int position, List<String> testCaseFieldList) {
    if (position < testCaseFieldList.size()) {
      return testCaseFieldList.get(position).trim();
    }
    return "";
  }

  private Date readDateField(int position, List<String> testCaseFieldList, TestCase testCase) {
    String dateValue = readField(position, testCaseFieldList);
    if (dateValue.equals("")) {
      return null;
    }
    try {
      return sdf.parse(dateValue);
    } catch (ParseException parseException) {
      throw new IllegalArgumentException("Unable to parse date '" + dateValue + "' for test case "
          + testCase.getTestCaseNumber() + "");
    }
  }

  private int findFieldPos(String fieldName) {
    fieldName = fieldName.toUpperCase();
    for (int i = 0; i < headerFields.size(); i++) {
      String headerName = headerFields.get(i).toUpperCase();
      // System.out.print("[" + headerName + "]-");
      if (headerName.startsWith(fieldName)) {
        // System.out.println();
        return i;
      }
    }
    throw new IllegalArgumentException("Unable to find field that starts with '" + fieldName + "'");
  }

  private void readInputStream(InputStream in) throws IOException {
    List<String> testCaseFieldList = new ArrayList<String>();
    StringBuilder fieldValue = new StringBuilder();
    boolean quoted = false;
    boolean justQuoted = false;
    int nextChar = in.read();
    while (nextChar != -1) {
      if (nextChar == '"') {
        if (justQuoted) {
          fieldValue.append((char) nextChar);
          justQuoted = false;
        } else if (quoted) {
          quoted = false;
          justQuoted = true;
        } else {
          quoted = true;

        }
      } else if (!quoted && nextChar == ',') {
        testCaseFieldList.add(fieldValue.toString());
        fieldValue.setLength(0);
        justQuoted = false;
      } else {
        justQuoted = false;
        if (!quoted && nextChar == '\r') {
          testCaseFieldList.add(fieldValue.toString());
          fieldValue.setLength(0);
          if (testCaseFieldList.size() > 0 && testCaseFieldList.get(0).length() > 0) {
            testCaseFieldListList.add(testCaseFieldList);
          }
          testCaseFieldList = new ArrayList<String>();
        } else {
          if (nextChar >= ' ') {
            fieldValue.append((char) nextChar);
          } else {
            fieldValue.append(' ');
          }
        }
      }
      nextChar = in.read();
    }
    in.close();
  }

}
