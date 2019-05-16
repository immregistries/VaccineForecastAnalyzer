package org.immregistries.vfa.manager;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.immregistries.vfa.connect.model.Event;
import org.immregistries.vfa.connect.model.EventType;
import org.immregistries.vfa.connect.model.VaccineGroup;
import org.immregistries.vfa.model.ForecastExpected;
import org.immregistries.vfa.model.User;
import org.immregistries.vfa.connect.model.TestCase;
import org.immregistries.vfa.connect.model.TestEvent;

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

  private Map<String, VaccineGroup> vaccineGroupMap = new HashMap<String, VaccineGroup>();

  public void setVaccineGroups(Map<Integer, VaccineGroup> vaccineGroupListMap) {
    vaccineGroupMap.put("DTP", vaccineGroupListMap.get(2));
    vaccineGroupMap.put("DTaP", vaccineGroupListMap.get(2));
    vaccineGroupMap.put("H1N1", vaccineGroupListMap.get(3));
    vaccineGroupMap.put("HepA", vaccineGroupListMap.get(4));
    vaccineGroupMap.put("HepB", vaccineGroupListMap.get(5));
    vaccineGroupMap.put("HerpesZoster", vaccineGroupListMap.get(14));
    vaccineGroupMap.put("Hib", vaccineGroupListMap.get(6));
    vaccineGroupMap.put("HPV", vaccineGroupListMap.get(7));
    vaccineGroupMap.put("Influenza", vaccineGroupListMap.get(3));
    vaccineGroupMap.put("Flu", vaccineGroupListMap.get(3));
    vaccineGroupMap.put("MCV4", vaccineGroupListMap.get(8));
    vaccineGroupMap.put("MCV", vaccineGroupListMap.get(8));
    vaccineGroupMap.put("MMR", vaccineGroupListMap.get(9));
    vaccineGroupMap.put("MPSV", vaccineGroupListMap.get(8));
    vaccineGroupMap.put("Pneumo-Poly", vaccineGroupListMap.get(10));
    vaccineGroupMap.put("Pneumonia", vaccineGroupListMap.get(10));
    vaccineGroupMap.put("PCV", vaccineGroupListMap.get(10));
    vaccineGroupMap.put("Polio", vaccineGroupListMap.get(11));
    vaccineGroupMap.put("POL", vaccineGroupListMap.get(11));
    vaccineGroupMap.put("Rotavirus", vaccineGroupListMap.get(12));
    vaccineGroupMap.put("Rota", vaccineGroupListMap.get(12));
    vaccineGroupMap.put("Td/Tdap", vaccineGroupListMap.get(15));
    vaccineGroupMap.put("Varicella", vaccineGroupListMap.get(13));
    vaccineGroupMap.put("Var", vaccineGroupListMap.get(13));
    vaccineGroupMap.put("Typhoid", vaccineGroupListMap.get(0));
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
      if (event.getEventType() == EventType.VACCINATION) {
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
      VaccineGroup vaccineGroup = vaccineGroupMap.get(testCase.getCategoryName());
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
