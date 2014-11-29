package org.tch.ft.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.tch.fc.model.Admin;
import org.tch.fc.model.EventType;
import org.tch.fc.model.ForecastActual;
import org.tch.fc.model.Software;
import org.tch.fc.model.TestCase;
import org.tch.fc.model.TestEvent;
import org.tch.fc.model.VaccineGroup;
import org.tch.ft.manager.ForecastActualExpectedCompare;
import org.tch.ft.manager.SoftwareManager;
import org.tch.ft.model.ForecastExpected;
import org.tch.ft.model.TaskGroup;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.TestPanelCase;
import org.tch.ft.model.TestPanelForecast;
import org.tch.ft.model.User;
import org.tch.ft.web.testCase.RandomNames;

public class TestCasesServlet extends MainServlet
{
  public TestCasesServlet() {
    super("Test Cases", ServletProtection.ALL_USERS);
  }

  public static final String ACTION_SELECT_TASK_GROUP = "Select Task Group";
  public static final String ACTION_SELECT_TEST_PANEL = "Select Test Panel";
  public static final String ACTION_SELECT_TEST_PANEL_CASE = "Select Test Panel Case";
  public static final String ACTION_SELECT_CATEGORY = "Select Category";

  public static final String PARAM_TASK_GROUP_ID = "taskGroupId";
  public static final String PARAM_TEST_PANEL_ID = "testPanelId";
  public static final String PARAM_TEST_PANEL_CASE_ID = "testPanelCaseId";
  public static final String PARAM_CATEGORY_NAME = "categoryName";

  public static final String PARAM_LABEL = "label";
  public static final String PARAM_DESCRIPTION = "description";
  public static final String PARAM_PATIENT_FIRST = "patientFirst";
  public static final String PARAM_PATIENT_LAST = "patientLast";
  public static final String PARAM_PATIENT_SEX = "patientSex";
  public static final String PARAM_PATIENT_DOB = "patientDob";
  public static final String EVALUATION_DATE = "evaluationDate";
  public static final String PARAM_DATE_SET_CODE = "dateSetCode";
  public static final String PARAM_TEST_CASE_NUMBER = "testCaseNumber";

  public static final String SHOW_TEST_CASE = "testCase";
  public static final String SHOW_TASK_GROUP = "taskGroup";
  public static final String SHOW_TEST_PANEL = "testPanel";
  public static final String SHOW_ADD_TEST_CASE = "addTestCase";

  @Override
  public String execute(HttpServletRequest req, HttpServletResponse resp, String action) throws IOException {

    if (action != null) {
      Session dataSession = applicationSession.getDataSession();
      if (action.equals(ACTION_SELECT_TASK_GROUP)) {
        int taskGroupId = Integer.parseInt(req.getParameter(PARAM_TASK_GROUP_ID));
        Transaction trans = dataSession.beginTransaction();
        TaskGroup taskGroup = (TaskGroup) dataSession.get(TaskGroup.class, taskGroupId);
        User user = applicationSession.getUser();
        user.setSelectedTaskGroup(taskGroup);
        user.setSelectedTestPanel(null);
        user.setSelectedTestPanelCase(null);
        user.setSelectedCategoryName(null);
        user.setSelectedSoftware(taskGroup.getPrimarySoftware());
        user.setSelectedSoftwareCompare(null);
        dataSession.update(user);
        trans.commit();
        return SHOW_TASK_GROUP;
      } else if (action.equals(ACTION_SELECT_TEST_PANEL)) {
        int testPanelId = Integer.parseInt(req.getParameter(PARAM_TEST_PANEL_ID));
        Transaction trans = dataSession.beginTransaction();
        TestPanel testPanel = (TestPanel) dataSession.get(TestPanel.class, testPanelId);
        User user = applicationSession.getUser();
        user.setSelectedTestPanel(testPanel);
        user.setSelectedTestPanelCase(null);
        user.setSelectedCategoryName(null);
        dataSession.update(user);
        trans.commit();
        return SHOW_TEST_PANEL;
      } else if (action.equals(ACTION_SELECT_TEST_PANEL_CASE)) {
        int testPanelCaseId = Integer.parseInt(req.getParameter(PARAM_TEST_PANEL_CASE_ID));
        Transaction trans = dataSession.beginTransaction();
        TestPanelCase testPanelCase = (TestPanelCase) dataSession.get(TestPanelCase.class, testPanelCaseId);
        User user = applicationSession.getUser();
        user.setSelectedTestPanelCase(testPanelCase);
        user.setSelectedCategoryName(null);
        dataSession.update(user);
        trans.commit();
        return SHOW_TEST_CASE;
      } else if (action.equals(ACTION_SELECT_CATEGORY)) {
        applicationSession.getUser().setSelectedCategoryName(req.getParameter(PARAM_CATEGORY_NAME));
        return SHOW_TEST_CASE;
      }
    }
    return SHOW_TEST_CASE;
  }

  @Override
  protected void printPage(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, String show)
      throws ServletException, IOException {
    Session dataSession = applicationSession.getDataSession();

    User user = applicationSession.getUser();

    if (user.getSelectedCategoryName() == null && user.getSelectedTestPanelCase() != null) {
      user.setSelectedCategoryName(user.getSelectedTestPanelCase().getCategoryName());
    }

    printTree(out, dataSession, user);

    if (SHOW_TEST_CASE.equals(show)) {
      if (user.getSelectedTaskGroup() != null && user.getSelectedTestPanel() != null
          && user.getSelectedTestPanelCase() != null) {
        printTestCase(out, user);
        printExpectations(out, user);
      }
    } else if (SHOW_ADD_TEST_CASE.equals(show)) {
      String label = notNull(req.getParameter(PARAM_LABEL));
      String description = notNull(req.getParameter(PARAM_DESCRIPTION));
      String patientFirst = notNull(req.getParameter(PARAM_PATIENT_FIRST), RandomNames.getRandomFirstName());
      String patientLast = notNull(req.getParameter(PARAM_PATIENT_LAST));
      String patientSex = notNull(req.getParameter(PARAM_PATIENT_SEX));
      String patientDob = notNull(req.getParameter(PARAM_PATIENT_DOB));
      String categoryName = notNull(req.getParameter(PARAM_CATEGORY_NAME));
      String testCaseNumber = notNull(req.getParameter(PARAM_TEST_CASE_NUMBER));
      String dateSetCode = notNull(req.getParameter(PARAM_DATE_SET_CODE));
      out.println("<div class=\"centerLeftColumn\">");
      out.println("  <h2>Add Test Case</h2>");
      out.println("    <form method=\"POST\" action=\"home\">");
      out.println("      <table width=\"100%\">");
      out.println("        <tr>");
      out.println("          <th>Category</th>");
      out.println("          <td><input type=\"text\" name=\"" + PARAM_CATEGORY_NAME + "\" size=\"50\" value=\"" + categoryName
          + "\"/></td>");
      out.println("        </tr>");
      out.println("        <tr>");
      out.println("          <th>Label</th>");
      out.println("          <td><input type=\"text\" name=\"" + PARAM_LABEL + "\" size=\"50\" value=\"" + label
          + "\"/></td>");
      out.println("        </tr>");
      out.println("        <tr>");
      out.println("          <th>Description</th>");
      out.println("          <td><textarea type=\"text\" name=\"" + PARAM_DESCRIPTION + "\" cols=\"50\" rows=\"3\">"
          + description + "</textarea></td>");
      out.println("        </tr>");
      out.println("        <tr>");
      out.println("          <th>Patient First</th>");
      out.println("          <td><input type=\"text\" name=\"" + PARAM_PATIENT_FIRST + "\" size=\"15\" value=\""
          + patientFirst + "\"/></td>");
      out.println("        </tr>");
      out.println("        <tr>");
      out.println("          <th>Patient Last</th>");
      out.println("          <td><input type=\"text\" name=\"" + PARAM_PATIENT_LAST + "\" size=\"15\" value=\""
          + patientLast + "\"/></td>");
      out.println("        </tr>");
      out.println("        <tr>");
      out.println("          <th>Patient Sex</th>");
      out.println("          <td>");
      out.println("            <select type=\"text\" name=\"" + PARAM_PATIENT_SEX + "\">");
      if (patientSex.equals("F") || patientSex.equals("")) {
        out.println("              <option value=\"F\" selected=\"selected\">F</option>");
      } else {
        out.println("              <option value=\"F\">F</option>");
      }
      if (patientSex.equals("M")) {
        out.println("              <option value=\"M\" selected=\"selected\">M</option>");
      } else {
        out.println("              <option value=\"M\">M</option>");
      }
      out.println("            </select");
      out.println("          </td>");
      out.println("        </tr>");
      out.println("        <tr>");
      out.println("          <th>Date of Birth</th>");
      out.println("          <td><input type=\"text\" name=\"" + PARAM_PATIENT_DOB + "\" size=\"10\" value=\""
          + patientDob + "\"/></td>");
      out.println("        </tr>");
      out.println("      </table>");
      out.println("    </form>");
      out.println("</div>");
      /*
        <tr>
          <td>Evaluation Date</td>
          <td><input type="text" wicket:id="evalDate" size="10" /></td>
        </tr>
        <tr>
          <td>Category Name</td>
          <td><input type="text" / wicket:id="categoryName" size="30"></td>
        </tr>
        <tr>
          <td>Test Case Number</td>
          <td><input type="text" / wicket:id="testCaseNumber" size="5"></td>
        </tr>
        <tr>
          <td colspan="2" align="right"><input type="submit"
            value="save" class="fauxbutton" /></td>
        </tr>
       */
    }

  }

  public void printTree(PrintWriter out, Session dataSession, User user) throws UnsupportedEncodingException {
    Query query = dataSession.createQuery("from TaskGroup order by label");
    List<TaskGroup> taskGroupList = query.list();

    out.println("<div class=\"leftColumn\">");
    if (user.getSelectedTaskGroup() != null) {
      out.println("<h2>" + user.getSelectedTaskGroup().getLabel() + " Test Cases</h2>");
      out.println("<ul class=\"selectLevel1\">");
      printTaskGroupList(out, dataSession, user, user.getSelectedTaskGroup());
      out.println("</ul>");
      out.println("<h2>Other Expert Groups</h2>");
      out.println("<ul class=\"selectLevel1\">");
    } else {
      out.println("<h2>Select Expert Groups</h2>");
      out.println("<ul class=\"selectLevel1\">");
    }
    for (TaskGroup taskGroup : taskGroupList) {
      if (user.getSelectedTaskGroup() == null || !user.getSelectedTaskGroup().equals(taskGroup)) {
        printTaskGroupList(out, dataSession, user, taskGroup);
      }
    }
    out.println("</ul>");
    out.println("</div>");
  }

  public void printExpectations(PrintWriter out, User user) {
    out.println("<div class=\"centerRightColumn\">");

    Session dataSession = applicationSession.getDataSession();

    TestPanelCase testPanelCase = user.getSelectedTestPanelCase();
    TestCase testCase = testPanelCase.getTestCase();
    TaskGroup taskGroup = user.getSelectedTaskGroup();
    TestPanel testPanel = user.getSelectedTestPanel();
    Software software = user.getSelectedSoftware();
    SoftwareManager.initSoftware(software, dataSession);

    List<ForecastActualExpectedCompare> forecastCompareList = new ArrayList<ForecastActualExpectedCompare>();

    {
      ForecastActual forecastActual = null;
      ForecastExpected forecastExpected = null;

      if (testCase != null && testPanel != null) {
        Query query = dataSession
            .createQuery("from TestPanelForecast where testPanelCase.testCase = ? and testPanelCase.testPanel = ?");
        query.setParameter(0, testCase);
        query.setParameter(1, testPanel);
        List<TestPanelForecast> testPanelForecastList = query.list();
        for (TestPanelForecast testPanelForecast : testPanelForecastList) {
          forecastExpected = testPanelForecast.getForecastExpected();
          ForecastActualExpectedCompare forecastCompare = new ForecastActualExpectedCompare();
          forecastCompare.setForecastResultA(forecastExpected);
          forecastCompare.setVaccineGroup(forecastExpected.getVaccineGroup());
          forecastCompareList.add(forecastCompare);
          if (software != null) {
            query = dataSession
                .createQuery("from ForecastActual where softwareResult.software = ? and softwareResult.testCase = ? and vaccineGroup = ? order by softwareResult.runDate desc");
            query.setParameter(0, software);
            query.setParameter(1, testCase);
            query.setParameter(2, forecastExpected.getVaccineGroup());
            List<ForecastActual> forecastActualList = query.list();
            if (forecastActualList.size() > 0) {
              forecastActual = forecastActualList.get(0);
            }
            forecastCompare.setForecastResultB(forecastActual);
          }
        }
      }
    }

    if (forecastCompareList.size() > 0) {
      for (ForecastActualExpectedCompare forecastCompare : forecastCompareList) {
        final VaccineGroup vaccineGroup = forecastCompare.getVaccineGroup();
        out.println("<h2>" + vaccineGroup.getLabel() + " Forecast</h2>");
        out.println("  <table width=\"100%\">");
        out.println("    <tr>");
        out.println("      <th>Entity</th>");
        out.println("      <th>Status</th>");
        out.println("      <th>Dose</th>");
        out.println("      <th>Valid</th>");
        out.println("      <th>Due</th>");
        out.println("      <th>Overdue</th>");
        out.println("    </tr>");

        ForecastActual forecastActual = (ForecastActual) forecastCompare.getForecastResultB();
        final ForecastExpected forecastExpected = (ForecastExpected) forecastCompare.getForecastResultA();

        List<ForecastExpected> otherExpectedList;
        List<ForecastActual> otherActualList;

        Query query = dataSession.createQuery("from ForecastExpected where testCase = ? and vaccineGroup = ?");
        query.setParameter(0, testCase);
        query.setParameter(1, forecastExpected.getVaccineGroup());
        otherExpectedList = query.list();
        if (forecastExpected != null) {
          for (Iterator<ForecastExpected> it = otherExpectedList.iterator(); it.hasNext();) {
            ForecastExpected fe = it.next();
            if (fe.getForecastExpectedId() == forecastExpected.getForecastExpectedId()) {
              it.remove();
            }
          }
        }
        query = dataSession
            .createQuery("from ForecastActual where softwareResult.testCase = ? and vaccineGroup = ? order by softwareResult.runDate desc");
        query.setParameter(0, testCase);
        query.setParameter(1, forecastExpected.getVaccineGroup());
        otherActualList = query.list();
        {
          Set<Software> softwareSet = new HashSet<Software>();
          if (forecastActual != null) {
            softwareSet.add(forecastActual.getSoftwareResult().getSoftware());
          }
          for (Iterator<ForecastActual> it = otherActualList.iterator(); it.hasNext();) {
            ForecastActual fa = it.next();
            if (SoftwareManager.isSoftwareAccessRestricted(fa.getSoftwareResult().getSoftware(), user, dataSession)) {
              it.remove();
            } else if (forecastActual != null && fa.getForecastActualId() == forecastActual.getForecastActualId()) {
              it.remove();
            } else if (softwareSet.contains(fa.getSoftwareResult().getSoftware())) {
              it.remove();
            } else {
              softwareSet.add(fa.getSoftwareResult().getSoftware());
            }
          }
        }
        for (ForecastExpected otherForecastExpected : otherExpectedList) {
          printForecastExpected(out, otherForecastExpected);
        }

        printForecastComparison(out, forecastCompare);

        for (ForecastActual otherForecastActual : otherActualList) {
          printForecastActual(out, otherForecastActual);
        }

        out.println("  </table>");
      }
      out.println("  <p><a class=\"fauxbutton\" href=\"\">Add Forecast Expectation</a></p>");
    } else {
      out.println("  <p><a class=\"fauxbutton\" href=\"\">Add Forecast Expectation</a></p>");
    }

    out.println("  <p><a class=\"fauxbutton\" href=\"\">Add Guidance Expectation</a></p>");

    out.println("</div>");
  }

  public void printForecastActual(PrintWriter out, ForecastActual forecastActual) {
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    String entityLabel = "Actual from " + forecastActual.getSoftwareResult().getSoftware().getLabel();
    String expectedAdmin = (forecastActual.getAdmin() == null ? Admin.UNKNOWN : forecastActual.getAdmin()).getLabel();
    String actualDoseNumber = forecastActual.getDoseNumber() != null ? forecastActual.getDoseNumber() : "-";
    String actualValidDate = forecastActual.getValidDate() != null ? sdf.format(forecastActual.getValidDate()) : "-";
    String actualDueDate = forecastActual.getDueDate() != null ? sdf.format(forecastActual.getDueDate()) : "-";
    String actualOverdueDate = forecastActual.getOverdueDate() != null ? sdf.format(forecastActual.getOverdueDate())
        : "-";

    out.println("    <tr>");
    out.println("      <td>" + entityLabel + "</td>");
    out.println("      <td>" + expectedAdmin + "</td>");
    out.println("      <td>" + actualDoseNumber + "</td>");
    out.println("      <td>" + actualValidDate + "</td>");
    out.println("      <td>" + actualDueDate + "</td>");
    out.println("      <td>" + actualOverdueDate + "</td>");
    out.println("    </tr>");
  }

  public void printForecastExpected(PrintWriter out, ForecastExpected forecastExpected) {
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    String entityLabel = "Expected by " + forecastExpected.getAuthor().getName() + " at "
        + forecastExpected.getAuthor().getOrganization();
    String expectedAdmin = (forecastExpected.getAdmin() == null ? Admin.UNKNOWN : forecastExpected.getAdmin())
        .getLabel();
    String expectedDoseNumber = forecastExpected.getDoseNumber() != null ? forecastExpected.getDoseNumber() : "-";
    String expectedValidDate = forecastExpected.getValidDate() != null ? sdf.format(forecastExpected.getValidDate())
        : "-";
    String expectedDueDate = forecastExpected.getDueDate() != null ? sdf.format(forecastExpected.getDueDate()) : "-";
    String expectedOverdueDate = forecastExpected.getOverdueDate() != null ? sdf.format(forecastExpected
        .getOverdueDate()) : "-";

    out.println("    <tr>");
    out.println("      <td>" + entityLabel + "</td>");
    out.println("      <td>" + expectedAdmin + "</td>");
    out.println("      <td>" + expectedDoseNumber + "</td>");
    out.println("      <td>" + expectedValidDate + "</td>");
    out.println("      <td>" + expectedDueDate + "</td>");
    out.println("      <td>" + expectedOverdueDate + "</td>");
    out.println("    </tr>");
  }

  public void printForecastComparison(PrintWriter out, ForecastActualExpectedCompare forecastCompare) {
    ForecastExpected forecastExpected = (ForecastExpected) forecastCompare.getForecastResultA();
    ForecastActual forecastActual = (ForecastActual) forecastCompare.getForecastResultB();

    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    String expectedAdmin = (forecastExpected.getAdmin() == null ? Admin.UNKNOWN : forecastExpected.getAdmin())
        .getLabel();
    String actualAdmin = (forecastActual.getAdmin() == null ? Admin.UNKNOWN : forecastActual.getAdmin()).getLabel();
    String expectedDoseNumber = forecastExpected.getDoseNumber() != null ? forecastExpected.getDoseNumber() : "-";
    String actualDoseNumber = forecastActual.getDoseNumber() != null ? forecastActual.getDoseNumber() : "-";
    String expectedValidDate = forecastExpected.getValidDate() != null ? sdf.format(forecastExpected.getValidDate())
        : "-";
    String actualValidDate = forecastActual.getValidDate() != null ? sdf.format(forecastActual.getValidDate()) : "-";
    String expectedDueDate = forecastExpected.getDueDate() != null ? sdf.format(forecastExpected.getDueDate()) : "-";
    String actualDueDate = forecastActual.getDueDate() != null ? sdf.format(forecastActual.getDueDate()) : "-";
    String expectedOverdueDate = forecastExpected.getOverdueDate() != null ? sdf.format(forecastExpected
        .getOverdueDate()) : "-";
    String actualOverdueDate = forecastActual.getOverdueDate() != null ? sdf.format(forecastActual.getOverdueDate())
        : "-";

    String styleClassLabel = forecastCompare.matchExactly() ? "pass" : "fail";
    String styleClassAdmin = (expectedAdmin != null && actualAdmin != null && expectedAdmin.equals(actualAdmin)) ? "pass"
        : "fail";
    String styleClassDoseNumber = compareDoseNumbers(expectedDoseNumber, actualDoseNumber) ? "pass" : "fail";
    String styleClassValid = compareDoseNumbers(expectedValidDate, actualValidDate) ? "pass" : "fail";
    String styleClassDue = compareDoseNumbers(expectedDueDate, actualDueDate) ? "pass" : "fail";
    String styleClassOverdue = compareDoseNumbers(expectedOverdueDate, actualOverdueDate) ? "pass" : "fail";

    {
      String entityLabel = "Expected by " + forecastExpected.getAuthor().getName() + " at "
          + forecastExpected.getAuthor().getOrganization();
      out.println("    <tr>");
      out.println("      <td class=\"" + styleClassLabel + "\">" + entityLabel + "</td>");
      out.println("      <td class=\"" + styleClassAdmin + "\">" + expectedAdmin + "</td>");
      out.println("      <td class=\"" + styleClassDoseNumber + "\">" + expectedDoseNumber + "</td>");
      out.println("      <td class=\"" + styleClassValid + "\">" + expectedValidDate + "</td>");
      out.println("      <td class=\"" + styleClassDue + "\">" + expectedDueDate + "</td>");
      out.println("      <td class=\"" + styleClassOverdue + "\">" + expectedOverdueDate + "</td>");
      out.println("    </tr>");
    }

    {
      String entityLabel = "Actual from " + forecastActual.getSoftwareResult().getSoftware().getLabel();
      out.println("    <tr>");
      out.println("      <td class=\"" + styleClassLabel + "\">" + entityLabel + "</td>");
      out.println("      <td class=\"" + styleClassAdmin + "\">" + actualAdmin + "</td>");
      out.println("      <td class=\"" + styleClassDoseNumber + "\">" + actualDoseNumber + "</td>");
      out.println("      <td class=\"" + styleClassValid + "\">" + actualValidDate + "</td>");
      out.println("      <td class=\"" + styleClassDue + "\">" + actualDueDate + "</td>");
      out.println("      <td class=\"" + styleClassOverdue + "\">" + actualOverdueDate + "</td>");
      out.println("    </tr>");
    }

  }

  private boolean compareDoseNumbers(String expectedDoseNumber, String actualDoseNumber) {
    if (expectedDoseNumber == null || expectedDoseNumber.equals("-")) {
      expectedDoseNumber = "";
    }
    if (actualDoseNumber == null || actualDoseNumber.equals("-")) {
      actualDoseNumber = "";
    }
    return expectedDoseNumber.equals(actualDoseNumber);
  }

  public void printTestCase(PrintWriter out, User user) {
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    TestPanelCase testPanelCase = user.getSelectedTestPanelCase();
    TestCase testCase = testPanelCase.getTestCase();
    out.println("<div class=\"centerLeftColumn\">");
    out.println("  <h2>Test Case</h2>");
    out.println("  <table width=\"100%\">");
    out.println("    <tr>");
    out.println("      <th>Label</th>");
    out.println("      <td>" + testCase.getLabel() + "</td>");
    out.println("    </tr>");
    out.println("    <tr>");
    out.println("      <th>Description</th>");
    out.println("      <td>" + testCase.getDescription() + "</td>");
    out.println("    </tr>");
    out.println("    <tr>");
    out.println("      <th>Date Set</th>");
    out.println("      <td>" + testCase.getDateSet() + "</td>");
    out.println("    </tr>");
    out.println("    <tr>");
    out.println("      <th>Patient</th>");
    out.println("      <td>" + testCase.getPatientFirst() + " " + testCase.getPatientLast() + "</td>");
    out.println("    </tr>");
    out.println("    <tr>");
    out.println("      <th>Birth Date</th>");
    if (testCase.getEvalRule() != null) {
      out.println("      <td>" + sdf.format(testCase.getPatientDob()) + "<br/>" + testCase.getEvalRule() + " old</td>");
    } else {
      out.println("      <td>" + sdf.format(testCase.getPatientDob()) + "</td>");
    }
    out.println("    </tr>");
    if (testCase.getEvalRule() == null) {
      out.println("    <tr>");
      out.println("      <th>Evaluation Date</th>");
      out.println("      <td>" + sdf.format(testCase.getEvalDate()) + "</td>");
      out.println("    </tr>");
    }
    out.println("    <tr>");
    out.println("      <th>Test Case Id</th>");
    out.println("      <td>" + testPanelCase.getTestCaseNumber() + "</td>");
    out.println("    </tr>");
    out.println("    <tr>");
    out.println("      <th>Category</th>");
    out.println("      <td>" + testPanelCase.getCategoryName() + "</td>");
    out.println("    </tr>");
    out.println("  </table>");

    int countVaccination = 0;
    int countACIP = 0;
    int countCondition = 0;
    Query query = applicationSession.getDataSession().createQuery(
        "from TestEvent where testCase = ? order by eventDate");
    query.setParameter(0, testCase);
    List<TestEvent> testEventList = query.list();
    for (TestEvent testEvent : testEventList) {
      if (testEvent.getEvent().getEventType() == EventType.VACCINATION) {
        countVaccination++;
      } else if (testEvent.getEvent().getEventType() == EventType.ACIP_DEFINED_CONDITION) {
        countVaccination++;
      } else if (testEvent.getEvent().getEventType() == EventType.CONDITION_IMPLICATION) {
        countCondition++;
      }
    }
    if (countVaccination > 0) {
      out.println("  <h2>Vaccination History</h2>");
      out.println("  <table width=\"100%\">");
      out.println("    <tr>");
      out.println("      <th>Vaccine</th>");
      out.println("      <th>CVX</th>");
      out.println("      <th>MVX</th>");
      out.println("      <th>Date</th>");
      out.println("      <th>Age</th>");
      out.println("    </tr>");
      for (TestEvent testEvent : testEventList) {
        if (testEvent.getEvent().getEventType() == EventType.VACCINATION) {
          out.println("    <tr>");
          out.println("      <td>" + testEvent.getEvent().getLabel() + "</td>");
          out.println("      <td>" + testEvent.getEvent().getVaccineCvx() + "</td>");
          out.println("      <td>" + testEvent.getEvent().getVaccineMvx() + "</td>");
          out.println("      <td>" + sdf.format(testEvent.getEventDate()) + "</td>");
          out.println("      <td>" + testEvent.getAgeAlmost(testCase) + "</td>");
          out.println("    </tr>");
        }
      }
      out.println("  </table>");
    }

    out.println("  <p><a class=\"fauxbutton\" href=\"\">Add Vaccination</a></p>");
    out.println("  <p><a class=\"fauxbutton\" href=\"\">Add ACIP-Defined Condition</a></p>");
    out.println("  <p><a class=\"fauxbutton\" href=\"\">Add Condition Implication</a></p>");
    out.println("  <p><a class=\"fauxbutton\" href=\"\">Add Other Event</a></p>");
    out.println("</div>");
  }

  public void printTaskGroupList(PrintWriter out, Session dataSession, User user, TaskGroup taskGroup)
      throws UnsupportedEncodingException {
    Query query;
    final String link1 = "testCases?" + PARAM_ACTION + "=" + URLEncoder.encode(ACTION_SELECT_TASK_GROUP, "UTF-8") + "&"
        + PARAM_TASK_GROUP_ID + "=" + taskGroup.getTaskGroupId();
    if (user.getSelectedTaskGroup() != null && user.getSelectedTaskGroup().equals(taskGroup)) {
      out.println("  <li class=\"selectLevel1\"><a href=\"" + link1 + "\">" + taskGroup.getLabel() + "</a>");

      out.println("    <ul class=\"selectLevel2\">");
      query = dataSession.createQuery("from TestPanel where taskGroup = ? order by label");
      query.setParameter(0, taskGroup);
      List<TestPanel> testPanelList = query.list();
      if (user.getSelectedTestPanel() != null) {
        printTestPanel(out, dataSession, user, user.getSelectedTestPanel());
      }
      for (TestPanel testPanel : testPanelList) {
        if (user.getSelectedTestPanel() == null || !user.getSelectedTestPanel().equals(testPanel)) {
          printTestPanel(out, dataSession, user, testPanel);
        }
      }
      out.println("      <li><a class=\"add\" href=\"\">add test panel</a></li>");
      out.println("    </ul>");

      out.println("  </li>");
    } else {
      out.println("  <li class=\"selectLevel1\"><a href=\"" + link1 + "\">" + taskGroup.getLabel() + "</a></li>");
    }
  }

  public void printTestPanel(PrintWriter out, Session dataSession, User user, TestPanel testPanel)
      throws UnsupportedEncodingException {
    Query query;
    final String link2 = "testCases?" + PARAM_ACTION + "=" + URLEncoder.encode(ACTION_SELECT_TEST_PANEL, "UTF-8") + "&"
        + PARAM_TEST_PANEL_ID + "=" + testPanel.getTestPanelId();
    if (user.getSelectedTestPanel() != null && user.getSelectedTestPanel().equals(testPanel)) {
      out.println("      <li class=\"selectLevel2\"><a href=\"" + link2 + "\">" + testPanel.getLabel() + "</a>");

      out.println("        <ul class=\"selectLevel3\">");
      query = dataSession.createQuery("from TestPanelCase where testPanel = ? order by categoryName, testCase.label");
      query.setParameter(0, testPanel);
      List<TestPanelCase> testPanelCaseList = query.list();
      String lastCategoryName = "";
      boolean selectedCategoryOpened = false;
      for (TestPanelCase testPanelCase : testPanelCaseList) {
        if (!testPanelCase.getCategoryName().equals(lastCategoryName)) {
          if (selectedCategoryOpened) {
            out.println("            <li><a class=\"add\" href=\"testCases?show=" + SHOW_ADD_TEST_CASE + "&" + PARAM_CATEGORY_NAME + "=" + lastCategoryName + "\">add test case</a></li>");
            out.println("          </ul>");
            out.println("        </li>");
            selectedCategoryOpened = false;
          }
          final String link3 = "testCases?" + PARAM_ACTION + "=" + URLEncoder.encode(ACTION_SELECT_CATEGORY, "UTF-8")
              + "&" + PARAM_CATEGORY_NAME + "=" + testPanelCase.getCategoryName();
          if (user.getSelectedCategoryName() != null
              && testPanelCase.getCategoryName().equals(user.getSelectedCategoryName())) {
            out.println("          <li class=\"selectLevel3\"><a href=\"" + link3 + "\">"
                + testPanelCase.getCategoryName() + "</a>");
            out.println("            <ul class=\"selectLevel4\">");
            selectedCategoryOpened = true;
          } else {
            out.println("          <li class=\"selectLevel3\"><a href=\"" + link3 + "\">"
                + testPanelCase.getCategoryName() + "</a></li>");
          }
        }
        if (user.getSelectedCategoryName() != null
            && testPanelCase.getCategoryName().equals(user.getSelectedCategoryName())) {
          final String link4 = "testCases?" + PARAM_ACTION + "="
              + URLEncoder.encode(ACTION_SELECT_TEST_PANEL_CASE, "UTF-8") + "&" + PARAM_TEST_PANEL_CASE_ID + "="
              + testPanelCase.getTestPanelCaseId();
          out.println("              <li class=\"selectLevel4\"><a href=\"" + link4 + "\">"
              + testPanelCase.getTestCase().getLabel() + "</a></li>");
        }
        lastCategoryName = testPanelCase.getCategoryName();
      }
      if (selectedCategoryOpened) {
        out.println("            <li><a class=\"add\" href=\"testCases?show=" + SHOW_ADD_TEST_CASE + "&" + PARAM_CATEGORY_NAME + "=" + lastCategoryName + "\">add test case</a></li>");
        out.println("          </ul>");
        out.println("        </li>");
        selectedCategoryOpened = false;
      }
      if (user.getSelectedCategoryName() == null) {
        out.println("          <li><a class=\"add\" href=\"testCases?show=" + SHOW_ADD_TEST_CASE + "\">add test case</a></li>");
      }
      out.println("        </ul>");
      out.println("      </li>");
    } else {
      out.println("      <li class=\"selectLevel2\"><a href=\"" + link2 + "\">" + testPanel.getLabel() + "</a></li>");
    }
  }

}
