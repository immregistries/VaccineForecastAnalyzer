package org.tch.ft.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.tch.fc.model.AssociatedDate;
import org.tch.fc.model.Consideration;
import org.tch.fc.model.DateSet;
import org.tch.fc.model.Evaluation;
import org.tch.fc.model.Event;
import org.tch.fc.model.EventType;
import org.tch.fc.model.ForecastActual;
import org.tch.fc.model.Rationale;
import org.tch.fc.model.Recommend;
import org.tch.fc.model.RecommendRange;
import org.tch.fc.model.RelativeRule;
import org.tch.fc.model.RelativeTo;
import org.tch.fc.model.Resource;
import org.tch.fc.model.Software;
import org.tch.fc.model.TestCase;
import org.tch.fc.model.TestEvent;
import org.tch.fc.model.VaccineGroup;
import org.tch.fc.util.TimePeriod;
import org.tch.ft.manager.ForecastActualExpectedCompare;
import org.tch.ft.manager.RelativeRuleManager;
import org.tch.ft.manager.SoftwareManager;
import org.tch.ft.model.Expert;
import org.tch.ft.model.ForecastExpected;
import org.tch.ft.model.Include;
import org.tch.ft.model.Result;
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
  public static final String ACTION_ADD_TEST_CASE = "Add Test Case";
  public static final String ACTION_UPDATE_TEST_CASE = "Update Test Case";
  public static final String ACTION_DELETE_EVENT = "Delete Event";
  public static final String ACTION_ADD_VACCINATION = "Add Vaccination";
  public static final String ACTION_ADD_EVENT = "Add Event";

  public static final String PARAM_TASK_GROUP_ID = "taskGroupId";
  public static final String PARAM_TEST_PANEL_ID = "testPanelId";
  public static final String PARAM_TEST_PANEL_CASE_ID = "testPanelCaseId";
  public static final String PARAM_CATEGORY_NAME = "categoryName";

  public static final String PARAM_LABEL = "label";
  public static final String PARAM_DESCRIPTION = "description";
  public static final String PARAM_VACCINE_GROUP_ID = "vaccineGroupId";
  public static final String PARAM_PATIENT_FIRST = "patientFirst";
  public static final String PARAM_PATIENT_LAST = "patientLast";
  public static final String PARAM_PATIENT_SEX = "patientSex";
  public static final String PARAM_PATIENT_DOB = "patientDob";
  public static final String PARAM_EVAL_DATE = "evalDate";
  public static final String PARAM_EVAL_RULE = "evalRule";
  public static final String PARAM_DATE_SET_CODE = "dateSetCode";
  public static final String PARAM_TEST_CASE_NUMBER = "testCaseNumber";
  public static final String PARAM_TEST_EVENT_ID = "testEventId";
  public static final String PARAM_NEW_EVENT_LABEL = "newEventLabel";
  public static final String PARAM_EVENT_ID = "eventId";
  public static final String PARAM_EVENT_DATE = "eventDate";
  public static final String PARAM_EVENT_RULE = "eventRule";
  public static final String PARAM_EVENT_RULE_BEFORE_OR_AFTER = "eventRuleBeforeOrAfter";
  public static final String PARAM_EVENT_LABEL = "eventLabel";
  public static final String PARAM_CONDITION_CODE = "conditionCode";
  public static final String PARAM_RELATIVE_RULE_TEST_EVENT_ID = "relativeRuleTestEventId";
  public static final String PARAM_EVENT_TYPE_CODE = "eventTypeCode";

  public static final String SHOW_TEST_CASE = "testCase";
  public static final String SHOW_TASK_GROUP = "taskGroup";
  public static final String SHOW_TEST_PANEL = "testPanel";
  public static final String SHOW_EDIT_TEST_CASE = "editTestCase";
  public static final String SHOW_ADD_TEST_CASE = "addTestCase";
  public static final String SHOW_EDIT_VACCINATIONS = "editVaccinations";
  public static final String SHOW_EDIT_EXPECTATIONS = "editExpectations";
  public static final String SHOW_EDIT_EVENTS = "editEvents";

  private static final int EVALUATION_TEST_EVENT_ID = -2;
  private static final int BIRTH_TEST_EVENT_ID = -1;

  private static String lastIssuedTestNumberPrefix = "";
  private static int lastIssuedTestNumberSuffix = 0;

  private List<TestEvent> testEventList;
  private int countVaccination = 0;
  private int countACIP = 0;
  private int countCondition = 0;
  private int countOther = 0;

  private static synchronized String getNextTestCaseNumber() {
    SimpleDateFormat sdfShort = new SimpleDateFormat("yyMMdd");
    String testNumberPrefix = sdfShort.format(new Date());
    if (!testNumberPrefix.equals(lastIssuedTestNumberPrefix)) {
      lastIssuedTestNumberPrefix = testNumberPrefix;
      lastIssuedTestNumberSuffix = 0;
    }
    lastIssuedTestNumberSuffix++;
    return lastIssuedTestNumberPrefix + "-" + lastIssuedTestNumberSuffix;
  }

  private static String getNextTestCaseNumber(Session dataSession) {
    boolean notFound = true;
    String testNumber = getNextTestCaseNumber();
    while (notFound) {
      Query query = dataSession.createQuery("from TestPanelCase where testCaseNumber = ?");
      query.setParameter(0, testNumber);
      List<TestPanelCase> testPanelCaseList = query.list();
      if (testPanelCaseList.size() == 0) {
        // test number with no test panel case assigned has been found
        notFound = false;
      } else {
        testNumber = getNextTestCaseNumber();
      }
    }
    return testNumber;
  }

  @Override
  public String execute(HttpServletRequest req, HttpServletResponse resp, String action, String show)
      throws IOException {
    if (show == null) {
      show = SHOW_TEST_CASE;
    }
    if (action != null) {
      User user = applicationSession.getUser();
      Session dataSession = applicationSession.getDataSession();
      if (req.getParameter(PARAM_TEST_PANEL_CASE_ID) != null) {
        int testPanelCaseId = Integer.parseInt(req.getParameter(PARAM_TEST_PANEL_CASE_ID));
        Transaction trans = dataSession.beginTransaction();
        TestPanelCase testPanelCase = (TestPanelCase) dataSession.get(TestPanelCase.class, testPanelCaseId);
        user.setSelectedTaskGroup(testPanelCase.getTestPanel().getTaskGroup());
        user.setSelectedTestPanel(testPanelCase.getTestPanel());
        user.setSelectedTestPanelCase(testPanelCase);
        user.setSelectedTestCase(testPanelCase.getTestCase());
        user.setSelectedCategoryName(testPanelCase.getCategoryName());
        dataSession.update(user);
        trans.commit();
      }
      if (action.equals(ACTION_SELECT_TASK_GROUP)) {
        int taskGroupId = Integer.parseInt(req.getParameter(PARAM_TASK_GROUP_ID));
        Transaction trans = dataSession.beginTransaction();
        TaskGroup taskGroup = (TaskGroup) dataSession.get(TaskGroup.class, taskGroupId);
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
        user.setSelectedTestPanel(testPanel);
        user.setSelectedTestPanelCase(null);
        user.setSelectedCategoryName(null);
        dataSession.update(user);
        trans.commit();
        return SHOW_TEST_PANEL;
      } else if (action.equals(ACTION_SELECT_TEST_PANEL_CASE)) {
        return SHOW_TEST_CASE;
      } else if (action.equals(ACTION_SELECT_CATEGORY)) {
        applicationSession.getUser().setSelectedCategoryName(req.getParameter(PARAM_CATEGORY_NAME));
        return SHOW_TEST_CASE;
      } else if (action.equals(ACTION_ADD_TEST_CASE) || action.equals(ACTION_UPDATE_TEST_CASE)) {
        return saveTestCase(req, action, user, dataSession);
      } else if (action.equals(ACTION_DELETE_EVENT)) {
        int testEventId = notNull(req.getParameter(PARAM_TEST_EVENT_ID), 0);
        return doDelete(testEventId, show, dataSession);
      } else if (action.equals(ACTION_ADD_VACCINATION) || action.equals(ACTION_ADD_EVENT)) {
        EventType eventType = null;
        if (req.getParameter(PARAM_EVENT_TYPE_CODE) != null) {
          eventType = EventType.getEventType(req.getParameter(PARAM_EVENT_TYPE_CODE));
        }
        boolean isVacc = action.equals(ACTION_ADD_VACCINATION);
        int eventId = Integer.parseInt(req.getParameter(PARAM_EVENT_ID));
        if (eventId == -1) {
          String newEventLabel = req.getParameter(PARAM_NEW_EVENT_LABEL);
          if (!isVacc && newEventLabel.length() > 0) {
            Transaction transaction = dataSession.beginTransaction();
            Event event = new Event();
            event.setLabel(newEventLabel);
            event.setEventType(eventType);
            dataSession.save(event);
            transaction.commit();
            eventId = event.getEventId();
          } else {
            applicationSession.setAlertError("Please indicate the new ACIP-Defined Condition label");
            return show;
          }
        }
        if (eventId == 0) {
          applicationSession.setAlertError((isVacc ? "Vaccination" : "Condition")
              + " is required, please select the appropriate vaccination. ");
          return show;
        }
        TestCase testCase = user.getSelectedTestCase();
        Event event = (Event) dataSession.get(Event.class, eventId);
        TestEvent testEvent = new TestEvent();
        testEvent.setTestCase(testCase);
        testEvent.setEvent(event);
        if (testCase.getDateSet() == DateSet.FIXED) {
          String eventDateString = notNull(req.getParameter(PARAM_EVENT_DATE));
          if (eventDateString.equals("")) {
            applicationSession
                .setAlertError((isVacc ? "Administered date is required, please indicate date to create vaccination."
                    : "Observed date is required, please indicate date condition was observed.") + "  ");
            return show;
          }
          try {
            testEvent.setEventDate(sdf.parse(eventDateString));
          } catch (ParseException pe) {
            applicationSession.setAlertError("Date is not in MM/DD/YYYY format, please correct date. ");
            return show;
          }
        } else {
          RelativeRule relativeRule = readRelativeRules(req, dataSession, testCase);
          if (relativeRule == null || relativeRule.getTestEvent() == null) {
            applicationSession
                .setAlertError((isVacc ? "Administered" : "Observed")
                    + " needs to be set before or after a selected event. Please select event that this event is before or after. ");
            return show;
          }
          relativeRule.convertBeforeToAfter();
          testEvent.setEventRule(relativeRule);
          RelativeRule andRule = relativeRule.getAndRule();
          RelativeRule parentRule = relativeRule;
          while (andRule != null) {
            andRule.convertBeforeToAfter();
            if (andRule.getTestEvent() == null) {
              parentRule.setAndRule(null);
              andRule = null;
            } else {
              parentRule = andRule;
              andRule = andRule.getAndRule();
            }
          }
        }
        testEvent.calculateFixedDates();

        Transaction transaction = dataSession.beginTransaction();
        if (testEvent.getEventRule() != null) {
          saveRelativeRule(dataSession, testEvent.getEventRule());
        }
        dataSession.save(testEvent);
        transaction.commit();

        return show;
      }

    }
    return show;
  }

  public void saveRelativeRule(Session dataSession, RelativeRule relativeRule) {
    if (relativeRule.getAndRule() != null) {
      saveRelativeRule(dataSession, relativeRule.getAndRule());
    }
    if (relativeRule.getRelativeTo() == RelativeTo.BIRTH || relativeRule.getRelativeTo() == RelativeTo.EVALUATION) {
      relativeRule.setTestEvent(null);
    }
    dataSession.save(relativeRule);
  }

  public String doDelete(int testEventId, String show, Session dataSession) {

    TestEvent testEvent = (TestEvent) dataSession.get(TestEvent.class, testEventId);
    Query query = dataSession.createQuery("from EvaluationActual where testEvent = ?");
    query.setParameter(0, testEvent);
    if (query.list().size() > 0) {
      applicationSession
          .setAlertError("Unable to delete vaccination, actual evaluation(s) have been stored from CDSi request(s). You can no longer edit this test case. Create a new one instead. ");
      return SHOW_EDIT_VACCINATIONS;
    }
    query = dataSession.createQuery("from EvaluationExpected where testEvent = ?");
    query.setParameter(0, testEvent);
    if (query.list().size() > 0) {
      applicationSession
          .setAlertError("Unable to delete vaccination, evaluation expectation(s) have been set. You can no longer edit this test case. Create a new one instead.");
      return SHOW_EDIT_VACCINATIONS;
    }
    query = dataSession.createQuery("from RelativeRule where testEvent = ?");
    query.setParameter(0, testEvent);
    if (query.list().size() > 0) {
      applicationSession
          .setAlertError("Unable to delete event, another test event depends on this one to define its date. First delete that event before deleting this one. ");
      return SHOW_EDIT_VACCINATIONS;
    }
    Transaction transaction = dataSession.beginTransaction();
    query = dataSession.createQuery("from AssociatedDate where testEvent = ?");
    query.setParameter(0, testEvent);
    List<AssociatedDate> associatedDateList = query.list();
    for (AssociatedDate associatedDate : associatedDateList) {
      dataSession.delete(associatedDate);
    }
    if (testEvent.getEventRule() != null) {
      dataSession.delete(testEvent.getEventRule());
    }
    dataSession.delete(testEvent);
    transaction.commit();

    return show;
  }

  public String saveTestCase(HttpServletRequest req, String action, User user, Session dataSession) {
    boolean update = action.equals(ACTION_UPDATE_TEST_CASE);
    String label = req.getParameter(PARAM_LABEL);
    String description = req.getParameter(PARAM_DESCRIPTION);
    int vaccineGroupId = Integer.parseInt(req.getParameter(PARAM_VACCINE_GROUP_ID));
    String patientFirst = req.getParameter(PARAM_PATIENT_FIRST);
    String patientLast = req.getParameter(PARAM_PATIENT_LAST);
    String patientSex = req.getParameter(PARAM_PATIENT_SEX);
    String patientDobString = req.getParameter(PARAM_PATIENT_DOB);
    String categoryName = req.getParameter(PARAM_CATEGORY_NAME);
    String testCaseNumber = req.getParameter(PARAM_TEST_CASE_NUMBER);
    String dateSetCode = req.getParameter(PARAM_DATE_SET_CODE);
    String evalDateString = req.getParameter(PARAM_EVAL_DATE);
    String evalRule = req.getParameter(PARAM_EVAL_RULE);
    Date evalDate = null;
    Date patientDob = null;

    String problem = null;
    if (label.equals("")) {
      problem = "Label is required";
    } else if (categoryName.equals("")) {
      problem = "Category is required";
    } else if (description.equals("")) {
      problem = "Description is required";
    } else if (vaccineGroupId == 0) {
      problem = "Vaccine Group is required";
    } else if (testCaseNumber.equals("")) {
      problem = "Number is required";
    } else if (patientFirst.equals("")) {
      problem = "Patient first is required";
    } else if (patientLast.equals("")) {
      problem = "Patient last is required";
    } else if (patientSex.equals("")) {
      problem = "Patient sex is required";
    } else if (dateSetCode.equals(DateSet.RELATIVE.getDateSetCode())) {
      if (evalRule.equals("")) {
        problem = "Patient age is required";
      }
    } else if (dateSetCode.equals(DateSet.FIXED.getDateSetCode())) {
      if (patientDobString == null || patientDobString.equals("")) {
        problem = "Date of Birth is required";
      } else if (evalDateString == null || evalDateString.equals("")) {
        problem = "Evaluation Date is required";
      } else {
        try {
          patientDob = sdf.parse(patientDobString);
        } catch (ParseException pe) {
          problem = "Date of Birth is not in MM/DD/YYYY format";
        }
        try {
          evalDate = sdf.parse(evalDateString);
        } catch (ParseException pe) {
          problem = "Evaluation Date is not in MM/DD/YYYY format";
        }
      }
    }
    if (problem == null) {
      Query query = dataSession.createQuery("from TestPanelCase where testPanel = ? and testCaseNumber = ?");
      query.setParameter(0, user.getSelectedTestPanel());
      query.setParameter(1, testCaseNumber);
      List<TestPanelCase> testPanelCaseList = query.list();
      if (testPanelCaseList.size() > 0) {
        if (update) {
          if (testPanelCaseList.size() > 1 || !testPanelCaseList.get(0).equals(user.getSelectedTestPanelCase())) {
            problem = "Number is already in use by another test case in this test panel";
          }
        } else {
          problem = "Number is already in use by another test case in this test panel";
        }
      }
    }
    if (problem != null) {
      applicationSession.setAlertError("Unable to save test case: " + problem);
      return update ? SHOW_EDIT_TEST_CASE : SHOW_ADD_TEST_CASE;
    } else {
      Transaction transaction = dataSession.beginTransaction();
      TestCase testCase;
      TestPanelCase testPanelCase;
      if (update) {
        testCase = user.getSelectedTestCase();
        testPanelCase = user.getSelectedTestPanelCase();
      } else {
        testCase = new TestCase();
        testPanelCase = new TestPanelCase();
      }

      testCase.setDateSet(DateSet.getDateSet(dateSetCode));
      testCase.setLabel(label);
      testCase.setDescription(description);
      testCase.setVaccineGroup((VaccineGroup) dataSession.get(VaccineGroup.class, vaccineGroupId));
      testCase.setEvalDate(evalDate);
      testCase.setPatientDob(patientDob);
      if (!evalRule.equals("")) {
        if (testCase.getEvalRule() == null) {
          testCase.setEvalRule(new RelativeRule(evalRule));
        } else {
          testCase.getEvalRule().setTimePeriod((new TimePeriod(evalRule)));
        }
        testCase.calculateFixedDates(new Date());
      }
      testCase.setPatientFirst(patientFirst);
      testCase.setPatientLast(patientLast);
      testCase.setPatientSex(patientSex);

      testPanelCase.setTestCaseNumber(testCaseNumber);
      testPanelCase.setCategoryName(categoryName);

      if (update) {
        if (testCase.getEvalRule() != null) {
          dataSession.saveOrUpdate(testCase.getEvalRule());
        }
        dataSession.update(testCase);
        dataSession.update(testPanelCase);
      } else {
        testPanelCase.setTestPanel(user.getSelectedTestPanel());
        testPanelCase.setTestCase(testCase);
        testPanelCase.setInclude(Include.INCLUDED);
        testPanelCase.setResult(Result.RESEARCH);

        if (testCase.getEvalRule() != null) {
          dataSession.save(testCase.getEvalRule());
        }
        dataSession.save(testCase);
        dataSession.save(testPanelCase);
      }
      user.setSelectedTestPanelCase(testPanelCase);
      user.setSelectedCategoryName(categoryName);
      dataSession.update(user);
      transaction.commit();
      return SHOW_TEST_CASE;
    }
  }

  @Override
  protected void printPage(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, String show)
      throws ServletException, IOException {
    Session dataSession = applicationSession.getDataSession();

    User user = applicationSession.getUser();

    setupForPrinting(dataSession, user);

    printTree(out, dataSession, user);

    if (SHOW_TEST_CASE.equals(show)) {
      if (user.getSelectedTaskGroup() != null && user.getSelectedTestPanel() != null
          && user.getSelectedTestPanelCase() != null) {
        TestCase testCase = user.getSelectedTestPanelCase().getTestCase();

        RelativeRuleManager.updateFixedDatesForRelativeRules(testCase, dataSession, false);
        printTestCase(out, user);
        printActualsVsExpected(out, user);
      }
    } else if (SHOW_ADD_TEST_CASE.equals(show)) {
      printAddEditTestCases(req, out, dataSession, null);
    } else if (SHOW_EDIT_TEST_CASE.equals(show)) {
      printAddEditTestCases(req, out, dataSession, user.getSelectedTestPanelCase());
    } else if (SHOW_TASK_GROUP.equals(show)) {
      printTaskGroup(out, dataSession, user);
    } else if (SHOW_EDIT_VACCINATIONS.equals(show)) {
      printEditEvents(req, out, dataSession, user, EventType.VACCINATION, show);
    } else if (SHOW_EDIT_EVENTS.equals(show)) {
      EventType eventType = EventType.getEventType(req.getParameter(PARAM_EVENT_TYPE_CODE));
      printEditEvents(req, out, dataSession, user, eventType, show);
    } else if (SHOW_EDIT_EXPECTATIONS.equals(show)) {
      VaccineGroup vacineGroup = (VaccineGroup) dataSession.get(VaccineGroup.class,
          notNull(req.getParameter(PARAM_VACCINE_GROUP_ID), 0));
      TestPanelCase testPanelCase = user.getSelectedTestPanelCase();
      TestCase testCase = testPanelCase.getTestCase();
      out.println("<div class=\"centerColumn\">");
      out.println("  <h2>Expectations for " + vacineGroup.getLabel());
      out.println("    <a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW + "=" + SHOW_TEST_CASE + "&"
          + PARAM_TEST_PANEL_CASE_ID + "=" + user.getSelectedTestPanelCase().getTestPanelCaseId() + "\">Cancel</a>");
      out.println("  </h2>");

      setupTestEventList(user);

      out.println("  <form method=\"POST\" action=\"testCases\">");
      if (countVaccination > 0) {
        out.println("  <h2>Evaluation of Vaccinations</h2>");
        out.println("  <table width=\"100%\">");
        out.println("    <tr>");
        out.println("      <th>#</th>");
        out.println("      <th>Vaccinations</th>");
        out.println("      <th>CVX</th>");
        out.println("      <th>MVX</th>");
        if (testCase.getDateSet() == DateSet.RELATIVE) {
          out.println("      <th>Date Rule</th>");
        }
        out.println("      <th>Date</th>");
        out.println("      <th>Status</th>");
        out.println("    </tr>");

        for (TestEvent testEvent : testEventList) {
          if (testEvent.getEvent().getEventType() == EventType.VACCINATION) {
            out.println("    <tr>");
            out.println("      <td>" + testEvent.getScreenId() + "</td>");
            out.println("      <td>" + testEvent.getEvent().getLabel() + "</td>");
            out.println("      <td>" + testEvent.getEvent().getVaccineCvx() + "</td>");
            out.println("      <td>" + testEvent.getEvent().getVaccineMvx() + "</td>");
            if (testCase.getDateSet() == DateSet.RELATIVE) {
              out.print("      <td>");
              printOutRelativeRule(out, testEvent.getEventRule());
              out.print("</td>");
            }
            out.println("      <td>" + (testEvent.getEventDate() == null ? "" : sdf.format(testEvent.getEventDate()))
                + "</td>");
            out.println("      <td>");
            out.println("          <select name=\"" + "todo" + "\">");
            out.println("            <option value=\"0\">--select--</option>");
            String evaluationStatus = ""; // todo 
            for (Evaluation evaluation : Evaluation.values()) {
              if (evaluationStatus == evaluation.getEvaluationStatus()) {
                out.println("            <option value=\"" + evaluation.getEvaluationStatus()
                    + "\" selected=\"selected\">" + evaluation.getLabel() + "</option>");
              } else {
                out.println("            <option value=\"" + evaluation.getEvaluationStatus() + "\">"
                    + evaluation.getLabel() + "</option>");
              }
            }
            out.println("          </select>");
            out.println("      </td>");
            out.println("    </tr>");
          }
        }
        out.println("  </table>");
      }

      out.println("  <h3>Forecast</h3>");
      out.println("  <table>");
      out.println("    <tr>");
      out.println("      <th>Status</th>");
      out.println("      <td>");
      out.println("          <select name=\"" + "todo" + "\">");
      out.println("            <option value=\"0\">--select--</option>");
      String adminStatus = ""; // todo 
      for (Admin admin : Admin.values()) {
        if (adminStatus == admin.getAdminStatus()) {
          out.println("            <option value=\"" + admin.getAdminStatus() + "\" selected=\"selected\">"
              + admin.getLabel() + "</option>");
        } else {
          out.println("            <option value=\"" + admin.getAdminStatus() + "\">" + admin.getLabel() + "</option>");
        }
      }
      out.println("          </select>");
      out.println("      </td>");
      out.println("    </tr>");
      out.println("    <tr>");
      out.println("      <th>Dose</th>");
      out.println("      <td><input type=\"text\" name=\"\" value=\"\" size=\"5\"/></td>");
      out.println("    </tr>");
      out.println("    <tr>");
      out.println("      <th>Valid</th>");
      out.println("      <td><input type=\"text\" name=\"\" value=\"\" size=\"10\"/></td>");
      out.println("    </tr>");
      out.println("    <tr>");
      out.println("      <th>Due</th>");
      out.println("      <td><input type=\"text\" name=\"\" value=\"\" size=\"10\"/></td>");
      out.println("    </tr>");
      out.println("    <tr>");
      out.println("      <th>Overdue</th>");
      out.println("      <td><input type=\"text\" name=\"\" value=\"\" size=\"10\"/></td>");
      out.println("    </tr>");
      out.println("  </table>");

      out.println("  <h3>Guidance: Recommended Action</h3>");

      out.println("  <table width=\"100%\">");
      {
        out.println("      <tr>");
        out.println("        <td><div class=\"scrollRadioBox\">");
        Query query = dataSession.createQuery("from Recommend order by recommendText");
        List<Recommend> recommendList = query.list();
        for (Recommend recommend : recommendList) {
          if (false) {
            out.println("          <input type=\"radio\" name=\"" + "" + "\" value=\"" + recommend.getRecommendId()
                + "\" checked=\"checked\">" + recommend.getRecommendType().getLabel() + ": "
                + recommend.getRecommendText() + "<br/>");
          } else {
            out.println("          <input type=\"radio\" name=\"" + "" + "\" value=\"" + recommend.getRecommendId()
                + "\">" + recommend.getRecommendType().getLabel() + ": " + recommend.getRecommendText()
                + (recommend.getRecommendRange() == RecommendRange.TEMPORAL ? " (temporary)" : "") + "<br/>");
          }
        }
        out.println("          <input type=\"radio\" name=\"" + ""
            + "\" value=\"-1\">none of these, proposing a new value<br/>");
        out.println("        </div>propose new ");
        out.println("        <input type=\"text\" name=\"" + "" + "\" value=\"" + ""
            + "\" size=\"50\" /></td>");
        out.println("      </tr>");
      }
      out.println("  </table>");
      
      out.println("  <h3>Guidance: Consideration</h3>");
      out.println("  <table width=\"100%\">");
      {
        out.println("      <tr>");
        out.println("        <td><div class=\"scrollRadioBox\">");
        Query query = dataSession.createQuery("from Consideration order by considerationText");
        List<Consideration> considerationList = query.list();
        for (Consideration consideration : considerationList) {
          if (false) {
            out.println("          <input type=\"radio\" name=\"" + "" + "\" value=\""
                + consideration.getConsiderationId() + "\" checked=\"checked\">" + consideration.getConsiderationText()
                + "<br/>");
          } else {
            out.println("          <input type=\"radio\" name=\"" + "" + "\" value=\""
                + consideration.getConsiderationId() + "\">" + consideration.getConsiderationText() + "<br/>");
          }
        }
        out.println("          <input type=\"radio\" name=\"" + ""
            + "\" value=\"-1\">none of these, proposing a new value<br/>");
        out.println("        </div>propose new ");
        out.println("        <input type=\"text\" name=\"" + "" + "\" value=\"" + ""
            + "\" size=\"50\" /></td>");
        out.println("      </tr>");
      }
      out.println("  </table>");
      
      out.println("  <h3>Guidance: Guidance Rationale</h3>");
      out.println("  <table width=\"100%\">");
      {
        out.println("      <tr>");
        out.println("        <td><div class=\"scrollRadioBox\">");
        Query query = dataSession.createQuery("from Rationale order by rationaleText");
        List<Rationale> rationaleList = query.list();
        for (Rationale rationale : rationaleList) {
          if (false) {
            out.println("          <input type=\"radio\" name=\"" + "" + "\" value=\""
                + rationale.getRationaleId() + "\" checked=\"checked\">" + rationale.getRationaleText()
                + "<br/>");
          } else {
            out.println("          <input type=\"radio\" name=\"" + "" + "\" value=\""
                + rationale.getRationaleId() + "\" >" + rationale.getRationaleText()
                + "<br/>");
          }
        }
        out.println("          <input type=\"radio\" name=\"" + ""
            + "\" value=\"-1\">none of these, proposing a new value<br/>");
        out.println("        </div>propose new ");
        out.println("        <input type=\"text\" name=\"" + "" + "\" value=\"" + ""
            + "\" size=\"50\" /></td>");
        out.println("      </tr>");
      }
      out.println("  </table>");
      
      out.println("  <h3>Guidance: Additional Resource</h3>");
      out.println("  <table width=\"100%\">");
      {
        out.println("      <tr>");
        out.println("        <td><div class=\"scrollRadioBox\">");
        Query query = dataSession.createQuery("from Resource order by resourceText");
        List<Resource> resourceList = query.list();
        for (Resource resource : resourceList) {
          if (false) {
            out.println("          <input type=\"radio\" name=\"" + "" + "\" value=\""
                + resource.getResourceId() + "\" checked=\"checked\">" + resource.getResourceText() + " (" + resource.getResourceLink() + ")" 
                + "<br/>");
          } else {
            out.println("          <input type=\"radio\" name=\"" + "" + "\" value=\""
                + resource.getResourceId() + "\">" + resource.getResourceText() + " (" + resource.getResourceLink() + ")" 
                + "<br/>");
          }
        }
        out.println("          <input type=\"radio\" name=\"" + ""
            + "\" value=\"-1\">none of these, proposing a new link<br/>");
        out.println("        </div>");
        out.println("        propose new text ");
        out.println("        <input type=\"text\" name=\"" + "" + "\" value=\"" + ""
            + "\" size=\"50\" /><br/>");
        out.println("        propose new link ");
        out.println("        <input type=\"text\" name=\"" + "" + "\" value=\"" + ""
            + "\" size=\"50\" />");
        out.println("        </td>");
        out.println("      </tr>");
      }
      out.println("  </table>");
      
      out.println("<p>&nbsp;</p>");
      out.println("<p>&nbsp;</p>");
      out.println("<p>&nbsp;</p>");

      out.println("  </form>");
    }

  }

  public void printEditEvents(HttpServletRequest req, PrintWriter out, Session dataSession, User user,
      EventType eventType, String show) throws UnsupportedEncodingException {
    out.println("<script>");
    out.println("  <!-- ");
    out.println("  function showRow(rowId) { ");
    out.println("    var rowToShow = document.getElementById(rowId); ");
    out.println("    if (rowToShow != null) { ");
    out.println("      rowToShow.style.display = 'table-row'; ");
    out.println("    }");
    out.println("  }");
    out.println("  -->");
    out.println("</script>");

    setupTestEventList(user);

    TestCase testCase = user.getSelectedTestCase();
    out.println("<div class=\"centerColumn\">");
    out.println("  <h2>Test Context");
    out.println("    <a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW + "=" + SHOW_TEST_CASE + "&"
        + PARAM_TEST_PANEL_CASE_ID + "=" + user.getSelectedTestPanelCase().getTestPanelCaseId() + "\">Cancel</a>");
    out.println("  </h2>");
    out.println("  <table>");
    out.println("    <tr>");
    out.println("      <th>Birth</th>");
    if (testCase.getPatientDob() == null) {
      out.println("<td></td>");
    } else if (testCase.getDateSet() == DateSet.RELATIVE && testCase.getEvalRule() != null) {
      out.println("      <td>" + sdf.format(testCase.getPatientDob()) + " (" + testCase.getEvalRule().getTimePeriod()
          + " old)</td>");
    } else {
      out.println("      <td>" + sdf.format(testCase.getPatientDob()) + "</td>");
    }
    out.println("    </tr>");
    out.println("    <tr>");
    out.println("      <th>Evaluation</th>");
    out.println("      <td>" + sdf.format(testCase.getEvalDate()) + "</td>");
    out.println("    </tr>");
    out.println("  </table>");

    if (countVaccination > 0) {
      printEventTable(out, user, EventType.VACCINATION, testEventList, "Vaccination History", "Vaccination", show);
    }
    if (countACIP > 0) {
      printEventTable(out, user, EventType.ACIP_DEFINED_CONDITION, testEventList, "ACIP-Defined Conditions Observed",
          "Condition", show);
    }
    if (countCondition > 0) {
      printEventTable(out, user, EventType.CONDITION_IMPLICATION, testEventList, "Condition Implications Asserted",
          "Implication", show);
    }
    if (countOther > 0) {
      printEventTable(out, user, null, testEventList, "Other Events Observed", "Event", show);
    }

    int eventId = 0;
    if (req.getParameter(PARAM_EVENT_ID) != null) {
      eventId = Integer.parseInt(req.getParameter(PARAM_EVENT_ID));
    }
    String eventDateString = notNull(req.getParameter(PARAM_EVENT_DATE));
    String newEventLabel = notNull(req.getParameter(PARAM_NEW_EVENT_LABEL));

    RelativeRule relativeRule = readRelativeRules(req, dataSession, user.getSelectedTestCase());

    String editButton = "";
    if (eventType != EventType.CONDITION_IMPLICATION) {
      String editLink = "testCases?" + PARAM_SHOW + "=" + SHOW_EDIT_EVENTS + "&" + PARAM_TEST_PANEL_CASE_ID + "="
          + user.getSelectedTestPanelCase().getTestPanelCaseId() + "&" + PARAM_EVENT_TYPE_CODE + "=";
      editButton += " <a class=\"fauxbutton\" href=\"" + editLink + EventType.CONDITION_IMPLICATION.getEventTypeCode()
          + "\">Condition Implication</a>";

    }
    if (eventType != EventType.ACIP_DEFINED_CONDITION) {
      String editLink = "testCases?" + PARAM_SHOW + "=" + SHOW_EDIT_EVENTS + "&" + PARAM_TEST_PANEL_CASE_ID + "="
          + user.getSelectedTestPanelCase().getTestPanelCaseId() + "&" + PARAM_EVENT_TYPE_CODE + "=";
      editButton += " <a class=\"fauxbutton\" href=\"" + editLink + EventType.ACIP_DEFINED_CONDITION.getEventTypeCode()
          + "\">ACIP-Defined Condition</a>";

    }
    if (eventType != EventType.VACCINATION) {
      String editLink = "testCases?" + PARAM_SHOW + "=" + SHOW_EDIT_VACCINATIONS + "&" + PARAM_TEST_PANEL_CASE_ID + "="
          + user.getSelectedTestPanelCase().getTestPanelCaseId() + "&" + PARAM_EVENT_TYPE_CODE + "=";
      editButton += " <a class=\"fauxbutton\" href=\"" + editLink + EventType.VACCINATION.getEventTypeCode()
          + "\">Vaccination</a>";

    }
    if (eventType == EventType.VACCINATION) {

      out.println("  <h2>Add Vaccination" + editButton + "</h2>");
      out.println("  <form method=\"POST\" action=\"testCases\">");
      out.println("    <input type=\"hidden\" name=\"" + PARAM_TEST_PANEL_CASE_ID + "\" value=\""
          + user.getSelectedTestPanelCase().getTestPanelCaseId() + "\"/>");
      out.println("    <table width=\"100%\">");
      out.println("      <tr>");
      out.println("        <th>Vaccination</th>");
      out.println("        <td>");
      out.println("          <select name=\"" + PARAM_EVENT_ID + "\">");
      out.println("            <option value=\"0\">--select--</option>");
      Query query = dataSession.createQuery("from Event where eventTypeCode = ? order by label");
      query.setParameter(0, EventType.VACCINATION.getEventTypeCode());
      List<Event> eventList = query.list();
      for (Event event : eventList) {
        if (eventId == event.getEventId()) {
          out.println("            <option value=\"" + event.getEventId() + "\" selected=\"selected\">"
              + event.getLabel() + "</option>");
        } else {
          out.println("            <option value=\"" + event.getEventId() + "\">" + event.getLabel() + " - CVX "
              + event.getVaccineCvx() + "</option>");
        }
      }
      out.println("          </select>");
      out.println("        </td>");
      out.println("      </tr>");
      if (user.getSelectedTestPanelCase().getTestCase().getDateSet() == DateSet.FIXED) {
        out.println("      <tr>");
        out.println("          <th>Administered Date</th>");
        out.println("          <td><input type=\"text\" name=\"" + PARAM_EVENT_DATE + "\" size=\"10\" value=\""
            + eventDateString + "\"/></td>");
        out.println("        </td>");
        out.println("      </tr>");
      } else {
        RelativeRule rr = relativeRule;
        for (int pos = 1; pos <= 4; pos++) {
          printRelativeRuleRow(out, testEventList, rr, pos, "Administered");
          if (rr != null) {
            rr = rr.getAndRule();
          }
        }
      }
      out.println("        <tr>");
      out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION
          + "\" size=\"15\" value=\"" + ACTION_ADD_VACCINATION + "\"/></td>");
      out.println("        </tr>");

      out.println("    </table>");
      out.println("    <input type=\"hidden\" name=\"" + PARAM_SHOW + "\" value=\"" + SHOW_EDIT_VACCINATIONS + "\"");
      out.println("  </form>");

    }

    if (eventType == EventType.ACIP_DEFINED_CONDITION) {

      out.println("  <h2>Add ACIP-Defined Condition" + editButton + "</h2>");
      out.println("  <form method=\"POST\" action=\"testCases\">");
      out.println("    <input type=\"hidden\" name=\"" + PARAM_TEST_PANEL_CASE_ID + "\" value=\""
          + user.getSelectedTestPanelCase().getTestPanelCaseId() + "\"/>");
      out.println("    <table width=\"100%\">");
      out.println("      <tr>");
      out.println("        <th>Condition</th>");
      out.println("        <td><div class=\"scrollRadioBox\">");
      Query query = dataSession.createQuery("from Event where eventTypeCode = ? order by label");
      query.setParameter(0, EventType.ACIP_DEFINED_CONDITION.getEventTypeCode());
      List<Event> eventList = query.list();
      for (Event event : eventList) {
        if (eventId == event.getEventId()) {
          out.println("          <input type=\"radio\" name=\"" + PARAM_EVENT_ID + "\" value=\"" + event.getEventId()
              + "\" checked=\"checked\">" + event.getLabel() + "<br/>");
        } else {
          out.println("          <input type=\"radio\" name=\"" + PARAM_EVENT_ID + "\" value=\"" + event.getEventId()
              + "\">" + event.getLabel() + "<br/>");
        }
      }
      out.println("          <input type=\"radio\" name=\"" + PARAM_EVENT_ID
          + "\" value=\"-1\">none of these, proposing a new label<br/>");
      out.println("        </div>propose new label");
      out.println("        <input type=\"text\" name=\"" + PARAM_NEW_EVENT_LABEL + "\" value=\"" + newEventLabel
          + "\" size=\"50\" /></td>");
      out.println("      </tr>");

      if (user.getSelectedTestPanelCase().getTestCase().getDateSet() == DateSet.FIXED) {
        out.println("      <tr>");
        out.println("          <th>Observed Date</th>");
        out.println("          <td><input type=\"text\" name=\"" + PARAM_EVENT_DATE + "\" size=\"10\" value=\""
            + eventDateString + "\"/></td>");
        out.println("        </td>");
        out.println("      </tr>");
      } else {
        RelativeRule rr = relativeRule;
        for (int pos = 1; pos <= 4; pos++) {
          printRelativeRuleRow(out, testEventList, rr, pos, "Observed");
          if (rr != null) {
            rr = rr.getAndRule();
          }
        }
      }
      out.println("        <tr>");
      out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION
          + "\" value=\"" + ACTION_ADD_EVENT + "\"/></td>");
      out.println("        </tr>");

      out.println("    </table>");
      out.println("    <input type=\"hidden\" name=\"" + PARAM_SHOW + "\" value=\"" + SHOW_EDIT_EVENTS + "\"/>");
      out.println("    <input type=\"hidden\" name=\"" + PARAM_EVENT_TYPE_CODE + "\" value=\""
          + eventType.getEventTypeCode() + "\"/>");
      out.println("  </form>");

    }

    if (eventType == EventType.CONDITION_IMPLICATION) {

      out.println("  <h2>Add Condition Implication" + editButton + "</h2>");
      out.println("  <form method=\"POST\" action=\"testCases\">");
      out.println("    <input type=\"hidden\" name=\"" + PARAM_TEST_PANEL_CASE_ID + "\" value=\""
          + user.getSelectedTestPanelCase().getTestPanelCaseId() + "\"/>");
      out.println("    <table width=\"100%\">");
      out.println("      <tr>");
      out.println("        <th>Implication</th>");
      out.println("        <td><div class=\"scrollRadioBox\">");
      Query query = dataSession.createQuery("from Event where eventTypeCode = ? order by label");
      query.setParameter(0, EventType.CONDITION_IMPLICATION.getEventTypeCode());
      List<Event> eventList = query.list();
      for (Event event : eventList) {
        if (eventId == event.getEventId()) {
          out.println("          <input type=\"radio\" name=\"" + PARAM_EVENT_ID + "\" value=\"" + event.getEventId()
              + "\" checked=\"checked\">" + event.getLabel() + "<br/>");
        } else {
          out.println("          <input type=\"radio\" name=\"" + PARAM_EVENT_ID + "\" value=\"" + event.getEventId()
              + "\">" + event.getLabel() + "<br/>");
        }
      }
      out.println("          <input type=\"radio\" name=\"" + PARAM_EVENT_ID
          + "\" value=\"-1\">none of these, proposing a new label<br/>");
      out.println("        </div>propose new label");
      out.println("        <input type=\"text\" name=\"" + PARAM_NEW_EVENT_LABEL + "\" value=\"" + newEventLabel
          + "\" size=\"50\" /></td>");
      out.println("      </tr>");

      if (user.getSelectedTestPanelCase().getTestCase().getDateSet() == DateSet.FIXED) {
        out.println("      <tr>");
        out.println("          <th>Asserted Date</th>");
        out.println("          <td><input type=\"text\" name=\"" + PARAM_EVENT_DATE + "\" size=\"10\" value=\""
            + eventDateString + "\"/></td>");
        out.println("        </td>");
        out.println("      </tr>");
      } else {
        RelativeRule rr = relativeRule;
        for (int pos = 1; pos <= 4; pos++) {
          printRelativeRuleRow(out, testEventList, rr, pos, "Asserted");
          if (rr != null) {
            rr = rr.getAndRule();
          }
        }
      }
      out.println("        <tr>");
      out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION
          + "\" value=\"" + ACTION_ADD_EVENT + "\"/></td>");
      out.println("        </tr>");

      out.println("    </table>");
      out.println("    <input type=\"hidden\" name=\"" + PARAM_SHOW + "\" value=\"" + SHOW_EDIT_EVENTS + "\"/>");
      out.println("    <input type=\"hidden\" name=\"" + PARAM_EVENT_TYPE_CODE + "\" value=\""
          + eventType.getEventTypeCode() + "\"/>");
      out.println("  </form>");
    }

    out.println("<p>");

    out.println("</p>");

    out.println("</div>");
  }

  public void printEventTable(PrintWriter out, User user, EventType eventType, List<TestEvent> testEventList,
      String sectionLabel, String itemLabel, String show) throws UnsupportedEncodingException {
    TestCase testCase = user.getSelectedTestCase();

    out.println("  <h2>" + sectionLabel + "</h2>");
    out.println("  <table width=\"100%\">");
    out.println("    <tr>");
    out.println("      <th>#</th>");
    out.println("      <th>" + itemLabel + "</th>");
    if (eventType != null && eventType == EventType.VACCINATION) {
      out.println("      <th>CVX</th>");
      out.println("      <th>MVX</th>");
    }
    if (testCase.getDateSet() == DateSet.RELATIVE) {
      out.println("      <th>Date Rule</th>");
    }
    out.println("      <th>Date</th>");
    out.println("      <th>Action</th>");
    out.println("    </tr>");

    for (TestEvent testEvent : testEventList) {
      boolean shouldShow;
      if (eventType == null) {
        shouldShow = testEvent.getEvent().getEventType() != EventType.VACCINATION
            && testEvent.getEvent().getEventType() != EventType.ACIP_DEFINED_CONDITION
            && testEvent.getEvent().getEventType() != EventType.CONDITION_IMPLICATION;
      } else {
        shouldShow = eventType != EventType.BIRTH && eventType != EventType.EVALUATION
            && testEvent.getEvent().getEventType() == eventType;
      }
      if (shouldShow) {
        String deleteLink = "testCases?" + PARAM_SHOW + "=" + show + "&" + PARAM_TEST_EVENT_ID + "="
            + testEvent.getTestEventId() + "&" + PARAM_ACTION + "=" + URLEncoder.encode(ACTION_DELETE_EVENT, "UTF-8");
        if (eventType != null) {
          deleteLink += "&" + PARAM_EVENT_TYPE_CODE + "=" + eventType.getEventTypeCode();
        }
        out.println("    <tr>");
        out.println("      <td>" + testEvent.getScreenId() + "</td>");
        out.println("      <td>" + testEvent.getEvent().getLabel() + "</td>");
        if (eventType != null && eventType == EventType.VACCINATION) {
          out.println("      <td>" + testEvent.getEvent().getVaccineCvx() + "</td>");
          out.println("      <td>" + testEvent.getEvent().getVaccineMvx() + "</td>");
        }
        if (testCase.getDateSet() == DateSet.RELATIVE) {
          out.print("      <td>");
          printOutRelativeRule(out, testEvent.getEventRule());
          out.print("</td>");
        }
        out.println("      <td>" + (testEvent.getEventDate() == null ? "" : sdf.format(testEvent.getEventDate()))
            + "</td>");
        out.println("      <td><a class=\"fauxbutton\" href=\"" + deleteLink + "\">Delete</a></td>");
        out.println("    </tr>");
      }
    }
    out.println("  </table>");
  }

  public void printOutRelativeRule(PrintWriter out, RelativeRule eventRule) {
    if (eventRule != null) {
      out.print(eventRule.getTimePeriod().toStringNoSign());
      out.print(" " + eventRule.getLabelScreen());
      RelativeRule childRule = eventRule.getAndRule();
      while (childRule != null) {
        out.print(" but not ");
        out.print(childRule.getTimePeriod().toStringNoSign());
        if (childRule.getTestEvent() != null) {
          out.print(" " + childRule.getLabelScreen());
        }
        childRule = childRule.getAndRule();
      }
    }
  }

  public void setupTestEventList(User user) {
    TestCase testCase = user.getSelectedTestCase();
    Query query = applicationSession.getDataSession().createQuery(
        "from TestEvent where testCase = ? order by eventDate");
    query.setParameter(0, testCase);
    testEventList = query.list();
    testEventList.add(0, createBirthEvent(testCase));
    testEventList.add(createEvaluationEvent(testCase));

    countVaccination = 0;
    countACIP = 0;
    countCondition = 0;
    countOther = 0;
    for (TestEvent testEvent : testEventList) {
      if (testEvent.getEvent().getEventType() == EventType.VACCINATION) {
        countVaccination++;
        testEvent.setScreenId(countVaccination);
      } else if (testEvent.getEvent().getEventType() == EventType.ACIP_DEFINED_CONDITION) {
        countACIP++;
        testEvent.setScreenId(countACIP);
      } else if (testEvent.getEvent().getEventType() == EventType.CONDITION_IMPLICATION) {
        countCondition++;
        testEvent.setScreenId(countCondition);
      } else if (testEvent.getEvent().getEventType() == EventType.BIRTH) {
        // do not count
      } else if (testEvent.getEvent().getEventType() == EventType.EVALUATION) {
        // do not count
      } else {
        countOther++;
        testEvent.setScreenId(countOther);
      }
    }
  }

  public TestEvent createEvaluationEvent(TestCase testCase) {
    TestEvent evaluationEvent = new TestEvent();
    evaluationEvent.setTestEventId(EVALUATION_TEST_EVENT_ID);
    evaluationEvent.setEventDate(testCase.getEvalDate());
    evaluationEvent.setTestCase(testCase);
    evaluationEvent.setEvent(Event.EVALUATION_EVENT);
    return evaluationEvent;
  }

  public TestEvent createBirthEvent(TestCase testCase) {
    TestEvent birthEvent = new TestEvent();
    birthEvent.setTestEventId(BIRTH_TEST_EVENT_ID);
    birthEvent.setEventDate(testCase.getPatientDob());
    birthEvent.setTestCase(testCase);
    birthEvent.setEvent(Event.BIRTH_EVENT);
    return birthEvent;
  }

  public RelativeRule readRelativeRules(HttpServletRequest req, Session dataSession, TestCase testCase) {
    RelativeRule relativeRule = null;
    RelativeRule childRule = null;
    int i = 1;

    while (req.getParameter(PARAM_EVENT_RULE + i) != null) {
      RelativeRule parentRule = childRule;
      childRule = new RelativeRule(notNull(req.getParameter(PARAM_EVENT_RULE + i)));
      if (parentRule == null) {
        relativeRule = childRule;
      } else {
        parentRule.setAndRule(childRule);
      }
      String beforeOrAfter = notNull(req.getParameter(PARAM_EVENT_RULE_BEFORE_OR_AFTER + i), "A");
      childRule.setBeforeOrAfter(beforeOrAfter.equals("B") ? RelativeRule.BeforeOrAfter.BEFORE
          : RelativeRule.BeforeOrAfter.AFTER);
      String relativeRuleTestEventIdString = req.getParameter(PARAM_RELATIVE_RULE_TEST_EVENT_ID + i);
      if (relativeRuleTestEventIdString != null) {
        int relativeRuleTestEventId = Integer.parseInt(relativeRuleTestEventIdString);
        TestEvent relativeRuleTestEvent;
        if (relativeRuleTestEventId == BIRTH_TEST_EVENT_ID) {
          relativeRuleTestEvent = createBirthEvent(testCase);
          childRule.setRelativeTo(RelativeTo.BIRTH);
        } else if (relativeRuleTestEventId == EVALUATION_TEST_EVENT_ID) {
          relativeRuleTestEvent = createEvaluationEvent(testCase);
          childRule.setRelativeTo(RelativeTo.EVALUATION);
        } else {
          relativeRuleTestEvent = (TestEvent) dataSession.get(TestEvent.class, relativeRuleTestEventId);
          childRule.setRelativeTo(RelativeTo.EVENT);
        }
        childRule.setTestEvent(relativeRuleTestEvent);
      }
      i++;
    }
    return relativeRule;
  }

  public void printRelativeRuleRow(PrintWriter out, List<TestEvent> testEventList, RelativeRule relativeRule, int pos,
      String label) {

    if (pos == 1) {
      out.println("      <tr id=\"" + label + "." + pos + "\">");
      out.println("          <th>" + label + "</th>");
    } else {
      out.println("      <tr id=\"" + label + "." + pos + "\" style=\"display: none;\">");
      out.println("          <td>but not before</td>");
    }
    out.println("          <td id=\"\">");
    out.println("            <input type=\"text\" name=\"" + PARAM_EVENT_RULE + pos + "\" size=\"17\" value=\""
        + (relativeRule == null || relativeRule.getTimePeriod() == null ? "" : relativeRule.getTimePeriod()) + "\"/>");
    out.println("            <input type=\"radio\" name=\""
        + PARAM_EVENT_RULE_BEFORE_OR_AFTER
        + pos
        + "\" value=\"B\""
        + ((relativeRule != null && relativeRule.getBeforeOrAfter() == RelativeRule.BeforeOrAfter.BEFORE) ? " checked=\"true\""
            : "") + "/> Before ");
    out.println("            <input type=\"radio\" name=\""
        + PARAM_EVENT_RULE_BEFORE_OR_AFTER
        + pos
        + "\" value=\"O\""
        + ((relativeRule == null || (relativeRule.isZero() && relativeRule.getBeforeOrAfter() == RelativeRule.BeforeOrAfter.AFTER)) ? " checked=\"true\""
            : "") + "/> On ");
    out.println("            <input type=\"radio\" name=\""
        + PARAM_EVENT_RULE_BEFORE_OR_AFTER
        + pos
        + "\" value=\"A\""
        + ((relativeRule != null && !relativeRule.isZero() && relativeRule.getBeforeOrAfter() == RelativeRule.BeforeOrAfter.AFTER) ? " checked=\"true\""
            : "") + "/> After ");
    out.println("          <select name=\"" + PARAM_RELATIVE_RULE_TEST_EVENT_ID + pos + "\" onChange=\"showRow('"
        + label + "." + (pos + 1) + "')\">");
    out.println("            <option value=\"0\">--select--</option>");
    for (TestEvent testEvent : testEventList) {
      String eventLabel;
      if (testEvent.getEvent().getEventType() == EventType.BIRTH) {
        eventLabel = "Birth";
      } else if (testEvent.getEvent().getEventType() == EventType.EVALUATION) {
        eventLabel = "Evaluation";
      } else if (testEvent.getEvent().getEventType() == EventType.VACCINATION) {
        eventLabel = "Vaccination #" + testEvent.getScreenId();
      } else if (testEvent.getEvent().getEventType() == EventType.ACIP_DEFINED_CONDITION) {
        eventLabel = "ACIP-Defined Condition #" + testEvent.getScreenId();
      } else if (testEvent.getEvent().getEventType() == EventType.CONDITION_IMPLICATION) {
        eventLabel = "Condition Implication #" + testEvent.getScreenId();
      } else {
        eventLabel = "Other Event #" + testEvent.getScreenId();
      }
      if (relativeRule != null && relativeRule.getTestEvent() != null && relativeRule.getTestEvent().equals(testEvent)) {
        out.println("            <option value=\"" + testEvent.getTestEventId() + "\" selected=\"selected\">"
            + eventLabel + "</option>");
      } else {
        out.println("            <option value=\"" + testEvent.getTestEventId() + "\">" + eventLabel + "</option>");
      }
    }
    out.println("          </select>");
    out.println("        </td>");
    out.println("      </tr>");
  }

  public void printTaskGroup(PrintWriter out, Session dataSession, User user) {
    if (user.getSelectedTaskGroup() != null) {
      Query query = dataSession.createQuery("from Expert where taskGroup = ? order by roleStatus, user.name");
      query.setParameter(0, user.getSelectedTaskGroup());
      List<Expert> expertList = query.list();
      out.println("<div class=\"centerColumn\">");
      out.println("  <h2>Task Group Members</h2>");
      out.println("  <table width=\"100%\">");
      out.println("    <tr>");
      out.println("      <th>Name</th>");
      out.println("      <th>Organization</th>");
      out.println("      <th>Role</th>");
      out.println("    </tr>");
      for (Expert expert : expertList) {
        out.println("    <tr>");
        out.println("      <td>" + expert.getUser().getName() + "</td>");
        out.println("      <td>" + expert.getUser().getOrganization() + "</td>");
        out.println("      <td>" + expert.getRole().getLabel() + "</td>");
        out.println("    </tr>");
      }
      out.println("  </table>");
      out.println("</div>");
    }
  }

  public void setupForPrinting(Session dataSession, User user) {
    TestPanelCase testPanelCase = user.getSelectedTestPanelCase();
    if (testPanelCase != null) {
      if (user.getSelectedCategoryName() == null) {
        user.setSelectedCategoryName(testPanelCase.getCategoryName());
      }
    }

    TaskGroup taskGroup = user.getSelectedTaskGroup();
    if (testPanelCase != null || taskGroup != null) {
      user.setCanEditTestCase(false);
      Query query = dataSession.createQuery("from Expert where user = ? and taskGroup = ?");
      query.setParameter(0, user);
      query.setParameter(1, taskGroup);
      List<Expert> expertList = query.list();
      if (expertList.size() == 0) {
        user.setSelectedExpert(null);
      } else {
        Expert expert = expertList.get(0);
        user.setSelectedExpert(expert);
        if (testPanelCase != null && expert.getRole().canEdit()) {
          query = dataSession.createQuery("from TestPanelCase where testCase = ?");
          query.setParameter(0, testPanelCase.getTestCase());
          int size = query.list().size();
          if (size <= 1) {
            user.setCanEditTestCase(true);
          }
        }
      }
    }
  }

  public void printAddEditTestCases(HttpServletRequest req, PrintWriter out, Session dataSession,
      TestPanelCase testPanelCase) {
    String label;
    String description;
    int vaccineGroupId;
    String patientFirst;
    String patientLast;
    String patientSex;
    String patientDob;
    String categoryName;
    String testCaseNumber;
    String dateSetCode;
    String evalDate;
    String evalRule;

    TestCase testCase = null;
    if (testPanelCase != null) {
      testCase = testPanelCase.getTestCase();
    }

    if (testPanelCase == null) {
      label = notNull(req.getParameter(PARAM_LABEL));
      description = notNull(req.getParameter(PARAM_DESCRIPTION));
      vaccineGroupId = notNull(req.getParameter(PARAM_VACCINE_GROUP_ID), 0);
      patientFirst = notNull(req.getParameter(PARAM_PATIENT_FIRST), RandomNames.getRandomFirstName());
      patientLast = notNull(req.getParameter(PARAM_PATIENT_LAST), RandomNames.getRandomLastName());
      patientSex = notNull(req.getParameter(PARAM_PATIENT_SEX), "F");
      patientDob = notNull(req.getParameter(PARAM_PATIENT_DOB));
      categoryName = notNull(req.getParameter(PARAM_CATEGORY_NAME));
      testCaseNumber = notNull(req.getParameter(PARAM_TEST_CASE_NUMBER));
      dateSetCode = notNull(req.getParameter(PARAM_DATE_SET_CODE), DateSet.RELATIVE.getDateSetCode());
      evalDate = notNull(req.getParameter(PARAM_EVAL_DATE));
      evalRule = notNull(req.getParameter(PARAM_EVAL_RULE));
    } else {
      label = notNull(req.getParameter(PARAM_LABEL), testCase.getLabel());
      description = notNull(req.getParameter(PARAM_DESCRIPTION), testCase.getDescription());
      vaccineGroupId = notNull(req.getParameter(PARAM_VACCINE_GROUP_ID), (testCase.getVaccineGroup() == null ? 0
          : testCase.getVaccineGroup().getVaccineGroupId()));
      patientFirst = notNull(req.getParameter(PARAM_PATIENT_FIRST), testCase.getPatientFirst());
      patientLast = notNull(req.getParameter(PARAM_PATIENT_LAST), testCase.getPatientLast());
      patientSex = notNull(req.getParameter(PARAM_PATIENT_SEX), testCase.getPatientSex());
      patientDob = notNull(req.getParameter(PARAM_PATIENT_DOB), sdf.format(testCase.getPatientDob()));
      categoryName = notNull(req.getParameter(PARAM_CATEGORY_NAME), testPanelCase.getCategoryName());
      testCaseNumber = notNull(req.getParameter(PARAM_TEST_CASE_NUMBER), testPanelCase.getTestCaseNumber());
      dateSetCode = notNull(req.getParameter(PARAM_DATE_SET_CODE), testCase.getDateSetCode());
      evalDate = notNull(req.getParameter(PARAM_EVAL_DATE), sdf.format(testCase.getEvalDate()));
      evalRule = notNull(req.getParameter(PARAM_EVAL_RULE), testCase.getEvalRule() != null ? testCase.getEvalRule()
          .getTimePeriodString() : "");
    }

    if (testCaseNumber.equals("")) {
      testCaseNumber = getNextTestCaseNumber(dataSession);
    }

    out.println("<script>");
    out.println("  <!-- ");
    out.println("  function changeDateSet(dateSetField) { ");
    out.println("    var relativeRow = document.getElementById('relativeRow'); ");
    out.println("    var fixedRow1 = document.getElementById('fixedRow1'); ");
    out.println("    var fixedRow2 = document.getElementById('fixedRow2'); ");
    out.println("    if (dateSetField.value == 'R') { ");
    out.println("      relativeRow.style.display = 'table-row'; ");
    out.println("      fixedRow1.style.display = 'none'; ");
    out.println("      fixedRow2.style.display = 'none'; ");
    out.println("    } else { ");
    out.println("      relativeRow.style.display = 'none'; ");
    out.println("      fixedRow1.style.display = 'table-row'; ");
    out.println("      fixedRow2.style.display = 'table-row'; ");
    out.println("    }");
    out.println("  }");
    out.println("  -->");
    out.println("</script>");

    out.println("<div class=\"centerColumn\">");
    out.println("  <h2>" + (testPanelCase == null ? "Add" : "Update")
        + " Test Case <a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW + "=" + SHOW_TEST_CASE + "&"
        + PARAM_TEST_PANEL_CASE_ID + "=" + applicationSession.getUser().getSelectedTestPanelCase().getTestPanelCaseId()
        + "\">Cancel</a></h2>");
    out.println("    <form method=\"POST\" action=\"testCases\">");
    if (testPanelCase != null) {
      out.println("      <input type=\"hidden\" name=\"" + PARAM_TEST_PANEL_CASE_ID + "\" value=\""
          + testPanelCase.getTestPanelCaseId() + "\"/>");
    }
    out.println("      <table width=\"100%\">");
    out.println("        <tr>");
    out.println("          <th>Category</th>");
    out.println("          <td><input type=\"text\" name=\"" + PARAM_CATEGORY_NAME + "\" size=\"50\" value=\""
        + categoryName + "\"/></td>");
    out.println("        </tr>");
    if (applicationSession.getUser().isCanEditTestCase()) {
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
      out.println("        <tr>");
      out.println("          <th>Vaccine Group</th>");
      out.println("          <td>");
      out.println("            <select type=\"text\" name=\"" + PARAM_VACCINE_GROUP_ID + "\">");
      out.println("              <option value=\"0\">--select--</option>");
      Query query = dataSession.createQuery("from VaccineGroup order by label");
      List<VaccineGroup> vaccineGroupList = query.list();
      for (VaccineGroup vaccineGroup : vaccineGroupList) {
        if (vaccineGroup.getVaccineGroupId() == vaccineGroupId) {
          out.println("              <option value=\"" + vaccineGroup.getVaccineGroupId() + "\" selected=\"selected\">"
              + vaccineGroup.getLabel() + "</option>");
        } else {
          out.println("              <option value=\"" + vaccineGroup.getVaccineGroupId() + "\">"
              + vaccineGroup.getLabel() + "</option>");
        }
      }
      out.println("            </select");
      out.println("          </td>");
      out.println("        </tr>");
    }

    out.println("          <th>Number</th>");
    out.println("          <td><input type=\"text\" name=\"" + PARAM_TEST_CASE_NUMBER + "\" size=\"15\" value=\""
        + testCaseNumber + "\"/></td>");
    out.println("        </tr>");
    out.println("        <tr>");
    if (applicationSession.getUser().isCanEditTestCase()) {

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
      out.println("          <th>Date Set</th>");
      out.println("          <td>");
      out.println("            <input type=\"radio\" name=\"" + PARAM_DATE_SET_CODE + "\" value=\""
          + DateSet.RELATIVE.getDateSetCode() + "\""
          + (dateSetCode.equals(DateSet.RELATIVE.getDateSetCode()) ? " checked=\"true\"" : "")
          + " onChange=\"changeDateSet(this)\"/> Relative ");
      out.println("            <input type=\"radio\" name=\"" + PARAM_DATE_SET_CODE + "\" value=\""
          + DateSet.FIXED.getDateSetCode() + "\""
          + (dateSetCode.equals(DateSet.FIXED.getDateSetCode()) ? " checked=\"true\"" : "")
          + " onChange=\"changeDateSet(this)\"/> Fixed");
      out.println("          </td>");
      out.println("        </tr>");

      String showRelative = "";
      String showFixed = "";
      if (dateSetCode.equals(DateSet.RELATIVE.getDateSetCode())) {
        showFixed = " style=\"display: none;\"";
      } else {
        showRelative = " style=\"display: none;\"";
      }

      out.println("        <tr" + showRelative + " id=\"relativeRow\">");
      out.println("          <th>Patient Age</th>");
      out.println("          <td><input type=\"text\" name=\"" + PARAM_EVAL_RULE + "\" size=\"30\" value=\"" + evalRule
          + "\"/></td>");
      out.println("        </tr>");
      out.println("        <tr" + showFixed + " id=\"fixedRow1\">");
      out.println("          <th>Date of Birth</th>");
      out.println("          <td><input type=\"text\" name=\"" + PARAM_PATIENT_DOB + "\" size=\"10\" value=\""
          + patientDob + "\"/></td>");
      out.println("        </tr>");
      out.println("        <tr" + showFixed + " id=\"fixedRow2\">");
      out.println("          <th>Evaluation Date</th>");
      out.println("          <td><input type=\"text\" name=\"" + PARAM_EVAL_DATE + "\" size=\"10\" value=\"" + evalDate
          + "\"/></td>");
      out.println("        </tr>");
    }
    out.println("        <tr>");
    out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION
        + "\" size=\"15\" value=\"" + (testPanelCase == null ? ACTION_ADD_TEST_CASE : ACTION_UPDATE_TEST_CASE)
        + "\"/></td>");
    out.println("        </tr>");
    out.println("      </table>");
    out.println("    </form>");
    out.println("</div>");
  }

  public void printTree(PrintWriter out, Session dataSession, User user) throws UnsupportedEncodingException {
    Query query = dataSession.createQuery("from TaskGroup order by label");
    List<TaskGroup> taskGroupList = query.list();

    out.println("<div class=\"leftColumn\">");
    if (user.getSelectedTaskGroup() != null) {
      out.println("<h2>" + user.getSelectedTaskGroup().getLabel() + "</h2>");
      TaskGroup taskGroup1 = user.getSelectedTaskGroup();
      Query query1;

      out.println("    <ul class=\"selectLevel1\">");
      query1 = dataSession.createQuery("from TestPanel where taskGroup = ? order by label");
      query1.setParameter(0, taskGroup1);
      List<TestPanel> testPanelList = query1.list();
      if (user.getSelectedTestPanel() != null) {
        printTestPanel(out, dataSession, user, user.getSelectedTestPanel());
      }
      for (TestPanel testPanel : testPanelList) {
        if (user.getSelectedTestPanel() == null || !user.getSelectedTestPanel().equals(testPanel)) {
          printTestPanel(out, dataSession, user, testPanel);
        }
      }
      if (user.getSelectedExpert() != null && user.getSelectedExpert().getRole().canEdit()) {
        out.println("      <li><a class=\"add\" href=\"\">add test panel</a></li>");
      } else if (testPanelList.size() == 0) {
        out.println("      <li><em>no test panels defined</em></li>");
      }
      out.println("    </ul>");

      out.println("<h2>All Expert Groups</h2>");
      out.println("<ul class=\"selectLevel1\">");
    } else {
      out.println("<h2>Select Expert Groups</h2>");
      out.println("<ul class=\"selectLevel1\">");
    }
    for (TaskGroup taskGroup : taskGroupList) {
      final String link1 = "testCases?" + PARAM_ACTION + "=" + URLEncoder.encode(ACTION_SELECT_TASK_GROUP, "UTF-8")
          + "&" + PARAM_TASK_GROUP_ID + "=" + taskGroup.getTaskGroupId();
      out.println("  <li class=\"selectLevel1\"><a href=\"" + link1 + "\">" + taskGroup.getLabel() + "</a></li>");
    }
    out.println("</ul>");
    out.println("</div>");
  }

  public void printActualsVsExpected(PrintWriter out, User user) {
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

    if (testCase.getVaccineGroup() != null) {
      out.println("<h2>Actual vs Expected for " + testCase.getVaccineGroup().getLabel()
          + " <a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW + "=" + SHOW_EDIT_EXPECTATIONS + "&"
          + PARAM_TEST_PANEL_ID + "=" + testPanel.getTestPanelId() + "&" + PARAM_VACCINE_GROUP_ID + "="
          + testCase.getVaccineGroup().getVaccineGroupId() + "\">Edit</a></h2>");

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
    }

    out.println("</div>");
  }

  public void printForecastActual(PrintWriter out, ForecastActual forecastActual) {
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

    boolean hasActual = forecastActual != null;

    String expectedAdmin = (forecastExpected.getAdmin() == null ? Admin.UNKNOWN : forecastExpected.getAdmin())
        .getLabel();
    String actualAdmin = (forecastActual == null || forecastActual.getAdmin() == null ? Admin.UNKNOWN : forecastActual
        .getAdmin()).getLabel();
    String expectedDoseNumber = forecastExpected.getDoseNumber() != null ? forecastExpected.getDoseNumber() : "-";
    String actualDoseNumber = hasActual && forecastActual.getDoseNumber() != null ? forecastActual.getDoseNumber()
        : "-";
    String expectedValidDate = forecastExpected.getValidDate() != null ? sdf.format(forecastExpected.getValidDate())
        : "-";
    String actualValidDate = hasActual && forecastActual.getValidDate() != null ? sdf.format(forecastActual
        .getValidDate()) : "-";
    String expectedDueDate = forecastExpected.getDueDate() != null ? sdf.format(forecastExpected.getDueDate()) : "-";
    String actualDueDate = hasActual && forecastActual.getDueDate() != null ? sdf.format(forecastActual.getDueDate())
        : "-";
    String expectedOverdueDate = forecastExpected.getOverdueDate() != null ? sdf.format(forecastExpected
        .getOverdueDate()) : "-";
    String actualOverdueDate = hasActual && forecastActual.getOverdueDate() != null ? sdf.format(forecastActual
        .getOverdueDate()) : "-";

    String styleClassLabel = forecastCompare.matchExactly() ? "pass" : "fail";
    String styleClassAdmin = (hasActual && expectedAdmin != null && actualAdmin != null && expectedAdmin
        .equals(actualAdmin)) ? "pass" : "fail";
    String styleClassDoseNumber = hasActual && compareDoseNumbers(expectedDoseNumber, actualDoseNumber) ? "pass"
        : "fail";
    String styleClassValid = hasActual && compareDoseNumbers(expectedValidDate, actualValidDate) ? "pass" : "fail";
    String styleClassDue = hasActual && compareDoseNumbers(expectedDueDate, actualDueDate) ? "pass" : "fail";
    String styleClassOverdue = hasActual && compareDoseNumbers(expectedOverdueDate, actualOverdueDate) ? "pass"
        : "fail";

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

    if (forecastActual == null) {
      String entityLabel = "Actual from " + applicationSession.getUser().getSelectedSoftware().getLabel();
      out.println("    <tr>");
      out.println("      <td class=\"" + styleClassLabel + "\">" + entityLabel + "</td>");
      out.println("      <td class=\"" + styleClassLabel + "\" colspan=\"5\"><em>no results to compare</em></td>");
      out.println("    </tr>");
    } else {
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

  public List<TestEvent> printTestCase(PrintWriter out, User user) {
    TestPanelCase testPanelCase = user.getSelectedTestPanelCase();
    TestCase testCase = testPanelCase.getTestCase();
    String editLink = "testCases?" + PARAM_SHOW + "=" + SHOW_EDIT_TEST_CASE + "&" + PARAM_TEST_PANEL_CASE_ID + "="
        + testPanelCase.getTestPanelCaseId();
    String editButton = "";
    if (user.getSelectedExpert() != null && user.getSelectedExpert().getRole().canEdit()) {
      editButton = " <a class=\"fauxbutton\" href=\"" + editLink + "\">Edit</a>";
    }
    out.println("<div class=\"centerLeftColumn\">");
    out.println("  <h2>Test Case" + editButton + "</h2>");
    out.println("  <table width=\"100%\">");
    out.println("    <tr>");
    out.println("      <th>Category</th>");
    out.println("      <td>" + testPanelCase.getCategoryName() + "</td>");
    out.println("    </tr>");
    out.println("    <tr>");
    out.println("      <th>Label</th>");
    out.println("      <td>" + testCase.getLabel() + "</td>");
    out.println("    </tr>");
    out.println("    <tr>");
    out.println("      <th>Description</th>");
    out.println("      <td>" + testCase.getDescription() + "</td>");
    out.println("    </tr>");
    out.println("        <tr>");
    out.println("          <th>Vaccine Group</th>");
    out.println("          <td>"
        + (testPanelCase.getTestCase().getVaccineGroup() == null ? "" : testPanelCase.getTestCase().getVaccineGroup()
            .getLabel()) + "</td>");
    out.println("    </tr>");
    out.println("    <tr>");
    out.println("      <th>Number</th>");
    out.println("      <td>" + testPanelCase.getTestCaseNumber() + "</td>");
    out.println("    </tr>");
    out.println("    <tr>");
    out.println("      <th>Patient</th>");
    out.println("      <td>" + testCase.getPatientFirst() + " " + testCase.getPatientLast() + " ("
        + testCase.getPatientSex() + ")</td>");
    out.println("    </tr>");
    out.println("    <tr>");
    out.println("      <th>Birth Date</th>");
    if (testCase.getPatientDob() == null) {
      out.println("<td></td>");
    } else if (testCase.getDateSet() == DateSet.RELATIVE && testCase.getEvalRule() != null) {
      out.println("      <td>" + sdf.format(testCase.getPatientDob()) + " (" + testCase.getEvalRule().getTimePeriod()
          + " old)</td>");
    } else {
      out.println("      <td>" + sdf.format(testCase.getPatientDob()) + "</td>");
    }
    out.println("    </tr>");
    out.println("    <tr>");
    out.println("      <th>Evaluation Date</th>");
    out.println("      <td>" + sdf.format(testCase.getEvalDate()) + "</td>");
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
        countACIP++;
      } else if (testEvent.getEvent().getEventType() == EventType.CONDITION_IMPLICATION) {
        countCondition++;
      }
    }

    editLink = "testCases?" + PARAM_SHOW + "=" + SHOW_EDIT_VACCINATIONS + "&" + PARAM_TEST_PANEL_CASE_ID + "="
        + testPanelCase.getTestPanelCaseId();
    editButton = "";
    if (user.isCanEditTestCase()) {
      editButton = " <a class=\"fauxbutton\" href=\"" + editLink + "\">Edit</a>";
    }
    out.println("  <h2>Vaccination History" + editButton + "</h2>");
    if (countVaccination > 0) {
      out.println("  <table width=\"100%\">");
      out.println("    <tr>");
      out.println("      <th>#</th>");
      out.println("      <th>Vaccination</th>");
      out.println("      <th>CVX</th>");
      out.println("      <th>MVX</th>");
      out.println("      <th>Date</th>");
      out.println("      <th>Age</th>");
      out.println("    </tr>");
      int screenId = 0;
      for (TestEvent testEvent : testEventList) {
        if (testEvent.getEvent().getEventType() == EventType.VACCINATION) {
          screenId++;
          testEvent.setScreenId(screenId);
          out.println("    <tr>");
          out.println("      <td>" + testEvent.getScreenId() + "</td>");
          out.println("      <td>" + testEvent.getEvent().getLabel() + "</td>");
          out.println("      <td>" + testEvent.getEvent().getVaccineCvx() + "</td>");
          out.println("      <td>" + testEvent.getEvent().getVaccineMvx() + "</td>");
          out.println("      <td>" + (testEvent.getEventDate() == null ? "" : sdf.format(testEvent.getEventDate()))
              + "</td>");
          out.println("      <td>" + testEvent.getAgeAlmost(testCase) + "</td>");
          out.println("    </tr>");
        }
      }
      out.println("  </table>");
    }

    editLink = "testCases?" + PARAM_SHOW + "=" + SHOW_EDIT_EVENTS + "&" + PARAM_TEST_PANEL_CASE_ID + "="
        + testPanelCase.getTestPanelCaseId() + "&" + PARAM_EVENT_TYPE_CODE + "=";
    if (user.isCanEditTestCase()) {
      editButton = " <a class=\"fauxbutton\" href=\"" + editLink + EventType.ACIP_DEFINED_CONDITION.getEventTypeCode()
          + "\">Edit</a>";
    } else {
      editButton = "";
    }
    out.println("  <h2>ACIP-Defined Conditions" + editButton + "</h2>");

    if (countACIP > 0) {
      printEventViewTable(out, testCase, testEventList, EventType.ACIP_DEFINED_CONDITION, "Condition");
    }

    if (user.isCanEditTestCase()) {
      editButton = " <a class=\"fauxbutton\" href=\"" + editLink + EventType.CONDITION_IMPLICATION.getEventTypeCode()
          + "\">Edit</a>";
    } else {
      editButton = "";
    }
    out.println("  <h2>Condition Implications" + editButton + "</h2>");
    if (countCondition > 0) {
      printEventViewTable(out, testCase, testEventList, EventType.CONDITION_IMPLICATION, "Condition");
    }

    out.println("</div>");

    return testEventList;
  }

  public void printEventViewTable(PrintWriter out, TestCase testCase, List<TestEvent> testEventList,
      EventType eventType, String eventLabel) {
    out.println("  <table width=\"100%\">");
    out.println("    <tr>");
    out.println("      <th>#</th>");
    out.println("      <th>" + eventLabel + "</th>");
    out.println("      <th>Date</th>");
    out.println("      <th>Age</th>");
    out.println("    </tr>");
    int screenId = 0;
    for (TestEvent testEvent : testEventList) {
      if (testEvent.getEvent().getEventType() == eventType) {
        screenId++;
        testEvent.setScreenId(screenId);
        out.println("    <tr>");
        out.println("      <td>" + testEvent.getScreenId() + "</td>");
        out.println("      <td>" + testEvent.getEvent().getLabel() + "</td>");
        out.println("      <td>" + (testEvent.getEventDate() == null ? "" : sdf.format(testEvent.getEventDate()))
            + "</td>");
        out.println("      <td>" + testEvent.getAgeAlmost(testCase) + "</td>");
        out.println("    </tr>");
      }
    }
    out.println("  </table>");
  }

  public void printTestPanel(PrintWriter out, Session dataSession, User user, TestPanel testPanel)
      throws UnsupportedEncodingException {
    Query query;
    final String link1 = "testCases?" + PARAM_ACTION + "=" + URLEncoder.encode(ACTION_SELECT_TEST_PANEL, "UTF-8") + "&"
        + PARAM_TEST_PANEL_ID + "=" + testPanel.getTestPanelId();
    if (user.getSelectedTestPanel() != null && user.getSelectedTestPanel().equals(testPanel)) {
      out.println("      <li class=\"selectLevel1\"><a href=\"" + link1 + "\">" + testPanel.getLabel() + "</a>");

      out.println("        <ul class=\"selectLevel2\">");
      query = dataSession.createQuery("from TestPanelCase where testPanel = ? order by categoryName, testCase.label");
      query.setParameter(0, testPanel);
      List<TestPanelCase> testPanelCaseList = query.list();
      String lastCategoryName = "";
      boolean selectedCategoryOpened = false;
      for (TestPanelCase testPanelCase : testPanelCaseList) {
        if (!testPanelCase.getCategoryName().equals(lastCategoryName)) {
          if (selectedCategoryOpened) {
            if (user.getSelectedExpert() != null && user.getSelectedExpert().getRole().canEdit()) {
              out.println("            <li><a class=\"add\" href=\"testCases?show=" + SHOW_ADD_TEST_CASE + "&"
                  + PARAM_CATEGORY_NAME + "=" + lastCategoryName + "\">add test case</a></li>");
            }
            out.println("          </ul>");
            out.println("        </li>");
            selectedCategoryOpened = false;
          }
          final String link2 = "testCases?" + PARAM_ACTION + "=" + URLEncoder.encode(ACTION_SELECT_CATEGORY, "UTF-8")
              + "&" + PARAM_CATEGORY_NAME + "=" + testPanelCase.getCategoryName();
          if (user.getSelectedCategoryName() != null
              && testPanelCase.getCategoryName().equals(user.getSelectedCategoryName())) {
            out.println("          <li class=\"selectLevel2\"><a href=\"" + link2 + "\">"
                + testPanelCase.getCategoryName() + "</a>");
            out.println("            <ul class=\"selectLevel3\">");
            selectedCategoryOpened = true;
          } else {
            out.println("          <li class=\"selectLevel2\"><a href=\"" + link2 + "\">"
                + testPanelCase.getCategoryName() + "</a></li>");
          }
        }
        if (user.getSelectedCategoryName() != null
            && testPanelCase.getCategoryName().equals(user.getSelectedCategoryName())) {
          final String link3 = "testCases?" + PARAM_ACTION + "="
              + URLEncoder.encode(ACTION_SELECT_TEST_PANEL_CASE, "UTF-8") + "&" + PARAM_TEST_PANEL_CASE_ID + "="
              + testPanelCase.getTestPanelCaseId();
          out.println("              <li class=\"selectLevel4\"><a href=\"" + link3 + "\">"
              + testPanelCase.getTestCase().getLabel() + "</a></li>");
        }
        lastCategoryName = testPanelCase.getCategoryName();
      }
      if (selectedCategoryOpened) {
        if (user.getSelectedExpert() != null && user.getSelectedExpert().getRole().canEdit()) {
          out.println("            <li><a class=\"add\" href=\"testCases?show=" + SHOW_ADD_TEST_CASE + "&"
              + PARAM_CATEGORY_NAME + "=" + lastCategoryName + "\">add test case</a></li>");
        }
        out.println("          </ul>");
        out.println("        </li>");
        selectedCategoryOpened = false;
      }
      if (user.getSelectedCategoryName() == null) {
        if (user.getSelectedExpert() != null && user.getSelectedExpert().getRole().canEdit()) {
          out.println("          <li><a class=\"add\" href=\"testCases?show=" + SHOW_ADD_TEST_CASE
              + "\">add test case</a></li>");
        }
      }
      out.println("        </ul>");
      out.println("      </li>");
    } else {
      out.println("      <li class=\"selectLevel1\"><a href=\"" + link1 + "\">" + testPanel.getLabel() + "</a></li>");
    }
  }

}
