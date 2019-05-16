package org.immregistries.vfa.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.request.UrlEncoder;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.immregistries.vfa.connect.LSVFConnector;
import org.immregistries.vfa.connect.model.Admin;
import org.immregistries.vfa.connect.model.AssociatedDate;
import org.immregistries.vfa.connect.model.Consideration;
import org.immregistries.vfa.connect.model.ConsiderationGuidance;
import org.immregistries.vfa.connect.model.ConsiderationType;
import org.immregistries.vfa.connect.model.DateSet;
import org.immregistries.vfa.connect.model.Evaluation;
import org.immregistries.vfa.connect.model.Event;
import org.immregistries.vfa.connect.model.EventType;
import org.immregistries.vfa.connect.model.ForecastActual;
import org.immregistries.vfa.connect.model.Guidance;
import org.immregistries.vfa.connect.model.Rationale;
import org.immregistries.vfa.connect.model.RationaleGuidance;
import org.immregistries.vfa.connect.model.Recommend;
import org.immregistries.vfa.connect.model.RecommendGuidance;
import org.immregistries.vfa.connect.model.RecommendRange;
import org.immregistries.vfa.connect.model.RecommendType;
import org.immregistries.vfa.connect.model.RelativeRule;
import org.immregistries.vfa.connect.model.RelativeTo;
import org.immregistries.vfa.connect.model.Resource;
import org.immregistries.vfa.connect.model.ResourceGuidance;
import org.immregistries.vfa.connect.model.Service;
import org.immregistries.vfa.connect.model.ServiceOption;
import org.immregistries.vfa.connect.model.Software;
import org.immregistries.vfa.connect.model.SoftwareResult;
import org.immregistries.vfa.connect.model.TestCase;
import org.immregistries.vfa.connect.model.TestCaseSetting;
import org.immregistries.vfa.connect.model.TestEvent;
import org.immregistries.vfa.connect.model.VaccineGroup;
import org.immregistries.vfa.connect.util.TimePeriod;
import org.immregistries.vfa.manager.ExpectationsManager;
import org.immregistries.vfa.manager.ForecastActualExpectedCompare;
import org.immregistries.vfa.manager.ForecastActualGenerator;
import org.immregistries.vfa.manager.RelativeRuleManager;
import org.immregistries.vfa.manager.SoftwareManager;
import org.immregistries.vfa.model.Available;
import org.immregistries.vfa.model.EvaluationExpected;
import org.immregistries.vfa.model.Expert;
import org.immregistries.vfa.model.ForecastExpected;
import org.immregistries.vfa.model.GuidanceExpected;
import org.immregistries.vfa.model.Include;
import org.immregistries.vfa.model.Result;
import org.immregistries.vfa.model.Role;
import org.immregistries.vfa.model.TaskGroup;
import org.immregistries.vfa.model.TestNote;
import org.immregistries.vfa.model.TestPanel;
import org.immregistries.vfa.model.TestPanelCase;
import org.immregistries.vfa.model.TestPanelEvaluation;
import org.immregistries.vfa.model.TestPanelForecast;
import org.immregistries.vfa.model.TestPanelGuidance;
import org.immregistries.vfa.model.User;
import org.immregistries.vfa.web.testCase.RandomNames;

public class TestCasesServlet extends MainServlet {
  public TestCasesServlet() {
    super("Test Cases", ServletProtection.ALL_USERS);
  }

  public static final String ACTION_SELECT_TASK_GROUP = "Select Task Group";
  public static final String ACTION_SELECT_TEST_PANEL = "Select Test Panel";
  public static final String ACTION_SELECT_TEST_PANEL_CASE = "Select Test Panel Case";
  public static final String ACTION_SELECT_CATEGORY = "Select Category";
  public static final String ACTION_ADD_TEST_CASE = "Add Test Case";
  public static final String ACTION_ADD_TEST_PANEL = "Add Test Panel";
  public static final String ACTION_ADD_EXPECTATIONS = "Add Expectations";
  public static final String ACTION_UPDATE_TEST_CASE = "Update Test Case";
  public static final String ACTION_UPDATE_TEST_PANEL = "Update Test Panel";
  public static final String ACTION_COPY_TEST_CASE = "Copy Test Case";
  public static final String ACTION_DELETE_EVENT = "Delete Event";
  public static final String ACTION_ADD_VACCINATION = "Add Vaccination";
  public static final String ACTION_ADD_EVENT = "Add Event";
  public static final String ACTION_ADD_COMMENT = "Add Comment";
  public static final String ACTION_UPDATE_COMMENT = "Update Comment";
  public static final String ACTION_SAVE_EXPECTATIONS = "Save Expectations";
  public static final String ACTION_SAVE_TEST_CASE_SETTINGS = "Save Test Case Settings";
  public static final String ACTION_UPDATE_RELATIVE_DATES = "Update Relative Dates";
  public static final String ACTION_REQUEST_ACTUAL_RESULTS = "Request Actual Results";

  public static final String PARAM_TASK_GROUP_ID = "taskGroupId";
  public static final String PARAM_TEST_PANEL_ID = "testPanelId";
  public static final String PARAM_TEST_PANEL_CASE_ID = "testPanelCaseId";
  public static final String PARAM_CATEGORY_NAME = "categoryName";

  public static final String PARAM_SHOW_EXCLUDED_TEST_CASES = "showExcludedTestCases";
  public static final String PARAM_LABEL = "label";
  public static final String PARAM_AVAILABLE_CODE = "availableCode";
  public static final String PARAM_DESCRIPTION = "description";
  public static final String PARAM_VACCINE_GROUP_ID = "vaccineGroupId";
  public static final String PARAM_INCLUDE_STATUS = "includeStatus";
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
  public static final String PARAM_EVENT_LABEL = "eventLabel";
  public static final String PARAM_CONDITION_CODE = "conditionCode";
  public static final String PARAM_EVENT_TYPE_CODE = "eventTypeCode";
  public static final String PARAM_VALID_DATE = "validDate";
  public static final String PARAM_VALID_RULE = "validRule";
  public static final String PARAM_DUE_DATE = "dueDate";
  public static final String PARAM_DUE_RULE = "dueRule";
  public static final String PARAM_OVERDUE_DATE = "overdueDate";
  public static final String PARAM_OVERDUE_RULE = "overdueRule";
  public static final String PARAM_FINISHED_DATE = "finishedDate";
  public static final String PARAM_FINISHED_RULE = "finishedRule";
  public static final String PARAM_ADMIN_STATUS = "adminStatus";
  public static final String PARAM_DOSE_NUMBER = "doseNumber";
  public static final String PARAM_EVALUATION_STATUS = "evaluationStatus";
  public static final String PARAM_RECOMMEND_ID = "recommendId";
  public static final String PARAM_RECOMMEND_TEXT = "recommendText";
  public static final String PARAM_RECOMMEND_TYPE_CODE = "recommendTypeCode";
  public static final String PARAM_RECOMMEND_RANGE_CODE = "recommendRangeCode";
  public static final String PARAM_CONSIDERATION_ID = "considerationId";
  public static final String PARAM_CONSIDERATION_TEXT = "considerationText";
  public static final String PARAM_CONSIDERATION_TYPE_CODE = "considerationTypeCode";
  public static final String PARAM_RATIONALE_ID = "rationaleId";
  public static final String PARAM_RATIONALE_TEXT = "rationaleText";
  public static final String PARAM_RESOURCE_ID = "resourceId";
  public static final String PARAM_RESOURCE_TEXT = "resourceText";
  public static final String PARAM_RESOURCE_LINK = "resourceLink";
  public static final String PARAM_SOFTWARE_ID_SELECTED = "softwareIdSelected";
  public static final String PARAM_NOTE_TEXT = "noteText";
  public static final String PARAM_RESULT_STATUS = "resultStatus";
  public static final String PARAM_OPTION_VALUE = "optionValue";
  public static final String PARAM_TEST_NOTE_ID = "testNoteId";
  public static final String PARAM_TEST_NOTE_EDIT = "testNoteEdit";
  public static final String PARAM_SOFTWARE_RESULT_ID = "softwareResultId";

  public static final String RULE_BEFORE_OR_AFTER = "BeforeOrAfter";
  public static final String RULE_TEST_EVENT_ID = "TestEventId";

  public static final String SHOW_TEST_CASE = "testCase";
  public static final String SHOW_TASK_GROUP = "taskGroup";
  public static final String SHOW_TEST_PANEL = "testPanel";
  public static final String SHOW_EDIT_TEST_CASE = "editTestCase";
  public static final String SHOW_EDIT_TEST_CASE_SETTINGS = "editTestCaseSettings";
  public static final String SHOW_EDIT_TEST_PANEL = "editTestPanel";
  public static final String SHOW_ADD_TEST_CASE = "addTestCase";
  public static final String SHOW_ADD_TEST_PANEL = "addTestPanel";
  public static final String SHOW_COPY_TEST_CASE = "copyTestCase";
  public static final String SHOW_PREVIEW_TEST_CASE = "previewTestCase";
  public static final String SHOW_EDIT_VACCINATIONS = "editVaccinations";
  public static final String SHOW_EDIT_EXPECTATIONS = "editExpectations";
  public static final String SHOW_EDIT_EVENTS = "editEvents";
  public static final String SHOW_ADD_ACTUAL_VS_EXPECTED = "Add Actual vs Expected";
  public static final String SHOW_REQUEST_ACTUAL_RESULTS = "Request Actual Results";
  public static final String SHOW_DEBUGGING_TOOLS = "Debugging Tools";
  public static final String SHOW_SOFTWARE_RESULT = "softwareResult";

  private static final int EVALUATION_TEST_EVENT_ID = -2;
  private static final int BIRTH_TEST_EVENT_ID = -1;

  private static String lastIssuedTestNumberPrefix = "";
  private static int lastIssuedTestNumberSuffix = 0;

  private List<TestEvent> testEventList;
  private int countVaccination = 0;
  private int countACIP = 0;
  private int countCondition = 0;
  private int countOther = 0;

  private static Admin[] ADMIN_STANDARD_LIST = { Admin.NOT_COMPLETE, Admin.COMPLETE, Admin.IMMUNE, Admin.CONTRAINDICATED, Admin.AGED_OUT };
  private static Admin[] ADMIN_NON_STANDARD_LIST = { Admin.OVERDUE, Admin.DUE, Admin.DUE_LATER, Admin.FINISHED, Admin.COMPLETE_FOR_SEASON,
      Admin.ASSUMED_COMPLETE_OR_IMMUNE, Admin.CONTRAINDICATED, Admin.NO_RESULTS };

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
  public String execute(HttpServletRequest req, HttpServletResponse resp, String action, String show) throws IOException {
    User user = applicationSession.getUser();
    Session dataSession = applicationSession.getDataSession();
    switchToTestPanel(req, user, dataSession);
    if (show == null) {
      show = SHOW_TEST_CASE;
    }
    if (action != null) {
      if (action.equals(ACTION_SELECT_TASK_GROUP)) {
        doSelectTaskGroup(req, user, dataSession);
        return SHOW_TASK_GROUP;
      } else if (action.equals(ACTION_SELECT_TEST_PANEL)) {
        doSelectTestPanel(req, user, dataSession);
        return SHOW_TEST_PANEL;
      } else if (action.equals(ACTION_SELECT_TEST_PANEL_CASE)) {
        return SHOW_TEST_CASE;
      } else if (action.equals(ACTION_SELECT_CATEGORY)) {
        applicationSession.getUser().setSelectedCategoryName(req.getParameter(PARAM_CATEGORY_NAME).trim());
        return SHOW_TEST_CASE;
      } else if (action.equals(ACTION_ADD_TEST_CASE) || action.equals(ACTION_UPDATE_TEST_CASE) || action.equals(ACTION_COPY_TEST_CASE)) {
        return saveTestCase(req, action, show, user, dataSession);
      } else if (action.equals(ACTION_ADD_TEST_PANEL) || action.equals(ACTION_UPDATE_TEST_PANEL)) {
        return saveTestPanel(req, action, show, user, dataSession);
      } else if (action.equals(ACTION_DELETE_EVENT)) {
        int testEventId = notNull(req.getParameter(PARAM_TEST_EVENT_ID), 0);
        return doDelete(testEventId, show, dataSession);
      } else if (action.equals(ACTION_ADD_VACCINATION) || action.equals(ACTION_ADD_EVENT)) {
        return doAddEvent(req, action, show, user, dataSession);
      } else if (action.equals(ACTION_SAVE_EXPECTATIONS)) {
        return doSaveExpectations(req, user, dataSession);
      } else if (action.equals(ACTION_UPDATE_RELATIVE_DATES)) {
        boolean canEdit = user.getSelectedExpert() != null && user.getSelectedExpert().getRole().canEdit();
        if (canEdit && user.getSelectedTaskGroup() != null && user.getSelectedTestPanel() != null) {
          TestPanel testPanel = user.getSelectedTestPanel();
          for (TestPanelCase testPanelCase : getTestPanelCaseList(dataSession, testPanel)) {
            TestCase testCase = testPanelCase.getTestCase();
            RelativeRuleManager.updateFixedDatesForRelativeRules(testCase, dataSession, true);
          }
        }
      } else if (action.equals(ACTION_ADD_EXPECTATIONS)) {
        if (req.getParameter(PARAM_VACCINE_GROUP_ID).equals("")) {
          applicationSession.setAlertError("Unable to Add Expectation, please select vaccine group first.");
          return SHOW_TEST_CASE;
        }
        return SHOW_EDIT_EXPECTATIONS;
      } else if (action.equals(ACTION_REQUEST_ACTUAL_RESULTS)) {
        doRequestActualResults(req, user, dataSession);
        return SHOW_TEST_CASE;
      } else if (action.equals(ACTION_ADD_COMMENT)) {
        doAddComment(req, user, dataSession);
      } else if (action.equals(ACTION_UPDATE_COMMENT)) {
        doUpdateComment(req, user, dataSession);
      } else if (action.equals(ACTION_SAVE_TEST_CASE_SETTINGS)) {
        int softwareId = Integer.parseInt(req.getParameter(PARAM_SOFTWARE_ID_SELECTED));
        Software software = (Software) dataSession.get(Software.class, softwareId);
        user.setSelectedSoftware(software);
        TestCase testCase = user.getSelectedTestCase();

        Query query = applicationSession.getDataSession().createQuery("from TestCaseSetting where testCase = ? and serviceOption.serviceType = ?");
        query.setParameter(0, testCase);
        query.setParameter(1, software.getServiceType());
        List<TestCaseSetting> testCaseSettingList = query.list();
        Map<ServiceOption, TestCaseSetting> serviceOptionMap = new HashMap<ServiceOption, TestCaseSetting>();
        for (TestCaseSetting testCaseSetting : testCaseSettingList) {
          serviceOptionMap.put(testCaseSetting.getServiceOption(), testCaseSetting);
        }
        query = dataSession.createQuery("from ServiceOption where serviceType = ? order by optionLabel");
        query.setParameter(0, software.getServiceType());
        List<ServiceOption> serviceOptionList = query.list();
        Transaction transaction = dataSession.beginTransaction();
        dataSession.saveOrUpdate(user);
        for (ServiceOption serviceOption : serviceOptionList) {
          String optionValue = req.getParameter(PARAM_OPTION_VALUE + serviceOption.getOptionId());
          TestCaseSetting testCaseSetting = serviceOptionMap.get(serviceOption);
          if (optionValue.equals("")) {
            if (testCaseSetting != null) {
              dataSession.delete(testCaseSetting);
            }
          } else {
            if (testCaseSetting == null) {
              testCaseSetting = new TestCaseSetting();
              testCaseSetting.setTestCase(testCase);
              testCaseSetting.setServiceOption(serviceOption);
            }
            testCaseSetting.setOptionValue(optionValue);
            dataSession.saveOrUpdate(testCaseSetting);
          }
        }
        transaction.commit();
      }

    }
    return show;
  }

  public void doSelectTaskGroup(HttpServletRequest req, User user, Session dataSession) {
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
  }

  public void doSelectTestPanel(HttpServletRequest req, User user, Session dataSession) {
    int testPanelId = Integer.parseInt(req.getParameter(PARAM_TEST_PANEL_ID));
    Transaction trans = dataSession.beginTransaction();
    TestPanel testPanel = (TestPanel) dataSession.get(TestPanel.class, testPanelId);
    user.setSelectedTestPanel(testPanel);
    user.setSelectedTestPanelCase(null);
    user.setSelectedCategoryName(null);
    dataSession.update(user);
    trans.commit();
  }

  public void doAddComment(HttpServletRequest req, User user, Session dataSession) {
    String noteText = req.getParameter(PARAM_NOTE_TEXT).trim();
    if (noteText.equals("")) {
      applicationSession.setAlertError("Unable to save, you must enter your comment to save. ");
    } else {
      Transaction transaction = dataSession.beginTransaction();

      if (user.isExpertOrAdmin(user.getSelectedTaskGroup())) {
        String resultStatus = req.getParameter(PARAM_RESULT_STATUS);
        TestPanelCase testPanelCase = user.getSelectedTestPanelCase();
        testPanelCase.setResult(Result.getResult(resultStatus));
        dataSession.update(testPanelCase);
        if (!noteText.endsWith(".") && !noteText.endsWith("!") && !noteText.endsWith("?")) {
          noteText += ".";
        }
        noteText += " Changed test status to " + testPanelCase.getResult().getLabel() + ".";
      }
      TestNote testNote = new TestNote();
      testNote.setTestCase(user.getSelectedTestCase());
      testNote.setUser(user);
      testNote.setNoteText(noteText);
      testNote.setNoteDate(new Date());
      dataSession.save(testNote);
      transaction.commit();
    }
  }

  public void doUpdateComment(HttpServletRequest req, User user, Session dataSession) {
    int testNoteId = Integer.parseInt(req.getParameter(PARAM_TEST_NOTE_ID));
    TestNote testNote = (TestNote) dataSession.get(TestNote.class, testNoteId);
    Transaction transaction = dataSession.beginTransaction();
    String noteText = req.getParameter(PARAM_NOTE_TEXT).trim();
    if (noteText.equals("")) {
      dataSession.delete(testNote);
    } else {
      testNote.setNoteText(noteText);
      testNote.setNoteDate(new Date());
      dataSession.update(testNote);
    }
    transaction.commit();
  }

  private void doRequestActualResults(HttpServletRequest req, User user, Session dataSession) {
    TestPanelCase testPanelCase = user.getSelectedTestPanelCase();
    List<Software> softwareList = SoftwareManager.getListOfUnrestrictedSoftware(user, dataSession);
    for (Software softwareSelected : softwareList) {
      String isSoftwareSelected = req.getParameter(PARAM_SOFTWARE_ID_SELECTED + softwareSelected.getSoftwareId());
      if (isSoftwareSelected != null && isSoftwareSelected.equals("true")) {
        try {
          ForecastActualGenerator.runForecastActual(testPanelCase, softwareSelected, dataSession, true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void switchToTestPanel(HttpServletRequest req, User user, Session dataSession) {
    if (req.getParameter(PARAM_TEST_PANEL_CASE_ID) != null) {
      int testPanelCaseId = Integer.parseInt(req.getParameter(PARAM_TEST_PANEL_CASE_ID));
      Transaction trans = dataSession.beginTransaction();
      trans.commit();
      trans = dataSession.beginTransaction();
      TestPanelCase testPanelCase = (TestPanelCase) dataSession.get(TestPanelCase.class, testPanelCaseId);
      user.setSelectedTaskGroup(testPanelCase.getTestPanel().getTaskGroup());
      user.setSelectedTestPanel(testPanelCase.getTestPanel());
      user.setSelectedTestPanelCase(testPanelCase);
      user.setSelectedTestCase(testPanelCase.getTestCase());
      user.setSelectedCategoryName(testPanelCase.getCategoryName());
      dataSession.update(user);
      trans.commit();
    }
  }

  private String doSaveExpectations(HttpServletRequest req, User user, Session dataSession) {
    VaccineGroup vaccineGroup = (VaccineGroup) dataSession.get(VaccineGroup.class, notNull(req.getParameter(PARAM_VACCINE_GROUP_ID), 0));
    TestPanelCase testPanelCase = user.getSelectedTestPanelCase();
    TestCase testCase = testPanelCase.getTestCase();

    ExpectationsManager expectationsManager = new ExpectationsManager(user, vaccineGroup, false, dataSession);
    // Read evaluation expected
    readEvaluationExpectations(req, expectationsManager);
    String problem = readForecastExpectations(req, dataSession, testCase, expectationsManager);
    readGuidanceExpectations(req, dataSession, expectationsManager);

    if (problem != null) {
      applicationSession.setAlertError("Unable to save expectations: " + problem);
      return SHOW_EDIT_EXPECTATIONS;
    } else {

      boolean canSetTesPanelExpectations = false;
      {
        Query query = dataSession.createQuery("from Expert where user = ? and taskGroup = ?");
        query.setParameter(0, user);
        query.setParameter(1, user.getSelectedTaskGroup());
        List<Expert> expertList = query.list();
        if (expertList.size() > 0) {
          Role role = expertList.get(0).getRole();
          canSetTesPanelExpectations = role == Role.ADMIN || role == Role.EXPERT;
        }
      }
      Transaction transaction = dataSession.beginTransaction();
      for (TestEvent vaccinationEvent : expectationsManager.getVaccinationEvents()) {
        EvaluationExpected evaluationExpected = expectationsManager.getTestEventMapToEvaluationExpected().get(vaccinationEvent);
        evaluationExpected.setAuthor(user);
        evaluationExpected.setUpdatedDate(new Date());
        dataSession.saveOrUpdate(evaluationExpected);
        if (canSetTesPanelExpectations) {
          TestPanelEvaluation testPanelEvaluation = null;
          Query query = dataSession.createQuery(
              "from TestPanelEvaluation where testPanelCase = ? and evaluationExpected.testCase = ? and evaluationExpected.vaccineGroup =  ? and evaluationExpected.testEvent = ?");
          query.setParameter(0, testPanelCase);
          query.setParameter(1, testCase);
          query.setParameter(2, vaccineGroup);
          query.setParameter(3, vaccinationEvent);
          List<TestPanelEvaluation> testPanelEvaluationList = query.list();
          if (testPanelEvaluationList.size() > 0) {
            testPanelEvaluation = testPanelEvaluationList.get(0);
          } else {
            testPanelEvaluation = new TestPanelEvaluation();
            testPanelEvaluation.setTestPanelCase(testPanelCase);
          }
          testPanelEvaluation.setEvaluationExpected(evaluationExpected);
          dataSession.saveOrUpdate(testPanelEvaluation);
        }
      }
      ForecastExpected forecastExpected = expectationsManager.getForecastExpected();
      forecastExpected.setAuthor(user);
      forecastExpected.setUpdatedDate(new Date());
      if (forecastExpected.getValidRule() != null) {
        forecastExpected.setValidDate(forecastExpected.getValidRule().calculateDate(testCase));
        saveRelativeRule(dataSession, forecastExpected.getValidRule());
      }
      if (forecastExpected.getDueRule() != null) {
        forecastExpected.setDueDate(forecastExpected.getDueRule().calculateDate(testCase));
        saveRelativeRule(dataSession, forecastExpected.getDueRule());
      }
      if (forecastExpected.getOverdueRule() != null) {
        forecastExpected.setOverdueDate(forecastExpected.getOverdueRule().calculateDate(testCase));
        saveRelativeRule(dataSession, forecastExpected.getOverdueRule());
      }
      if (forecastExpected.getFinishedRule() != null) {
        forecastExpected.setFinishedDate(forecastExpected.getFinishedRule().calculateDate(testCase));
        saveRelativeRule(dataSession, forecastExpected.getFinishedRule());
      }
      dataSession.saveOrUpdate(forecastExpected);
      if (canSetTesPanelExpectations) {
        TestPanelForecast testPanelForecast = null;
        Query query = dataSession
            .createQuery("from TestPanelForecast where forecastExpected.testCase = ? and testPanelCase = ? and forecastExpected.vaccineGroup = ? ");
        query.setParameter(0, testCase);
        query.setParameter(1, testPanelCase);
        query.setParameter(2, vaccineGroup);
        List<TestPanelForecast> testPanelForecastList = query.list();
        if (testPanelForecastList.size() > 0) {
          testPanelForecast = testPanelForecastList.get(0);
        } else {
          testPanelForecast = new TestPanelForecast();
          testPanelForecast.setTestPanelCase(testPanelCase);
        }
        testPanelForecast.setForecastExpected(forecastExpected);
        dataSession.saveOrUpdate(testPanelForecast);
      }
      {
        GuidanceExpected guidanceExpected = expectationsManager.getGuidanceExpected();
        guidanceExpected.setAuthor(user);
        guidanceExpected.setUpdatedDate(new Date());
        dataSession.saveOrUpdate(guidanceExpected.getGuidance());
        dataSession.saveOrUpdate(guidanceExpected);

        Guidance guidance = guidanceExpected.getGuidance();
        if (canSetTesPanelExpectations) {
          TestPanelGuidance testPanelGuidance = null;
          Query query = dataSession.createQuery(
              "from TestPanelGuidance where guidanceExpected.testCase = ? and testPanelCase = ? and guidanceExpected.guidance.vaccineGroup = ?");
          query.setParameter(0, testCase);
          query.setParameter(1, testPanelCase);
          query.setParameter(2, vaccineGroup);
          List<TestPanelGuidance> testPanelGuidanceList = query.list();
          if (testPanelGuidanceList.size() > 0) {
            testPanelGuidance = testPanelGuidanceList.get(0);
          } else {
            testPanelGuidance = new TestPanelGuidance();
            testPanelGuidance.setTestPanelCase(testPanelCase);
          }
          testPanelGuidance.setGuidanceExpected(guidanceExpected);
          dataSession.saveOrUpdate(testPanelGuidance);
        }
        for (RecommendGuidance recommendGuidance : expectationsManager.getRecommendGuidanceDeleteList()) {
          if (recommendGuidance.getRecommendGuidanceId() > 0) {
            dataSession.delete(recommendGuidance);
          }
        }
        for (RecommendGuidance recommendGuidance : expectationsManager.getRecommendGuidanceList()) {
          if (recommendGuidance.getRecommendGuidanceId() == 0) {
            if (recommendGuidance.getRecommend().getRecommendId() == 0) {
              dataSession.save(recommendGuidance.getRecommend());
            }
            recommendGuidance.setGuidance(guidance);
            dataSession.save(recommendGuidance);
          }
        }

        for (ConsiderationGuidance considerationGuidance : expectationsManager.getConsiderationGuidanceDeleteList()) {
          if (considerationGuidance.getConsiderationGuidanceId() > 0) {
            dataSession.delete(considerationGuidance);
          }
        }
        for (ConsiderationGuidance considerationGuidance : expectationsManager.getConsiderationGuidanceList()) {
          if (considerationGuidance.getConsiderationGuidanceId() == 0) {
            if (considerationGuidance.getConsideration().getConsiderationId() == 0) {
              dataSession.save(considerationGuidance.getConsideration());
            }
            considerationGuidance.setGuidance(guidance);
            dataSession.save(considerationGuidance);
          }
        }

        for (RationaleGuidance rationalGuidance : expectationsManager.getRationaleGuidanceDeleteList()) {
          if (rationalGuidance.getRationaleGuidanceId() > 0) {
            dataSession.delete(rationalGuidance);
          }
        }
        for (RationaleGuidance rationalGuidance : expectationsManager.getRationaleGuidanceList()) {
          if (rationalGuidance.getRationaleGuidanceId() == 0) {
            if (rationalGuidance.getRationale().getRationaleId() == 0) {
              dataSession.save(rationalGuidance.getRationale());
            }
            rationalGuidance.setGuidance(guidance);
            dataSession.save(rationalGuidance);
          }
        }

        for (RationaleGuidance resourceGuidance : expectationsManager.getRationaleGuidanceDeleteList()) {
          if (resourceGuidance.getRationaleGuidanceId() > 0) {
            dataSession.delete(resourceGuidance);
          }
        }
        for (ResourceGuidance resourceGuidance : expectationsManager.getResourceGuidanceList()) {
          if (resourceGuidance.getResourceGuidanceId() == 0) {
            if (resourceGuidance.getResource().getResourceId() == 0) {
              dataSession.save(resourceGuidance.getResource());
            }
            resourceGuidance.setGuidance(guidance);
            dataSession.save(resourceGuidance);
          }
        }
      }
      transaction.commit();
      return SHOW_EDIT_EXPECTATIONS;
    }
  }

  public void readGuidanceExpectations(HttpServletRequest req, Session dataSession, ExpectationsManager expectationsManager) {

    expectationsManager.getRecommendGuidanceDeleteList().clear();
    expectationsManager.getRecommendGuidanceDeleteList().addAll(expectationsManager.getRecommendGuidanceList());
    expectationsManager.getRecommendGuidanceList().clear();
    String[] recommendIdStrings = req.getParameterValues(PARAM_RECOMMEND_ID);
    if (recommendIdStrings != null) {
      for (String recommendIdString : recommendIdStrings) {
        int recommendId = Integer.parseInt(recommendIdString);
        if (recommendId > 0) {
          RecommendGuidance moveRecommendGuidance = null;
          for (RecommendGuidance recommendGuidance : expectationsManager.getRecommendGuidanceDeleteList()) {
            if (recommendGuidance.getRecommend().getRecommendId() == recommendId) {
              moveRecommendGuidance = recommendGuidance;
              break;
            }
          }
          if (moveRecommendGuidance != null) {
            expectationsManager.getRecommendGuidanceDeleteList().remove(moveRecommendGuidance);
            expectationsManager.getRecommendGuidanceList().add(moveRecommendGuidance);
          } else {
            Recommend recommend = (Recommend) dataSession.get(Recommend.class, recommendId);
            RecommendGuidance recommendGuidance = new RecommendGuidance();
            recommendGuidance.setRecommend(recommend);
            expectationsManager.getRecommendGuidanceList().add(recommendGuidance);
          }
        } else if (recommendId == -1) {
          String recommendText = req.getParameter(PARAM_RECOMMEND_TEXT);
          if (!recommendText.equals("")) {
            Recommend recommend = new Recommend();
            recommend.setRecommendText(recommendText);
            String recommendTypeCode = req.getParameter(PARAM_RECOMMEND_TYPE_CODE);
            if (!recommendTypeCode.equals("")) {
              recommend.setRecommendTypeCode(recommendTypeCode);
            }
            String recommendRangeCode = req.getParameter(PARAM_RECOMMEND_RANGE_CODE);
            if (!recommendRangeCode.equals("")) {
              recommend.setRecommendRangeCode(recommendRangeCode);
            }
            RecommendGuidance recommendGuidance = new RecommendGuidance();
            recommendGuidance.setRecommend(recommend);
            expectationsManager.getRecommendGuidanceList().add(recommendGuidance);
          }
        }
      }
    }

    expectationsManager.getConsiderationGuidanceDeleteList().clear();
    expectationsManager.getConsiderationGuidanceDeleteList().addAll(expectationsManager.getConsiderationGuidanceList());
    expectationsManager.getConsiderationGuidanceList().clear();
    String[] considerationIdStrings = req.getParameterValues(PARAM_CONSIDERATION_ID);
    if (considerationIdStrings != null) {
      expectationsManager.getConsiderationGuidanceList().clear();
      for (String considerationIdString : considerationIdStrings) {
        int considerationId = Integer.parseInt(considerationIdString);
        if (considerationId > 0) {
          ConsiderationGuidance moveConsiderationGuidance = null;
          for (ConsiderationGuidance considerationGuidance : expectationsManager.getConsiderationGuidanceDeleteList()) {
            if (considerationGuidance.getConsideration().getConsiderationId() == considerationId) {
              moveConsiderationGuidance = considerationGuidance;
            }
          }
          if (moveConsiderationGuidance != null) {
            expectationsManager.getConsiderationGuidanceDeleteList().remove(moveConsiderationGuidance);
            expectationsManager.getConsiderationGuidanceList().add(moveConsiderationGuidance);
          } else {
            Consideration consideration = (Consideration) dataSession.get(Consideration.class, considerationId);
            ConsiderationGuidance considerationGuidance = new ConsiderationGuidance();
            considerationGuidance.setConsideration(consideration);
            expectationsManager.getConsiderationGuidanceList().add(considerationGuidance);
          }
        } else if (considerationId == -1) {
          String considerationText = req.getParameter(PARAM_CONSIDERATION_TEXT);
          if (!considerationText.equals("")) {
            Consideration consideration = new Consideration();
            consideration.setConsiderationText(considerationText);
            String considerationTypeCode = req.getParameter(PARAM_CONSIDERATION_TYPE_CODE);
            if (!considerationTypeCode.equals("")) {
              consideration.setConsiderationTypeCode(considerationTypeCode);
            }
            ConsiderationGuidance considerationGuidance = new ConsiderationGuidance();
            considerationGuidance.setConsideration(consideration);
            expectationsManager.getConsiderationGuidanceList().add(considerationGuidance);
          }
        }
      }
    }

    expectationsManager.getRationaleGuidanceDeleteList().clear();
    expectationsManager.getRationaleGuidanceDeleteList().addAll(expectationsManager.getRationaleGuidanceList());
    expectationsManager.getRationaleGuidanceList().clear();
    String[] rationaleIdStrings = req.getParameterValues(PARAM_RATIONALE_ID);
    if (rationaleIdStrings != null) {
      expectationsManager.getRationaleGuidanceList().clear();
      for (String rationaleIdString : rationaleIdStrings) {
        int rationaleId = Integer.parseInt(rationaleIdString);
        if (rationaleId > 0) {
          RationaleGuidance moveRationaleGuidance = null;
          for (RationaleGuidance rationaleGuidance : expectationsManager.getRationaleGuidanceDeleteList()) {
            if (rationaleGuidance.getRationale().getRationaleId() == rationaleId) {
              moveRationaleGuidance = rationaleGuidance;
            }
          }
          if (moveRationaleGuidance != null) {
            expectationsManager.getRationaleGuidanceDeleteList().remove(moveRationaleGuidance);
            expectationsManager.getRationaleGuidanceList().add(moveRationaleGuidance);
          } else {
            Rationale rationale = (Rationale) dataSession.get(Rationale.class, rationaleId);
            RationaleGuidance rationaleGuidance = new RationaleGuidance();
            rationaleGuidance.setRationale(rationale);
            expectationsManager.getRationaleGuidanceList().add(rationaleGuidance);
          }
        } else if (rationaleId == -1) {
          String rationaleText = req.getParameter(PARAM_RATIONALE_TEXT);
          if (!rationaleText.equals("")) {
            Rationale rationale = new Rationale();
            rationale.setRationaleText(rationaleText);
            RationaleGuidance rationaleGuidance = new RationaleGuidance();
            rationaleGuidance.setRationale(rationale);
            expectationsManager.getRationaleGuidanceList().add(rationaleGuidance);
          }
        }
      }
    }

    expectationsManager.getResourceGuidanceDeleteList().clear();
    expectationsManager.getResourceGuidanceDeleteList().addAll(expectationsManager.getResourceGuidanceList());
    expectationsManager.getResourceGuidanceList().clear();
    String[] resourceIdStrings = req.getParameterValues(PARAM_RESOURCE_ID);
    if (resourceIdStrings != null) {
      expectationsManager.getResourceGuidanceList().clear();
      for (String resourceIdString : resourceIdStrings) {
        int resourceId = Integer.parseInt(resourceIdString);
        if (resourceId > 0) {
          ResourceGuidance moveResourceGuidance = null;
          for (ResourceGuidance resourceGuidance : expectationsManager.getResourceGuidanceDeleteList()) {
            if (resourceGuidance.getResource().getResourceId() == resourceId) {
              moveResourceGuidance = resourceGuidance;
            }
          }
          if (moveResourceGuidance != null) {
            expectationsManager.getResourceGuidanceDeleteList().remove(moveResourceGuidance);
            expectationsManager.getResourceGuidanceList().add(moveResourceGuidance);
          } else {
            Resource resource = (Resource) dataSession.get(Resource.class, resourceId);
            ResourceGuidance resourceGuidance = new ResourceGuidance();
            resourceGuidance.setResource(resource);
            expectationsManager.getResourceGuidanceList().add(resourceGuidance);
          }
        } else if (resourceId == -1) {
          String resourceText = req.getParameter(PARAM_RESOURCE_TEXT);
          String resourceLink = req.getParameter(PARAM_RESOURCE_LINK);
          if (!resourceText.equals("") && !resourceLink.equals("")) {
            Resource resource = new Resource();
            resource.setResourceText(resourceText);
            resource.setResourceLink(resourceLink);
            ResourceGuidance resourceGuidance = new ResourceGuidance();
            resourceGuidance.setResource(resource);
            expectationsManager.getResourceGuidanceList().add(resourceGuidance);
            ;
          }
        }
      }
    }
  }

  public String readForecastExpectations(HttpServletRequest req, Session dataSession, TestCase testCase, ExpectationsManager expectationsManager) {
    ForecastExpected forecastExpected = expectationsManager.getForecastExpected();
    String adminStatus = notNull(req.getParameter(PARAM_ADMIN_STATUS), forecastExpected.getAdminStatus());
    if (adminStatus.equals("")) {
      forecastExpected.setAdmin(null);
    } else {
      forecastExpected.setAdminStatus(adminStatus);
    }
    if (forecastExpected.getDoseNumber() == null) {
      forecastExpected.setDoseNumber("");
    }
    String doseNumber = notNull(req.getParameter(PARAM_DOSE_NUMBER), forecastExpected.getDoseNumber());
    forecastExpected.setDoseNumber(doseNumber);

    if (testCase.getDateSet() == DateSet.FIXED) {
      if (forecastExpected.getValidDate() == null) {
        forecastExpected.setValidDateString("");
      } else {
        forecastExpected.setValidDateString(sdf.format(forecastExpected.getValidDate()));
      }
      if (forecastExpected.getDueDate() == null) {
        forecastExpected.setDueDateString("");
      } else {
        forecastExpected.setDueDateString(sdf.format(forecastExpected.getDueDate()));
      }
      if (forecastExpected.getOverdueDate() == null) {
        forecastExpected.setOverdueDateString("");
      } else {
        forecastExpected.setOverdueDateString(sdf.format(forecastExpected.getOverdueDate()));
      }
      if (forecastExpected.getFinishedDate() == null) {
        forecastExpected.setFinishedDateString("");
      } else {
        forecastExpected.setFinishedDateString(sdf.format(forecastExpected.getFinishedDate()));
      }
      {
        String validDateString = notNull(req.getParameter(PARAM_VALID_DATE), forecastExpected.getValidDateString());
        forecastExpected.setValidDateString(validDateString);
        if (validDateString.equals("")) {
          forecastExpected.setValidDate(null);
        } else {
          try {
            forecastExpected.setValidDate(sdf.parse(validDateString));
          } catch (ParseException pe) {
            return "Earliest Date is not in MM/DD/YYYY format, please correct date. ";
          }
        }
      }
      {
        String dueDateString = notNull(req.getParameter(PARAM_DUE_DATE), forecastExpected.getDueDateString());
        forecastExpected.setDueDateString(dueDateString);
        if (dueDateString.equals("")) {
          forecastExpected.setDueDate(null);
        } else {
          try {
            forecastExpected.setDueDate(sdf.parse(dueDateString));
          } catch (ParseException pe) {
            return "Recommended Date is not in MM/DD/YYYY format, please correct date. ";
          }
        }
      }
      {
        String overdueDateString = notNull(req.getParameter(PARAM_OVERDUE_DATE), forecastExpected.getOverdueDateString());
        forecastExpected.setOverdueDateString(overdueDateString);
        if (overdueDateString.equals("")) {
          forecastExpected.setOverdueDate(null);
        } else {
          try {
            forecastExpected.setOverdueDate(sdf.parse(overdueDateString));
          } catch (ParseException pe) {
            return "Past Due Date is not in MM/DD/YYYY format, please correct date. ";
          }
        }
      }
      {
        String finishedDateString = notNull(req.getParameter(PARAM_FINISHED_DATE), forecastExpected.getFinishedDateString());
        forecastExpected.setFinishedDateString(finishedDateString);
        if (finishedDateString.equals("")) {
          forecastExpected.setFinishedDate(null);
        } else {
          try {
            forecastExpected.setFinishedDate(sdf.parse(finishedDateString));
          } catch (ParseException pe) {
            return "Latest Date is not in MM/DD/YYYY format, please correct date. ";
          }
        }
      }
    } else {
      forecastExpected.setValidRule(updateRelativeRule(PARAM_VALID_RULE, req, dataSession, testCase, forecastExpected.getValidRule()));
      forecastExpected.setDueRule(updateRelativeRule(PARAM_DUE_RULE, req, dataSession, testCase, forecastExpected.getDueRule()));
      forecastExpected.setOverdueRule(updateRelativeRule(PARAM_OVERDUE_RULE, req, dataSession, testCase, forecastExpected.getOverdueRule()));
      forecastExpected.setFinishedRule(updateRelativeRule(PARAM_FINISHED_RULE, req, dataSession, testCase, forecastExpected.getFinishedRule()));

    }
    return null;
  }

  public void readEvaluationExpectations(HttpServletRequest req, ExpectationsManager expectationsManager) {
    for (TestEvent vaccinationEvent : expectationsManager.getVaccinationEvents()) {
      EvaluationExpected evaluationExpected = expectationsManager.getTestEventMapToEvaluationExpected().get(vaccinationEvent);
      String evaluationStatus = notNull(req.getParameter(PARAM_EVALUATION_STATUS + vaccinationEvent.getTestEventId()),
          evaluationExpected.getEvaluationStatus());
      if (evaluationStatus == null || evaluationStatus.equals("")) {
        evaluationExpected.setEvaluation(null);
      } else {
        evaluationExpected.setEvaluationStatus(evaluationStatus);
      }
    }

  }

  public RelativeRule updateRelativeRule(String paramRuleName, HttpServletRequest req, Session dataSession, TestCase testCase, RelativeRule rrOld) {
    RelativeRule rrNew = readRelativeRules(paramRuleName, req, dataSession, testCase);
    if (rrOld == null) {
      rrOld = rrNew;
    } else if (rrNew != null) {
      RelativeRule posOld = rrOld;
      RelativeRule posNew = rrNew;
      while (posOld != null && posNew != null) {
        posOld.setTimePeriod(posNew.getTimePeriod());
        posOld.setRelativeToCode(posNew.getRelativeToCode());
        posOld.setTestEvent(posNew.getTestEvent());
        posOld = posOld.getAndRule();
        posNew = posNew.getAndRule();
      }
      if (posOld == null) {
        posOld = posNew;
      }
    }

    if (rrOld != null) {
      rrOld.convertBeforeToAfter();
      RelativeRule andRule = rrOld.getAndRule();
      RelativeRule parentRule = rrOld;
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

    return rrOld;
  }

  public String doAddEvent(HttpServletRequest req, String action, String show, User user, Session dataSession) {
    EventType eventType = null;
    if (req.getParameter(PARAM_EVENT_TYPE_CODE) != null) {
      eventType = EventType.getEventType(req.getParameter(PARAM_EVENT_TYPE_CODE));
    }
    boolean isVacc = action.equals(ACTION_ADD_VACCINATION);
    if (req.getParameter(PARAM_EVENT_ID) == null) {
      if (req.getParameter(PARAM_NEW_EVENT_LABEL).length() > 0) {
        applicationSession.setAlertError(
            "If you would like to add a new ACIP-Defined Condition please review the list and select 'none of these, proposing a new label' ");
      } else {
        applicationSession.setAlertError("Please indicate which ACIP-Defined Condition you want to add. ");
      }
      return show;
    }
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
      applicationSession.setAlertError((isVacc ? "Vaccination" : "Condition") + " is required, please select the appropriate vaccination. ");
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
        applicationSession.setAlertError((isVacc ? "Administered date is required, please indicate date to create vaccination."
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
      RelativeRule relativeRule = readRelativeRules(PARAM_EVENT_RULE, req, dataSession, testCase);
      if (relativeRule == null || relativeRule.getTestEvent() == null) {
        applicationSession.setAlertError((isVacc ? "Administered" : "Observed")
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

  public void saveRelativeRule(Session dataSession, RelativeRule relativeRule) {
    if (relativeRule.getAndRule() != null) {
      saveRelativeRule(dataSession, relativeRule.getAndRule());
    }
    if (relativeRule.getRelativeTo() == RelativeTo.BIRTH || relativeRule.getRelativeTo() == RelativeTo.EVALUATION) {
      relativeRule.setTestEvent(null);
    }
    dataSession.saveOrUpdate(relativeRule);
  }

  public void deleteRelativeRule(Session dataSession, RelativeRule relativeRule) {
    if (relativeRule.getRuleId() > 0) {
      if (relativeRule.getAndRule() != null) {
        deleteRelativeRule(dataSession, relativeRule.getAndRule());
      }
      dataSession.delete(relativeRule);
    }
  }

  public String doDelete(int testEventId, String show, Session dataSession) {

    TestEvent testEvent = (TestEvent) dataSession.get(TestEvent.class, testEventId);
    Query query = dataSession.createQuery("from EvaluationActual where testEvent = ?");
    query.setParameter(0, testEvent);
    if (query.list().size() > 0) {
      applicationSession.setAlertError(
          "Unable to delete vaccination, actual evaluation(s) have been stored from CDSi request(s). You can no longer edit this test case. Create a new one instead. ");
      return SHOW_EDIT_VACCINATIONS;
    }
    query = dataSession.createQuery("from EvaluationExpected where testEvent = ?");
    query.setParameter(0, testEvent);
    if (query.list().size() > 0) {
      applicationSession.setAlertError(
          "Unable to delete vaccination, evaluation expectation(s) have been set. You can no longer edit this test case. Create a new one instead.");
      return SHOW_EDIT_VACCINATIONS;
    }
    query = dataSession.createQuery("from RelativeRule where testEvent = ?");
    query.setParameter(0, testEvent);
    if (query.list().size() > 0) {
      applicationSession.setAlertError(
          "Unable to delete event, another test event depends on this one to define its date. First delete that event before deleting this one. ");
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

  public String saveTestCase(HttpServletRequest req, String action, String show, User user, Session dataSession) {
    String label = req.getParameter(PARAM_LABEL);
    String description = req.getParameter(PARAM_DESCRIPTION);
    int vaccineGroupId = Integer.parseInt(req.getParameter(PARAM_VACCINE_GROUP_ID));
    String includeStatus = req.getParameter(PARAM_INCLUDE_STATUS);
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
        problem = "Assessment Date is required";
      } else {
        try {
          patientDob = sdf.parse(patientDobString);
        } catch (ParseException pe) {
          problem = "Date of Birth is not in MM/DD/YYYY format";
        }
        try {
          evalDate = sdf.parse(evalDateString);
        } catch (ParseException pe) {
          problem = "Assessment Date is not in MM/DD/YYYY format";
        }
      }
    }
    if (problem == null) {
      Query query = dataSession.createQuery("from TestPanelCase where testPanel = ? and testCaseNumber = ?");
      query.setParameter(0, user.getSelectedTestPanel());
      query.setParameter(1, testCaseNumber);
      List<TestPanelCase> testPanelCaseList = query.list();
      if (testPanelCaseList.size() > 0) {
        if (action.equals(ACTION_UPDATE_TEST_CASE)) {
          if (testPanelCaseList.size() > 1 || !testPanelCaseList.get(0).equals(user.getSelectedTestPanelCase())) {
            problem = "Number is already in use by another test case in this test panel";
          }
        } else {
          problem = "Number is already in use by another test case in this test panel";
        }
      }
    }
    if (action.equals(ACTION_COPY_TEST_CASE)) {
      if (label.equals(user.getSelectedTestCase().getLabel()) && categoryName.equals(user.getSelectedTestCase().getCategoryName())) {
        problem = "Label and category name are the same as test case. To copy you must specify a new label and/or category name.";
      }
    }
    if (problem != null) {
      applicationSession.setAlertError("Unable to save test case: " + problem);
      return show;
    } else {
      Transaction transaction = dataSession.beginTransaction();
      TestCase testCase;
      TestPanelCase testPanelCase;
      if (action.equals(ACTION_UPDATE_TEST_CASE)) {
        testCase = user.getSelectedTestCase();
        testPanelCase = user.getSelectedTestPanelCase();
      } else if (action.equals(ACTION_COPY_TEST_CASE)) {
        testCase = new TestCase();
        testPanelCase = new TestPanelCase();
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
      testCase.setPatientFirst(patientFirst.trim());
      testCase.setPatientLast(patientLast.trim());
      testCase.setPatientSex(patientSex.trim());

      testPanelCase.setTestCaseNumber(testCaseNumber.trim());
      testPanelCase.setCategoryName(categoryName.trim());
      testPanelCase.setInclude(Include.getInclude(includeStatus));

      if (action.equals(ACTION_UPDATE_TEST_CASE)) {
        if (testCase.getEvalRule() != null) {
          dataSession.saveOrUpdate(testCase.getEvalRule());
        }
        dataSession.update(testCase);
        dataSession.update(testPanelCase);
      } else if (action.equals(ACTION_COPY_TEST_CASE) || action.equals(ACTION_ADD_TEST_CASE)) {
        testPanelCase.setTestPanel(user.getSelectedTestPanel());
        testPanelCase.setTestCase(testCase);
        testPanelCase.setResult(Result.RESEARCH);

        if (testCase.getEvalRule() != null) {
          dataSession.save(testCase.getEvalRule());
        }
        dataSession.save(testCase);
        dataSession.save(testPanelCase);
      }
      if (action.equals(ACTION_COPY_TEST_CASE)) {
        TestCase originalTestCase = user.getSelectedTestCase();
        Query query = dataSession.createQuery("from TestEvent where testCase = ?");
        query.setParameter(0, originalTestCase);
        List<TestEvent> originalTestEventList = query.list();
        Map<TestEvent, TestEvent> testEventMap = new HashMap<TestEvent, TestEvent>();
        for (TestEvent originalTestEvent : originalTestEventList) {
          TestEvent testEvent = new TestEvent();
          testEvent.setTestCase(testCase);
          testEvent.setEvent(originalTestEvent.getEvent());
          testEvent.setEventDate(originalTestEvent.getEventDate());
          testEvent.setCondition(originalTestEvent.getCondition());
          dataSession.save(testEvent);
          testEventMap.put(originalTestEvent, testEvent);
        }
        for (TestEvent originalTestEvent : originalTestEventList) {
          TestEvent testEvent = testEventMap.get(originalTestEvent);
          RelativeRule originalRR = originalTestEvent.getEventRule();
          if (originalRR != null) {
            RelativeRule rr = new RelativeRule();
            testEvent.setEventRule(rr);
            rr.setTimePeriod(originalRR.getTimePeriod());
            rr.setRelativeTo(originalRR.getRelativeTo());
            if (originalRR.getTestEvent() != null) {
              rr.setTestEvent(testEventMap.get(originalRR.getTestEvent()));
            }
            while (originalRR.getAndRule() != null) {
              rr.setAndRule(new RelativeRule());
              rr = rr.getAndRule();
              originalRR = originalRR.getAndRule();
              rr.setTimePeriod(originalRR.getTimePeriod());
              rr.setRelativeTo(originalRR.getRelativeTo());
              if (originalRR.getTestEvent() != null) {
                rr.setTestEvent(testEventMap.get(originalRR.getTestEvent()));
              }
            }
            saveRelativeRule(dataSession, testEvent.getEventRule());
          }
        }

      }
      user.setSelectedTestCase(testCase);
      user.setSelectedTestPanelCase(testPanelCase);
      user.setSelectedCategoryName(categoryName);

      dataSession.update(user);
      transaction.commit();

      RelativeRuleManager.updateFixedDatesForRelativeRules(testCase, dataSession, true);
      return SHOW_TEST_CASE;
    }

  }

  public String saveTestPanel(HttpServletRequest req, String action, String show, User user, Session dataSession) {
    String label = req.getParameter(PARAM_LABEL);
    String availableCode = req.getParameter(PARAM_AVAILABLE_CODE);

    String problem = null;
    if (label.equals("")) {
      problem = "Label is required";
    }
    if (availableCode.equals("")) {
      problem = "Available status is required";
    }
    if (problem != null) {
      applicationSession.setAlertError("Unable to save test panel: " + problem);
      return show;
    } else {
      Transaction transaction = dataSession.beginTransaction();
      TestPanel testPanel;

      if (action.equals(ACTION_UPDATE_TEST_PANEL)) {
        testPanel = user.getSelectedTestPanel();
      } else {
        testPanel = new TestPanel();
      }
      testPanel.setLabel(label);
      testPanel.setAvailableCode(availableCode);
      testPanel.setTaskGroup(user.getSelectedTaskGroup());

      if (action.equals(ACTION_UPDATE_TEST_PANEL)) {
        dataSession.update(testPanel);
      } else if (action.equals(ACTION_ADD_TEST_PANEL)) {
        dataSession.save(testPanel);
      }
      user.setSelectedTestPanel(testPanel);
      user.setSelectedTestCase(null);
      user.setSelectedTestPanelCase(null);
      user.setSelectedCategoryName(null);
      dataSession.update(user);
      transaction.commit();
    }
    return SHOW_TEST_PANEL;
  }

  @Override
  protected void printPage(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, String show) throws ServletException, IOException {
    Session dataSession = applicationSession.getDataSession();

    User user = applicationSession.getUser();
    setupForPrinting(dataSession, user, req);

    printTree(out, dataSession, user);

    if (SHOW_TEST_CASE.equals(show)) {
      if (user.getSelectedTaskGroup() != null && user.getSelectedTestPanel() != null && user.getSelectedTestPanelCase() != null) {
        TestCase testCase = user.getSelectedTestPanelCase().getTestCase();
        printTestCase(out, user, req, dataSession);
        printActualsVsExpected(out, user);
      }
    } else if (SHOW_TEST_PANEL.equals(show)) {
      boolean canEdit = user.getSelectedExpert() != null && user.getSelectedExpert().getRole().canEdit();
      TestPanel testPanel = user.getSelectedTestPanel();
      String editLink = "testCases?" + PARAM_SHOW + "=" + SHOW_EDIT_TEST_PANEL + "&" + PARAM_TEST_PANEL_ID + "=" + testPanel.getTestPanelId();
      String editButton = "";
      if (canEdit) {
        editButton = " <a class=\"fauxbutton\" href=\"" + editLink + "\">Edit</a>";
      }
      out.println("<div class=\"centerLeftColumn\">");
      out.println("  <h2>Test Panel" + editButton + "</h2>");
      out.println("  <form method=\"POST\" action=\"testCases\">");
      out.println("  <table width=\"100%\">");
      out.println("    <tr>");
      out.println("      <th>Label</th>");
      out.println("      <td>" + testPanel.getLabel() + "</td>");
      out.println("    </tr>");
      out.println("    <tr>");
      out.println("      <th>Available</th>");
      out.println("      <td>" + testPanel.getAvailable() + "</td>");
      out.println("    </tr>");
      if (canEdit) {
        out.println("    <tr>");
        out.println("      <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION + "\" value=\""
            + ACTION_UPDATE_RELATIVE_DATES + "\"/></td>");
        out.println("  <input type=\"hidden\" name=\"" + PARAM_TEST_PANEL_ID + "\" value=\"" + testPanel.getTestPanelId() + "\"/>");
        out.println("    </tr>");
      }
      out.println("  </table>");
      out.println("  </form>");
    } else if (SHOW_PREVIEW_TEST_CASE.equals(show)) {
      if (user.getSelectedTaskGroup() != null && user.getSelectedTestPanel() != null && user.getSelectedTestPanelCase() != null) {
        VaccineGroup vaccineGroup = (VaccineGroup) dataSession.get(VaccineGroup.class, Integer.parseInt(req.getParameter(PARAM_VACCINE_GROUP_ID)));
        TestCase testCase = user.getSelectedTestPanelCase().getTestCase();
        printTestCase(out, user, req, dataSession);
        printPreview(out, user, vaccineGroup);
      }
    } else if (SHOW_ADD_TEST_CASE.equals(show)) {
      printAddEditTestCases(req, out, dataSession, null, show);
    } else if (SHOW_EDIT_TEST_CASE.equals(show)) {
      printAddEditTestCases(req, out, dataSession, user.getSelectedTestPanelCase(), show);
    } else if (SHOW_COPY_TEST_CASE.equals(show)) {
      printAddEditTestCases(req, out, dataSession, user.getSelectedTestPanelCase(), show);
    } else if (SHOW_ADD_TEST_PANEL.equals(show)) {
      printAddEditTestPanel(req, out, dataSession, null, show);
    } else if (SHOW_EDIT_TEST_PANEL.equals(show)) {
      printAddEditTestPanel(req, out, dataSession, user.getSelectedTestPanel(), show);
    } else if (SHOW_TASK_GROUP.equals(show)) {
      printTaskGroup(out, dataSession, user);
    } else if (SHOW_EDIT_VACCINATIONS.equals(show)) {
      printEditEvents(req, out, dataSession, user, EventType.VACCINATION, show);
    } else if (SHOW_EDIT_EVENTS.equals(show)) {
      EventType eventType = EventType.getEventType(req.getParameter(PARAM_EVENT_TYPE_CODE));
      printEditEvents(req, out, dataSession, user, eventType, show);
    } else if (SHOW_EDIT_EXPECTATIONS.equals(show)) {
      VaccineGroup vaccineGroup = (VaccineGroup) dataSession.get(VaccineGroup.class, notNull(req.getParameter(PARAM_VACCINE_GROUP_ID), 0));
      printEditExpectations(req, out, dataSession, user, vaccineGroup);
    } else if (SHOW_ADD_ACTUAL_VS_EXPECTED.equals(show)) {
      printActualVsExpected(out, dataSession, user);
    } else if (SHOW_REQUEST_ACTUAL_RESULTS.equals(show)) {
      printRequestActualResults(out, dataSession, user);
    } else if (SHOW_DEBUGGING_TOOLS.equals(show)) {
      printDebuggingTools(out, dataSession, user);
    } else if (SHOW_SOFTWARE_RESULT.equals(show)) {
      int softwareResultId = Integer.parseInt(req.getParameter(PARAM_SOFTWARE_RESULT_ID));
      SoftwareResult softwareResult = (SoftwareResult) dataSession.get(SoftwareResult.class, softwareResultId);
      printSoftwareResult(out, dataSession, user, softwareResult);
    } else if (SHOW_EDIT_TEST_CASE_SETTINGS.equals(show)) {
      printEditTestCaseSettings(req, out, user);
    }
  }

  public void printEditTestCaseSettings(HttpServletRequest req, PrintWriter out, User user) {
    Software software = user.getSelectedSoftware();
    TestCase testCase = user.getSelectedTestCase();
    out.println("<div class=\"centerColumn\">");
    out.println("<h2>Edit Software Settings for " + software.getService().getServiceType() + "</h2>");
    Query query = applicationSession.getDataSession().createQuery("from TestCaseSetting where testCase = ? and serviceOption.serviceType = ?");
    query.setParameter(0, testCase);
    query.setParameter(1, software.getServiceType());
    List<TestCaseSetting> testCaseSettingList = query.list();
    Map<ServiceOption, TestCaseSetting> serviceOptionMap = new HashMap<ServiceOption, TestCaseSetting>();
    for (TestCaseSetting testCaseSetting : testCaseSettingList) {
      serviceOptionMap.put(testCaseSetting.getServiceOption(), testCaseSetting);
    }
    query = applicationSession.getDataSession().createQuery("from ServiceOption where serviceType = ?");
    query.setParameter(0, software.getServiceType());
    List<ServiceOption> serviceOptionList = query.list();
    out.println("  <table width=\"100%\">");
    out.println("  <form method=\"POST\" action=\"testCases\">");
    out.println("  <input type=\"hidden\" name=\"" + PARAM_SOFTWARE_ID_SELECTED + "\" value=\"" + software.getSoftwareId() + "\"/>");
    out.println("  <input type=\"hidden\" name=\"" + PARAM_TEST_PANEL_CASE_ID + "\" value=\"" + user.getSelectedTestPanelCase().getTestPanelCaseId()
        + "\"/>");
    for (ServiceOption serviceOption : serviceOptionList) {
      out.println("    <tr>");
      out.println("      <th>" + serviceOption.getOptionLabel() + "</th>");
      TestCaseSetting testCaseSetting = serviceOptionMap.get(serviceOption);
      String optionValue;
      if (testCaseSetting == null) {
        optionValue = notNull(req.getParameter(PARAM_OPTION_VALUE + serviceOption.getOptionId()), "");
      } else {
        optionValue = notNull(req.getParameter(PARAM_OPTION_VALUE + serviceOption.getOptionId()), testCaseSetting.getOptionValue());
      }
      if (serviceOption.getValidValues().equals("")) {
        out.println("      <td><input type=\"text\" size=\"20\" name=\"" + PARAM_OPTION_VALUE + serviceOption.getOptionId() + "\" value=\""
            + optionValue + "\"/></td>");
      } else {
        out.println("      <td>");
        out.println("        <select name=\"" + PARAM_OPTION_VALUE + serviceOption.getOptionId() + "\">");
        out.println("          <option value=\"\">--select--</option>");
        for (String validValue : serviceOption.getValidValues().split("\\,")) {
          validValue = validValue.trim();
          if (optionValue.equalsIgnoreCase(validValue)) {
            out.println("              <option value=\"" + validValue + "\" selected=\"selected\">" + validValue + "</option>");
          } else {
            out.println("              <option value=\"" + validValue + "\">" + validValue + "</option>");
          }
        }
        out.println("        </select>");
        out.println("      </td>");
      }
      out.println("    </tr>");
    }
    out.println("        <tr>");
    out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION + "\" value=\""
        + ACTION_SAVE_TEST_CASE_SETTINGS + "\"/></td>");
    out.println("        </tr>");
    out.println("  </table>");

    out.println("</div>");
  }

  public void printDebuggingTools(PrintWriter out, Session dataSession, User user) {
    Software software = user.getSelectedSoftware();
    TestCase testCase = user.getSelectedTestCase();
    out.println("<div class=\"centerColumn\">");
    Software tchSoftware = (Software) dataSession.get(Software.class, Software.LSVF_SOFTWARE_ID);
    out.println("<h2>Debugging Tools <a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW + "=" + SHOW_TEST_CASE + "&" + PARAM_TEST_PANEL_CASE_ID
        + "=" + user.getSelectedTestPanelCase().getTestPanelCaseId() + "\">Back</a></h2>");
    out.println("<ul>");
    String forecastLink = tchSoftware.getServiceUrl() + LSVFConnector.createQueryString(testCase, tchSoftware, "html");
    out.println("  <li><a href=\"" + forecastLink + "\" target=\"_blank\">" + tchSoftware.getLabel() + "</a></li>");
    if (software != null && !software.equals(tchSoftware) && software.getService() == Service.LSVF) {
      forecastLink = software.getServiceUrl() + LSVFConnector.createQueryString(testCase, software, "html");
      out.println("  <li><a href=\"" + forecastLink + "\" target=\"_blank\">" + software.getLabel() + "</a></li>");
    }
    String stepLink = tchSoftware.getServiceUrl().substring(0, tchSoftware.getServiceUrl().length() - "forecast".length()) + "fv/step"
        + LSVFConnector.createQueryString(testCase, tchSoftware, "text");
    out.println("  <li><a href=\"" + stepLink + "\" target=\"_blank\">TCH Forecast Step Through</a></li>");
    out.println("</ul>");

    Query query = dataSession.createQuery("from SoftwareResult where testCase = ?");
    query.setParameter(0, user.getSelectedTestPanelCase().getTestCase());
    List<SoftwareResult> softwareResultList = query.list();
    for (Iterator<SoftwareResult> it = softwareResultList.iterator(); it.hasNext();) {
      SoftwareResult sr = it.next();
      if (SoftwareManager.isSoftwareAccessRestricted(sr.getSoftware(), user, dataSession)) {
        it.remove();
      }
    }
    if (softwareResultList.size() > 0) {
      out.println("<h3>Actual Results</h3>");
      out.println("<ul>");
      for (SoftwareResult softwareResult : softwareResultList) {
        String link = "testCases?" + PARAM_SHOW + "=" + SHOW_SOFTWARE_RESULT + "&" + PARAM_SOFTWARE_RESULT_ID + "="
            + softwareResult.getSoftwareResultId();
        out.println("<li><a href=\"" + link + "\">" + softwareResult.getSoftware().getLabel() + "</a></li>");
      }
      out.println("</ul>");
    }

    out.println("</div>");
  }

  public void printSoftwareResult(PrintWriter out, Session dataSession, User user, SoftwareResult softwareResult) {
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    out.println("<div class=\"centerColumn\">");
    out.println("<h2>Actual Results <a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW + "=" + SHOW_DEBUGGING_TOOLS + "&"
        + PARAM_TEST_PANEL_CASE_ID + "=" + user.getSelectedTestPanelCase().getTestPanelCaseId() + "\">Back</a></h2>");
    out.println("<table>");
    out.println("  <tr>");
    out.println("    <th>Software</th>");
    out.println("    <td>" + softwareResult.getSoftware().getLabel() + "</td>");
    out.println("  </tr>");
    out.println("  <tr>");
    out.println("    <th>Run Date</th>");
    out.println("    <td>" + sdf.format(softwareResult.getRunDate()) + "</td>");
    out.println("  </tr>");
    out.println("</table>");

    {
      Query query = dataSession.createQuery("from ForecastActual where softwareResult = ? order by vaccineGroup.label");
      query.setParameter(0, softwareResult);
      List<ForecastActual> forecastActualList = query.list();
      if (forecastActualList.size() > 0) {
        out.println("<h3>Forecast</h3>");
        out.println("<table>");
        out.println("  <tr>");
        out.println("    <th>Schedule</th>");
        out.println("    <th>Vaccine Group</th>");
        out.println("    <th>Status</th>");
        out.println("    <th>Dose</th>");
        out.println("    <th>Earliest</th>");
        out.println("    <th>Recommend</th>");
        out.println("    <th>Past Due</th>");
        out.println("    <th>Finished</th>");
        out.println("    <th>CVX</th>");
        out.println("    <th>Reason</th>");
        out.println("  </tr>");
        for (ForecastActual fa : forecastActualList) {
          out.println("  <tr>");
          out.println("    <td>" + n(fa.getScheduleName()) + "</td>");
          out.println("    <td>" + fa.getVaccineGroup().getLabel() + "</td>");
          out.println("    <td>" + (fa.getAdmin() == null ? "" : fa.getAdmin().getLabel()) + "</td>");
          out.println("    <td>" + n(fa.getDoseNumber()) + "</td>");
          out.println("    <td>" + (fa.getValidDate() == null ? "" : sdf.format(fa.getValidDate())) + "</td>");
          out.println("    <td>" + (fa.getDueDate() == null ? "" : sdf.format(fa.getDueDate())) + "</td>");
          out.println("    <td>" + (fa.getOverdueDate() == null ? "" : sdf.format(fa.getOverdueDate())) + "</td>");
          out.println("    <td>" + (fa.getFinishedDate() == null ? "" : sdf.format(fa.getFinishedDate())) + "</td>");
          out.println("    <td>" + n(fa.getVaccineCvx()) + "</td>");
          out.println("    <td>" + n(fa.getForecastReason()) + "</td>");
          out.println("  </tr>");
        }
        out.println("</table>");
      }
    }

    if (softwareResult.getLogText() != null && !softwareResult.getLogText().equals("")) {
      out.println("<h3>Original Text</h3>");
      out.println("<pre>");
      out.println(softwareResult.getLogText().replaceAll("\\<", "&lt;").replaceAll("\\>", "&gt;"));
      out.println("</pre>");
    }

    out.println("</div>");
  }

  public void printRequestActualResults(PrintWriter out, Session dataSession, User user) {
    Software software = user.getSelectedSoftware();
    out.println("<div class=\"centerColumn\">");
    out.println("<h2>Request Actual Results <a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW + "=" + SHOW_TEST_CASE + "&"
        + PARAM_TEST_PANEL_CASE_ID + "=" + user.getSelectedTestPanelCase().getTestPanelCaseId() + "\">Back</a></h2>");
    out.println("  <form method=\"POST\" action=\"testCases\">");
    out.println("<table>");
    out.println("  <tr>");
    out.println("    <th>CDSi Engine</th>");
    out.println("    <td>");
    List<Software> softwareList = SoftwareManager.getListOfUnrestrictedSoftware(user, dataSession);
    for (Software softwareSelected : softwareList) {
      if (softwareSelected.getServiceUrl() != null && softwareSelected.getServiceUrl().length() > 0) {
        out.println("          <input type=\"checkbox\" name=\"" + PARAM_SOFTWARE_ID_SELECTED + softwareSelected.getSoftwareId()
            + "\" value=\"true\">" + softwareSelected.getLabel() + "<br/>");
      }
    }
    out.println("    </td>");
    out.println("  </tr>");
    out.println("        <tr>");
    out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION + "\" value=\""
        + ACTION_REQUEST_ACTUAL_RESULTS + "\"/></td>");
    out.println("        </tr>");
    out.println("</table>");
    out.println("  </form>");

    out.println("</div>");
  }

  public void printActualVsExpected(PrintWriter out, Session dataSession, User user) {
    TestPanelCase testPanelCase = user.getSelectedTestPanelCase();
    Set<VaccineGroup> vaccineGroupDisplayedSet = new HashSet<VaccineGroup>();
    {
      HashMap<VaccineGroup, ForecastActualExpectedCompare> forecastCompareMap = new HashMap<VaccineGroup, ForecastActualExpectedCompare>();
      if (forecastCompareMap.size() > 0) {
        for (VaccineGroup vg : forecastCompareMap.keySet()) {
          vaccineGroupDisplayedSet.add(vg);
        }
      }
    }
    out.println("<div class=\"centerColumn\">");
    out.println("<h2>Add Actual vs Expected <a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW + "=" + SHOW_TEST_CASE + "&"
        + PARAM_TEST_PANEL_CASE_ID + "=" + user.getSelectedTestPanelCase().getTestPanelCaseId() + "\">Back</a></h2>");
    out.println("  <form method=\"POST\" action=\"testCases\">");
    out.println("    <input type=\"hidden\" name=\"" + PARAM_TEST_PANEL_CASE_ID + "\" value=\"" + testPanelCase.getTestPanelCaseId() + "\"/>");
    out.println("    <input type=\"hidden\" name=\"" + PARAM_SHOW + "\" value=\"" + SHOW_EDIT_EXPECTATIONS + "\"/>");
    out.println("  <table>");
    out.println("        <tr>");
    out.println("          <th>Vaccine Group</th>");
    out.println("          <td>");
    out.println("            <select type=\"text\" name=\"" + PARAM_VACCINE_GROUP_ID + "\">");
    out.println("              <option value=\"0\">--select--</option>");
    Query query = dataSession.createQuery("from VaccineGroup order by label");
    List<VaccineGroup> vaccineGroupList = query.list();
    for (VaccineGroup vg : vaccineGroupList) {
      if (!vaccineGroupDisplayedSet.contains(vg)) {
        out.println("              <option value=\"" + vg.getVaccineGroupId() + "\">" + vg.getLabel() + "</option>");
      }
    }
    out.println("            </select>");
    out.println("          </td>");
    out.println("        </tr>");
    out.println("        <tr>");
    out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION + "\" value=\"" + ACTION_ADD_EXPECTATIONS
        + "\"/></td>");
    out.println("        </tr>");
    out.println("  </table>");
    out.println("  </form>");

    out.println("</div>");
  }

  private void printEditExpectations(HttpServletRequest req, PrintWriter out, Session dataSession, User user, VaccineGroup vaccineGroup) {
    TestPanelCase testPanelCase = user.getSelectedTestPanelCase();
    TestCase testCase = testPanelCase.getTestCase();

    ExpectationsManager expectationsManager = new ExpectationsManager(user, vaccineGroup, true, dataSession);

    readEvaluationExpectations(req, expectationsManager);
    readForecastExpectations(req, dataSession, testCase, expectationsManager);
    String action = req.getParameter(PARAM_ACTION);
    if (action != null && action.equals(ACTION_SAVE_EXPECTATIONS)) {
      readGuidanceExpectations(req, dataSession, expectationsManager);
    }

    printShowRowScript(out);
    out.println("<div class=\"centerColumn\">");
    out.println("  <h2>Expectations for " + vaccineGroup.getLabel());
    out.println("    <a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW + "=" + SHOW_TEST_CASE + "&" + PARAM_TEST_PANEL_CASE_ID + "="
        + user.getSelectedTestPanelCase().getTestPanelCaseId() + "\">Back</a>");
    out.println("  </h2>");

    setupTestEventList(user);

    out.println("  <form method=\"POST\" action=\"testCases\">");
    out.println("    <input type=\"hidden\" name=\"" + PARAM_VACCINE_GROUP_ID + "\" value=\"" + vaccineGroup.getVaccineGroupId() + "\"/>");
    out.println("    <input type=\"hidden\" name=\"" + PARAM_TEST_PANEL_CASE_ID + "\" value=\"" + testPanelCase.getTestPanelCaseId() + "\"/>");
    if (countVaccination > 0) {
      out.println("  <h3>Evaluation</h3>");
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

      for (TestEvent vaccinationEvent : expectationsManager.getVaccinationEvents()) {
        out.println("    <tr>");
        out.println("      <td>" + vaccinationEvent.getScreenId() + "</td>");
        out.println("      <td>" + vaccinationEvent.getEvent().getLabel() + "</td>");
        out.println("      <td>" + vaccinationEvent.getEvent().getVaccineCvx() + "</td>");
        out.println("      <td>" + vaccinationEvent.getEvent().getVaccineMvx() + "</td>");
        if (testCase.getDateSet() == DateSet.RELATIVE) {
          out.print("      <td>");
          printOutRelativeRule(out, vaccinationEvent.getEventRule());
          out.print("</td>");
        }
        out.println("      <td>" + (vaccinationEvent.getEventDate() == null ? "" : sdf.format(vaccinationEvent.getEventDate())) + "</td>");
        out.println("      <td>");
        EvaluationExpected evaluationExpected = expectationsManager.getTestEventMapToEvaluationExpected().get(vaccinationEvent);
        out.println("          <select name=\"" + PARAM_EVALUATION_STATUS + vaccinationEvent.getTestEventId() + "\">");
        out.println("            <option value=\"0\">--select--</option>");
        for (Evaluation evaluation : Evaluation.values()) {
          if (evaluationExpected != null && evaluationExpected.getEvaluation() == evaluation) {
            out.println("            <option value=\"" + evaluation.getEvaluationStatus() + "\" selected=\"selected\">" + evaluation.getLabel()
                + "</option>");
          } else {
            out.println("            <option value=\"" + evaluation.getEvaluationStatus() + "\">" + evaluation.getLabel() + "</option>");
          }
        }
        out.println("          </select>");
        out.println("      </td>");
        out.println("    </tr>");
      }
      out.println("  </table>");
    }

    ForecastExpected forecastExpected = expectationsManager.getForecastExpected();
    out.println("  <h3>Forecast</h3>");
    out.println("  <table>");
    out.println("    <tr>");
    out.println("      <th>Series Status</th>");
    out.println("      <td>");
    out.println("          <select name=\"" + PARAM_ADMIN_STATUS + "\">");
    out.println("            <option value=\"0\">--select CDSi values--</option>");
    String adminStatus = forecastExpected.getAdminStatus();
    for (Admin admin : ADMIN_STANDARD_LIST) {
      printAdminSelect(out, adminStatus, admin);
    }
    out.println("            <option value=\"0\">--select other values--</option>");
    for (Admin admin : ADMIN_NON_STANDARD_LIST) {
      printAdminSelect(out, adminStatus, admin);
    }
    out.println("          </select>");
    out.println("      </td>");
    out.println("    </tr>");
    out.println("    <tr>");
    out.println("      <th>Dose</th>");
    out.println(
        "      <td><input type=\"text\" name=\"" + PARAM_DOSE_NUMBER + "\" value=\"" + forecastExpected.getDoseNumber() + "\" size=\"5\"/></td>");
    out.println("    </tr>");
    if (testCase.getDateSet() == DateSet.FIXED) {
      out.println("    <tr>");
      out.println("      <th>Earliest Date</th>");
      out.println("      <td><input type=\"text\" name=\"" + PARAM_VALID_DATE + "\" value=\"" + forecastExpected.getValidDateString()
          + "\" size=\"10\"/></td>");
      out.println("    </tr>");
      out.println("    <tr>");
      out.println("      <th>Recommended Date</th>");
      out.println(
          "      <td><input type=\"text\" name=\"" + PARAM_DUE_DATE + "\" value=\"" + forecastExpected.getDueDateString() + "\" size=\"10\"/></td>");
      out.println("    </tr>");
      out.println("    <tr>");
      out.println("      <th>Past Due Date</th>");
      out.println("      <td><input type=\"text\" name=\"" + PARAM_OVERDUE_DATE + "\" value=\"" + forecastExpected.getOverdueDateString()
          + "\" size=\"10\"/></td>");
      out.println("    </tr>");
      out.println("    <tr>");
      out.println("      <th>Latest Date</th>");
      out.println("      <td><input type=\"text\" name=\"" + PARAM_OVERDUE_DATE + "\" value=\"" + forecastExpected.getFinishedDateString()
          + "\" size=\"10\"/></td>");
      out.println("    </tr>");
    } else {
      printRelativeRuleRow(PARAM_VALID_RULE, out, forecastExpected.getValidRule(), "Earliest Date", null);
      printRelativeRuleRow(PARAM_DUE_RULE, out, forecastExpected.getDueRule(), "Recommended Date", null);
      printRelativeRuleRow(PARAM_OVERDUE_RULE, out, forecastExpected.getOverdueRule(), "Past Due Date", null);
      printRelativeRuleRow(PARAM_FINISHED_RULE, out, forecastExpected.getFinishedRule(), "Finished Date", null);
    }
    out.println("        <tr>");
    out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION + "\" value=\"" + ACTION_SAVE_EXPECTATIONS
        + "\"/></td>");
    out.println("        </tr>");

    out.println("  </table>");

    out.println("  <h3>Guidance</h3>");

    out.println("  <table width=\"100%\">");
    {
      out.println("      <tr>");
      out.println("        <th colspan=\"2\">Recommended Actions</th>");
      out.println("      </tr>");
      out.println("      <tr>");
      out.println("        <td colspan=\"2\"><div class=\"scrollRadioBox\">");
      Set<Recommend> recommendSet = new HashSet<Recommend>();
      if (expectationsManager.getRecommendGuidanceList().size() > 0) {
        out.println("<b>Currently Selected</b><br/>");
        for (RecommendGuidance recommendGuidance : expectationsManager.getRecommendGuidanceList()) {
          out.println("          <input type=\"checkbox\" name=\"" + PARAM_RECOMMEND_ID + "" + "\" value=\""
              + recommendGuidance.getRecommend().getRecommendId() + "\" checked=\"checked\">" + recommendGuidance.getRecommend() + "<br/>");
          recommendSet.add(recommendGuidance.getRecommend());
        }
        out.println("<b>Select Additional</b><br/>");
      }
      Query query = dataSession.createQuery("from Recommend order by recommendText");
      List<Recommend> recommendList = query.list();
      for (Recommend recommend : recommendList) {
        if (!recommendSet.contains(recommend)) {
          out.println("          <input type=\"checkbox\" name=\"" + PARAM_RECOMMEND_ID + "\" value=\"" + recommend.getRecommendId() + "\">"
              + recommend + "<br/>");
        }
      }
      out.println("          <input type=\"checkbox\" name=\"" + PARAM_RECOMMEND_ID + ""
          + "\" value=\"-1\" onChange=\"showRow('recommendNewValue1');showRow('recommendNewValue2');showRow('recommendNewValue3');\">propose a new value<br/>");
      out.println("        </div></td>");
      out.println("      </tr>");
      out.println("      <tr id=\"recommendNewValue1\" style=\"display: none;\">");
      out.println("        <th>New Text</th>");
      out.println("        <td>");
      out.println("          <textarea name=\"" + PARAM_RECOMMEND_TEXT + "\" value=\"\" cols=\"60\" rows=\"3\"></textarea>");
      out.println("        </td>");
      out.println("      </tr>");
      out.println("      <tr id=\"recommendNewValue2\" style=\"display: none;\">");
      out.println("        <th>Type</th>");
      out.println("        <td>");
      out.println("          <select name=\"" + PARAM_RECOMMEND_TYPE_CODE + "\" >");
      out.println("            <option value=\"0\">--select--</option>");
      for (RecommendType recommendType : RecommendType.values()) {
        out.println("            <option value=\"" + recommendType.getRecommendTypeCode() + "\">" + recommendType.getLabel() + "</option>");
      }
      out.println("          </select>");
      out.println("        </td>");
      out.println("      </tr>");
      out.println("      <tr id=\"recommendNewValue3\" style=\"display: none;\">");
      out.println("        <th>Range</th>");
      out.println("        <td>");
      out.println("           <select name=\"" + PARAM_RECOMMEND_RANGE_CODE + "\" >");
      out.println("            <option value=\"0\">--select--</option>");
      for (RecommendRange recommendRange : RecommendRange.values()) {
        out.println("            <option value=\"" + recommendRange.getRecommendRangeCode() + "\">" + recommendRange.getLabel() + "</option>");
      }
      out.println("          </select>");
      out.println("        </td>");
      out.println("      </tr>");
    }
    {
      out.println("      <tr>");
      out.println("        <th colspan=\"2\">Other</th>");
      out.println("      </tr>");
      out.println("      <tr>");
      out.println("        <td colspan=\"2\"><div class=\"scrollRadioBox\">");
      Set<Consideration> considerationSet = new HashSet<Consideration>();
      if (expectationsManager.getConsiderationGuidanceList().size() > 0) {
        out.println("<b>Currently Selected</b><br/>");
        for (ConsiderationGuidance considerationGuidance : expectationsManager.getConsiderationGuidanceList()) {
          out.println("          <input type=\"checkbox\" name=\"" + PARAM_CONSIDERATION_ID + "\" value=\""
              + considerationGuidance.getConsideration().getConsiderationId() + "\" checked=\"checked\">" + considerationGuidance.getConsideration()
              + "<br/>");
          considerationSet.add(considerationGuidance.getConsideration());
        }
        out.println("<b>Select Additional</b><br/>");
      }
      Query query = dataSession.createQuery("from Consideration order by considerationText");
      List<Consideration> considerationList = query.list();
      for (Consideration consideration : considerationList) {
        if (!considerationSet.contains(consideration)) {
          out.println("          <input type=\"checkbox\" name=\"" + PARAM_CONSIDERATION_ID + "\" value=\"" + consideration.getConsiderationId()
              + "\">" + consideration + "<br/>");
        }
      }
      out.println("          <input type=\"checkbox\" name=\"" + PARAM_CONSIDERATION_ID + ""
          + "\" value=\"-1\" onChange=\"showRow('considerationNewValue1');\">propose a new value<br/>");
      out.println("        </div></td>");
      out.println("      </tr>");
      out.println("      <tr id=\"considerationNewValue1\" style=\"display: none;\">");
      out.println("        <th>New Text</th>");
      out.println("        <td>");
      out.println("          <textarea name=\"" + PARAM_CONSIDERATION_TEXT + "\" value=\"\" cols=\"60\" rows=\"3\"></textarea>");
      out.println("        </td>");
      out.println("      </tr>");
      out.println("      <tr id=\"considerationNewValue2\" style=\"display: none;\">");
      out.println("        <th>Type</th>");
      out.println("        <td>");
      out.println("          <select name=\"" + PARAM_CONSIDERATION_TYPE_CODE + "\" >");
      out.println("            <option value=\"0\">--select--</option>");
      for (ConsiderationType considerationType : ConsiderationType.values()) {
        out.println(
            "            <option value=\"" + considerationType.getConsiderationTypeCode() + "\">" + considerationType.getLabel() + "</option>");
      }
      out.println("          </select>");
      out.println("        </td>");
      out.println("      </tr>");

    }
    {
      out.println("      <tr>");
      out.println("        <th colspan=\"2\">ACIP Guideline Rationale</th>");
      out.println("      </tr>");
      out.println("      <tr>");
      out.println("        <td colspan=\"2\"><div class=\"scrollRadioBox\">");
      Set<Rationale> rationaleSet = new HashSet<Rationale>();
      if (expectationsManager.getRationaleGuidanceList().size() > 0) {
        out.println("<b>Currently Selected</b><br/>");
        for (RationaleGuidance rationaleGuidance : expectationsManager.getRationaleGuidanceList()) {
          out.println(
              "          <input type=\"checkbox\" name=\"" + PARAM_RATIONALE_ID + "\" value=\"" + rationaleGuidance.getRationale().getRationaleId()
                  + "\" checked=\"checked\">" + rationaleGuidance.getRationale().getRationaleText() + "<br/>");
          rationaleSet.add(rationaleGuidance.getRationale());
        }
        out.println("<b>Select Additional</b><br/>");
      }
      Query query = dataSession.createQuery("from Rationale order by rationaleText");
      List<Rationale> rationaleList = query.list();
      for (Rationale rationale : rationaleList) {
        if (!rationaleSet.contains(rationale)) {
          out.println("          <input type=\"checkbox\" name=\"" + PARAM_RATIONALE_ID + "\" value=\"" + rationale.getRationaleId() + "\">"
              + rationale.getRationaleText() + "<br/>");
        }
      }
      out.println("          <input type=\"checkbox\" name=\"" + PARAM_RATIONALE_ID + ""
          + "\" value=\"-1\" onChange=\"showRow('rationaleNewValue1');\">propose a new value<br/>");
      out.println("        </div></td>");
      out.println("      </tr>");
      out.println("      <tr id=\"rationaleNewValue1\" style=\"display: none;\">");
      out.println("        <th>New Text</th>");
      out.println("        <td>");
      out.println("          <textarea name=\"" + PARAM_RATIONALE_TEXT + "\" value=\"\" cols=\"60\" rows=\"3\"></textarea>");
      out.println("        </td>");
      out.println("      </tr>");
    }
    {
      out.println("      <tr>");
      out.println("        <th colspan=\"2\">Additional Resources</th>");
      out.println("      </tr>");
      out.println("      <tr>");
      out.println("        <td colspan=\"2\"><div class=\"scrollRadioBox\">");
      Set<Resource> resourceSet = new HashSet<Resource>();
      if (expectationsManager.getResourceGuidanceList().size() > 0) {
        out.println("<b>Currently Selected</b><br/>");
        for (ResourceGuidance resourceGuidance : expectationsManager.getResourceGuidanceList()) {
          out.println("          <input type=\"checkbox\" name=\"" + PARAM_RESOURCE_ID + "\" value=\""
              + resourceGuidance.getResource().getResourceId() + "\" checked=\"checked\">" + resourceGuidance.getResource().getResourceText()
              + " - <a href=\"" + resourceGuidance.getResource().getResourceLink() + "\" target=\"_blank\">"
              + resourceGuidance.getResource().getResourceLink() + "</a><br/>");
          resourceSet.add(resourceGuidance.getResource());
        }
        out.println("<b>Select Additional</b><br/>");
      }
      Query query = dataSession.createQuery("from Resource order by resourceText");
      List<Resource> resourceList = query.list();
      for (Resource resource : resourceList) {
        if (!resourceSet.contains(resource)) {
          out.println("          <input type=\"checkbox\" name=\"" + PARAM_RESOURCE_ID + "\" value=\"" + resource.getResourceId() + "\">"
              + resource.getResourceText() + " - <a href=\"" + resource.getResourceLink() + "\" target=\"_blank\">" + resource.getResourceLink()
              + "</a><br/>");
        }
      }
      out.println("          <input type=\"checkbox\" name=\"" + PARAM_RESOURCE_ID + ""
          + "\" value=\"-1\" onChange=\"showRow('resourceNewValue1');showRow('resourceNewValue2');\">propose a new value<br/>");
      out.println("        </div></td>");
      out.println("      </tr>");
      out.println("      <tr id=\"resourceNewValue1\" style=\"display: none;\">");
      out.println("        <th>New Text</th>");
      out.println("        <td>");
      out.println("          <input type=\"text\" name=\"" + PARAM_RESOURCE_TEXT + "\" value=\"\" size=\"60\"/>");
      out.println("        </td>");
      out.println("      </tr>");
      out.println("      <tr id=\"resourceNewValue2\" style=\"display: none;\">");
      out.println("        <th>Link</th>");
      out.println("        <td>");
      out.println("          <input type=\"text\" name=\"" + PARAM_RESOURCE_LINK + "\" value=\"\" size=\"60\"/>");
      out.println("        </td>");
      out.println("      </tr>");
    }
    out.println("        <tr>");
    out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION + "\" value=\"" + ACTION_SAVE_EXPECTATIONS
        + "\"/></td>");
    out.println("        </tr>");
    out.println("  </table>");
    out.println("  </form>");
  }

  public void printAdminSelect(PrintWriter out, String adminStatus, Admin admin) {
    if (adminStatus == admin.getAdminStatus()) {
      out.println("            <option value=\"" + admin.getAdminStatus() + "\" selected=\"selected\">" + admin.getLabel() + "</option>");
    } else {
      out.println("            <option value=\"" + admin.getAdminStatus() + "\">" + admin.getLabel() + "</option>");
    }
  }

  public void printEditEvents(HttpServletRequest req, PrintWriter out, Session dataSession, User user, EventType eventType, String show)
      throws UnsupportedEncodingException {
    printShowRowScript(out);

    setupTestEventList(user);

    TestCase testCase = user.getSelectedTestCase();
    out.println("<div class=\"centerColumn\">");
    out.println("  <h2>Test Context");
    out.println("    <a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW + "=" + SHOW_TEST_CASE + "&" + PARAM_TEST_PANEL_CASE_ID + "="
        + user.getSelectedTestPanelCase().getTestPanelCaseId() + "\">Back</a>");
    out.println("  </h2>");
    out.println("  <table>");
    out.println("    <tr>");
    out.println("      <th>Birth</th>");
    if (testCase.getPatientDob() == null) {
      out.println("<td></td>");
    } else if (testCase.getDateSet() == DateSet.RELATIVE && testCase.getEvalRule() != null) {
      out.println("      <td>" + sdf.format(testCase.getPatientDob()) + " (" + testCase.getEvalRule().getTimePeriod() + " old)</td>");
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
      printEventTable(out, user, EventType.ACIP_DEFINED_CONDITION, testEventList, "ACIP-Defined Conditions Observed", "Condition", show);
    }
    if (countCondition > 0) {
      printEventTable(out, user, EventType.CONDITION_IMPLICATION, testEventList, "Condition Implications Asserted", "Implication", show);
    }
    if (countOther > 0) {
      printEventTable(out, user, null, testEventList, "Other Events Observed", "Event", show);
    }

    int eventId = 0;
    if (req.getParameter(PARAM_EVENT_ID) != null) {
      eventId = Integer.parseInt(req.getParameter(PARAM_EVENT_ID));
    }
    String eventDateDefaultString = "";
    if (eventType == EventType.ACIP_DEFINED_CONDITION || eventType == EventType.CONDITION_IMPLICATION) {
      eventDateDefaultString = sdf.format(new Date());
    }
    String eventDateString = notNull(req.getParameter(PARAM_EVENT_DATE), eventDateDefaultString);
    String newEventLabel = notNull(req.getParameter(PARAM_NEW_EVENT_LABEL));

    RelativeRule relativeRule = readRelativeRules(PARAM_EVENT_RULE, req, dataSession, user.getSelectedTestCase());

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
      editButton += " <a class=\"fauxbutton\" href=\"" + editLink + EventType.VACCINATION.getEventTypeCode() + "\">Vaccination</a>";

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
          out.println("            <option value=\"" + event.getEventId() + "\" selected=\"selected\">" + event.getLabel() + "</option>");
        } else {
          out.println(
              "            <option value=\"" + event.getEventId() + "\">" + event.getLabel() + " - CVX " + event.getVaccineCvx() + "</option>");
        }
      }
      out.println("          </select>");
      out.println("        </td>");
      out.println("      </tr>");
      if (user.getSelectedTestPanelCase().getTestCase().getDateSet() == DateSet.FIXED) {
        out.println("      <tr>");
        out.println("          <th>Administered Date</th>");
        out.println("          <td><input type=\"text\" name=\"" + PARAM_EVENT_DATE + "\" size=\"10\" value=\"" + eventDateString + "\"/></td>");
        out.println("        </td>");
        out.println("      </tr>");
      } else {
        String label = "Administered";
        printRelativeRuleRow(PARAM_EVENT_RULE, out, relativeRule, label, RelativeTo.BIRTH);
      }
      out.println("        <tr>");
      out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION + "\" value=\"" + ACTION_ADD_VACCINATION
          + "\"/></td>");
      out.println("        </tr>");

      out.println("    </table>");
      out.println("    <input type=\"hidden\" name=\"" + PARAM_SHOW + "\" value=\"" + SHOW_EDIT_VACCINATIONS + "\"/>");
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
          out.println("          <input type=\"radio\" name=\"" + PARAM_EVENT_ID + "\" value=\"" + event.getEventId() + "\" checked=\"checked\">"
              + event.getLabel() + "<br/>");
        } else {
          out.println(
              "          <input type=\"radio\" name=\"" + PARAM_EVENT_ID + "\" value=\"" + event.getEventId() + "\">" + event.getLabel() + "<br/>");
        }
      }
      out.println("          <input type=\"radio\" name=\"" + PARAM_EVENT_ID + "\" value=\"-1\">none of these, proposing a new label<br/>");
      out.println("        </div>propose new label");
      out.println("        <input type=\"text\" name=\"" + PARAM_NEW_EVENT_LABEL + "\" value=\"" + newEventLabel + "\" size=\"50\" /></td>");
      out.println("      </tr>");

      if (user.getSelectedTestPanelCase().getTestCase().getDateSet() == DateSet.FIXED) {
        out.println("      <tr>");
        out.println("          <th>Observed Date</th>");
        out.println("          <td><input type=\"text\" name=\"" + PARAM_EVENT_DATE + "\" size=\"10\" value=\"" + eventDateString + "\"/></td>");
        out.println("        </td>");
        out.println("      </tr>");
      } else {
        String label = "Observed";
        printRelativeRuleRow(PARAM_EVENT_RULE, out, relativeRule, label, RelativeTo.EVALUATION);
      }
      out.println("        <tr>");
      out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION + "\" value=\"" + ACTION_ADD_EVENT
          + "\"/></td>");
      out.println("        </tr>");

      out.println("    </table>");
      out.println("    <input type=\"hidden\" name=\"" + PARAM_SHOW + "\" value=\"" + SHOW_EDIT_EVENTS + "\"/>");
      out.println("    <input type=\"hidden\" name=\"" + PARAM_EVENT_TYPE_CODE + "\" value=\"" + eventType.getEventTypeCode() + "\"/>");
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
          out.println("          <input type=\"radio\" name=\"" + PARAM_EVENT_ID + "\" value=\"" + event.getEventId() + "\" checked=\"checked\">"
              + event.getLabel() + "<br/>");
        } else {
          out.println(
              "          <input type=\"radio\" name=\"" + PARAM_EVENT_ID + "\" value=\"" + event.getEventId() + "\">" + event.getLabel() + "<br/>");
        }
      }
      out.println("          <input type=\"radio\" name=\"" + PARAM_EVENT_ID + "\" value=\"-1\">none of these, proposing a new label<br/>");
      out.println("        </div>propose new label");
      out.println("        <input type=\"text\" name=\"" + PARAM_NEW_EVENT_LABEL + "\" value=\"" + newEventLabel + "\" size=\"50\" /></td>");
      out.println("      </tr>");

      if (user.getSelectedTestPanelCase().getTestCase().getDateSet() == DateSet.FIXED) {
        out.println("      <tr>");
        out.println("          <th>Asserted Date</th>");
        out.println("          <td><input type=\"text\" name=\"" + PARAM_EVENT_DATE + "\" size=\"10\" value=\"" + eventDateString + "\"/></td>");
        out.println("        </td>");
        out.println("      </tr>");
      } else {
        String label = "Asserted";
        printRelativeRuleRow(PARAM_EVENT_RULE, out, relativeRule, label, RelativeTo.EVALUATION);
      }
      out.println("        <tr>");
      out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION + "\" value=\"" + ACTION_ADD_EVENT
          + "\"/></td>");
      out.println("        </tr>");

      out.println("    </table>");
      out.println("    <input type=\"hidden\" name=\"" + PARAM_SHOW + "\" value=\"" + SHOW_EDIT_EVENTS + "\"/>");
      out.println("    <input type=\"hidden\" name=\"" + PARAM_EVENT_TYPE_CODE + "\" value=\"" + eventType.getEventTypeCode() + "\"/>");
      out.println("  </form>");
    }

    out.println("<p>");

    out.println("</p>");

    out.println("</div>");
  }

  public void printShowRowScript(PrintWriter out) {
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
  }

  public void printRelativeRuleRow(String paramRuleName, PrintWriter out, RelativeRule relativeRule, String label, RelativeTo relativeTo) {
    RelativeRule rr = relativeRule;
    boolean showRow = true;
    for (int pos = 1; pos <= 4; pos++) {
      printRelativeRuleRow(paramRuleName, out, testEventList, rr, pos, label, (pos == 1 ? relativeTo : null), showRow);
      showRow = false;
      if (rr != null) {
        showRow = rr.getTestEvent() != null || rr.getRelativeTo() == RelativeTo.BIRTH || rr.getRelativeTo() == RelativeTo.EVALUATION;
        rr = rr.getAndRule();
      }
    }
  }

  public void printEventTable(PrintWriter out, User user, EventType eventType, List<TestEvent> testEventList, String sectionLabel, String itemLabel,
      String show) throws UnsupportedEncodingException {
    TestCase testCase = user.getSelectedTestCase();

    out.println("  <h3>" + sectionLabel + "</h3>");
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
        shouldShow = eventType != EventType.BIRTH && eventType != EventType.EVALUATION && testEvent.getEvent().getEventType() == eventType;
      }
      if (shouldShow) {
        String deleteLink = "testCases?" + PARAM_SHOW + "=" + show + "&" + PARAM_TEST_EVENT_ID + "=" + testEvent.getTestEventId() + "&" + PARAM_ACTION
            + "=" + URLEncoder.encode(ACTION_DELETE_EVENT, "UTF-8");
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
        out.println("      <td>" + (testEvent.getEventDate() == null ? "" : sdf.format(testEvent.getEventDate())) + "</td>");
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
    Query query = applicationSession.getDataSession().createQuery("from TestEvent where testCase = ? order by eventDate");
    query.setParameter(0, testCase);
    testEventList = query.list();
    testCase.setTestEventList(testEventList);
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

  public RelativeRule readRelativeRules(String paramRuleName, HttpServletRequest req, Session dataSession, TestCase testCase) {
    RelativeRule relativeRule = null;
    RelativeRule childRule = null;
    int i = 1;

    while (req.getParameter(paramRuleName + i) != null) {
      RelativeRule parentRule = childRule;
      childRule = new RelativeRule(notNull(req.getParameter(paramRuleName + i)));
      if (parentRule == null) {
        relativeRule = childRule;
      } else {
        parentRule.setAndRule(childRule);
      }
      String beforeOrAfter = notNull(req.getParameter(paramRuleName + RULE_BEFORE_OR_AFTER + i), "A");
      childRule.setBeforeOrAfter(beforeOrAfter.equals("B") ? RelativeRule.BeforeOrAfter.BEFORE : RelativeRule.BeforeOrAfter.AFTER);
      String relativeRuleTestEventIdString = req.getParameter(paramRuleName + RULE_TEST_EVENT_ID + i);
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

  public void printRelativeRuleRow(String paramRuleName, PrintWriter out, List<TestEvent> testEventList, RelativeRule relativeRule, int pos,
      String label, RelativeTo relativeTo, boolean showRow) {

    if (pos == 1) {
      out.println("      <tr id=\"" + label + "." + pos + "\">");
      out.println("          <th>" + label + "</th>");
    } else {
      if (showRow) {
        out.println("      <tr id=\"" + label + "." + pos + "\">");
        out.println("          <td>but not before</td>");
      } else {
        out.println("      <tr id=\"" + label + "." + pos + "\" style=\"display: none;\">");
        out.println("          <td>but not before</td>");
      }
    }
    out.println("          <td id=\"\">");
    out.println("            <input type=\"text\" name=\"" + paramRuleName + pos + "\" size=\"17\" value=\""
        + (relativeRule == null || relativeRule.getTimePeriod() == null ? "" : relativeRule.getTimePeriod()) + "\"/>");
    out.println("            <input type=\"radio\" name=\"" + paramRuleName + RULE_BEFORE_OR_AFTER + pos + "\" value=\"B\""
        + ((relativeRule != null && relativeRule.getBeforeOrAfter() == RelativeRule.BeforeOrAfter.BEFORE) ? " checked=\"true\"" : "") + "/> Before ");
    out.println("            <input type=\"radio\" name=\"" + paramRuleName + RULE_BEFORE_OR_AFTER + pos + "\" value=\"O\""
        + ((relativeRule == null || (relativeRule.isZero() && relativeRule.getBeforeOrAfter() == RelativeRule.BeforeOrAfter.AFTER))
            ? " checked=\"true\"" : "")
        + "/> On ");
    out.println("            <input type=\"radio\" name=\"" + paramRuleName + RULE_BEFORE_OR_AFTER + pos + "\" value=\"A\""
        + ((relativeRule != null && !relativeRule.isZero() && relativeRule.getBeforeOrAfter() == RelativeRule.BeforeOrAfter.AFTER)
            ? " checked=\"true\"" : "")
        + "/> After ");
    out.println(
        "          <select name=\"" + paramRuleName + RULE_TEST_EVENT_ID + pos + "\" onChange=\"showRow('" + label + "." + (pos + 1) + "')\">");
    out.println("            <option value=\"0\">--select--</option>");
    for (TestEvent testEvent : testEventList) {
      boolean selected = false;
      String eventLabel;
      if (testEvent.getEvent().getEventType() == EventType.BIRTH) {
        eventLabel = "Birth";
        if (relativeRule != null && relativeRule.getRelativeTo() == RelativeTo.BIRTH) {
          selected = true;
        }
      } else if (testEvent.getEvent().getEventType() == EventType.EVALUATION) {
        eventLabel = "Evaluation";
        if (relativeRule != null && relativeRule.getRelativeTo() == RelativeTo.EVALUATION) {
          selected = true;
        }
      } else if (testEvent.getEvent().getEventType() == EventType.VACCINATION) {
        eventLabel = "Vaccination #" + testEvent.getScreenId();
      } else if (testEvent.getEvent().getEventType() == EventType.ACIP_DEFINED_CONDITION) {
        eventLabel = "ACIP-Defined Condition #" + testEvent.getScreenId();
      } else if (testEvent.getEvent().getEventType() == EventType.CONDITION_IMPLICATION) {
        eventLabel = "Condition Implication #" + testEvent.getScreenId();
      } else {
        eventLabel = "Other Event #" + testEvent.getScreenId();
      }
      if (!selected) {
        if (relativeRule == null) {
          if (relativeTo != null) {
            if (relativeTo == RelativeTo.BIRTH && testEvent.getEvent().getEventType() == EventType.BIRTH) {
              selected = true;
            } else if (relativeTo == RelativeTo.EVALUATION && testEvent.getEvent().getEventType() == EventType.EVALUATION) {
              selected = true;
            }
          }
        } else {
          if (relativeRule.getTestEvent() != null && relativeRule.getTestEvent().getTestEventId() != -1 && testEvent != null
              && testEvent.getTestEventId() != -1) {
            selected = relativeRule.getTestEvent().equals(testEvent);
          }
        }
      }
      if (selected) {
        out.println("            <option value=\"" + testEvent.getTestEventId() + "\" selected=\"selected\">" + eventLabel + "</option>");
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

  public void setupForPrinting(Session dataSession, User user, HttpServletRequest req) {
    TestPanelCase testPanelCase = user.getSelectedTestPanelCase();
    boolean showExcludedTestCases = req.getParameter(PARAM_SHOW_EXCLUDED_TEST_CASES) != null;
    user.setSelectedCategoryNameExcluded(showExcludedTestCases);

    if (testPanelCase != null) {
      if (user.getSelectedCategoryName() == null) {
        user.setSelectedCategoryName(testPanelCase.getCategoryName());
      }
    }

    TaskGroup taskGroup = user.getSelectedTaskGroup();
    user.setCanViewPrivate(false);
    if (taskGroup != null) {
      user.setCanEditTestCase(false);
      Query query = dataSession.createQuery("from Expert where user = ? and taskGroup = ?");
      query.setParameter(0, user);
      query.setParameter(1, taskGroup);
      List<Expert> expertList = query.list();
      if (expertList.size() == 0) {
        user.setSelectedExpert(null);
      } else {
        user.setCanViewPrivate(true);
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

  public void printAddEditTestCases(HttpServletRequest req, PrintWriter out, Session dataSession, TestPanelCase testPanelCase, String show) {
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
    String includeStatus;

    TestCase testCase = null;
    if (testPanelCase != null) {
      testCase = testPanelCase.getTestCase();
    }

    if (show.equals(SHOW_EDIT_TEST_CASE)) {
      label = notNull(req.getParameter(PARAM_LABEL), testCase.getLabel());
      description = notNull(req.getParameter(PARAM_DESCRIPTION), testCase.getDescription());
      vaccineGroupId = notNull(req.getParameter(PARAM_VACCINE_GROUP_ID),
          (testCase.getVaccineGroup() == null ? 0 : testCase.getVaccineGroup().getVaccineGroupId()));
      includeStatus = notNull(req.getParameter(PARAM_INCLUDE_STATUS), testPanelCase.getIncludeStatus());
      patientFirst = notNull(req.getParameter(PARAM_PATIENT_FIRST), testCase.getPatientFirst());
      patientLast = notNull(req.getParameter(PARAM_PATIENT_LAST), testCase.getPatientLast());
      patientSex = notNull(req.getParameter(PARAM_PATIENT_SEX), testCase.getPatientSex());
      patientDob = notNull(req.getParameter(PARAM_PATIENT_DOB), sdf.format(testCase.getPatientDob()));
      categoryName = notNull(req.getParameter(PARAM_CATEGORY_NAME), testPanelCase.getCategoryName()).trim();
      testCaseNumber = notNull(req.getParameter(PARAM_TEST_CASE_NUMBER), testPanelCase.getTestCaseNumber());
      dateSetCode = notNull(req.getParameter(PARAM_DATE_SET_CODE), testCase.getDateSetCode());
      evalDate = notNull(req.getParameter(PARAM_EVAL_DATE), sdf.format(testCase.getEvalDate()));
      evalRule = notNull(req.getParameter(PARAM_EVAL_RULE), testCase.getEvalRule() != null ? testCase.getEvalRule().getTimePeriodString() : "");
    } else if (show.equals(SHOW_COPY_TEST_CASE)) {
      label = notNull(req.getParameter(PARAM_LABEL), testCase.getLabel());
      description = notNull(req.getParameter(PARAM_DESCRIPTION), testCase.getDescription());
      vaccineGroupId = notNull(req.getParameter(PARAM_VACCINE_GROUP_ID),
          (testCase.getVaccineGroup() == null ? 0 : testCase.getVaccineGroup().getVaccineGroupId()));
      includeStatus = notNull(req.getParameter(PARAM_INCLUDE_STATUS), Include.INCLUDED.getIncludeStatus());
      patientFirst = notNull(req.getParameter(PARAM_PATIENT_FIRST), RandomNames.getRandomFirstName());
      patientLast = notNull(req.getParameter(PARAM_PATIENT_LAST), RandomNames.getRandomLastName());
      patientSex = notNull(req.getParameter(PARAM_PATIENT_SEX), testCase.getPatientSex());
      patientDob = notNull(req.getParameter(PARAM_PATIENT_DOB), sdf.format(testCase.getPatientDob()));
      categoryName = notNull(req.getParameter(PARAM_CATEGORY_NAME), testPanelCase.getCategoryName()).trim();
      testCaseNumber = notNull(req.getParameter(PARAM_TEST_CASE_NUMBER));
      dateSetCode = notNull(req.getParameter(PARAM_DATE_SET_CODE), testCase.getDateSetCode());
      evalDate = notNull(req.getParameter(PARAM_EVAL_DATE), sdf.format(testCase.getEvalDate()));
      evalRule = notNull(req.getParameter(PARAM_EVAL_RULE), testCase.getEvalRule() != null ? testCase.getEvalRule().getTimePeriodString() : "");
    } else {
      label = notNull(req.getParameter(PARAM_LABEL));
      description = notNull(req.getParameter(PARAM_DESCRIPTION));
      vaccineGroupId = notNull(req.getParameter(PARAM_VACCINE_GROUP_ID), 0);
      includeStatus = notNull(req.getParameter(PARAM_INCLUDE_STATUS), Include.PROPOSED.getIncludeStatus());
      patientFirst = notNull(req.getParameter(PARAM_PATIENT_FIRST), RandomNames.getRandomFirstName());
      patientLast = notNull(req.getParameter(PARAM_PATIENT_LAST), RandomNames.getRandomLastName());
      patientSex = notNull(req.getParameter(PARAM_PATIENT_SEX), "F");
      patientDob = notNull(req.getParameter(PARAM_PATIENT_DOB));
      categoryName = notNull(req.getParameter(PARAM_CATEGORY_NAME)).trim();
      testCaseNumber = notNull(req.getParameter(PARAM_TEST_CASE_NUMBER));
      dateSetCode = notNull(req.getParameter(PARAM_DATE_SET_CODE), DateSet.RELATIVE.getDateSetCode());
      evalDate = notNull(req.getParameter(PARAM_EVAL_DATE));
      evalRule = notNull(req.getParameter(PARAM_EVAL_RULE));
    }

    if (testCaseNumber == null || testCaseNumber.equals("")) {
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
    String cancelButton = "";
    if (testPanelCase != null) {
      cancelButton = " <a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW + "=" + SHOW_TEST_CASE + "&" + PARAM_TEST_PANEL_CASE_ID + "="
          + testPanelCase.getTestPanelCaseId() + "\">Back</a>";
    }
    if (show.equals(SHOW_ADD_TEST_CASE)) {
      out.println("  <h2>Add Test Case" + cancelButton + "</h2>");
    } else if (show.equals(SHOW_EDIT_TEST_CASE)) {
      out.println("  <h2>Edit Test Case" + cancelButton + "</h2>");
    } else if (show.equals(SHOW_COPY_TEST_CASE)) {
      out.println("  <h2>Copy Test Case" + cancelButton + "</h2>");
    }
    out.println("    <form method=\"POST\" action=\"testCases\">");
    if (testPanelCase != null) {
      out.println("      <input type=\"hidden\" name=\"" + PARAM_TEST_PANEL_CASE_ID + "\" value=\"" + testPanelCase.getTestPanelCaseId() + "\"/>");
    }
    out.println("      <table width=\"100%\">");
    out.println("        <tr>");
    out.println("          <th>Category</th>");
    out.println("          <td><input type=\"text\" name=\"" + PARAM_CATEGORY_NAME + "\" size=\"50\" value=\"" + categoryName + "\"/></td>");
    out.println("        </tr>");
    if (testPanelCase == null || applicationSession.getUser().isCanEditTestCase() || show.equals(SHOW_COPY_TEST_CASE)) {
      out.println("        <tr>");
      out.println("          <th>Label</th>");
      out.println("          <td><input type=\"text\" name=\"" + PARAM_LABEL + "\" size=\"50\" value=\"" + label + "\"/></td>");
      out.println("        </tr>");
      out.println("        <tr>");
      out.println("          <th>Description</th>");
      out.println(
          "          <td><textarea type=\"text\" name=\"" + PARAM_DESCRIPTION + "\" cols=\"50\" rows=\"3\">" + description + "</textarea></td>");
      out.println("        </tr>");
      out.println("        <tr>");
      out.println("          <th>Vaccine Group</th>");
      out.println("          <td>");
      out.println("            <select type=\"text\" name=\"" + PARAM_VACCINE_GROUP_ID + "\">");
      out.println("              <option value=\"0\">--select--</option>");
      Query query = dataSession.createQuery("from VaccineGroup order by label");
      List<VaccineGroup> vaccineGroupList = query.list();
      for (VaccineGroup vaccineGroup : vaccineGroupList) {
        if (vaccineGroup.getVaccineGroupId() == vaccineGroupId) {
          out.println("              <option value=\"" + vaccineGroup.getVaccineGroupId() + "\" selected=\"selected\">" + vaccineGroup.getLabel()
              + "</option>");
        } else {
          out.println("              <option value=\"" + vaccineGroup.getVaccineGroupId() + "\">" + vaccineGroup.getLabel() + "</option>");
        }
      }
      out.println("            </select>");
      out.println("          </td>");
      out.println("        </tr>");
    }
    out.println("        <tr>");
    out.println("          <th>Include</th>");
    out.println("          <td>");
    out.println("            <select type=\"text\" name=\"" + PARAM_INCLUDE_STATUS + "\">");
    for (Include include : Include.valueList()) {
      if (include.getIncludeStatus().equals(includeStatus)) {
        out.println("              <option value=\"" + include.getIncludeStatus() + "\" selected=\"selected\">" + include.getLabel() + "</option>");
      } else {
        out.println("              <option value=\"" + include.getIncludeStatus() + "\">" + include.getLabel() + "</option>");
      }
    }
    out.println("            </select>");
    out.println("          </td>");
    out.println("        </tr>");

    if (testCaseNumber != null) {
      out.println("          <th>Number</th>");
      out.println("          <td><input type=\"text\" name=\"" + PARAM_TEST_CASE_NUMBER + "\" size=\"15\" value=\"" + testCaseNumber + "\"/></td>");
      out.println("        </tr>");
    }
    out.println("        <tr>");
    if (testPanelCase == null || applicationSession.getUser().isCanEditTestCase() || show.equals(SHOW_COPY_TEST_CASE)) {

      out.println("          <th>Patient First</th>");
      out.println("          <td><input type=\"text\" name=\"" + PARAM_PATIENT_FIRST + "\" size=\"15\" value=\"" + patientFirst + "\"/></td>");
      out.println("        </tr>");
      out.println("        <tr>");
      out.println("          <th>Patient Last</th>");
      out.println("          <td><input type=\"text\" name=\"" + PARAM_PATIENT_LAST + "\" size=\"15\" value=\"" + patientLast + "\"/></td>");
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
      out.println("            </select>");
      out.println("          </td>");
      out.println("        </tr>");
      out.println("        <tr>");
      out.println("          <th>Date Set</th>");
      out.println("          <td>");
      out.println("            <input type=\"radio\" name=\"" + PARAM_DATE_SET_CODE + "\" value=\"" + DateSet.RELATIVE.getDateSetCode() + "\""
          + (dateSetCode.equals(DateSet.RELATIVE.getDateSetCode()) ? " checked=\"true\"" : "") + " onChange=\"changeDateSet(this)\"/> Relative ");
      out.println("            <input type=\"radio\" name=\"" + PARAM_DATE_SET_CODE + "\" value=\"" + DateSet.FIXED.getDateSetCode() + "\""
          + (dateSetCode.equals(DateSet.FIXED.getDateSetCode()) ? " checked=\"true\"" : "") + " onChange=\"changeDateSet(this)\"/> Fixed");
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
      out.println("          <td><input type=\"text\" name=\"" + PARAM_EVAL_RULE + "\" size=\"30\" value=\"" + evalRule + "\"/></td>");
      out.println("        </tr>");
      out.println("        <tr" + showFixed + " id=\"fixedRow1\">");
      out.println("          <th>Date of Birth</th>");
      out.println("          <td><input type=\"text\" name=\"" + PARAM_PATIENT_DOB + "\" size=\"10\" value=\"" + patientDob + "\"/></td>");
      out.println("        </tr>");
      out.println("        <tr" + showFixed + " id=\"fixedRow2\">");
      out.println("          <th>Assessment Date</th>");
      out.println("          <td><input type=\"text\" name=\"" + PARAM_EVAL_DATE + "\" size=\"10\" value=\"" + evalDate + "\"/></td>");
      out.println("        </tr>");
    }
    out.println("        <tr>");
    if (show.equals(SHOW_ADD_TEST_CASE)) {
      out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION + "\" size=\"15\" value=\""
          + ACTION_ADD_TEST_CASE + "\"/></td>");
    } else if (show.equals(SHOW_EDIT_TEST_CASE)) {
      out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION + "\" size=\"15\" value=\""
          + ACTION_UPDATE_TEST_CASE + "\"/></td>");
    } else if (show.equals(SHOW_COPY_TEST_CASE)) {
      out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION + "\" size=\"15\" value=\""
          + ACTION_COPY_TEST_CASE + "\"/></td>");
    }
    out.println("        </tr>");
    out.println("      </table>");
    out.println("    </form>");
    out.println("</div>");
  }

  public void printAddEditTestPanel(HttpServletRequest req, PrintWriter out, Session dataSession, TestPanel testPanel, String show) {
    String label;

    if (show.equals(SHOW_EDIT_TEST_PANEL)) {
      label = notNull(req.getParameter(PARAM_LABEL), testPanel.getLabel());
    } else {
      label = notNull(req.getParameter(PARAM_LABEL));
    }

    out.println("<div class=\"centerLeftColumn\">");
    String cancelButton = "";
    cancelButton = " <a class=\"fauxbutton\" href=\"testCases\">Back</a>";
    if (show.equals(SHOW_ADD_TEST_PANEL)) {
      out.println("  <h2>Add Test Panel" + cancelButton + "</h2>");
    } else if (show.equals(SHOW_EDIT_TEST_PANEL)) {
      out.println("  <h2>Edit Test Panel" + cancelButton + "</h2>");
    }
    out.println("    <form method=\"POST\" action=\"testCases\">");
    if (show.equals(SHOW_EDIT_TEST_CASE)) {
      out.println("      <input type=\"hidden\" name=\"" + PARAM_TEST_PANEL_ID + "\" value=\"" + testPanel.getTestPanelId() + "\"/>");
    }
    out.println("      <table>");
    out.println("        <tr>");
    out.println("          <th>Label</th>");
    out.println("          <td><input type=\"text\" name=\"" + PARAM_LABEL + "\" size=\"50\" value=\"" + label + "\"/></td>");
    out.println("        </tr>");

    out.println("        <tr>");
    out.println("          <th>Available</th>");
    out.println("          <td>");
    out.println("            <select type=\"text\" name=\"" + PARAM_AVAILABLE_CODE + "\">");
    for (Available available : Available.valueList()) {
      if (testPanel != null && available == testPanel.getAvailable()) {
        out.println("              <option value=\"" + available.getAvailableCode() + "\" selected=\"true\">" + available.getLabel() + "</option>");
      } else {
        out.println("              <option value=\"" + available.getAvailableCode() + "\">" + available.getLabel() + "</option>");
      }
    }
    out.println("            </select>");
    out.println("          </td>");
    out.println("        </tr>");

    out.println("        <tr>");
    if (show.equals(SHOW_ADD_TEST_PANEL)) {
      out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION + "\" size=\"15\" value=\""
          + ACTION_ADD_TEST_PANEL + "\"/></td>");
    } else if (show.equals(SHOW_EDIT_TEST_PANEL)) {
      out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION + "\" size=\"15\" value=\""
          + ACTION_UPDATE_TEST_PANEL + "\"/></td>");
    }
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
      removeNonVisibleTestPanels(user, testPanelList);

      for (TestPanel testPanel : testPanelList) {
        if (user.getSelectedTestPanel() == null || !user.getSelectedTestPanel().equals(testPanel)) {
          printTestPanel(out, dataSession, user, testPanel);
        }
      }
      if (user.getSelectedExpert() != null && user.getSelectedExpert().getRole().canEdit()) {
        out.println("      <li><a class=\"add\" href=\"testCases?" + PARAM_SHOW + "=" + SHOW_ADD_TEST_PANEL + "\">add test panel</a></li>");
      } else if (testPanelList.size() == 0) {
        out.println("      <li><em>no test panels defined</em></li>");
      }
      out.println("    </ul>");

      out.println("<h3>All Expert Groups</h3>");
      out.println("<ul class=\"selectLevel1\">");
    } else {
      out.println("<h2>Select Expert Groups</h2>");
      out.println("<ul class=\"selectLevel1\">");
    }
    for (TaskGroup taskGroup : taskGroupList) {
      final String link1 = "testCases?" + PARAM_ACTION + "=" + URLEncoder.encode(ACTION_SELECT_TASK_GROUP, "UTF-8") + "&" + PARAM_TASK_GROUP_ID + "="
          + taskGroup.getTaskGroupId();
      out.println("  <li class=\"selectLevel1\"><a href=\"" + link1 + "\">" + taskGroup.getLabel() + "</a></li>");
    }
    out.println("</ul>");
    out.println("</div>");
  }

  public static void removeNonVisibleTestPanels(User user, List<TestPanel> testPanelList) {
    for (Iterator<TestPanel> it = testPanelList.iterator(); it.hasNext();) {
      TestPanel testPanel = it.next();
      if (testPanel.getAvailable() == Available.DELETED) {
        it.remove();
      } else if (testPanel.getAvailable() == Available.PUBLIC || user.isCanViewPrivate()) {
        // can display
      } else {
        it.remove();
      }
    }
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

    HashMap<VaccineGroup, ForecastActualExpectedCompare> forecastCompareMap = new HashMap<VaccineGroup, ForecastActualExpectedCompare>();
    {
      ForecastActual forecastActual = null;
      ForecastExpected forecastExpected = null;

      if (testCase != null && testPanel != null) {
        Query query = dataSession.createQuery("from TestPanelForecast where testPanelCase.testCase = ? and testPanelCase.testPanel = ?");
        query.setParameter(0, testCase);
        query.setParameter(1, testPanel);
        List<TestPanelForecast> testPanelForecastList = query.list();
        for (TestPanelForecast testPanelForecast : testPanelForecastList) {
          forecastExpected = testPanelForecast.getForecastExpected();
          ForecastActualExpectedCompare forecastCompare = new ForecastActualExpectedCompare();
          forecastCompare.setForecastResultA(forecastExpected);
          forecastCompare.setVaccineGroup(forecastExpected.getVaccineGroup());
          forecastCompareMap.put(forecastCompare.getVaccineGroup(), forecastCompare);
          if (software != null) {
            query = dataSession.createQuery(
                "from ForecastActual where softwareResult.software = ? and softwareResult.testCase = ? and vaccineGroup = ? order by softwareResult.runDate desc");
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

    boolean hasPrimaryExpectations = false;

    Set<VaccineGroup> vaccineGroupDisplayedSet = new HashSet<VaccineGroup>();
    VaccineGroup vaccineGroup = testCase.getVaccineGroup();
    if (vaccineGroup != null) {
      vaccineGroupDisplayedSet.add(vaccineGroup);
      out.println("<h2>Actual vs Expected for " + vaccineGroup.getLabel() + " <a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW + "="
          + SHOW_EDIT_EXPECTATIONS + "&" + PARAM_TEST_PANEL_ID + "=" + testPanel.getTestPanelId() + "&" + PARAM_VACCINE_GROUP_ID + "="
          + vaccineGroup.getVaccineGroupId() + "\">Edit</a><a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW + "=" + SHOW_PREVIEW_TEST_CASE
          + "&" + PARAM_TEST_PANEL_ID + "=" + testPanel.getTestPanelId() + "&" + PARAM_VACCINE_GROUP_ID + "=" + vaccineGroup.getVaccineGroupId()
          + "\">Preview</a></h2>");
      boolean printedEvaluation = printEvaluationCompare(out, dataSession, testCase, vaccineGroup);
      boolean printedForecast = printForecastCompare(out, user, dataSession, testCase, forecastCompareMap, vaccineGroup);
      boolean printedGuidance = printGuidanceCompare(out, dataSession, testCase, vaccineGroup);
      hasPrimaryExpectations = printedEvaluation || printedForecast || printedGuidance;
    }

    if (forecastCompareMap.size() > 0) {
      for (VaccineGroup vg : forecastCompareMap.keySet()) {
        if (vaccineGroup == null || !vg.equals(vaccineGroup)) {
          vaccineGroupDisplayedSet.add(vg);
          out.println(
              "<h2>Actual vs Expected for " + vg.getLabel() + " <a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW + "=" + SHOW_EDIT_EXPECTATIONS
                  + "&" + PARAM_TEST_PANEL_ID + "=" + testPanel.getTestPanelId() + "&" + PARAM_VACCINE_GROUP_ID + "=" + vg.getVaccineGroupId()
                  + "\">Edit</a><a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW + "=" + SHOW_PREVIEW_TEST_CASE + "&" + PARAM_TEST_PANEL_ID
                  + "=" + testPanel.getTestPanelId() + "&" + PARAM_VACCINE_GROUP_ID + "=" + vg.getVaccineGroupId() + "\">Preview</a></h2>");

          printEvaluationCompare(out, dataSession, testCase, vg);
          printForecastCompare(out, user, dataSession, testCase, forecastCompareMap, vg);
          printGuidanceCompare(out, dataSession, testCase, vg);
        }
      }
    }

    out.println("<h2>Other ");
    out.println("<a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW + "=" + SHOW_DEBUGGING_TOOLS + "&" + PARAM_TEST_PANEL_CASE_ID + "="
        + user.getSelectedTestPanelCase().getTestPanelCaseId() + "\">Debugging Tools</a>");
    out.println("<a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW + "=" + SHOW_REQUEST_ACTUAL_RESULTS + "&" + PARAM_TEST_PANEL_CASE_ID + "="
        + user.getSelectedTestPanelCase().getTestPanelCaseId() + "\">Request Actual Results</a>");
    out.println("<a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW + "=" + SHOW_ADD_ACTUAL_VS_EXPECTED + "&" + PARAM_TEST_PANEL_CASE_ID + "="
        + user.getSelectedTestPanelCase().getTestPanelCaseId() + "\">Add Actual vs Expected</a>");
    out.println("</h2>");
    if (hasPrimaryExpectations) {

    }

    out.println("</div>");
  }

  private boolean printGuidanceCompare(PrintWriter out, Session dataSession, TestCase testCase, VaccineGroup vaccineGroup) {
    Query query = dataSession.createQuery("from GuidanceExpected where testCase = ? and guidance.vaccineGroup = ? order by updatedDate desc");
    query.setParameter(0, testCase);
    query.setParameter(1, vaccineGroup);
    List<GuidanceExpected> guidanceExpectedList = query.list();
    if (guidanceExpectedList.size() > 0) {
      out.println("<h3>Guidance</h3>");
      out.println("<table width=\"100%\">");
      out.println("  <tr>");
      out.println("    <th>Recommended Actions</th>");
      out.println("  </tr>");
      out.println("  <tr>");
      out.println("    <td>");
      for (GuidanceExpected guidanceExpected : guidanceExpectedList) {
        query = dataSession.createQuery("from RecommendGuidance where guidance = ? order by recommend.recommendTypeCode, recommend.recommendText");
        query.setParameter(0, guidanceExpected.getGuidance());
        List<RecommendGuidance> recommendGuidanceList = query.list();
        User author = guidanceExpected.getAuthor();
        if (recommendGuidanceList.size() > 0) {
          out.println("     Expected by " + author.getName() + " at " + author.getOrganization() + "<br/>");
          out.println("     <ul>");
          for (RecommendGuidance recommendGuidance : recommendGuidanceList) {
            out.println("     <li>" + recommendGuidance.getRecommend() + "</li>");
          }
          out.println("     </ul>");
        }
      }
      out.println("");
      out.println("    </td>");
      out.println("  </tr>");
      out.println("  <tr>");
      out.println("    <th>Other</th>");
      out.println("  </tr>");
      out.println("  <tr>");
      out.println("    <td>");
      for (GuidanceExpected guidanceExpected : guidanceExpectedList) {
        query = dataSession.createQuery("from ConsiderationGuidance where guidance = ? order by consideration.considerationText");
        query.setParameter(0, guidanceExpected.getGuidance());
        List<ConsiderationGuidance> considerationGuidanceList = query.list();
        if (considerationGuidanceList.size() > 0) {
          User author = guidanceExpected.getAuthor();
          out.println("     Expected by " + author.getName() + " at " + author.getOrganization() + "<br/>");
          out.println("     <ul>");
          for (ConsiderationGuidance considerationGuidance : considerationGuidanceList) {
            out.println("     <li>" + considerationGuidance.getConsideration() + "</li>");
          }
          out.println("     </ul>");
        }
      }
      out.println("");
      out.println("    </td>");
      out.println("  </tr>");
      out.println("  <tr>");
      out.println("    <th>ACIP Guideline Rationale</th>");
      out.println("  </tr>");
      out.println("  <tr>");
      out.println("    <td>");
      for (GuidanceExpected guidanceExpected : guidanceExpectedList) {
        query = dataSession.createQuery("from RationaleGuidance where guidance = ? order by rationale.rationaleText");
        query.setParameter(0, guidanceExpected.getGuidance());
        List<RationaleGuidance> rationaleGuidanceList = query.list();
        User author = guidanceExpected.getAuthor();
        if (rationaleGuidanceList.size() > 0) {
          out.println("     Expected by " + author.getName() + " at " + author.getOrganization() + "<br/>");
          out.println("     <ul>");
          for (RationaleGuidance rationaleGuidance : rationaleGuidanceList) {
            out.println("     <li>" + rationaleGuidance.getRationale().getRationaleText() + "</li>");
          }
          out.println("     </ul>");
        }
      }
      out.println("");
      out.println("    </td>");
      out.println("  </tr>");
      out.println("  <tr>");
      out.println("    <th>Additional Resources</th>");
      out.println("  </tr>");
      out.println("  <tr>");
      out.println("    <td>");
      for (GuidanceExpected guidanceExpected : guidanceExpectedList) {
        query = dataSession.createQuery("from ResourceGuidance where guidance = ? order by resource.resourceText");
        query.setParameter(0, guidanceExpected.getGuidance());
        List<ResourceGuidance> resourceGuidanceList = query.list();
        if (resourceGuidanceList.size() > 0) {
          User author = guidanceExpected.getAuthor();
          out.println("     Expected by " + author.getName() + " at " + author.getOrganization() + "<br/>");
          out.println("     <ul>");
          for (ResourceGuidance resourceGuidance : resourceGuidanceList) {
            out.println(
                "     <li>" + resourceGuidance.getResource().getResourceText() + " - <a href=\"" + resourceGuidance.getResource().getResourceLink()
                    + "\" target=\"_blank\">" + resourceGuidance.getResource().getResourceLink() + "</a></li>");
          }
          out.println("     </ul>");
        }
      }
      out.println("");
      out.println("    </td>");
      out.println("  </tr>");
      out.println("</table>");
      return true;
    }
    return false;
  }

  public void printPreview(PrintWriter out, User user, VaccineGroup vaccineGroup) {
    out.println("<div class=\"centerRightColumn\">");

    Session dataSession = applicationSession.getDataSession();

    TestPanelCase testPanelCase = user.getSelectedTestPanelCase();
    TestCase testCase = testPanelCase.getTestCase();
    TaskGroup taskGroup = user.getSelectedTaskGroup();
    TestPanel testPanel = user.getSelectedTestPanel();
    Software software = user.getSelectedSoftware();
    SoftwareManager.initSoftware(software, dataSession);

    HashMap<VaccineGroup, ForecastActualExpectedCompare> forecastCompareMap = new HashMap<VaccineGroup, ForecastActualExpectedCompare>();
    {
      ForecastActual forecastActual = null;
      ForecastExpected forecastExpected = null;

      if (testCase != null && testPanel != null) {
        Query query = dataSession.createQuery("from TestPanelForecast where testPanelCase.testCase = ? and testPanelCase.testPanel = ?");
        query.setParameter(0, testCase);
        query.setParameter(1, testPanel);
        List<TestPanelForecast> testPanelForecastList = query.list();
        for (TestPanelForecast testPanelForecast : testPanelForecastList) {
          forecastExpected = testPanelForecast.getForecastExpected();
          ForecastActualExpectedCompare forecastCompare = new ForecastActualExpectedCompare();
          forecastCompare.setForecastResultA(forecastExpected);
          forecastCompare.setVaccineGroup(forecastExpected.getVaccineGroup());
          forecastCompareMap.put(forecastCompare.getVaccineGroup(), forecastCompare);
        }
      }
    }

    out.println("<h2>Preview for " + vaccineGroup.getLabel() + " <a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW + "=" + SHOW_TEST_CASE + "&"
        + PARAM_TEST_PANEL_ID + "=" + testPanel.getTestPanelId() + "\">Back</a></h2>");

    out.println("<div class=\"preview\">");
    out.println("  <h2 class=\"previewHeading\">Patient</h2>");
    out.println("  <table>");
    out.println("    <tr>");
    out.println("      <th>Patient</th>");
    out.println("      <td>" + testCase.getPatientFirst() + " " + testCase.getPatientLast() + " (" + testCase.getPatientSex() + ")</td>");
    out.println("    </tr>");
    out.println("    <tr>");
    out.println("      <th>Birth Date</th>");
    out.println("      <td>" + sdf.format(testCase.getPatientDob()) + "</td>");
    out.println("    </tr>");
    out.println("    <tr>");
    out.println("      <th>Assessment Date</th>");
    out.println("      <td>" + sdf.format(testCase.getEvalDate()) + "</td>");
    out.println("    </tr>");
    out.println("  </table>");

    if (countVaccination > 0) {

      Query query = dataSession
          .createQuery("from EvaluationExpected where testCase = ? and vaccineGroup = ? and author = ? order by updatedDate desc");
      query.setParameter(0, testCase);
      query.setParameter(1, vaccineGroup);
      query.setParameter(2, user);
      List<EvaluationExpected> evaluationExpectedList = query.list();
      Map<TestEvent, EvaluationExpected> authorMap = new HashMap<TestEvent, EvaluationExpected>();
      for (EvaluationExpected evaluationExpected : evaluationExpectedList) {
        TestEvent testEvent = evaluationExpected.getTestEvent();
        authorMap.put(testEvent, evaluationExpected);
      }

      out.println("  <h2 class=\"previewHeading\">Vaccinations Administered</h2>");
      out.println("  <table>");
      out.println("    <tr>");
      out.println("      <th>#</th>");
      out.println("      <th>Vaccination</th>");
      out.println("      <th>Date</th>");
      out.println("      <th>Status</th>");
      out.println("    </tr>");
      int screenId = 0;
      for (TestEvent testEvent : testEventList) {
        if (testEvent.getEvent().getEventType() == EventType.VACCINATION) {
          screenId++;
          testEvent.setScreenId(screenId);
          out.println("    <tr>");
          out.println("      <td>" + testEvent.getScreenId() + "</td>");
          out.println("      <td>" + testEvent.getEvent().getLabel() + "</td>");
          out.println("      <td>" + (testEvent.getEventDate() == null ? "" : sdf.format(testEvent.getEventDate())) + "</td>");
          EvaluationExpected evaluationExpected = authorMap.get(testEvent);
          if (evaluationExpected == null || evaluationExpected.getEvaluation() == null) {
            out.println("    <td></td>");
          } else {
            out.println("    <td>" + evaluationExpected.getEvaluation().getLabel() + "</td>");
          }
          out.println("    </tr>");
        }
      }
      out.println("  </table>");

    }
    if (countACIP > 0) {
      printEventPreview(out, testCase, testEventList, EventType.ACIP_DEFINED_CONDITION, "Patient Conditions Considered");
    }

    if (countCondition > 0) {
      printEventPreview(out, testCase, testEventList, EventType.CONDITION_IMPLICATION, "Conditions Determined");
    }

    ForecastActualExpectedCompare forecastCompare = forecastCompareMap.get(vaccineGroup);
    if (forecastCompare != null) {
      out.println("<h2 class=\"previewHeading\">Forecast</h2>");
      out.println("  <table width=\"100%\">");
      out.println("    <tr>");
      out.println("      <th>Vaccine</th>");
      out.println("      <th>Status</th>");
      out.println("      <th>Dose</th>");
      out.println("      <th>Earliest</th>");
      out.println("      <th>Recommend</th>");
      out.println("      <th>Past Due</th>");
      out.println("    </tr>");

      final ForecastExpected forecastExpected = (ForecastExpected) forecastCompare.getForecastResultA();

      List<ForecastExpected> expecedList;
      List<ForecastActual> otherActualList;

      Query query1 = dataSession.createQuery("from ForecastExpected where testCase = ? and vaccineGroup = ?");
      query1.setParameter(0, testCase);
      query1.setParameter(1, vaccineGroup);
      List<ForecastExpected> expectedList = query1.list();

      String expectedAdmin = (forecastExpected.getAdmin() == null ? Admin.UNKNOWN : forecastExpected.getAdmin()).getLabel();
      String expectedDoseNumber = forecastExpected.getDoseNumber() != null ? forecastExpected.getDoseNumber() : "-";
      String expectedValidDate = forecastExpected.getValidDate() != null ? sdf.format(forecastExpected.getValidDate()) : "-";
      String expectedDueDate = forecastExpected.getDueDate() != null ? sdf.format(forecastExpected.getDueDate()) : "-";
      String expectedOverdueDate = forecastExpected.getOverdueDate() != null ? sdf.format(forecastExpected.getOverdueDate()) : "-";
      out.println("    <tr>");
      out.println("      <td>" + vaccineGroup.getLabel() + "</td>");
      out.println("      <td>" + expectedAdmin + "</td>");
      out.println("      <td>" + expectedDoseNumber + "</td>");
      out.println("      <td>" + expectedValidDate + "</td>");
      out.println("      <td>" + expectedDueDate + "</td>");
      out.println("      <td>" + expectedOverdueDate + "</td>");
      out.println("    </tr>");

      out.println("  </table>");
    }

    Query query = dataSession
        .createQuery("from GuidanceExpected where testCase = ? and guidance.vaccineGroup = ? and author = ? order by updatedDate desc");
    query.setParameter(0, testCase);
    query.setParameter(1, vaccineGroup);
    query.setParameter(2, user);
    List<GuidanceExpected> guidanceExpectedList = query.list();
    if (guidanceExpectedList.size() > 0) {
      GuidanceExpected guidanceExpected = guidanceExpectedList.get(0);
      out.println("<h2 class=\"previewHeading\">Guidance</h2>");
      query = dataSession.createQuery("from RecommendGuidance where guidance = ? order by recommend.recommendTypeCode, recommend.recommendText");
      query.setParameter(0, guidanceExpected.getGuidance());
      List<RecommendGuidance> recommendGuidanceList = query.list();
      if (recommendGuidanceList.size() > 0) {
        out.println("<h3 class=\"previewHeading\">Recommended Actions</h3>");
        out.println("     <ul>");
        for (RecommendGuidance recommendGuidance : recommendGuidanceList) {
          out.println("     <li>" + recommendGuidance.getRecommend() + "</li>");
        }
        out.println("     </ul>");
      }
      query = dataSession.createQuery("from ConsiderationGuidance where guidance = ? order by consideration.considerationText");
      query.setParameter(0, guidanceExpected.getGuidance());
      List<ConsiderationGuidance> considerationGuidanceList = query.list();
      if (considerationGuidanceList.size() > 0) {
        out.println("<h3 class=\"previewHeading\">Other</h3>");
        out.println("     <ul>");
        for (ConsiderationGuidance considerationGuidance : considerationGuidanceList) {
          out.println("     <li>" + considerationGuidance.getConsideration().toString() + "</li>");
        }
        out.println("     </ul>");
      }
      query = dataSession.createQuery("from RationaleGuidance where guidance = ? order by rationale.rationaleText");
      query.setParameter(0, guidanceExpected.getGuidance());
      List<RationaleGuidance> rationaleGuidanceList = query.list();
      if (rationaleGuidanceList.size() > 0) {
        out.println("<h3 class=\"previewHeading\">Guidance Rationale</h3>");
        out.println("     <ul>");
        for (RationaleGuidance rationaleGuidance : rationaleGuidanceList) {
          out.println("     <li>" + rationaleGuidance.getRationale().getRationaleText() + "</li>");
        }
        out.println("     </ul>");
      }
      query = dataSession.createQuery("from ResourceGuidance where guidance = ? order by resource.resourceText");
      query.setParameter(0, guidanceExpected.getGuidance());
      List<ResourceGuidance> resourceGuidanceList = query.list();
      if (resourceGuidanceList.size() > 0) {
        out.println("<h3 class=\"previewHeading\">Additional Resources</h3>");
        out.println("     <ul>");
        for (ResourceGuidance resourceGuidance : resourceGuidanceList) {
          out.println(
              "     <li>" + resourceGuidance.getResource().getResourceText() + " - <a href=\"" + resourceGuidance.getResource().getResourceLink()
                  + "\" target=\"_blank\">" + resourceGuidance.getResource().getResourceLink() + "</a></li>");
        }
        out.println("     </ul>");
      }
    }

    out.println("</div>");
    out.println("</div>");
  }

  public void printEventPreview(PrintWriter out, TestCase testCase, List<TestEvent> testEventList, EventType eventType, String eventLabel) {
    out.println("<h3 class=\"previewHeading\">" + eventLabel + "</h3>");
    out.println("<ul>");
    int screenId = 0;
    for (TestEvent testEvent : testEventList) {
      if (testEvent.getEvent().getEventType() == eventType) {
        screenId++;
        testEvent.setScreenId(screenId);
        if (testEvent.getEventDate() == null || testEvent.getEventDate().equals(testCase.getEvalDate())) {
          out.println("  <li>" + testEvent.getEvent().getLabel() + "</li>");
        } else {
          out.println("  <li>" + testEvent.getEvent().getLabel() + " on " + sdf.format(testEvent.getEventDate()) + "</li>");
        }
      }
    }
    out.println("</ul>");
  }

  private boolean printEvaluationCompare(PrintWriter out, Session dataSession, TestCase testCase, VaccineGroup vaccineGroup) {
    if (countVaccination > 0) {
      Map<User, Map<TestEvent, EvaluationExpected>> expectedMap = new HashMap<User, Map<TestEvent, EvaluationExpected>>();
      List<User> authorList = new ArrayList<User>();

      Query query = dataSession.createQuery("from EvaluationExpected where testCase = ? and vaccineGroup = ? order by updatedDate desc");
      query.setParameter(0, testCase);
      query.setParameter(1, vaccineGroup);
      List<EvaluationExpected> evaluationExpectedList = query.list();
      for (EvaluationExpected evaluationExpected : evaluationExpectedList) {
        User author = evaluationExpected.getAuthor();
        TestEvent testEvent = evaluationExpected.getTestEvent();
        Map<TestEvent, EvaluationExpected> authorMap = expectedMap.get(author);
        if (authorMap == null) {
          authorMap = new HashMap<TestEvent, EvaluationExpected>();
          expectedMap.put(author, authorMap);
          authorList.add(author);
        }
        authorMap.put(testEvent, evaluationExpected);
      }

      if (authorList.size() > 0) {
        out.println("<h3>Evaluation</h3>");
        out.println("<table>");
        out.println("  <tr>");
        out.println("    <th>Entity</th>");
        for (TestEvent testEvent : testEventList) {
          if (testEvent.getEvent().getEventType() == EventType.VACCINATION) {
            out.println("    <th>Vacc #" + testEvent.getScreenId() + "</th>");
          }
        }
        out.println("  </tr>");

        for (User author : authorList) {
          String entityLabel = "Expected by " + author.getName() + " at " + author.getOrganization();
          out.println("  <tr>");
          out.println("    <td>" + entityLabel + "</td>");
          Map<TestEvent, EvaluationExpected> authorMap = expectedMap.get(author);
          for (TestEvent testEvent : testEventList) {
            if (testEvent.getEvent().getEventType() == EventType.VACCINATION) {
              EvaluationExpected evaluationExpected = authorMap.get(testEvent);
              if (evaluationExpected == null || evaluationExpected.getEvaluation() == null) {
                out.println("    <td></td>");
              } else {
                out.println("    <td>" + evaluationExpected.getEvaluation().getLabel() + "</td>");
              }
            }
          }
          out.println("  </tr>");
        }
        out.println("</table>");
        return true;
      }
    }
    return false;
  }

  private boolean printForecastCompare(PrintWriter out, User user, Session dataSession, TestCase testCase,
      HashMap<VaccineGroup, ForecastActualExpectedCompare> forecastCompareMap, VaccineGroup vaccineGroup) {
    ForecastActualExpectedCompare forecastCompare = forecastCompareMap.get(vaccineGroup);
    if (forecastCompare != null) {
      out.println("<h3>Forecast</h3>");
      out.println("  <table width=\"100%\">");
      out.println("    <tr>");
      out.println("      <th>Entity</th>");
      out.println("      <th>Status</th>");
      out.println("      <th>Dose</th>");
      out.println("      <th>Earliest</th>");
      out.println("      <th>Recommend</th>");
      out.println("      <th>Past Due</th>");
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
      return true;
    }
    return false;
  }

  public void printForecastActual(PrintWriter out, ForecastActual forecastActual) {
    String entityLabel = "Actual from " + forecastActual.getSoftwareResult().getSoftware().getLabel();
    String expectedAdmin = (forecastActual.getAdmin() == null ? Admin.UNKNOWN : forecastActual.getAdmin()).getLabel();
    String actualDoseNumber = forecastActual.getDoseNumber() != null ? forecastActual.getDoseNumber() : "-";
    String actualValidDate = forecastActual.getValidDate() != null ? sdf.format(forecastActual.getValidDate()) : "-";
    String actualDueDate = forecastActual.getDueDate() != null ? sdf.format(forecastActual.getDueDate()) : "-";
    String actualOverdueDate = forecastActual.getOverdueDate() != null ? sdf.format(forecastActual.getOverdueDate()) : "-";

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
    String entityLabel = "Expected by " + forecastExpected.getAuthor().getName() + " at " + forecastExpected.getAuthor().getOrganization();
    String expectedAdmin = (forecastExpected.getAdmin() == null ? Admin.UNKNOWN : forecastExpected.getAdmin()).getLabel();
    String expectedDoseNumber = forecastExpected.getDoseNumber() != null ? forecastExpected.getDoseNumber() : "-";
    String expectedValidDate = forecastExpected.getValidDate() != null ? sdf.format(forecastExpected.getValidDate()) : "-";
    String expectedDueDate = forecastExpected.getDueDate() != null ? sdf.format(forecastExpected.getDueDate()) : "-";
    String expectedOverdueDate = forecastExpected.getOverdueDate() != null ? sdf.format(forecastExpected.getOverdueDate()) : "-";

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

    String expectedAdmin = (forecastExpected.getAdmin() == null ? Admin.UNKNOWN : forecastExpected.getAdmin()).getLabel();
    String actualAdmin = (forecastActual == null || forecastActual.getAdmin() == null ? Admin.UNKNOWN : forecastActual.getAdmin()).getLabel();
    String expectedDoseNumber = forecastExpected.getDoseNumber() != null ? forecastExpected.getDoseNumber() : "-";
    String actualDoseNumber = hasActual && forecastActual.getDoseNumber() != null ? forecastActual.getDoseNumber() : "-";
    String expectedValidDate = forecastExpected.getValidDate() != null ? sdf.format(forecastExpected.getValidDate()) : "-";
    String actualValidDate = hasActual && forecastActual.getValidDate() != null ? sdf.format(forecastActual.getValidDate()) : "-";
    String expectedDueDate = forecastExpected.getDueDate() != null ? sdf.format(forecastExpected.getDueDate()) : "-";
    String actualDueDate = hasActual && forecastActual.getDueDate() != null ? sdf.format(forecastActual.getDueDate()) : "-";
    String expectedOverdueDate = forecastExpected.getOverdueDate() != null ? sdf.format(forecastExpected.getOverdueDate()) : "-";
    String actualOverdueDate = hasActual && forecastActual.getOverdueDate() != null ? sdf.format(forecastActual.getOverdueDate()) : "-";

    String styleClassLabel = "";
    String styleClassAdmin = "";
    String styleClassDoseNumber = "";
    String styleClassValid = "";
    String styleClassDue = "";
    String styleClassOverdue = "";

    if (forecastActual != null) {
      styleClassAdmin = (hasActual
          && ForecastActualExpectedCompare.same(forecastExpected.getAdmin(), forecastActual == null ? null : forecastActual.getAdmin())) ? "pass"
              : "fail";
      styleClassDoseNumber = hasActual && compareDoseNumbers(expectedDoseNumber, actualDoseNumber) ? "pass" : "fail";
      styleClassValid = hasActual && compareDoseNumbers(expectedValidDate, actualValidDate) ? "pass" : "fail";
      styleClassDue = hasActual && compareDoseNumbers(expectedDueDate, actualDueDate) ? "pass" : "fail";
      styleClassOverdue = hasActual && compareDoseNumbers(expectedOverdueDate, actualOverdueDate) ? "pass" : "fail";
      styleClassLabel = styleClassAdmin.equals("pass") && styleClassDoseNumber.equals("pass") && styleClassValid.equals("pass")
          && styleClassDue.equals("pass") && styleClassOverdue.equals("pass") ? "pass" : "fail";

    }

    {
      String entityLabel = "Expected by " + forecastExpected.getAuthor().getName() + " at " + forecastExpected.getAuthor().getOrganization();
      out.println("    <tr>");
      out.println("      <td class=\"" + styleClassLabel + "\">" + entityLabel + "</td>");
      out.println("      <td class=\"" + styleClassAdmin + "\">" + expectedAdmin + "</td>");
      out.println("      <td class=\"" + styleClassDoseNumber + "\">" + expectedDoseNumber + "</td>");
      out.println("      <td class=\"" + styleClassValid + "\">" + expectedValidDate + "</td>");
      out.println("      <td class=\"" + styleClassDue + "\">" + expectedDueDate + "</td>");
      out.println("      <td class=\"" + styleClassOverdue + "\">" + expectedOverdueDate + "</td>");
      out.println("    </tr>");
    }

    if (forecastActual != null) {
      String entityLabel = "Actual from " + forecastActual.getSoftwareResult().getSoftware().getLabel();
      // todo
      if (forecastActual.getSoftwareResult() != null) {
        SoftwareResult softwareResult = forecastActual.getSoftwareResult();
        String todayString = sdf.format(new Date());
        String runDateString = sdf.format(softwareResult.getRunDate());
        if (todayString.equals(runDateString)) {
          // will work on displaying it somewhere
        }
      }
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

  public List<TestEvent> printTestCase(PrintWriter out, User user, HttpServletRequest req, Session dataSession) {
    TestPanelCase testPanelCase = user.getSelectedTestPanelCase();
    TestCase testCase = testPanelCase.getTestCase();
    String editLink = "testCases?" + PARAM_SHOW + "=" + SHOW_EDIT_TEST_CASE + "&" + PARAM_TEST_PANEL_CASE_ID + "="
        + testPanelCase.getTestPanelCaseId();
    String copyLink = "testCases?" + PARAM_SHOW + "=" + SHOW_COPY_TEST_CASE + "&" + PARAM_TEST_PANEL_CASE_ID + "="
        + testPanelCase.getTestPanelCaseId();
    String editButton = "";
    if (user.getSelectedExpert() != null && user.getSelectedExpert().getRole().canEdit()) {
      editButton = " <a class=\"fauxbutton\" href=\"" + editLink + "\">Edit</a>";
    }
    editButton += " <a class=\"fauxbutton\" href=\"" + copyLink + "\">Copy</a>";
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
        + (testPanelCase.getTestCase().getVaccineGroup() == null ? "" : testPanelCase.getTestCase().getVaccineGroup().getLabel()) + "</td>");
    out.println("    </tr>");
    out.println("    <tr>");
    out.println("      <th>Include Status</th>");
    out.println("      <td>" + testPanelCase.getInclude().getLabel() + "</td>");
    out.println("    </tr>");
    String styleClass = "";
    if (user.isExpertOrAdmin(user.getSelectedTaskGroup())) {
      Result result = testPanelCase.getResult();
      if (result == Result.ACCEPT || result == Result.PASS) {
        styleClass = "pass";
      } else {
        styleClass = "fail";
      }
      out.println("    <tr>");
      out.println("      <th>Result Status</th>");
      out.println("      <td class=\"" + styleClass + "\">" + (result == null ? "-" : result.getLabel()) + "</td>");
      out.println("    </tr>");
    }
    out.println("    <tr>");
    out.println("      <th>Number</th>");
    out.println("      <td>" + testPanelCase.getTestCaseNumber() + "</td>");
    out.println("    </tr>");
    out.println("    <tr>");
    out.println("      <th>Patient</th>");
    out.println("      <td>" + testCase.getPatientFirst() + " " + testCase.getPatientLast() + " (" + testCase.getPatientSex() + ")</td>");
    out.println("    </tr>");
    out.println("    <tr>");
    out.println("      <th>Birth Date</th>");
    if (testCase.getPatientDob() == null) {
      out.println("<td></td>");
    } else if (testCase.getDateSet() == DateSet.RELATIVE && testCase.getEvalRule() != null) {
      out.println("      <td>" + sdf.format(testCase.getPatientDob()) + " (" + testCase.getEvalRule().getTimePeriod() + " old)</td>");
    } else {
      out.println("      <td>" + sdf.format(testCase.getPatientDob()) + "</td>");
    }
    out.println("    </tr>");
    out.println("    <tr>");
    out.println("      <th>Assessment Date</th>");
    out.println("      <td>" + sdf.format(testCase.getEvalDate()) + "</td>");
    out.println("    </tr>");
    out.println("  </table>");

    countVaccination = 0;
    countACIP = 0;
    countCondition = 0;
    countOther = 0;
    Query query = applicationSession.getDataSession().createQuery("from TestEvent where testCase = ? order by eventDate");
    query.setParameter(0, testCase);
    testEventList = query.list();
    testCase.setTestEventList(testEventList);
    for (TestEvent testEvent : testEventList) {
      if (testEvent.getEvent().getEventType() == EventType.VACCINATION) {
        countVaccination++;
      } else if (testEvent.getEvent().getEventType() == EventType.ACIP_DEFINED_CONDITION) {
        countACIP++;
      } else if (testEvent.getEvent().getEventType() == EventType.CONDITION_IMPLICATION) {
        countCondition++;
      } else {
        countOther++;
      }
    }

    editLink = "testCases?" + PARAM_SHOW + "=" + SHOW_EDIT_VACCINATIONS + "&" + PARAM_TEST_PANEL_CASE_ID + "=" + testPanelCase.getTestPanelCaseId();
    editButton = "";
    if (user.isCanEditTestCase()) {
      editButton = " <a class=\"fauxbutton\" href=\"" + editLink + "\">Edit</a>";
    }
    out.println("  <h3>Vaccination History" + editButton + "</h3>");
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
          out.println("      <td>" + (testEvent.getEventDate() == null ? "" : sdf.format(testEvent.getEventDate())) + "</td>");
          out.println("      <td>" + testEvent.getAgeAlmost(testCase) + "</td>");
          out.println("    </tr>");
        }
      }
      out.println("  </table>");
    }

    editLink = "testCases?" + PARAM_SHOW + "=" + SHOW_EDIT_EVENTS + "&" + PARAM_TEST_PANEL_CASE_ID + "=" + testPanelCase.getTestPanelCaseId() + "&"
        + PARAM_EVENT_TYPE_CODE + "=";
    if (user.isCanEditTestCase()) {
      editButton = " <a class=\"fauxbutton\" href=\"" + editLink + EventType.ACIP_DEFINED_CONDITION.getEventTypeCode() + "\">Edit</a>";
    } else {
      editButton = "";
    }
    out.println("  <h3>ACIP-Defined Conditions" + editButton + "</h3>");

    if (countACIP > 0) {
      printEventViewTable(out, testCase, testEventList, EventType.ACIP_DEFINED_CONDITION, "Condition");
    }

    if (user.isCanEditTestCase()) {
      editButton = " <a class=\"fauxbutton\" href=\"" + editLink + EventType.CONDITION_IMPLICATION.getEventTypeCode() + "\">Edit</a>";
    } else {
      editButton = "";
    }
    out.println("  <h3>Condition Implications" + editButton + "</h3>");
    if (countCondition > 0) {
      printEventViewTable(out, testCase, testEventList, EventType.CONDITION_IMPLICATION, "Condition");
    }

    String editSoftwareSettings = "";
    if (user.isExpertOrAdmin(user.getSelectedTaskGroup())) {
      editSoftwareSettings = " <a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW + "=" + SHOW_EDIT_TEST_CASE_SETTINGS + "&"
          + PARAM_TEST_PANEL_CASE_ID + "=" + testPanelCase.getTestPanelCaseId() + "\">Edit</a>";
    }

    out.println("  <h3>Software Settings" + editSoftwareSettings + "</h3>");
    query = applicationSession.getDataSession().createQuery("from TestCaseSetting where testCase = ?");
    query.setParameter(0, testCase);
    List<TestCaseSetting> testCaseSettingList = query.list();
    if (testCaseSettingList.size() > 0) {
      out.println("  <table width=\"100%\">");
      out.println("    <tr>");
      out.println("      <th>Software</th>");
      out.println("      <th>Option</th>");
      out.println("      <th>Value</th>");
      out.println("    </tr>");
      for (TestCaseSetting testCaseSetting : testCaseSettingList) {
        out.println("    <tr>");
        out.println("      <td>" + testCaseSetting.getServiceOption().getService().getLabel() + "</td>");
        out.println("      <td>" + testCaseSetting.getServiceOption().getOptionName() + "</td>");
        out.println("      <td>" + testCaseSetting.getOptionValue() + "</td>");
        out.println("    </tr>");
      }
      out.println("  </table>");
    }

    TestNote testNoteToEdit = null;
    if (req.getParameter(PARAM_TEST_NOTE_ID) != null && req.getParameter(PARAM_TEST_NOTE_EDIT) != null) {
      int testNoteId = Integer.parseInt(req.getParameter(PARAM_TEST_NOTE_ID));
      testNoteToEdit = (TestNote) dataSession.get(TestNote.class, testNoteId);
    }
    if (testNoteToEdit == null) {
      printShowRowScript(out);
    }
    String addCommentButton = "";
    if (testNoteToEdit == null) {
      addCommentButton = " <a class=\"fauxbutton\" href=\"javascript: showRow('commentForm')\">Add Comment</a>";
    }
    out.println("  <h2>Comments" + addCommentButton + "</h2>");

    out.println(
        "  <form method=\"POST\" action=\"testCases\" id=\"commentForm\" style=\"" + (testNoteToEdit == null ? "display: none;" : "") + "\">");
    out.println("    <input type=\"hidden\" name=\"" + PARAM_TEST_PANEL_CASE_ID + "\" value=\"" + testPanelCase.getTestPanelCaseId() + "\"/>");
    if (testNoteToEdit != null) {
      out.println("    <input type=\"hidden\" name=\"" + PARAM_TEST_NOTE_ID + "\" value=\"" + testNoteToEdit.getTestNoteId() + "\"/>");
    }
    out.println("    <table width=\"100%\">");
    out.println("      <tr>");
    out.println("        <th>Commment</th>");
    out.println("        <td>");
    out.println("          <textarea name=\"" + PARAM_NOTE_TEXT + "\" value=\"\" cols=\"30\" rows=\"5\">"
        + (testNoteToEdit == null ? "" : testNoteToEdit.getNoteText()) + "</textarea>");
    out.println("        </td>");
    out.println("      </tr>");
    if (testNoteToEdit == null && user.isExpertOrAdmin(user.getSelectedTaskGroup())) {
      out.println("      <tr>");
      out.println("        <th>Test Case Result Status</th>");
      out.println("        <td>");
      out.println("            <select type=\"text\" name=\"" + PARAM_RESULT_STATUS + "\">");
      out.println("              <option value=\"0\">--select--</option>");
      for (Result result : Result.values()) {
        if (testPanelCase.getResult() == result) {
          out.println("              <option value=\"" + result.getResultStatus() + "\" selected=\"true\">" + result.getLabel() + "</option>");
        } else {
          out.println("              <option value=\"" + result.getResultStatus() + "\">" + result.getLabel() + "</option>");
        }
      }
      out.println("            </select>");
      out.println("        </td>");
      out.println("      </tr>");
    }
    out.println("      <tr>");
    out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION + "\" size=\"15\" value=\""
        + (testNoteToEdit == null ? ACTION_ADD_COMMENT : ACTION_UPDATE_COMMENT) + "\"/></td>");
    out.println("      </tr>");
    out.println("    </table>");
    out.println("  </form>");

    query = applicationSession.getDataSession().createQuery("from TestNote where testCase = ? order by noteDate");
    query.setParameter(0, testCase);
    List<TestNote> testNoteList = query.list();
    for (TestNote testNote : testNoteList) {
      if (!testNote.getNoteText().equals("")) {
        out.println("<p>" + testNote.getUser().getName() + " from " + testNote.getUser().getOrganization() + " writes on "
            + sdf.format(testNote.getNoteDate()) + ":</p>");
        out.println("<blockquote>");
        out.println(testNote.getNoteText());
        if (testNote.getUser().equals(user)) {
          String link = "testCases?" + PARAM_TEST_PANEL_CASE_ID + "=" + testPanelCase.getTestPanelCaseId() + "&" + PARAM_TEST_NOTE_ID + "="
              + testNote.getTestNoteId() + "&" + PARAM_TEST_NOTE_EDIT + "=true";
          out.println("<a class=\"add\" href=\"" + link + "\">Edit</a>");
        }
        out.println("</blockquote>");
      }
    }

    out.println("</div>");

    return testEventList;
  }

  public void printEventViewTable(PrintWriter out, TestCase testCase, List<TestEvent> testEventList, EventType eventType, String eventLabel) {
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
        out.println("      <td>" + (testEvent.getEventDate() == null ? "" : sdf.format(testEvent.getEventDate())) + "</td>");
        out.println("      <td>" + testEvent.getAgeAlmost(testCase) + "</td>");
        out.println("    </tr>");
      }
    }
    out.println("  </table>");
  }

  public void printTestPanel(PrintWriter out, Session dataSession, User user, TestPanel testPanel) throws UnsupportedEncodingException {
    final String link1 = "testCases?" + PARAM_ACTION + "=" + URLEncoder.encode(ACTION_SELECT_TEST_PANEL, "UTF-8") + "&" + PARAM_TEST_PANEL_ID + "="
        + testPanel.getTestPanelId();
    if (user.getSelectedTestPanel() != null && user.getSelectedTestPanel().equals(testPanel)) {
      out.println("      <li class=\"selectLevel1\"><a href=\"" + link1 + "\">" + testPanel.getLabel() + "</a>");
      out.println("        <ul class=\"selectLevel2\">");
      List<TestPanelCase> testPanelCaseList = getTestPanelCaseList(dataSession, testPanel);
      String lastCategoryName = "";
      boolean selectedCategoryOpened = false;
      Set<String> categoryHasProblemSet = null;
      boolean selectedTestPanelWasTested = applicationSession.getForecastCompareList() != null
          && applicationSession.getForecastCompareTestPanel().equals(user.getSelectedTestPanel());
      if (selectedTestPanelWasTested) {
        categoryHasProblemSet = applicationSession.getForecastCompareCategoryHasProblemSet();
      }
      List<TestPanelCase> testPanelCaseListExcluded = new ArrayList<TestPanelCase>();
      for (TestPanelCase testPanelCase : testPanelCaseList) {
        if (testPanelCase.getInclude() == Include.EXCLUDED) {
          testPanelCaseListExcluded.add(testPanelCase);
        } else {
          String categoryName = testPanelCase.getCategoryName().trim();
          if (!categoryName.equals(lastCategoryName)) {
            if (selectedCategoryOpened) {
              if (user.getSelectedExpert() != null && user.getSelectedExpert().getRole().canEdit()) {
                out.println("            <li><a class=\"add\" href=\"testCases?show=" + SHOW_ADD_TEST_CASE + "&" + PARAM_CATEGORY_NAME + "="
                    + lastCategoryName + "\">add test case</a></li>");
              }
              out.println("          </ul>");
              out.println("        </li>");
              selectedCategoryOpened = false;
            }
            final String link2 = "testCases?" + PARAM_ACTION + "=" + URLEncoder.encode(ACTION_SELECT_CATEGORY, "UTF-8") + "&" + PARAM_CATEGORY_NAME
                + "=" + categoryName;

            String classStyle = "selectLevel2";
            if (categoryHasProblemSet != null) {
              if (applicationSession.getForecastCompareCategoryNameSet() == null
                  || applicationSession.getForecastCompareCategoryNameSet().contains(categoryName)) {
                classStyle = categoryHasProblemSet.contains(categoryName) ? "selectLevelFail" : "selectLevelPass";
              }
            }
            if (user.getSelectedCategoryName() != null && categoryName.equals(user.getSelectedCategoryName())) {
              out.println("          <li class=\"" + classStyle + "\"><a href=\"" + link2 + "\">" + categoryName + "</a>");
              out.println("            <ul class=\"selectLevel3\">");
              selectedCategoryOpened = true;
            } else {
              out.println("          <li class=\"" + classStyle + "\"><a href=\"" + link2 + "\">" + categoryName + "</a></li>");
            }
          }

          if (user.getSelectedCategoryName() != null && categoryName.equals(user.getSelectedCategoryName())) {
            printTestCaseLine(out, user, selectedTestPanelWasTested, testPanelCase, categoryName);
          }
          lastCategoryName = categoryName;
        }
      }
      if (selectedCategoryOpened) {
        if (user.getSelectedExpert() != null && user.getSelectedExpert().getRole().canEdit()) {
          out.println("            <li><a class=\"add\" href=\"testCases?show=" + SHOW_ADD_TEST_CASE + "&" + PARAM_CATEGORY_NAME + "="
              + lastCategoryName + "\">add test case</a></li>");
        }
        out.println("          </ul>");
        out.println("        </li>");
        selectedCategoryOpened = false;
      }
      if (testPanelCaseListExcluded.size() > 0) {
        String link = "testCases?" + PARAM_SHOW + "=" + URLEncoder.encode(SHOW_TEST_CASE, "UTF-8");
        if (!user.isSelectedCategoryNameExcluded()) {
          link += "&" + PARAM_SHOW_EXCLUDED_TEST_CASES + "=true";
        }

        out.println("      <li class=\"selectLevel2\"><a href=\"" + link + "\"><em>Excluded</em></a>");
        if (user.isSelectedCategoryNameExcluded()) {
          out.println("        <ul class=\"selectLevel3\">");
          for (TestPanelCase testPanelCase : testPanelCaseListExcluded) {
            printTestCaseLine(out, user, false, testPanelCase, "");
          }
          out.println("          </ul>");
        }
        out.println("        </li>");
      }

      if (user.getSelectedCategoryName() == null) {
        if (user.getSelectedExpert() != null && user.getSelectedExpert().getRole().canEdit()) {
          out.println("          <li><a class=\"add\" href=\"testCases?show=" + SHOW_ADD_TEST_CASE + "\">add test case</a></li>");
        }
      }
      out.println("        </ul>");
      out.println("      </li>");
    } else {
      out.println("      <li class=\"selectLevel1\"><a href=\"" + link1 + "\">" + testPanel.getLabel() + "</a></li>");
    }
  }

  private List<TestPanelCase> getTestPanelCaseList(Session dataSession, TestPanel testPanel) {
    List<TestPanelCase> testPanelCaseList;
    {
      Query query;
      query = dataSession.createQuery("from TestPanelCase where testPanel = ? order by categoryName, testCase.label");
      query.setParameter(0, testPanel);
      testPanelCaseList = query.list();
    }
    return testPanelCaseList;
  }

  public void printTestCaseLine(PrintWriter out, User user, boolean selectedTestPanelWasTested, TestPanelCase testPanelCase, String categoryName)
      throws UnsupportedEncodingException {
    final String link3 = "testCases?" + PARAM_ACTION + "=" + URLEncoder.encode(ACTION_SELECT_TEST_PANEL_CASE, "UTF-8") + "&"
        + PARAM_TEST_PANEL_CASE_ID + "=" + testPanelCase.getTestPanelCaseId();
    String annotations = " ";
    String styleClass = "selectLevel3";
    if (selectedTestPanelWasTested) {
      if (applicationSession.getForecastCompareCategoryNameSet() == null
          || applicationSession.getForecastCompareCategoryNameSet().contains(categoryName)) {
        if (applicationSession.getForecastCompareTestPanelCaseHasProblemSet().contains(testPanelCase)) {
          styleClass = "selectLevelFail";
        } else {
          styleClass = "selectLevelPass";
        }
      }
    }
    if (user.isExpertOrAdmin(user.getSelectedTaskGroup())) {
      Result result = testPanelCase.getResult();
      if (result != null && result != Result.PASS) {
        if (result == Result.ACCEPT) {
          annotations += " <span class=\"passBox\">" + result.getLabel() + "</span>";
        } else {
          annotations += " <span class=\"failBox\">" + result.getLabel() + "</span>";
        }
      }
    }
    out.println("              <li class=\"" + styleClass + "\"><a href=\"" + link3 + "\">" + testPanelCase.getTestCase().getLabel() + "</a>"
        + annotations + "</li>");
  }

}
