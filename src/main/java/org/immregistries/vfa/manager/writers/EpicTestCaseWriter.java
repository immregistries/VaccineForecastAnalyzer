package org.immregistries.vfa.manager.writers;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.immregistries.vfa.connect.model.Admin;
import org.immregistries.vfa.connect.model.RelativeRule;
import org.immregistries.vfa.connect.model.TestCase;
import org.immregistries.vfa.connect.model.TestEvent;
import org.immregistries.vfa.connect.model.RelativeRule.BeforeOrAfter;
import org.immregistries.vfa.model.ForecastExpected;
import org.immregistries.vfa.model.TestPanel;
import org.immregistries.vfa.model.TestPanelCase;
import org.immregistries.vfa.model.TestPanelForecast;
import org.immregistries.vfa.connect.model.RelativeTo;

public class EpicTestCaseWriter extends GeneralWriterSupport implements WriterInterface {
  /*
   * TODO NOTES:
   * 
   * Need to create initial export of just dates. Then create export that puts
   * out relative format too.
   * 
   * Then create option to download either CDC or Epic format.
   * 
   * That should be it
   */

  private static final String COL_DATE_OF_BIRTH = "Date of Birth";
  private static final String COL_SEX = "Sex";
  private static final String COL_EXPECTED = "Expected";
  private static final String COL_TEST_CASE_NAME = "Test Case Name";
  private static final String COL_DESCRIPTION = "Description";
  private static final String COL_DOSE_ = "Dose ";
  private static final String _VACCINE = " Vaccine";
  private static final String _DATE = " Date";

  public void write(PrintWriter out) {
    List<TestPanelForecast> testPanelForecastList = getTestPanelForecastList();

    int maxVaccinationCount = 0;
    for (TestPanelForecast testPanelForecast : testPanelForecastList) {
      TestPanelCase testPanelCase = testPanelForecast.getTestPanelCase();
      TestCase testCase = testPanelCase.getTestCase();
      List<TestEvent> testEventList = getTextEventList(testCase);
      if (testEventList.size() > maxVaccinationCount) {
        maxVaccinationCount = testEventList.size();
      }
    }

    out.print(COL_DATE_OF_BIRTH + "\t");
    out.print(COL_SEX + "\t");
    out.print(COL_EXPECTED + "\t");
    out.print(COL_TEST_CASE_NAME + "\t");
    out.print(COL_DESCRIPTION + "\t");
    for (int i = 0; i < maxVaccinationCount; i++) {
      out.print(COL_DOSE_ + (i + 1) + _VACCINE + "\t");
      out.print(COL_DOSE_ + (i + 1) + _DATE + "\t");
    }
    out.println();

    for (TestPanelForecast testPanelForecast : testPanelForecastList) {
      if (vaccineGroup != null && !testPanelForecast.getForecastExpected().getVaccineGroup().equals(vaccineGroup))
      {
        continue;
      }
      TestPanelCase testPanelCase = testPanelForecast.getTestPanelCase();
      TestCase testCase = testPanelCase.getTestCase();
      ForecastExpected forecastExpected = testPanelForecast.getForecastExpected();
      List<TestEvent> testEventList = getTextEventList(testCase);
      if (testCase.getEvalRule() == null) {
        out.print(f(testCase.getPatientDob()));
      } else {
        out.print(f(testCase.getEvalRule(), null));
      }
      out.print(f(testCase.getPatientSex()));
      if (forecastExpected.getAdmin() == Admin.AGED_OUT) {
        out.print(f("Aged Out"));
      } else if (forecastExpected.getAdmin() == Admin.ASSUMED_COMPLETE_OR_IMMUNE || forecastExpected.getAdmin() == Admin.COMPLETE
          || forecastExpected.getAdmin() == Admin.COMPLETE_FOR_SEASON || forecastExpected.getAdmin() == Admin.FINISHED
          || forecastExpected.getAdmin() == Admin.IMMUNE) {
        out.print(f("Completion"));
      } else if (forecastExpected.getAdmin() == Admin.CONTRAINDICATED) {
        out.print(f("Contraindication"));
      } else if (forecastExpected.getAdmin() == Admin.ERROR) {
        out.print(f("Error"));
      } else if (forecastExpected.getAdmin() == Admin.NO_RESULTS) {
        out.print(f("No Results"));
      } else if (forecastExpected.getAdmin() == Admin.UNKNOWN)
      {
        out.print(f("Unknown"));
      } else
      {
        if (forecastExpected.getDueRule() == null) {
          out.print(f(forecastExpected.getDueDate()));
        } else {
          out.print(f(forecastExpected.getDueRule(), testEventList));
        }
      }
      out.print(f(testCase.getCategoryName() + ": " + testCase.getLabel() + " (" + testCase.getTestCaseId() + ")"));
      out.print(f(testCase.getDescription()));

      for (TestEvent testEvent : testEventList) {
        out.print(f(testEvent.getEvent().getVaccineCvx()));
        if (testEvent.getEventRule() == null) {
          out.print(f(testEvent.getEventDate()));
        } else {
          out.print(f(testEvent.getEventRule(), testEventList));
        }
      }
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
    return s + "\t";
  }

  private String f(RelativeRule relativeRule, List<TestEvent> testEventList) {
    if (testEventList == null) {
      return f(relativeRule.getTimePeriod().toStringForEpic());
    } else {
      String mnemonic = "";
      if (relativeRule.getRelativeTo() == RelativeTo.BIRTH) {
        mnemonic = "%DOB";
      } else if (relativeRule.getRelativeTo() == RelativeTo.EVENT && relativeRule.getTestEvent() != null) {
        mnemonic = "%";
        int i = 0;
        for (TestEvent testEvent : testEventList) {
          i++;
          if (testEvent.equals(relativeRule.getTestEvent())) {
            mnemonic = "%" + i;
          }
        }
      }
      if (mnemonic.length() > 0) {
        if (relativeRule.getBeforeOrAfter() == BeforeOrAfter.BEFORE) {
          mnemonic += " - ";
        } else if (relativeRule.getBeforeOrAfter() == BeforeOrAfter.AFTER) {
          mnemonic += " + ";
        } else {
          return f(mnemonic);
        }
      }
      return f(mnemonic + relativeRule.getTimePeriod().toStringForEpic());
    }
  }

  public String createFilename() {
    return testPanel.getLabel() + ".txt";
  }

}
