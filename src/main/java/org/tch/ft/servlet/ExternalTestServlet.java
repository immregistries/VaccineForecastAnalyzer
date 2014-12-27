package org.tch.ft.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.tch.fc.model.ForecastActual;
import org.tch.fc.model.VaccineGroup;
import org.tch.fc.model.Software;
import org.tch.fc.model.TestCase;
import org.tch.fc.model.TestEvent;
import org.tch.ft.CentralControl;
import org.tch.ft.model.ForecastCvx;
import org.tch.ft.model.ForecastExpected;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.TestPanelCase;
import org.tch.ft.model.TestPanelForecast;

public class ExternalTestServlet extends HttpServlet

{
  private static final String POST_FINISHED_DATE = "finishedDate";
  private static final String POST_OVERDUE_DATE = "overdueDate";
  private static final String POST_DUE_DATE = "dueDate";
  private static final String POST_VALID_DATE = "validDate";
  private static final String POST_DOSE_NUMBER = "doseNumber";
  private static final String POST_LOG = "log";
  private static final String POST_SCHEDULE_NAME = "scheduleName";
  private static final String POST_FORECAST_CVX = "forecastCvx";
  private static final String POST_SOFTWARE_ID = "softwareId";
  private static final String POST_TEST_CASE_ID = "testCaseId";

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
    PrintWriter out = new PrintWriter(resp.getOutputStream());
    resp.setContentType("text/plain");
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

      int testCaseId = Integer.parseInt(req.getParameter(POST_TEST_CASE_ID));
      int softwareId = Integer.parseInt(req.getParameter(POST_SOFTWARE_ID));
      String forecastCvxString = req.getParameter(POST_FORECAST_CVX);

      SessionFactory factory = CentralControl.getSessionFactory();
      Session dataSession = factory.openSession();

      Transaction transaction = dataSession.beginTransaction();

      TestCase testCase = (TestCase) dataSession.get(TestCase.class, testCaseId);
      Software software = (Software) dataSession.get(Software.class, softwareId);

      Query query = dataSession.createQuery("from ForecastCvx where vaccineCvx = ?");
      query.setParameter(0, forecastCvxString);
      List<ForecastCvx> forecastCvxList = query.list();
      

      for (ForecastCvx forecastCvx : forecastCvxList) {


        query = dataSession.createQuery("from ForecastActual where softwareResult.testCase = ? and softwareResult.software = ? and vaccineGroup = ?");
        query.setParameter(0, testCase);
        query.setParameter(1, software);
        query.setParameter(2, forecastCvx.getVaccineGroup());
        List<ForecastActual> forecastActualList = query.list();
        ForecastActual forecastActual = null;
        if (forecastActualList.size() > 0) {
          forecastActual = forecastActualList.get(0);
        } else {
          forecastActual = new ForecastActual();
          forecastActual.setTestCase(testCase);
          forecastActual.setVaccineGroup(forecastCvx.getVaccineGroup());
          forecastActual.getSoftwareResult().setSoftware(software);
        }
        forecastActual.setVaccineCvx(forecastCvxString);
        if (req.getParameter(POST_SCHEDULE_NAME) != null) {
          forecastActual.setScheduleName(req.getParameter(POST_SCHEDULE_NAME));
        } else {
          forecastActual.setScheduleName("");
        }
        forecastActual.getSoftwareResult().setRunDate(new Date());
        forecastActual.getSoftwareResult().setLogText(req.getParameter(POST_LOG));
        if (req.getParameter(POST_DOSE_NUMBER) != null) {
          forecastActual.setDoseNumber(req.getParameter(POST_DOSE_NUMBER));
        } else {
          forecastActual.setDoseNumber("*");
        }
        if (req.getParameter(POST_VALID_DATE) != null && !req.getParameter(POST_VALID_DATE).equals("")) {
          forecastActual.setValidDate(sdf.parse(req.getParameter(POST_VALID_DATE)));
        } else {
          forecastActual.setValidDate(null);
        }
        if (req.getParameter(POST_DUE_DATE) != null && !req.getParameter(POST_DUE_DATE).equals("")) {
          forecastActual.setDueDate(sdf.parse(req.getParameter(POST_DUE_DATE)));
        } else {
          forecastActual.setDueDate(null);
        }
        if (req.getParameter(POST_OVERDUE_DATE) != null && !req.getParameter(POST_OVERDUE_DATE).equals("")) {
          forecastActual.setOverdueDate(sdf.parse(req.getParameter(POST_OVERDUE_DATE)));
        } else {
          forecastActual.setOverdueDate(null);
        }
        if (req.getParameter(POST_FINISHED_DATE) != null && !req.getParameter(POST_FINISHED_DATE).equals("")) {
          forecastActual.setFinishedDate(sdf.parse(req.getParameter(POST_FINISHED_DATE)));
        } else {
          forecastActual.setFinishedDate(null);
        }
        dataSession.saveOrUpdate(forecastActual);
      }

      transaction.commit();

      dataSession.close();
      out.println("OK");
    } catch (ParseException pe) {
      throw new ServletException("Unable to parse input data", pe);
    } finally {
      out.close();
    }

  };
}