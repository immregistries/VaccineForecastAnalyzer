package org.tch.ft.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.tch.fc.model.DateSet;
import org.tch.fc.model.Event;
import org.tch.fc.model.EventType;
import org.tch.fc.model.ForecastActual;
import org.tch.fc.model.Software;
import org.tch.fc.model.SoftwareResult;
import org.tch.fc.model.TestCase;
import org.tch.fc.model.TestEvent;
import org.tch.fc.model.VaccineGroup;
import org.tch.ft.CentralControl;
import org.tch.ft.model.ForecastCvx;
import org.tch.ft.model.ForecastExpected;
import org.tch.ft.model.Include;
import org.tch.ft.model.Result;
import org.tch.ft.model.TaskGroup;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.TestPanelCase;
import org.tch.ft.model.TestPanelForecast;
import org.tch.ft.model.User;
import org.tch.ft.web.testCase.RandomNames;

public class ExternalTestServlet extends HttpServlet

{
  private static final String POST_TASK_GROUP_ID = "taskGroupId";
  private static final String TEST_PANEL_LABEL = "testPanelLabel";

  private static final String POST_SOFTWARE_ID = "softwareId";
  private static final String POST_TEST_CASE_ID = "testCaseId";
  private static final String POS_LOG_TEXT = "logText";

  private static final String POST_PATIENT_SEX = "patientSex";
  private static final String POST_PATIENT_DOB = "patientDob";
  private static final String POST_TEST_CASE_NUMBER = "testCaseNumber";
  private static final String POST_USER_NAME = "userName";

  private static final String POST_VACCINATION_DATE = "vaccinationDate";
  private static final String POST_VACCINATION_CVX = "vaccinationCvx";

  private static final String POST_FINISHED_DATE = "finishedDate";
  private static final String POST_OVERDUE_DATE = "overdueDate";
  private static final String POST_DUE_DATE = "dueDate";
  private static final String POST_VALID_DATE = "validDate";
  private static final String POST_DOSE_NUMBER = "doseNumber";
  private static final String POST_SCHEDULE_NAME = "scheduleName";
  private static final String POST_FORECAST_CVX = "forecastCvx";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    PrintWriter out = new PrintWriter(resp.getOutputStream());
    resp.setContentType("text/plain");
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      int testPanelId = Integer.parseInt(req.getParameter("testPanelId"));
      SessionFactory factory = CentralControl.getSessionFactory();
      Session dataSession = factory.openSession();

      TestPanel testPanel = (TestPanel) dataSession.get(TestPanel.class, testPanelId);

      Query query = dataSession
          .createQuery("from TestPanelCase where testPanel = ? and includeStatus = 'I' order by categoryName");
      query.setParameter(0, testPanel);
      List<TestPanelCase> testPanelCaseList = query.list();
      for (TestPanelCase testPanelCase : testPanelCaseList) {
        out.println("testPanelCase.testPanelCaseId=" + testPanelCase.getTestPanelCaseId());
        out.println("testPanelCase.categoryName=" + testPanelCase.getCategoryName());
        out.println("testPanelCase.testCaseNumber=" + testPanelCase.getTestCaseNumber());
        out.println("testCase.testCaseId=" + testPanelCase.getTestCase().getTestCaseId());
        out.println("testCase.label=" + testPanelCase.getTestCase().getLabel());
        out.println("testCase.description=" + testPanelCase.getTestCase().getDescription());
        out.println("testCase.evalDate=" + testPanelCase.getTestCase().getEvalDate());
        out.println("testCase.patientFirst=" + testPanelCase.getTestCase().getPatientFirst());
        out.println("testCase.patientLast=" + testPanelCase.getTestCase().getPatientLast());
        out.println("testCase.patientSex=" + testPanelCase.getTestCase().getPatientSex());
        out.println("testCase.patientDob=" + sdf.format(testPanelCase.getTestCase().getPatientDob()));

        query = dataSession.createQuery("from TestEvent where testCase = ?");
        query.setParameter(0, testPanelCase.getTestCase());
        List<TestEvent> testEventList = query.list();
        for (TestEvent testEvent : testEventList) {
          out.println("testEvent.testEventId=" + testEvent.getTestEventId());
          out.println("testEvent.label=" + testEvent.getEvent().getLabel());
          out.println("testEvent.eventTypeCode=" + testEvent.getEvent().getEventTypeCode());
          out.println("testEvent.vaccineCvx=" + testEvent.getEvent().getVaccineCvx());
          out.println("testEvent.vaccineMvx=" + testEvent.getEvent().getVaccineMvx());
          out.println("testEvent.eventDate=" + sdf.format(testEvent.getEventDate()));
        }

        query = dataSession.createQuery("from TestPanelForecast where testPanelCase = ?");
        query.setParameter(0, testPanelCase);
        List<TestPanelForecast> testPanelForecastList = query.list();
        for (TestPanelForecast testPanelForecast : testPanelForecastList) {
          ForecastExpected forecastExpected = testPanelForecast.getForecastExpected();
          out.println("forecastExpected.forecastExpectedId=" + forecastExpected.getForecastExpectedId());
          out.println("forecastExpected.doseNumber=" + forecastExpected.getDoseNumber());
          if (forecastExpected.getValidDate() != null) {
            out.println("forecastExpected.validDate=" + sdf.format(forecastExpected.getValidDate()));
          }
          if (forecastExpected.getDueDate() != null) {
            out.println("forecastExpected.dueDate=" + sdf.format(forecastExpected.getDueDate()));
          }
          if (forecastExpected.getOverdueDate() != null) {
            out.println("forecastExpected.overdueDate=" + sdf.format(forecastExpected.getOverdueDate()));
          }
          if (forecastExpected.getFinishedDate() != null) {
            out.println("forecastExpected.finishedDate=" + sdf.format(forecastExpected.getFinishedDate()));
          }
          if (forecastExpected.getVaccineCvx() != null && !forecastExpected.getVaccineCvx().equals("")) {
            out.println("forecastExpected.vaccineCvx=" + forecastExpected.getVaccineCvx());
          } else {
            out.println("forecastExpected.vaccineCvx=" + forecastExpected.getVaccineGroup().getVaccineCvx());
          }
        }
      }

      dataSession.close();
    } finally {
      out.close();
    }
  }

  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    System.out.println("--> Reporting results");
    PrintWriter out = new PrintWriter(resp.getOutputStream());
    resp.setContentType("text/plain");
    Session dataSession = null;
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

      SessionFactory factory = CentralControl.getSessionFactory();
      dataSession = factory.openSession();
      Transaction transaction = dataSession.beginTransaction();

      boolean createTestCaseAndExpectations = req.getParameter(POST_TASK_GROUP_ID) != null;
      TestCase testCase;
      if (createTestCaseAndExpectations) {
        testCase = createTestCase(req, dataSession, sdf);
      } else {
        testCase = findTestCase(req, dataSession);
      }
      Software software = findSoftware(req, dataSession);

      SoftwareResult softwareResult = loadOrCreateSoftwareResult(dataSession, testCase, software);
      updateSoftwareResult(req, dataSession, softwareResult);
      User author = readAuthor(req, dataSession);

      if (createTestCaseAndExpectations) {
        int pos = 1;
        String vaccinationCvxString;
        while ((vaccinationCvxString = req.getParameter(POST_VACCINATION_CVX + pos)) != null) {
          if (!vaccinationCvxString.equals("") && !vaccinationCvxString.equals("998")) {
            Event event = getVaccinationEvent(dataSession, vaccinationCvxString);
            if (event == null)
            {
              event = getVaccinationEvent(dataSession, "999");
            }
            
            pos++;
          }
        }
      }
      {
        int pos = 1;
        String forecastCvxString;
        while ((forecastCvxString = req.getParameter(POST_FORECAST_CVX + pos)) != null) {
          Query query = dataSession.createQuery("from ForecastCvx where vaccineCvx = ?");
          query.setParameter(0, forecastCvxString);
          List<ForecastCvx> forecastCvxList = query.list();
          for (ForecastCvx forecastCvx : forecastCvxList) {
            saveForecastActual(req, dataSession, sdf, createTestCaseAndExpectations, testCase, softwareResult, author,
                pos, forecastCvxString, forecastCvx);
          }
          pos++;
        }
      }

      transaction.commit();

      // System.out.println("--> Saved " + (pos - 1) + " results");
      dataSession.close();
      dataSession = null;
      out.println("OK");
    } catch (ParseException pe) {
      throw new ServletException("Unable to parse input data", pe);
    } finally {
      out.close();
      if (dataSession != null) {
        dataSession.close();
      }
    }

  }

  private Event getVaccinationEvent(Session dataSession, String vaccinationCvxString) {
    Event event = null;
    Query query = dataSession.createQuery("from Event where eventTypeCode = ? and vaccineCvx = ?");
    query.setParameter(0, EventType.VACCINATION.getEventTypeCode());
    query.setParameter(1, vaccinationCvxString);
    List<Event> eventList = query.list();
    if (eventList.size() > 0) {
      event = eventList.get(0);
    }
    return event;
  }

  private User readAuthor(HttpServletRequest req, Session dataSession) {
    User author = null;
    if (req.getParameter(POST_USER_NAME) != null) {
      Query query = dataSession.createQuery("from User where name = ?");
      query.setParameter(0, req.getParameter(POST_USER_NAME));
      List<User> userList = query.list();
      if (userList.size() > 0) {
        author = userList.get(0);
      }
    }
    return author;
  }

  private void saveForecastActual(HttpServletRequest req, Session dataSession, SimpleDateFormat sdf,
      boolean createTestCaseAndExpectations, TestCase testCase, SoftwareResult softwareResult, User author, int pos,
      String forecastCvxString, ForecastCvx forecastCvx) throws ParseException {
    ForecastActual forecastActual = new ForecastActual();
    forecastActual.setSoftwareResult(softwareResult);
    forecastActual.setTestCase(testCase);
    forecastActual.setVaccineGroup(forecastCvx.getVaccineGroup());
    forecastActual.setVaccineCvx(forecastCvxString);
    if (req.getParameter(POST_SCHEDULE_NAME + pos) != null) {
      forecastActual.setScheduleName(req.getParameter(POST_SCHEDULE_NAME + pos));
    } else {
      forecastActual.setScheduleName("");
    }
    forecastActual.getSoftwareResult().setRunDate(new Date());
    String doseNumber = req.getParameter(POST_DOSE_NUMBER + pos);
    if (doseNumber == null) {
      doseNumber = "*";
    }
    forecastActual.setDoseNumber(doseNumber);
    if (req.getParameter(POST_VALID_DATE + pos) != null && !req.getParameter(POST_VALID_DATE + pos).equals("")) {
      forecastActual.setValidDate(sdf.parse(req.getParameter(POST_VALID_DATE + pos)));
    } else {
      forecastActual.setValidDate(null);
    }
    if (req.getParameter(POST_DUE_DATE + pos) != null && !req.getParameter(POST_DUE_DATE + pos).equals("")) {
      forecastActual.setDueDate(sdf.parse(req.getParameter(POST_DUE_DATE + pos)));
    } else {
      forecastActual.setDueDate(null);
    }
    if (req.getParameter(POST_OVERDUE_DATE + pos) != null && !req.getParameter(POST_OVERDUE_DATE + pos).equals("")) {
      forecastActual.setOverdueDate(sdf.parse(req.getParameter(POST_OVERDUE_DATE + pos)));
    } else {
      forecastActual.setOverdueDate(null);
    }
    if (req.getParameter(POST_FINISHED_DATE + pos) != null && !req.getParameter(POST_FINISHED_DATE + pos).equals("")) {
      forecastActual.setFinishedDate(sdf.parse(req.getParameter(POST_FINISHED_DATE + pos)));
    } else {
      forecastActual.setFinishedDate(null);
    }
    dataSession.saveOrUpdate(forecastActual);
    if (author != null && createTestCaseAndExpectations) {
      ForecastExpected forecastExpected = new ForecastExpected();
      forecastExpected.setTestCase(testCase);
      forecastExpected.setAuthor(author);
      forecastExpected.setUpdatedDate(new Date());
      forecastExpected.setVaccineGroup(forecastCvx.getVaccineGroup());
      forecastExpected.setDoseNumber(doseNumber);
      forecastExpected.setValidDate(forecastActual.getValidDate());
      forecastExpected.setDueDate(forecastActual.getDueDate());
      forecastExpected.setOverdueDate(forecastActual.getOverdueDate());
      forecastExpected.setFinishedDate(forecastActual.getFinishedDate());
      forecastExpected.setVaccineCvx(forecastActual.getVaccineCvx());
      forecastExpected.setForecastReason(forecastActual.getForecastReason());
      dataSession.saveOrUpdate(forecastExpected);
    }
  }

  private void updateSoftwareResult(HttpServletRequest req, Session dataSession, SoftwareResult softwareResult) {
    String logText = req.getParameter(POS_LOG_TEXT);
    if (logText == null) {
      logText = "";
    }
    softwareResult.setRunDate(new Date());
    softwareResult.setLogText(logText);
    dataSession.saveOrUpdate(softwareResult);
  }

  private SoftwareResult loadOrCreateSoftwareResult(Session dataSession, TestCase testCase, Software software) {
    SoftwareResult softwareResult = null;
    {
      Query query = dataSession.createQuery("from SoftwareResult where testCase = ? and software = ?");
      query.setParameter(0, testCase);
      query.setParameter(1, software);
      List<SoftwareResult> softwareResultList = query.list();
      boolean firstOne = true;
      for (SoftwareResult sr : softwareResultList) {
        {
          query = dataSession.createQuery("delete ForecastActual where softwareResult = ?");
          query.setParameter(0, sr);
          query.executeUpdate();
        }
        {
          query = dataSession.createQuery("delete EvaluationActual where softwareResult = ?");
          query.setParameter(0, sr);
          query.executeUpdate();
        }
        if (firstOne) {
          softwareResult = sr;
          firstOne = false;
        } else {
          dataSession.delete(sr);
        }
      }
      if (softwareResult == null) {
        softwareResult = new SoftwareResult();
        softwareResult.setSoftware(software);
        softwareResult.setTestCase(testCase);
      }
    }
    return softwareResult;
  }

  private Software findSoftware(HttpServletRequest req, Session dataSession) {
    int softwareId = Integer.parseInt(req.getParameter(POST_SOFTWARE_ID));
    Software software = (Software) dataSession.get(Software.class, softwareId);
    return software;
  }

  private TestCase findTestCase(HttpServletRequest req, Session dataSession) {
    TestCase testCase;
    int testCaseId = Integer.parseInt(req.getParameter(POST_TEST_CASE_ID));
    testCase = (TestCase) dataSession.get(TestCase.class, testCaseId);
    return testCase;
  }

  private TestCase createTestCase(HttpServletRequest req, Session dataSession, SimpleDateFormat sdf)
      throws ParseException {
    TestCase testCase;
    int taskGroupId = Integer.parseInt(req.getParameter(POST_TASK_GROUP_ID));
    TaskGroup taskGroup = (TaskGroup) dataSession.get(TaskGroup.class, taskGroupId);
    String testPanelLabel = req.getParameter(TEST_PANEL_LABEL);
    TestPanel testPanel;
    {
      Query query = dataSession.createQuery("from TestPanel where taskGroup = ? and label = ?");
      query.setParameter(0, taskGroup);
      query.setParameter(1, testPanelLabel);
      List<TestPanel> testPanelList = query.list();
      if (testPanelList.size() == 0) {
        testPanel = new TestPanel();
        testPanel.setTaskGroup(taskGroup);
        testPanel.setLabel(testPanelLabel);
        dataSession.save(testPanel);
      } else {
        testPanel = testPanelList.get(0);
      }
    }
    String patientSex = req.getParameter(POST_PATIENT_SEX);
    Date patientDob = sdf.parse(req.getParameter(POST_PATIENT_DOB));
    String categoryName = determineCategoryName(patientDob);
    String testCaseNumber = req.getParameter(POST_TEST_CASE_NUMBER);

    testCase = new TestCase();
    testCase.setPatientFirst(RandomNames.getRandomFirstName());
    testCase.setPatientLast(RandomNames.getRandomLastName());
    testCase.setLabel(testCase.getPatientFirst() + " " + testCase.getPatientLast());
    testCase.setDescription("Test case built from forecast results from external source.");
    testCase.setEvalDate(new Date());
    testCase.setPatientSex(patientSex);
    testCase.setPatientDob(patientDob);
    testCase.setDateSet(DateSet.FIXED);
    testCase.setVaccineGroup(VaccineGroup.getForecastItem(VaccineGroup.ID_DTAP_TDAP_TD));
    dataSession.save(testCase);

    TestPanelCase testPanelCase = new TestPanelCase();
    testPanelCase.setTestPanel(testPanel);
    testPanelCase.setTestCase(testCase);
    testPanelCase.setCategoryName(categoryName);
    testPanelCase.setInclude(Include.INCLUDED);
    testPanelCase.setResult(Result.ACCEPT);
    testPanelCase.setTestCaseNumber(testCaseNumber);
    dataSession.save(testCaseNumber);
    return testCase;
  }

  private String determineCategoryName(Date patientDob) {
    String categoryLabel;
    {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.MONTH, -18);
      if (cal.before(patientDob)) {
        // Baby 0-18 months
        categoryLabel = "Baby";
      } else {
        cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -4);
        if (cal.before(patientDob)) {
          // Toddler 18 months - 4 years
          categoryLabel = "Toddler";
        } else {
          cal = Calendar.getInstance();
          cal.add(Calendar.YEAR, -7);
          if (cal.before(patientDob)) {
            // Young Child 4 years - 7 years
            categoryLabel = "Young Child";
          } else {
            cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -10);
            if (cal.before(patientDob)) {
              // Child 7 years - 10 years
              categoryLabel = "Child";
            } else {
              cal = Calendar.getInstance();
              cal.add(Calendar.YEAR, -13);
              if (cal.before(patientDob)) {
                // Tween 10 years - 13 years
                categoryLabel = "Tween";
              } else {
                cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, -20);
                if (cal.before(patientDob)) {
                  // Teenager 13 years - 20 years
                  categoryLabel = "Teenager";
                } else {
                  cal = Calendar.getInstance();
                  cal.add(Calendar.YEAR, -25);
                  if (cal.before(patientDob)) {
                    // Young Adult 19 years - 25 years
                    categoryLabel = "Young Adult";
                  } else {
                    cal = Calendar.getInstance();
                    cal.add(Calendar.YEAR, -55);
                    if (cal.before(patientDob)) {
                      // Adult 25 years - 55
                      categoryLabel = "Adult";
                    } else {
                      // Senior 55 years or older
                      categoryLabel = "Senior";
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return categoryLabel;
  }

}
