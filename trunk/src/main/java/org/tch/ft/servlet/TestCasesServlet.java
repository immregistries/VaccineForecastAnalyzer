package org.tch.ft.servlet;

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

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.tch.fc.model.Admin;
import org.tch.fc.model.AssociatedDate;
import org.tch.fc.model.Consideration;
import org.tch.fc.model.ConsiderationGuidance;
import org.tch.fc.model.ConsiderationType;
import org.tch.fc.model.DateSet;
import org.tch.fc.model.Evaluation;
import org.tch.fc.model.Event;
import org.tch.fc.model.EventType;
import org.tch.fc.model.ForecastActual;
import org.tch.fc.model.Guidance;
import org.tch.fc.model.Rationale;
import org.tch.fc.model.RationaleGuidance;
import org.tch.fc.model.Recommend;
import org.tch.fc.model.RecommendGuidance;
import org.tch.fc.model.RecommendRange;
import org.tch.fc.model.RecommendType;
import org.tch.fc.model.RelativeRule;
import org.tch.fc.model.RelativeTo;
import org.tch.fc.model.Resource;
import org.tch.fc.model.ResourceGuidance;
import org.tch.fc.model.Software;
import org.tch.fc.model.TestCase;
import org.tch.fc.model.TestEvent;
import org.tch.fc.model.VaccineGroup;
import org.tch.fc.util.TimePeriod;
import org.tch.ft.manager.ExpectationsManager;
import org.tch.ft.manager.ForecastActualExpectedCompare;
import org.tch.ft.manager.RelativeRuleManager;
import org.tch.ft.manager.SoftwareManager;
import org.tch.ft.model.EvaluationExpected;
import org.tch.ft.model.Expert;
import org.tch.ft.model.ForecastExpected;
import org.tch.ft.model.GuidanceExpected;
import org.tch.ft.model.Include;
import org.tch.ft.model.Result;
import org.tch.ft.model.Role;
import org.tch.ft.model.TaskGroup;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.TestPanelCase;
import org.tch.ft.model.TestPanelEvaluation;
import org.tch.ft.model.TestPanelForecast;
import org.tch.ft.model.TestPanelGuidance;
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
  public static final String ACTION_ADD_EXPECTATIONS = "Add Expectations";
  public static final String ACTION_UPDATE_TEST_CASE = "Update Test Case";
  public static final String ACTION_COPY_TEST_CASE = "Copy Test Case";
  public static final String ACTION_DELETE_EVENT = "Delete Event";
  public static final String ACTION_ADD_VACCINATION = "Add Vaccination";
  public static final String ACTION_ADD_EVENT = "Add Event";
  public static final String ACTION_SAVE_EXPECTATIONS = "Save Expectations";
  public static final String ACTION_UPDATE_RELATIVE_DATES = "Update Relative Dates";

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

  public static final String RULE_BEFORE_OR_AFTER = "BeforeOrAfter";
  public static final String RULE_TEST_EVENT_ID = "TestEventId";

  public static final String SHOW_TEST_CASE = "testCase";
  public static final String SHOW_TASK_GROUP = "taskGroup";
  public static final String SHOW_TEST_PANEL = "testPanel";
  public static final String SHOW_EDIT_TEST_CASE = "editTestCase";
  public static final String SHOW_ADD_TEST_CASE = "addTestCase";
  public static final String SHOW_COPY_TEST_CASE = "copyTestCase";
  public static final String SHOW_PREVIEW_TEST_CASE = "previewTestCase";
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

  private static Admin[] ADMIN_STANDARD_LIST = { Admin.NOT_COMPLETE, Admin.COMPLETE, Admin.IMMUNE,
      Admin.CONTRAINDICATED, Admin.AGED_OUT };
  private static Admin[] ADMIN_NON_STANDARD_LIST = { Admin.OVERDUE, Admin.DUE, Admin.DUE_LATER, Admin.FINISHED,
      Admin.COMPLETE_FOR_SEASON, Admin.ASSUMED_COMPLETE_OR_IMMUNE, Admin.CONTRAINDICATED };

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
    User user = applicationSession.getUser();
    Session dataSession = applicationSession.getDataSession();
    switchToTestPanel(req, user, dataSession);
    if (show == null) {
      show = SHOW_TEST_CASE;
    }
    if (action != null) {
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
        applicationSession.getUser().setSelectedCategoryName(req.getParameter(PARAM_CATEGORY_NAME).trim());
        return SHOW_TEST_CASE;
      } else if (action.equals(ACTION_ADD_TEST_CASE) || action.equals(ACTION_UPDATE_TEST_CASE)
          || action.equals(ACTION_COPY_TEST_CASE)) {
        return saveTestCase(req, action, show, user, dataSession);
      } else if (action.equals(ACTION_DELETE_EVENT)) {
        int testEventId = notNull(req.getParameter(PARAM_TEST_EVENT_ID), 0);
        return doDelete(testEventId, show, dataSession);
      } else if (action.equals(ACTION_ADD_VACCINATION) || action.equals(ACTION_ADD_EVENT)) {
        return doAddEvent(req, action, show, user, dataSession);
      } else if (action.equals(ACTION_SAVE_EXPECTATIONS)) {
        return doSaveExpectations(req, user, dataSession);
      } else if (action.equals(ACTION_UPDATE_RELATIVE_DATES)) {
        if (user.getSelectedTaskGroup() != null && user.getSelectedTestPanel() != null
            && user.getSelectedTestPanelCase() != null) {
          TestCase testCase = user.getSelectedTestPanelCase().getTestCase();
          RelativeRuleManager.updateFixedDatesForRelativeRules(testCase, dataSession, true);
        }
      } else if (action.equals(ACTION_ADD_EXPECTATIONS)) {
        if (req.getParameter(PARAM_VACCINE_GROUP_ID).equals("")) {
          applicationSession.setAlertError("Unable to Add Expectation, please select vaccine group first.");
          return SHOW_TEST_CASE;
        }
        return SHOW_EDIT_EXPECTATIONS;
      }

    }
    return show;
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
    VaccineGroup vaccineGroup = (VaccineGroup) dataSession.get(VaccineGroup.class,
        notNull(req.getParameter(PARAM_VACCINE_GROUP_ID), 0));
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
        EvaluationExpected evaluationExpected = expectationsManager.getTestEventMapToEvaluationExpected().get(
            vaccinationEvent);
        evaluationExpected.setAuthor(user);
        evaluationExpected.setUpdatedDate(new Date());
        dataSession.saveOrUpdate(evaluationExpected);
        if (canSetTesPanelExpectations) {
          TestPanelEvaluation testPanelEvaluation = null;
          Query query = dataSession
              .createQuery("from TestPanelEvaluation where testPanelCase = ? and evaluationExpected.testCase = ? and evaluationExpected.vaccineGroup =  ? and evaluationExpected.testEvent = ?");
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
        forecastExpected.setValidDate(forecastExpected.getValidRule().calculateDate());
        saveRelativeRule(dataSession, forecastExpected.getValidRule());
      }
      if (forecastExpected.getDueRule() != null) {
        forecastExpected.setDueDate(forecastExpected.getDueRule().calculateDate());
        saveRelativeRule(dataSession, forecastExpected.getDueRule());
      }
      if (forecastExpected.getOverdueRule() != null) {
        forecastExpected.setOverdueDate(forecastExpected.getOverdueRule().calculateDate());
        saveRelativeRule(dataSession, forecastExpected.getOverdueRule());
      }
      if (forecastExpected.getFinishedRule() != null) {
        forecastExpected.setFinishedDate(forecastExpected.getFinishedRule().calculateDate());
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
          Query query = dataSession
              .createQuery("from TestPanelGuidance where guidanceExpected.testCase = ? and testPanelCase = ? and guidanceExpected.guidance.vaccineGroup = ?");
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
      return SHOW_TEST_CASE;
    }
  }

  public void readGuidanceExpectations(HttpServletRequest req, Session dataSession,
      ExpectationsManager expectationsManager) {

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

  public String readForecastExpectations(HttpServletRequest req, Session dataSession, TestCase testCase,
      ExpectationsManager expectationsManager) {
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
        String overdueDateString = notNull(req.getParameter(PARAM_OVERDUE_DATE),
            forecastExpected.getOverdueDateString());
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
        String finishedDateString = notNull(req.getParameter(PARAM_FINISHED_DATE),
            forecastExpected.getFinishedDateString());
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
      forecastExpected.setValidRule(updateRelativeRule(PARAM_VALID_RULE, req, dataSession, testCase,
          forecastExpected.getValidRule()));
      forecastExpected.setDueRule(updateRelativeRule(PARAM_DUE_RULE, req, dataSession, testCase,
          forecastExpected.getDueRule()));
      forecastExpected.setOverdueRule(updateRelativeRule(PARAM_OVERDUE_RULE, req, dataSession, testCase,
          forecastExpected.getOverdueRule()));
      forecastExpected.setFinishedRule(updateRelativeRule(PARAM_FINISHED_RULE, req, dataSession, testCase,
          forecastExpected.getFinishedRule()));

    }
    return null;
  }

  public void readEvaluationExpectations(HttpServletRequest req, ExpectationsManager expectationsManager) {
    for (TestEvent vaccinationEvent : expectationsManager.getVaccinationEvents()) {
      EvaluationExpected evaluationExpected = expectationsManager.getTestEventMapToEvaluationExpected().get(
          vaccinationEvent);
      String evaluationStatus = notNull(req.getParameter(PARAM_EVALUATION_STATUS + vaccinationEvent.getTestEventId()),
          evaluationExpected.getEvaluationStatus());
      if (evaluationStatus == null || evaluationStatus.equals("")) {
        evaluationExpected.setEvaluation(null);
      } else {
        evaluationExpected.setEvaluationStatus(evaluationStatus);
      }
    }

  }

  public RelativeRule updateRelativeRule(String paramRuleName, HttpServletRequest req, Session dataSession,
      TestCase testCase, RelativeRule rrOld) {
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
        applicationSession
            .setAlertError("If you would like to add a new ACIP-Defined Condition please review the list and select 'none of these, proposing a new label' ");
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
      RelativeRule relativeRule = readRelativeRules(PARAM_EVENT_RULE, req, dataSession, testCase);
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

  public String saveTestCase(HttpServletRequest req, String action, String show, User user, Session dataSession) {
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
      if (label.equals(user.getSelectedTestCase().getLabel())
          && categoryName.equals(user.getSelectedTestCase().getCategoryName())) {
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

      if (action.equals(ACTION_UPDATE_TEST_CASE)) {
        if (testCase.getEvalRule() != null) {
          dataSession.saveOrUpdate(testCase.getEvalRule());
        }
        dataSession.update(testCase);
        dataSession.update(testPanelCase);
      } else if (action.equals(ACTION_COPY_TEST_CASE) || action.equals(ACTION_ADD_TEST_CASE)) {
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
        // RelativeRuleManager.updateFixedDatesForRelativeRules(testCase, dataSession, false);
        printTestCase(out, user);
        printActualsVsExpected(out, user);
      }
    } else if (SHOW_PREVIEW_TEST_CASE.equals(show)) {
      if (user.getSelectedTaskGroup() != null && user.getSelectedTestPanel() != null
          && user.getSelectedTestPanelCase() != null) {
        VaccineGroup vaccineGroup = (VaccineGroup) dataSession.get(VaccineGroup.class,
            Integer.parseInt(req.getParameter(PARAM_VACCINE_GROUP_ID)));
        TestCase testCase = user.getSelectedTestPanelCase().getTestCase();
        RelativeRuleManager.updateFixedDatesForRelativeRules(testCase, dataSession, false);
        printTestCase(out, user);
        printPreview(out, user, vaccineGroup);
      }
    } else if (SHOW_ADD_TEST_CASE.equals(show)) {
      printAddEditTestCases(req, out, dataSession, null, show);
    } else if (SHOW_EDIT_TEST_CASE.equals(show)) {
      printAddEditTestCases(req, out, dataSession, user.getSelectedTestPanelCase(), show);
    } else if (SHOW_COPY_TEST_CASE.equals(show)) {
      printAddEditTestCases(req, out, dataSession, user.getSelectedTestPanelCase(), show);
    } else if (SHOW_TASK_GROUP.equals(show)) {
      printTaskGroup(out, dataSession, user);
    } else if (SHOW_EDIT_VACCINATIONS.equals(show)) {
      printEditEvents(req, out, dataSession, user, EventType.VACCINATION, show);
    } else if (SHOW_EDIT_EVENTS.equals(show)) {
      EventType eventType = EventType.getEventType(req.getParameter(PARAM_EVENT_TYPE_CODE));
      printEditEvents(req, out, dataSession, user, eventType, show);
    } else if (SHOW_EDIT_EXPECTATIONS.equals(show)) {
      VaccineGroup vaccineGroup = (VaccineGroup) dataSession.get(VaccineGroup.class,
          notNull(req.getParameter(PARAM_VACCINE_GROUP_ID), 0));
      printEditExpectations(req, out, dataSession, user, vaccineGroup);
    }

  }

  private void printEditExpectations(HttpServletRequest req, PrintWriter out, Session dataSession, User user,
      VaccineGroup vaccineGroup) {
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
    out.println("    <a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW + "=" + SHOW_TEST_CASE + "&"
        + PARAM_TEST_PANEL_CASE_ID + "=" + user.getSelectedTestPanelCase().getTestPanelCaseId() + "\">Back</a>");
    out.println("  </h2>");

    setupTestEventList(user);

    out.println("  <form method=\"POST\" action=\"testCases\">");
    out.println("    <input type=\"hidden\" name=\"" + PARAM_VACCINE_GROUP_ID + "\" value=\""
        + vaccineGroup.getVaccineGroupId() + "\"/>");
    out.println("    <input type=\"hidden\" name=\"" + PARAM_TEST_PANEL_CASE_ID + "\" value=\""
        + testPanelCase.getTestPanelCaseId() + "\"/>");
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
        out.println("      <td>"
            + (vaccinationEvent.getEventDate() == null ? "" : sdf.format(vaccinationEvent.getEventDate())) + "</td>");
        out.println("      <td>");
        EvaluationExpected evaluationExpected = expectationsManager.getTestEventMapToEvaluationExpected().get(
            vaccinationEvent);
        out.println("          <select name=\"" + PARAM_EVALUATION_STATUS + vaccinationEvent.getTestEventId() + "\">");
        out.println("            <option value=\"0\">--select--</option>");
        for (Evaluation evaluation : Evaluation.values()) {
          if (evaluationExpected != null && evaluationExpected.getEvaluation() == evaluation) {
            out.println("            <option value=\"" + evaluation.getEvaluationStatus() + "\" selected=\"selected\">"
                + evaluation.getLabel() + "</option>");
          } else {
            out.println("            <option value=\"" + evaluation.getEvaluationStatus() + "\">"
                + evaluation.getLabel() + "</option>");
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
    out.println("      <td><input type=\"text\" name=\"" + PARAM_DOSE_NUMBER + "\" value=\""
        + forecastExpected.getDoseNumber() + "\" size=\"5\"/></td>");
    out.println("    </tr>");
    if (testCase.getDateSet() == DateSet.FIXED) {
      out.println("    <tr>");
      out.println("      <th>Earliest Date</th>");
      out.println("      <td><input type=\"text\" name=\"" + PARAM_VALID_DATE + "\" value=\""
          + forecastExpected.getValidDateString() + "\" size=\"10\"/></td>");
      out.println("    </tr>");
      out.println("    <tr>");
      out.println("      <th>Recommended Date</th>");
      out.println("      <td><input type=\"text\" name=\"" + PARAM_DUE_DATE + "\" value=\""
          + forecastExpected.getDueDateString() + "\" size=\"10\"/></td>");
      out.println("    </tr>");
      out.println("    <tr>");
      out.println("      <th>Past Due Date</th>");
      out.println("      <td><input type=\"text\" name=\"" + PARAM_OVERDUE_DATE + "\" value=\""
          + forecastExpected.getOverdueDateString() + "\" size=\"10\"/></td>");
      out.println("    </tr>");
      out.println("    <tr>");
      out.println("      <th>Latest Date</th>");
      out.println("      <td><input type=\"text\" name=\"" + PARAM_OVERDUE_DATE + "\" value=\""
          + forecastExpected.getFinishedDateString() + "\" size=\"10\"/></td>");
      out.println("    </tr>");
    } else {
      printRelativeRuleRow(PARAM_VALID_RULE, out, forecastExpected.getValidRule(), "Earliest Date", null);
      printRelativeRuleRow(PARAM_DUE_RULE, out, forecastExpected.getDueRule(), "Recommended Date", null);
      printRelativeRuleRow(PARAM_OVERDUE_RULE, out, forecastExpected.getOverdueRule(), "Past Due Date", null);
      printRelativeRuleRow(PARAM_FINISHED_RULE, out, forecastExpected.getFinishedRule(), "Finished Date", null);
    }
    out.println("        <tr>");
    out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION
        + "\" size=\"15\" value=\"" + ACTION_SAVE_EXPECTATIONS + "\"/></td>");
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
              + recommendGuidance.getRecommend().getRecommendId() + "\" checked=\"checked\">"
              + recommendGuidance.getRecommend() + "<br/>");
          recommendSet.add(recommendGuidance.getRecommend());
        }
        out.println("<b>Select Additional</b><br/>");
      }
      Query query = dataSession.createQuery("from Recommend order by recommendText");
      List<Recommend> recommendList = query.list();
      for (Recommend recommend : recommendList) {
        if (!recommendSet.contains(recommend)) {
          out.println("          <input type=\"checkbox\" name=\"" + PARAM_RECOMMEND_ID + "\" value=\""
              + recommend.getRecommendId() + "\">" + recommend + "<br/>");
        }
      }
      out.println("          <input type=\"checkbox\" name=\""
          + PARAM_RECOMMEND_ID
          + ""
          + "\" value=\"-1\" onChange=\"showRow('recommendNewValue1');showRow('recommendNewValue2');showRow('recommendNewValue3');\">propose a new value<br/>");
      out.println("        </div></td>");
      out.println("      </tr>");
      out.println("      <tr id=\"recommendNewValue1\" style=\"display: none;\">");
      out.println("        <th>New Text</th>");
      out.println("        <td>");
      out.println("          <textarea name=\"" + PARAM_RECOMMEND_TEXT
          + "\" value=\"\" cols=\"60\" rows=\"3\"></textarea>");
      out.println("        </td>");
      out.println("      </tr>");
      out.println("      <tr id=\"recommendNewValue2\" style=\"display: none;\">");
      out.println("        <th>Type</th>");
      out.println("        <td>");
      out.println("          <select name=\"" + PARAM_RECOMMEND_TYPE_CODE + "\" >");
      out.println("            <option value=\"0\">--select--</option>");
      for (RecommendType recommendType : RecommendType.values()) {
        out.println("            <option value=\"" + recommendType.getRecommendTypeCode() + "\">"
            + recommendType.getLabel() + "</option>");
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
        out.println("            <option value=\"" + recommendRange.getRecommendRangeCode() + "\">"
            + recommendRange.getLabel() + "</option>");
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
              + considerationGuidance.getConsideration().getConsiderationId() + "\" checked=\"checked\">"
              + considerationGuidance.getConsideration() + "<br/>");
          considerationSet.add(considerationGuidance.getConsideration());
        }
        out.println("<b>Select Additional</b><br/>");
      }
      Query query = dataSession.createQuery("from Consideration order by considerationText");
      List<Consideration> considerationList = query.list();
      for (Consideration consideration : considerationList) {
        if (!considerationSet.contains(consideration)) {
          out.println("          <input type=\"checkbox\" name=\"" + PARAM_CONSIDERATION_ID + "\" value=\""
              + consideration.getConsiderationId() + "\">" + consideration + "<br/>");
        }
      }
      out.println("          <input type=\"checkbox\" name=\"" + PARAM_CONSIDERATION_ID + ""
          + "\" value=\"-1\" onChange=\"showRow('considerationNewValue1');\">propose a new value<br/>");
      out.println("        </div></td>");
      out.println("      </tr>");
      out.println("      <tr id=\"considerationNewValue1\" style=\"display: none;\">");
      out.println("        <th>New Text</th>");
      out.println("        <td>");
      out.println("          <textarea name=\"" + PARAM_CONSIDERATION_TEXT
          + "\" value=\"\" cols=\"60\" rows=\"3\"></textarea>");
      out.println("        </td>");
      out.println("      </tr>");
      out.println("      <tr id=\"considerationNewValue2\" style=\"display: none;\">");
      out.println("        <th>Type</th>");
      out.println("        <td>");
      out.println("          <select name=\"" + PARAM_CONSIDERATION_TYPE_CODE + "\" >");
      out.println("            <option value=\"0\">--select--</option>");
      for (ConsiderationType considerationType : ConsiderationType.values()) {
        out.println("            <option value=\"" + considerationType.getConsiderationTypeCode() + "\">"
            + considerationType.getLabel() + "</option>");
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
          out.println("          <input type=\"checkbox\" name=\"" + PARAM_RATIONALE_ID + "\" value=\""
              + rationaleGuidance.getRationale().getRationaleId() + "\" checked=\"checked\">"
              + rationaleGuidance.getRationale().getRationaleText() + "<br/>");
          rationaleSet.add(rationaleGuidance.getRationale());
        }
        out.println("<b>Select Additional</b><br/>");
      }
      Query query = dataSession.createQuery("from Rationale order by rationaleText");
      List<Rationale> rationaleList = query.list();
      for (Rationale rationale : rationaleList) {
        if (!rationaleSet.contains(rationale)) {
          out.println("          <input type=\"checkbox\" name=\"" + PARAM_RATIONALE_ID + "\" value=\""
              + rationale.getRationaleId() + "\">" + rationale.getRationaleText() + "<br/>");
        }
      }
      out.println("          <input type=\"checkbox\" name=\"" + PARAM_RATIONALE_ID + ""
          + "\" value=\"-1\" onChange=\"showRow('rationaleNewValue1');\">propose a new value<br/>");
      out.println("        </div></td>");
      out.println("      </tr>");
      out.println("      <tr id=\"rationaleNewValue1\" style=\"display: none;\">");
      out.println("        <th>New Text</th>");
      out.println("        <td>");
      out.println("          <textarea name=\"" + PARAM_RATIONALE_TEXT
          + "\" value=\"\" cols=\"60\" rows=\"3\"></textarea>");
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
              + resourceGuidance.getResource().getResourceId() + "\" checked=\"checked\">"
              + resourceGuidance.getResource().getResourceText() + " - <a href=\""
              + resourceGuidance.getResource().getResourceLink() + "\" target=\"_blank\">"
              + resourceGuidance.getResource().getResourceLink() + "</a><br/>");
          resourceSet.add(resourceGuidance.getResource());
        }
        out.println("<b>Select Additional</b><br/>");
      }
      Query query = dataSession.createQuery("from Resource order by resourceText");
      List<Resource> resourceList = query.list();
      for (Resource resource : resourceList) {
        if (!resourceSet.contains(resource)) {
          out.println("          <input type=\"checkbox\" name=\"" + PARAM_RESOURCE_ID + "\" value=\""
              + resource.getResourceId() + "\">" + resource.getResourceText() + " - <a href=\""
              + resource.getResourceLink() + "\" target=\"_blank\">" + resource.getResourceLink() + "</a><br/>");
        }
      }
      out.println("          <input type=\"checkbox\" name=\""
          + PARAM_RESOURCE_ID
          + ""
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
    out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION
        + "\" size=\"15\" value=\"" + ACTION_SAVE_EXPECTATIONS + "\"/></td>");
    out.println("        </tr>");
    out.println("  </table>");
    out.println("  </form>");
  }

  public void printAdminSelect(PrintWriter out, String adminStatus, Admin admin) {
    if (adminStatus == admin.getAdminStatus()) {
      out.println("            <option value=\"" + admin.getAdminStatus() + "\" selected=\"selected\">"
          + admin.getLabel() + "</option>");
    } else {
      out.println("            <option value=\"" + admin.getAdminStatus() + "\">" + admin.getLabel() + "</option>");
    }
  }

  public void printEditEvents(HttpServletRequest req, PrintWriter out, Session dataSession, User user,
      EventType eventType, String show) throws UnsupportedEncodingException {
    printShowRowScript(out);

    setupTestEventList(user);

    TestCase testCase = user.getSelectedTestCase();
    out.println("<div class=\"centerColumn\">");
    out.println("  <h2>Test Context");
    out.println("    <a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW + "=" + SHOW_TEST_CASE + "&"
        + PARAM_TEST_PANEL_CASE_ID + "=" + user.getSelectedTestPanelCase().getTestPanelCaseId() + "\">Back</a>");
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
        String label = "Administered";
        printRelativeRuleRow(PARAM_EVENT_RULE, out, relativeRule, label, RelativeTo.BIRTH);
      }
      out.println("        <tr>");
      out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION
          + "\" size=\"15\" value=\"" + ACTION_ADD_VACCINATION + "\"/></td>");
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
        String label = "Observed";
        printRelativeRuleRow(PARAM_EVENT_RULE, out, relativeRule, label, RelativeTo.EVALUATION);
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
        String label = "Asserted";
        printRelativeRuleRow(PARAM_EVENT_RULE, out, relativeRule, label, RelativeTo.EVALUATION);
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

  public void printRelativeRuleRow(String paramRuleName, PrintWriter out, RelativeRule relativeRule, String label,
      RelativeTo relativeTo) {
    RelativeRule rr = relativeRule;
    boolean showRow = true;
    for (int pos = 1; pos <= 4; pos++) {
      printRelativeRuleRow(paramRuleName, out, testEventList, rr, pos, label, (pos == 1 ? relativeTo : null), showRow);
      showRow = false;
      if (rr != null) {
        showRow = rr.getTestEvent() != null || rr.getRelativeTo() == RelativeTo.BIRTH
            || rr.getRelativeTo() == RelativeTo.EVALUATION;
        rr = rr.getAndRule();
      }
    }
  }

  public void printEventTable(PrintWriter out, User user, EventType eventType, List<TestEvent> testEventList,
      String sectionLabel, String itemLabel, String show) throws UnsupportedEncodingException {
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

  public RelativeRule readRelativeRules(String paramRuleName, HttpServletRequest req, Session dataSession,
      TestCase testCase) {
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
      childRule.setBeforeOrAfter(beforeOrAfter.equals("B") ? RelativeRule.BeforeOrAfter.BEFORE
          : RelativeRule.BeforeOrAfter.AFTER);
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

  public void printRelativeRuleRow(String paramRuleName, PrintWriter out, List<TestEvent> testEventList,
      RelativeRule relativeRule, int pos, String label, RelativeTo relativeTo, boolean showRow) {

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
    out.println("            <input type=\"radio\" name=\""
        + paramRuleName
        + RULE_BEFORE_OR_AFTER
        + pos
        + "\" value=\"B\""
        + ((relativeRule != null && relativeRule.getBeforeOrAfter() == RelativeRule.BeforeOrAfter.BEFORE) ? " checked=\"true\""
            : "") + "/> Before ");
    out.println("            <input type=\"radio\" name=\""
        + paramRuleName
        + RULE_BEFORE_OR_AFTER
        + pos
        + "\" value=\"O\""
        + ((relativeRule == null || (relativeRule.isZero() && relativeRule.getBeforeOrAfter() == RelativeRule.BeforeOrAfter.AFTER)) ? " checked=\"true\""
            : "") + "/> On ");
    out.println("            <input type=\"radio\" name=\""
        + paramRuleName
        + RULE_BEFORE_OR_AFTER
        + pos
        + "\" value=\"A\""
        + ((relativeRule != null && !relativeRule.isZero() && relativeRule.getBeforeOrAfter() == RelativeRule.BeforeOrAfter.AFTER) ? " checked=\"true\""
            : "") + "/> After ");
    out.println("          <select name=\"" + paramRuleName + RULE_TEST_EVENT_ID + pos + "\" onChange=\"showRow('"
        + label + "." + (pos + 1) + "')\">");
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
            } else if (relativeTo == RelativeTo.EVALUATION
                && testEvent.getEvent().getEventType() == EventType.EVALUATION) {
              selected = true;
            }
          }
        } else {
          if (relativeRule.getTestEvent() != null) {
            selected = relativeRule.getTestEvent().equals(testEvent);
          }
        }
      }
      if (selected) {
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
      TestPanelCase testPanelCase, String show) {
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

    if (show.equals(SHOW_EDIT_TEST_CASE)) {
      label = notNull(req.getParameter(PARAM_LABEL), testCase.getLabel());
      description = notNull(req.getParameter(PARAM_DESCRIPTION), testCase.getDescription());
      vaccineGroupId = notNull(req.getParameter(PARAM_VACCINE_GROUP_ID), (testCase.getVaccineGroup() == null ? 0
          : testCase.getVaccineGroup().getVaccineGroupId()));
      patientFirst = notNull(req.getParameter(PARAM_PATIENT_FIRST), testCase.getPatientFirst());
      patientLast = notNull(req.getParameter(PARAM_PATIENT_LAST), testCase.getPatientLast());
      patientSex = notNull(req.getParameter(PARAM_PATIENT_SEX), testCase.getPatientSex());
      patientDob = notNull(req.getParameter(PARAM_PATIENT_DOB), sdf.format(testCase.getPatientDob()));
      categoryName = notNull(req.getParameter(PARAM_CATEGORY_NAME), testPanelCase.getCategoryName()).trim();
      testCaseNumber = notNull(req.getParameter(PARAM_TEST_CASE_NUMBER), testPanelCase.getTestCaseNumber());
      dateSetCode = notNull(req.getParameter(PARAM_DATE_SET_CODE), testCase.getDateSetCode());
      evalDate = notNull(req.getParameter(PARAM_EVAL_DATE), sdf.format(testCase.getEvalDate()));
      evalRule = notNull(req.getParameter(PARAM_EVAL_RULE), testCase.getEvalRule() != null ? testCase.getEvalRule()
          .getTimePeriodString() : "");
    } else if (show.equals(SHOW_COPY_TEST_CASE)) {
      label = notNull(req.getParameter(PARAM_LABEL), testCase.getLabel());
      description = notNull(req.getParameter(PARAM_DESCRIPTION), testCase.getDescription());
      vaccineGroupId = notNull(req.getParameter(PARAM_VACCINE_GROUP_ID), (testCase.getVaccineGroup() == null ? 0
          : testCase.getVaccineGroup().getVaccineGroupId()));
      patientFirst = notNull(req.getParameter(PARAM_PATIENT_FIRST), RandomNames.getRandomFirstName());
      patientLast = notNull(req.getParameter(PARAM_PATIENT_LAST), RandomNames.getRandomLastName());
      patientSex = notNull(req.getParameter(PARAM_PATIENT_SEX), testCase.getPatientSex());
      patientDob = notNull(req.getParameter(PARAM_PATIENT_DOB), sdf.format(testCase.getPatientDob()));
      categoryName = notNull(req.getParameter(PARAM_CATEGORY_NAME), testPanelCase.getCategoryName()).trim();
      testCaseNumber = notNull(req.getParameter(PARAM_TEST_CASE_NUMBER));
      dateSetCode = notNull(req.getParameter(PARAM_DATE_SET_CODE), testCase.getDateSetCode());
      evalDate = notNull(req.getParameter(PARAM_EVAL_DATE), sdf.format(testCase.getEvalDate()));
      evalRule = notNull(req.getParameter(PARAM_EVAL_RULE), testCase.getEvalRule() != null ? testCase.getEvalRule()
          .getTimePeriodString() : "");
    } else {
      label = notNull(req.getParameter(PARAM_LABEL));
      description = notNull(req.getParameter(PARAM_DESCRIPTION));
      vaccineGroupId = notNull(req.getParameter(PARAM_VACCINE_GROUP_ID), 0);
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
    String cancelButton = "";
    if (testPanelCase != null) {
      cancelButton = " <a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW + "=" + SHOW_TEST_CASE + "&"
          + PARAM_TEST_PANEL_CASE_ID + "=" + testPanelCase.getTestPanelCaseId() + "\">Back</a>";
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
      out.println("      <input type=\"hidden\" name=\"" + PARAM_TEST_PANEL_CASE_ID + "\" value=\""
          + testPanelCase.getTestPanelCaseId() + "\"/>");
    }
    out.println("      <table width=\"100%\">");
    out.println("        <tr>");
    out.println("          <th>Category</th>");
    out.println("          <td><input type=\"text\" name=\"" + PARAM_CATEGORY_NAME + "\" size=\"50\" value=\""
        + categoryName + "\"/></td>");
    out.println("        </tr>");
    if (testPanelCase == null || applicationSession.getUser().isCanEditTestCase()) {
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
    if (testPanelCase == null || applicationSession.getUser().isCanEditTestCase()) {

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
      out.println("          <th>Assessment Date</th>");
      out.println("          <td><input type=\"text\" name=\"" + PARAM_EVAL_DATE + "\" size=\"10\" value=\"" + evalDate
          + "\"/></td>");
      out.println("        </tr>");
    }
    out.println("        <tr>");
    if (show.equals(SHOW_ADD_TEST_CASE)) {
      out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION
          + "\" size=\"15\" value=\"" + ACTION_ADD_TEST_CASE + "\"/></td>");
    } else if (show.equals(SHOW_EDIT_TEST_CASE)) {
      out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION
          + "\" size=\"15\" value=\"" + ACTION_UPDATE_TEST_CASE + "\"/></td>");
    } else if (show.equals(SHOW_COPY_TEST_CASE)) {
      out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION
          + "\" size=\"15\" value=\"" + ACTION_COPY_TEST_CASE + "\"/></td>");
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

      out.println("<h3>All Expert Groups</h3>");
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

    HashMap<VaccineGroup, ForecastActualExpectedCompare> forecastCompareMap = new HashMap<VaccineGroup, ForecastActualExpectedCompare>();
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
          forecastCompareMap.put(forecastCompare.getVaccineGroup(), forecastCompare);
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

    Set<VaccineGroup> vaccineGroupDisplayedSet = new HashSet<VaccineGroup>();
    VaccineGroup vaccineGroup = testCase.getVaccineGroup();
    if (vaccineGroup != null) {
      vaccineGroupDisplayedSet.add(vaccineGroup);
      out.println("<h2>Actual vs Expected for " + vaccineGroup.getLabel()
          + " <a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW + "=" + SHOW_EDIT_EXPECTATIONS + "&"
          + PARAM_TEST_PANEL_ID + "=" + testPanel.getTestPanelId() + "&" + PARAM_VACCINE_GROUP_ID + "="
          + vaccineGroup.getVaccineGroupId() + "\">Edit</a><a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW
          + "=" + SHOW_PREVIEW_TEST_CASE + "&" + PARAM_TEST_PANEL_ID + "=" + testPanel.getTestPanelId() + "&"
          + PARAM_VACCINE_GROUP_ID + "=" + vaccineGroup.getVaccineGroupId() + "\">Preview</a></h2>");
      printEvaluationCompare(out, dataSession, testCase, vaccineGroup);
      printForecastCompare(out, user, dataSession, testCase, forecastCompareMap, vaccineGroup);
      printGuidanceCompare(out, dataSession, testCase, vaccineGroup);
    }

    if (forecastCompareMap.size() > 0) {
      for (VaccineGroup vg : forecastCompareMap.keySet()) {
        if (vaccineGroup != null && !vg.equals(vaccineGroup)) {
          vaccineGroupDisplayedSet.add(vg);
          out.println("<h2>Actual vs Expected for " + vg.getLabel() + " <a class=\"fauxbutton\" href=\"testCases?"
              + PARAM_SHOW + "=" + SHOW_EDIT_EXPECTATIONS + "&" + PARAM_TEST_PANEL_ID + "="
              + testPanel.getTestPanelId() + "&" + PARAM_VACCINE_GROUP_ID + "=" + vg.getVaccineGroupId()
              + "\">Edit</a><a class=\"fauxbutton\" href=\"testCases?" + PARAM_SHOW + "=" + SHOW_PREVIEW_TEST_CASE
              + "&" + PARAM_TEST_PANEL_ID + "=" + testPanel.getTestPanelId() + "&" + PARAM_VACCINE_GROUP_ID + "="
              + vg.getVaccineGroupId() + "\">Preview</a></h2>");

          printEvaluationCompare(out, dataSession, testCase, vg);
          printForecastCompare(out, user, dataSession, testCase, forecastCompareMap, vg);
          printGuidanceCompare(out, dataSession, testCase, vg);
        }
      }
    }

    out.println("<h2>Add Actual vs Expected</h2>");
    out.println("  <form method=\"POST\" action=\"testCases\">");
    out.println("    <input type=\"hidden\" name=\"" + PARAM_TEST_PANEL_CASE_ID + "\" value=\""
        + testPanelCase.getTestPanelCaseId() + "\"/>");
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
    out.println("            </select");
    out.println("          </td>");
    out.println("        </tr>");
    out.println("        <tr>");
    out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION
        + "\" size=\"15\" value=\"" + ACTION_ADD_EXPECTATIONS + "\"/></td>");
    out.println("        </tr>");
    out.println("  </table>");
    out.println("  </form>");

    out.println("</div>");
  }

  private void printGuidanceCompare(PrintWriter out, Session dataSession, TestCase testCase, VaccineGroup vaccineGroup) {
    Query query = dataSession
        .createQuery("from GuidanceExpected where testCase = ? and guidance.vaccineGroup = ? order by updatedDate desc");
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
        query = dataSession
            .createQuery("from RecommendGuidance where guidance = ? order by recommend.recommendTypeCode, recommend.recommendText");
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
        query = dataSession
            .createQuery("from ConsiderationGuidance where guidance = ? order by consideration.considerationText");
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
            out.println("     <li>" + resourceGuidance.getResource().getResourceText() + " - <a href=\""
                + resourceGuidance.getResource().getResourceLink() + "\" target=\"_blank\">"
                + resourceGuidance.getResource().getResourceLink() + "</a></li>");
          }
          out.println("     </ul>");
        }
      }
      out.println("");
      out.println("    </td>");
      out.println("  </tr>");
      out.println("</table>");
    }
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
          forecastCompareMap.put(forecastCompare.getVaccineGroup(), forecastCompare);
        }
      }
    }

    out.println("<h2>Preview for " + vaccineGroup.getLabel() + " <a class=\"fauxbutton\" href=\"testCases?"
        + PARAM_SHOW + "=" + SHOW_TEST_CASE + "&" + PARAM_TEST_PANEL_ID + "=" + testPanel.getTestPanelId()
        + "\">Back</a></h2>");

    out.println("<div class=\"preview\">");
    out.println("  <h2 class=\"previewHeading\">Patient</h2>");
    out.println("  <table>");
    out.println("    <tr>");
    out.println("      <th>Patient</th>");
    out.println("      <td>" + testCase.getPatientFirst() + " " + testCase.getPatientLast() + " ("
        + testCase.getPatientSex() + ")</td>");
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
          out.println("      <td>" + (testEvent.getEventDate() == null ? "" : sdf.format(testEvent.getEventDate()))
              + "</td>");
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

      String expectedAdmin = (forecastExpected.getAdmin() == null ? Admin.UNKNOWN : forecastExpected.getAdmin())
          .getLabel();
      String expectedDoseNumber = forecastExpected.getDoseNumber() != null ? forecastExpected.getDoseNumber() : "-";
      String expectedValidDate = forecastExpected.getValidDate() != null ? sdf.format(forecastExpected.getValidDate())
          : "-";
      String expectedDueDate = forecastExpected.getDueDate() != null ? sdf.format(forecastExpected.getDueDate()) : "-";
      String expectedOverdueDate = forecastExpected.getOverdueDate() != null ? sdf.format(forecastExpected
          .getOverdueDate()) : "-";
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
      query = dataSession
          .createQuery("from RecommendGuidance where guidance = ? order by recommend.recommendTypeCode, recommend.recommendText");
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
      query = dataSession
          .createQuery("from ConsiderationGuidance where guidance = ? order by consideration.considerationText");
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
          out.println("     <li>" + resourceGuidance.getResource().getResourceText() + " - <a href=\""
              + resourceGuidance.getResource().getResourceLink() + "\" target=\"_blank\">"
              + resourceGuidance.getResource().getResourceLink() + "</a></li>");
        }
        out.println("     </ul>");
      }
    }

    out.println("</div>");
    out.println("</div>");
  }

  public void printEventPreview(PrintWriter out, TestCase testCase, List<TestEvent> testEventList, EventType eventType,
      String eventLabel) {
    out.println("<h3 class=\"previewHeading\">" + eventLabel + "</h3>");
    out.println("<ul>");
    int screenId = 0;
    for (TestEvent testEvent : testEventList) {
      if (testEvent.getEvent().getEventType() == eventType) {
        screenId++;
        testEvent.setScreenId(screenId);
        if (testEvent.getEventDate().equals(testCase.getEvalDate())) {
          out.println("  <li>" + testEvent.getEvent().getLabel() + "</li>");
        } else {
          out.println("  <li>" + testEvent.getEvent().getLabel() + " on " + sdf.format(testEvent.getEventDate())
              + "</li>");
        }
      }
    }
    out.println("</ul>");
  }

  private void printEvaluationCompare(PrintWriter out, Session dataSession, TestCase testCase, VaccineGroup vaccineGroup) {
    if (countVaccination > 0) {
      Map<User, Map<TestEvent, EvaluationExpected>> expectedMap = new HashMap<User, Map<TestEvent, EvaluationExpected>>();
      List<User> authorList = new ArrayList<User>();

      Query query = dataSession
          .createQuery("from EvaluationExpected where testCase = ? and vaccineGroup = ? order by updatedDate desc");
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
      }
    }
  }

  private void printForecastCompare(PrintWriter out, User user, Session dataSession, TestCase testCase,
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
    }
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

    String styleClassLabel = "";
    String styleClassAdmin = "";
    String styleClassDoseNumber = "";
    String styleClassValid = "";
    String styleClassDue = "";
    String styleClassOverdue = "";

    if (forecastActual != null) {
      styleClassLabel = forecastCompare.matchExactly() ? "pass" : "fail";
      styleClassAdmin = (hasActual && expectedAdmin != null && actualAdmin != null && expectedAdmin.equals(actualAdmin)) ? "pass"
          : "fail";
      styleClassDoseNumber = hasActual && compareDoseNumbers(expectedDoseNumber, actualDoseNumber) ? "pass" : "fail";
      styleClassValid = hasActual && compareDoseNumbers(expectedValidDate, actualValidDate) ? "pass" : "fail";
      styleClassDue = hasActual && compareDoseNumbers(expectedDueDate, actualDueDate) ? "pass" : "fail";
      styleClassOverdue = hasActual && compareDoseNumbers(expectedOverdueDate, actualOverdueDate) ? "pass" : "fail";
    }

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

    if (forecastActual != null) {
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
    out.println("      <th>Assessment Date</th>");
    out.println("      <td>" + sdf.format(testCase.getEvalDate()) + "</td>");
    out.println("    </tr>");
    out.println("  </table>");

    countVaccination = 0;
    countACIP = 0;
    countCondition = 0;
    countOther = 0;
    Query query = applicationSession.getDataSession().createQuery(
        "from TestEvent where testCase = ? order by eventDate");
    query.setParameter(0, testCase);
    testEventList = query.list();
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

    editLink = "testCases?" + PARAM_SHOW + "=" + SHOW_EDIT_VACCINATIONS + "&" + PARAM_TEST_PANEL_CASE_ID + "="
        + testPanelCase.getTestPanelCaseId();
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
    out.println("  <h3>ACIP-Defined Conditions" + editButton + "</h3>");

    if (countACIP > 0) {
      printEventViewTable(out, testCase, testEventList, EventType.ACIP_DEFINED_CONDITION, "Condition");
    }

    if (user.isCanEditTestCase()) {
      editButton = " <a class=\"fauxbutton\" href=\"" + editLink + EventType.CONDITION_IMPLICATION.getEventTypeCode()
          + "\">Edit</a>";
    } else {
      editButton = "";
    }
    out.println("  <h3>Condition Implications" + editButton + "</h3>");
    if (countCondition > 0) {
      printEventViewTable(out, testCase, testEventList, EventType.CONDITION_IMPLICATION, "Condition");
    }

    if (false && testCase.getDateSet() == DateSet.RELATIVE) {
      try {
        final String link1 = "testCases?" + PARAM_ACTION + "="
            + URLEncoder.encode(ACTION_UPDATE_RELATIVE_DATES, "UTF-8") + "&" + PARAM_TEST_PANEL_CASE_ID + "="
            + testPanelCase.getTestPanelCaseId();
        out.println("  <p><a href=\"" + link1 + "\">" + ACTION_UPDATE_RELATIVE_DATES + "</a></p>");
      } catch (UnsupportedEncodingException usce) {
        usce.printStackTrace();
      }

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
        String categoryName = testPanelCase.getCategoryName().trim();
        if (!categoryName.equals(lastCategoryName)) {
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
              + "&" + PARAM_CATEGORY_NAME + "=" + categoryName;
          if (user.getSelectedCategoryName() != null && categoryName.equals(user.getSelectedCategoryName())) {
            out.println("          <li class=\"selectLevel2\"><a href=\"" + link2 + "\">" + categoryName + "</a>");
            out.println("            <ul class=\"selectLevel3\">");
            selectedCategoryOpened = true;
          } else {
            out.println("          <li class=\"selectLevel2\"><a href=\"" + link2 + "\">" + categoryName + "</a></li>");
          }
        }
        if (user.getSelectedCategoryName() != null && categoryName.equals(user.getSelectedCategoryName())) {
          final String link3 = "testCases?" + PARAM_ACTION + "="
              + URLEncoder.encode(ACTION_SELECT_TEST_PANEL_CASE, "UTF-8") + "&" + PARAM_TEST_PANEL_CASE_ID + "="
              + testPanelCase.getTestPanelCaseId();
          out.println("              <li class=\"selectLevel4\"><a href=\"" + link3 + "\">"
              + testPanelCase.getTestCase().getLabel() + "</a></li>");
        }
        lastCategoryName = categoryName;
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
