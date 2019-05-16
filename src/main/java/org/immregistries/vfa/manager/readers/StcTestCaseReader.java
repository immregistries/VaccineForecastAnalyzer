package org.immregistries.vfa.manager.readers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.immregistries.vfa.connect.model.Admin;
import org.immregistries.vfa.connect.model.Event;
import org.immregistries.vfa.connect.model.VaccineGroup;
import org.immregistries.vfa.model.ForecastExpected;
import org.immregistries.vfa.model.TestCaseWithExpectations;
import org.immregistries.vfa.connect.model.ForecastResult;
import org.immregistries.vfa.connect.model.TestCase;
import org.immregistries.vfa.connect.model.TestEvent;

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

  private Map<String, VaccineGroup> vaccineGroupMap = new HashMap<String, VaccineGroup>();

  public void setVaccineGroupss(Map<Integer, VaccineGroup> vaccineGroupListMap) {
    vaccineGroupMap.put("1", vaccineGroupListMap.get(VaccineGroup.ID_DTAP));
    vaccineGroupMap.put("2", vaccineGroupListMap.get(VaccineGroup.ID_HIB));
    vaccineGroupMap.put("3", vaccineGroupListMap.get(VaccineGroup.ID_POLIO));
    vaccineGroupMap.put("4", vaccineGroupListMap.get(VaccineGroup.ID_HEPB));
    vaccineGroupMap.put("12", vaccineGroupListMap.get(VaccineGroup.ID_HEPB));
    vaccineGroupMap.put("5", vaccineGroupListMap.get(VaccineGroup.ID_MMR));
    vaccineGroupMap.put("6", vaccineGroupListMap.get(VaccineGroup.ID_VAR));
    vaccineGroupMap.put("7", vaccineGroupListMap.get(VaccineGroup.ID_MENING));
    vaccineGroupMap.put("9", vaccineGroupListMap.get(VaccineGroup.ID_HEPA));
    vaccineGroupMap.put("10", vaccineGroupListMap.get(VaccineGroup.ID_INFLUENZA));
    vaccineGroupMap.put("11", vaccineGroupListMap.get(VaccineGroup.ID_PNEUMO));
    vaccineGroupMap.put("18", vaccineGroupListMap.get(VaccineGroup.ID_PPSV));
    vaccineGroupMap.put("20", vaccineGroupListMap.get(VaccineGroup.ID_ROTA));
    vaccineGroupMap.put("21", vaccineGroupListMap.get(VaccineGroup.ID_HPV));
    vaccineGroupMap.put("22", vaccineGroupListMap.get(VaccineGroup.ID_ZOSTER));

    // vaccineGroupMap.put("Td/Tdap", vaccineGroupListMap.get(15));
    // vaccineGroupMap.put("MPSV", vaccineGroupListMap.get(8));
  }

  private String[] ignoredItems = { "15"/* Measles */, "16" /* Mumps */, "17" /* Rubella */};

  private static Map<String, String> antigenToCvxMap = new HashMap<String, String>();

  static {
    antigenToCvxMap.put("Botulism IG, human, intravenous", "27");
    antigenToCvxMap.put("CMVIG", "29");
    antigenToCvxMap.put("DT (Pediatric)", "28");
    antigenToCvxMap.put("DTaP", "20");
    antigenToCvxMap.put("DTaP/Hep B/IPV", "146");
    antigenToCvxMap.put("DTaP/Hib/IPV", "120");
    antigenToCvxMap.put("DTaP/IPV", "130");
    antigenToCvxMap.put("DTP", "01");
    antigenToCvxMap.put("HBIG", "30");
    antigenToCvxMap.put("Hep A 2 dose - Adult", "83");
    antigenToCvxMap.put("Hep A 3 dose - Ped/Adol", "84");
    antigenToCvxMap.put("Hep A/Hep B - Adult", "104");
    antigenToCvxMap.put("Hep B 2 dose - Adol/Adult", "43");
    antigenToCvxMap.put("Hepatitis B--adol. or pediatric", "08");
    antigenToCvxMap.put("Hepatitis B--adult", "43");
    antigenToCvxMap.put("Hib--HbOC", "47");
    antigenToCvxMap.put("Hib--PRP-OMP", "49");
    antigenToCvxMap.put("Hib--PRP-T", "48");
    antigenToCvxMap.put("HPV, bivalent", "118");
    antigenToCvxMap.put("HPV, quadrivalent", "62");
    antigenToCvxMap.put("IG", "14");
    antigenToCvxMap.put("Immune globulin, (IGIV)", "87");
    antigenToCvxMap.put("Influ Inact 48+ mos pres free", "140");
    antigenToCvxMap.put("Influ Inact 9+ yrs pres free", "140");
    antigenToCvxMap.put("Influ split 18+ yrs", "140");
    antigenToCvxMap.put("Influenza Nasal Spray", "111");
    antigenToCvxMap.put("Influenza Split", "15");
    antigenToCvxMap.put("Influenza split, 6-35 mos.", "141");
    antigenToCvxMap.put("Influenza, High Dose", "135");
    antigenToCvxMap.put("IPV", "10");
    antigenToCvxMap.put("MCV4, unspecified formulation", "147");
    antigenToCvxMap.put("Measles", "05");
    antigenToCvxMap.put("Mening. (MCV4O)", "136");
    antigenToCvxMap.put("Meningococcal (MPSV4)", "32");
    antigenToCvxMap.put("MMR", "03");
    antigenToCvxMap.put("MMR/Varicella", "94");
    antigenToCvxMap.put("Mumps", "07");
    antigenToCvxMap.put("Novel H1N1,All Formulations", "128");
    antigenToCvxMap.put("OPV", "02");
    antigenToCvxMap.put("Pneumococcal - unspecified", "152");
    antigenToCvxMap.put("Pneumococcal(PCV)", "152");
    antigenToCvxMap.put("Pneumococcal(PPSV)", "33");
    antigenToCvxMap.put("Pneumococcal, PCV-13", "133");
    antigenToCvxMap.put("RIG", "34");
    antigenToCvxMap.put("Rotavirus, monovalent RV1", "119");
    antigenToCvxMap.put("Rotavirus, pentavalent RV5", "74");
    antigenToCvxMap.put("RSV-IGIV", "71");
    antigenToCvxMap.put("Rubella", "06");
    antigenToCvxMap.put("Td (Adult)", "138");
    antigenToCvxMap.put("Tdap", "115");
    antigenToCvxMap.put("TIG", "13");
    antigenToCvxMap.put("Varicella", "21");
    antigenToCvxMap.put("VZIG", "36");
    antigenToCvxMap.put("VZIG (IND)", "117");
    antigenToCvxMap.put("Zoster, live", "121");

  }

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

    int antigenPos = findFieldPos(FIELD_ANTIGEN);
    int doseNumPos = findFieldPos(FIELD_DOSE_NUMBER);
    int earliestDatePos = findFieldPos(FIELD_MIN_DATE);
    int recDatePos = findFieldPos(FIELD_REC_DATE);
    int overdueDatePos = findFieldPos(FIELD_PAST_DUE);
    int referenceDatePos = findFieldPos(FIELD_EVAL_DATE);
    int maxDatePos = findFieldPos(FIELD_MAX_DATE);
    int vaccDatePos = findFieldPos(FIELD_VACC_DATE);
    int cvxPos = findFieldPos(FIELD_CVX);
    int familyCodePos = findFieldPos(FIELD_FAMILY_CODE);

    TestCaseWithExpectations testCaseWithExpectations = null;
    TestCase testCase = null;
    List<TestEvent> testEventList = null;
    int testCaseNumberCount = 0;
    String currentTestCaseId = "";
    for (List<String> testCaseFieldList : testCaseFieldListList) {
      String testCaseId = readField(testCaseIdPosition, testCaseFieldList);
      String antigen = readField(antigenPos, testCaseFieldList);
      String familyCode = readField(familyCodePos, testCaseFieldList);
      if (antigen.equals("") && testCaseId.equals("") && familyCode.equals("")) {
        continue;
      }
      if (!testCaseId.equals("")) {
        currentTestCaseId = testCaseId;
        testCaseNumberCount = 0;
        testCaseWithExpectations = new TestCaseWithExpectations();
        testCase = testCaseWithExpectations.getTestCase();
        testCase.setTestCaseNumber(testCaseId + "." + testCaseNumberCount);
        testCase.setLabel("Test Case " + testCase.getTestCaseNumber());
        testCase.setDescription(readField(scheduleDescriptionPosition, testCaseFieldList));
        testCase.setPatientDob(readDateField(birthdatePos, testCaseFieldList, testCaseWithExpectations));
        testCase.setPatientSex(readField(genderPos, testCaseFieldList).toUpperCase().startsWith("M") ? "M" : "F");
        testCase.setCategoryName(readCategoryName(testCase.getTestCaseNumber()));
        testEventList = new ArrayList<TestEvent>();
        testCase.setTestEventList(testEventList);
        Date referenceDate = readDateField(referenceDatePos, testCaseFieldList, testCaseWithExpectations);
        if (referenceDate == null) {
          referenceDate = new Date();
        }
        testCase.setEvalDate(referenceDate);
        testCaseList.add(testCaseWithExpectations);
      } else if (!antigen.equals("")) {
        testCaseNumberCount++;
        testCaseWithExpectations = new TestCaseWithExpectations(testCase);
        testCase = testCaseWithExpectations.getTestCase();
        testEventList = testCase.getTestEventList();
        testCase.setTestCaseNumber(currentTestCaseId + "." + testCaseNumberCount);
        testCase.setLabel("Test Case " + testCase.getTestCaseNumber());
        Date referenceDate = readDateField(referenceDatePos, testCaseFieldList, testCaseWithExpectations);
        if (referenceDate == null) {
          referenceDate = new Date();
        }
        testCase.setEvalDate(referenceDate);
        testCaseList.add(testCaseWithExpectations);
      }
      if (!antigen.equals("")) {
        String cvxCode = readField(cvxPos, testCaseFieldList);
        if (cvxCode.equals("")) {
          cvxCode = antigenToCvxMap.get(antigen);
          if (cvxCode == null) {
            throw new IllegalArgumentException("Unrecognized Antigen code '" + antigen + "' for Shot test case "
                + testCase.getTestCaseNumber() + "");
          }
        }
        if (cvxCode.length() == 1) {
          cvxCode = "0" + cvxCode;
        }

        Date vaccDate = readDateField(vaccDatePos, testCaseFieldList, testCaseWithExpectations);
        if (!cvxCode.equals("") && vaccDate != null) {
          TestEvent testEvent = new TestEvent();
          Event event = cvxToEventMap.get(cvxCode);
          if (event == null) {
            throw new IllegalArgumentException("Unrecognized CVX code '" + cvxCode + "' for Shot test case "
                + testCase.getTestCaseNumber() + "");
          }
          testEvent.setEvent(event);
          testEvent.setEventDate(vaccDate);
          testEvent.setTestCase(testCase);
          testEventList.add(testEvent);
        }
      }
      if (!familyCode.equals("")) {
        VaccineGroup vaccineGroup = vaccineGroupMap.get(familyCode);
        if (vaccineGroup == null) {
          boolean found = false;
          for (String ignoredItem : ignoredItems) {
            if (familyCode.equals(ignoredItem)) {
              found = true;
              continue;
            }
          }
          if (!found) {
            throw new IllegalArgumentException("Unrecognized family code '" + familyCode + "'");
          }
        } else {
          String doseNumber = readField(doseNumPos, testCaseFieldList);
          String validDateString = readField(earliestDatePos, testCaseFieldList);
          ForecastExpected forecastExpected = new ForecastExpected();
          forecastExpected.setTestCase(testCase);
          forecastExpected.setAuthor(user);
          forecastExpected.setVaccineGroup(vaccineGroup);
          if (doseNumber.equals("-") || validDateString.equals("-")) {
            forecastExpected.setAdmin(Admin.COMPLETE);
          } else {
            forecastExpected.setDoseNumber(doseNumber);
            forecastExpected.setValidDate(readDateField(earliestDatePos, testCaseFieldList, testCaseWithExpectations));
            forecastExpected.setDueDate(readDateField(recDatePos, testCaseFieldList, testCaseWithExpectations));
            forecastExpected.setOverdueDate(readDateField(overdueDatePos, testCaseFieldList, testCaseWithExpectations));
            forecastExpected.setFinishedDate(readDateField(maxDatePos, testCaseFieldList, testCaseWithExpectations));
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

  private static String readCategoryName(String testCaseId) {
    testCaseId = testCaseId.trim();
    for (int i = 0; i < testCaseId.length(); i++) {
      if (testCaseId.charAt(i) < 'A') {
        return testCaseId.substring(0, i);
      }
    }
    return testCaseId;
  }

}
