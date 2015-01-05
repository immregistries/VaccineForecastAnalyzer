package org.tch.ft.manager.writers;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.tch.fc.model.Admin;
import org.tch.fc.model.EventType;
import org.tch.fc.model.TestCase;
import org.tch.fc.model.TestEvent;
import org.tch.ft.model.ForecastExpected;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.TestPanelCase;
import org.tch.ft.model.TestPanelEvaluation;
import org.tch.ft.model.TestPanelForecast;

public class CdcTestCaseWriter implements TestCaseWriter
{

  private static final String FIELD_CDC_TEST_ID = "CDC_Test_ID";
  private static final String FIELD_TEST_CASE_NAME = "Test_Case_Name";
  private static final String FIELD_DOB = "DOB";
  private static final String FIELD_GENDER = "Gender";
  private static final String FIELD_MED_HISTORY_TEXT = "Med_History_Text";
  private static final String FIELD_MED_HISTORY_CODE = "Med_History_Code";
  private static final String FIELD_MED_HISTORY_CODE_SYS = "Med_History_Code_Sys";
  private static final String FIELD_SERIES_STATUS = "Series_Status";

  private static final String FIELD_DATE_ADMINISTERED_ = "Date_Administered_";
  private static final String FIELD_VACCINE_NAME_ = "Vaccine_Name_";
  private static final String FIELD_CVX_ = "CVX_";
  private static final String FIELD_MVX_ = "MVX_";
  private static final String FIELD_EVALUATION_STATUS_ = "Evaluation_Status_";
  private static final String FIELD_EVALUATION_REASON_ = "Evaluation_Reason_";

  private static final String FIELD_FORECAST_NUM = "Forecast_#";
  private static final String FIELD_EARLIEST_DATE = "Earliest_Date";
  private static final String FIELD_RECOMMENDED_DATE = "Recommended_Date";
  private static final String FIELD_PAST_DUE_DATE = "Past_Due_Date";
  private static final String FIELD_VACCINE_GROUP = "Vaccine_Group";
  private static final String FIELD_ASSESSMENT_DATE = "Assessment_Date";
  private static final String FIELD_EVALUATION_TEST_TYPE = "Evaluation_Test_Type";
  private static final String FIELD_DATE_ADDED = "Date_added";
  private static final String FIELD_DATE_UPDATED = "Date_updated";
  private static final String FIELD_FORECAST_TEST_TYPE = "Forecast_Test_Type";
  private static final String FIELD_REASON_FOR_CHANGE = "Reason_For_Change";
  private static final String FIELD_CHANGED_IN_VERSION = "Changed_In_Version";

  private static final String[] firstPart = { FIELD_CDC_TEST_ID, FIELD_TEST_CASE_NAME, FIELD_DOB, FIELD_GENDER,
      FIELD_MED_HISTORY_TEXT, FIELD_MED_HISTORY_CODE, FIELD_MED_HISTORY_CODE_SYS, FIELD_SERIES_STATUS };
  private static final String[] middlePart = { FIELD_DATE_ADMINISTERED_, FIELD_VACCINE_NAME_, FIELD_CVX_, FIELD_MVX_,
      FIELD_EVALUATION_STATUS_, FIELD_EVALUATION_REASON_ };
  private static final String[] lastPart = { FIELD_FORECAST_NUM, FIELD_EARLIEST_DATE, FIELD_RECOMMENDED_DATE,
      FIELD_PAST_DUE_DATE, FIELD_VACCINE_GROUP, FIELD_ASSESSMENT_DATE, FIELD_EVALUATION_TEST_TYPE, FIELD_DATE_ADDED,
      FIELD_DATE_UPDATED, FIELD_FORECAST_TEST_TYPE, FIELD_REASON_FOR_CHANGE, FIELD_CHANGED_IN_VERSION };

  private TestPanel testPanel = null;
  private Set<String> categoryNameSet = null;
  private Session dataSession = null;

  public void setDataSession(Session dataSession) {
    this.dataSession = dataSession;
  }

  public void setCategoryNameSet(Set<String> categoryNameSet) {
    this.categoryNameSet = categoryNameSet;
  }

  public void setTestPanel(TestPanel testPanel) {
    this.testPanel = testPanel;
  }

  private static final String SERIES_STATUS_NOT_COMPLETE = "Not Complete";
  private static final String SERIES_STATUS_CONTRAINDICATED = "Contraindicated";
  private static final String SERIES_STATUS_COMPLETE = "Complete";
  private static final String SERIES_STATUS_AGED_OUT = "Aged Out";
  private static final String SERIES_STATUS_IMMUNE = "Immune";

  public void write(PrintWriter out) {
    for (String fieldName : firstPart) {
      out.print(f(fieldName));
    }
    for (int i = 1; i <= 7; i++) {
      for (String fieldName : middlePart) {
        out.print(f(fieldName + i));
      }
    }
    for (String fieldName : lastPart) {
      out.print(f(fieldName));
    }
    out.println();

    List<TestPanelForecast> testPanelForecastList;
    {
      Query query = dataSession
          .createQuery("from TestPanelForecast where testPanelCase.testPanel = ? and testPanelCase.resultStatus <> 'E' order by testPanelCase.categoryName, testPanelCase.testCase.label");
      query.setParameter(0, testPanel);
      if (categoryNameSet == null) {
        testPanelForecastList = query.list();
      } else {
        testPanelForecastList = new ArrayList<TestPanelForecast>();
        for (TestPanelForecast testPanelForecast : (List<TestPanelForecast>) query.list()) {
          if (categoryNameSet.contains(testPanelForecast.getTestPanelCase().getCategoryName())) {
            testPanelForecastList.add(testPanelForecast);
          }
        }
      }
    }

    for (TestPanelForecast testPanelForecast : testPanelForecastList) {
      TestPanelCase testPanelCase = testPanelForecast.getTestPanelCase();
      TestCase testCase = testPanelCase.getTestCase();
      ForecastExpected forecastExpected = testPanelForecast.getForecastExpected();
      out.print(f(testPanelCase.getTestCaseNumber()));
      out.print(f(testPanelCase.getCategoryName() + ": " + testCase.getLabel()));
      out.print(f(testCase.getPatientDob()));
      out.print(f(testCase.getPatientSex()));
      out.print(f("")); // Med history text
      out.print(f("")); // Med history code
      out.print(f("")); // Med history code sys
      String seriesStatus = SERIES_STATUS_COMPLETE;
      Admin admin = forecastExpected.getAdmin();
      if (admin != null) {
        switch (admin) {
        case AGED_OUT:
          seriesStatus = SERIES_STATUS_AGED_OUT;
          break;
        case ASSUMED_COMPLETE_OR_IMMUNE:
          seriesStatus = SERIES_STATUS_COMPLETE;
          break;
        case COMPLETE:
          seriesStatus = SERIES_STATUS_COMPLETE;
          break;
        case COMPLETE_FOR_SEASON:
          seriesStatus = SERIES_STATUS_COMPLETE;
          break;
        case CONTRAINDICATED:
          seriesStatus = SERIES_STATUS_CONTRAINDICATED;
          break;
        case DUE:
          seriesStatus = SERIES_STATUS_NOT_COMPLETE;
          break;
        case DUE_LATER:
          seriesStatus = SERIES_STATUS_NOT_COMPLETE;
          break;
        case ERROR:
          seriesStatus = SERIES_STATUS_COMPLETE;
          break;
        case FINISHED:
          seriesStatus = SERIES_STATUS_AGED_OUT;
          break;
        case IMMUNE:
          seriesStatus = SERIES_STATUS_IMMUNE;
          break;
        case NO_RESULTS:
          seriesStatus = SERIES_STATUS_COMPLETE;
          break;
        case NOT_COMPLETE:
          seriesStatus = SERIES_STATUS_NOT_COMPLETE;
          break;
        case OVERDUE:
          seriesStatus = SERIES_STATUS_NOT_COMPLETE;
          break;
        case UNKNOWN:
          seriesStatus = SERIES_STATUS_COMPLETE;
          break;
        }
      }
      out.print(f(seriesStatus));
      Query query = dataSession
          .createQuery("from TestEvent where testCase = ? and event.eventTypeCode = ? order by eventDate");
      query.setParameter(0, testCase);
      query.setParameter(1, EventType.VACCINATION.getEventTypeCode());
      List<TestEvent> testEventList = query.list();
      int count = 0;
      for (TestEvent testEvent : testEventList) {
        count++;
        if (count > 7) {
          break;
        }
        out.print(f(testEvent.getEventDate()));
        out.print(f(testEvent.getEvent().getLabel()));
        out.print(f(testEvent.getEvent().getVaccineCvx()));
        out.print(f(testEvent.getEvent().getVaccineMvx()));
        query = dataSession
            .createQuery("from TestPanelEvaluation where testPanelCase = ? and evaluationExpected.testEvent = ? ");
        query.setParameter(0, testPanelCase);
        query.setParameter(1, testEvent);
        List<TestPanelEvaluation> testPanelEvaluationList = query.list();
        if (testPanelEvaluationList.size() > 0) {
          out.print(f(testPanelEvaluationList.get(0).getEvaluationExpected().getEvaluation().getLabel()));
          out.print(f(testPanelEvaluationList.get(0).getEvaluationExpected().getEvaluationReason()));
        } else {
          out.print(",,");
        }
      }
      while (count < 7) {
        count++;
        for (int i = 0; i < middlePart.length; i++) {
          out.print(",");
        }
      }
      if (forecastExpected.getDoseNumber() != null && forecastExpected.getDoseNumber().equals("-")) {
        out.print(f(""));
      } else {
        out.print(f(forecastExpected.getDoseNumber()));
      }
      out.print(f(forecastExpected.getValidDate()));
      out.print(f(forecastExpected.getDueDate()));
      out.print(f(forecastExpected.getOverdueDate()));
      if (forecastExpected.getVaccineGroup().getMapToCdsiCode() == null) {
        out.print(f(forecastExpected.getVaccineGroup().getLabel()));
      } else {
        out.print(f(forecastExpected.getVaccineGroup().getMapToCdsiCode()));
      }
      out.print(f(testCase.getEvalDate()));
      if (testCase.getEvaluationType() == null) {
        out.print(f(""));
      } else {
        out.print(f(testCase.getEvaluationType().getLabel()));
      }
      out.print(f("")); // date added
      out.print(f("")); // date updated
      if (testCase.getForecastType() == null) {
        out.print(f(""));
      } else {
        out.print(f(testCase.getForecastType().getLabel()));
      }
      out.print(f("")); // reason for change
      out.print(f("")); // changed in version
      out.println();
    }

  }

  private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

  private String f(Date d) {
    if (d == null) {
      return f("");
    }
    return f(sdf.format(d));
  }

  private String f(String s) {
    if (s == null) {
      s = "";
    }
    return "\"" + s + "\",";
  }

}
