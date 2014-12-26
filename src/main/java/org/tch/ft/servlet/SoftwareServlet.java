package org.tch.ft.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.tch.fc.model.Service;
import org.tch.fc.model.ServiceOption;
import org.tch.fc.model.Software;
import org.tch.fc.model.SoftwareSetting;
import org.tch.ft.manager.ForecastActualExpectedCompare;
import org.tch.ft.manager.ForecastActualGenerator;
import org.tch.ft.manager.SoftwareManager;
import org.tch.ft.model.Expert;
import org.tch.ft.model.Result;
import org.tch.ft.model.Role;
import org.tch.ft.model.TaskGroup;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.TestPanelCase;
import org.tch.ft.model.User;

public class SoftwareServlet extends MainServlet
{

  public SoftwareServlet() {
    super("Software", ServletProtection.ALL_USERS);
  }

  private final static String ACTION_RUN_TESTS = "Run Tests";

  private final static String ACTION_ADD_SOFTWARE = "Add Software";
  private final static String ACTION_UPDATE_SOFTWARE = "Update Software";

  private final static String SHOW_EXECUTE = "execute";
  private final static String SHOW_EDIT_SOFTWARE = "showEditSoftware";
  private final static String SHOW_ADD_SOFTWARE = "showAddSoftware";

  private final static String PARAM_SOFTWARE_ID = "softwareId";
  private final static String PARAM_TASK_GROUP_ID = "taskGroupId";
  private final static String PARAM_TEST_PANEL_ID = "testPanelId";
  private final static String PARAM_CATEGORY_NAME = "categoryName";
  private final static String PARAM_RUN_ALL = "runAll";
  private final static String PARAM_UPDATE_TEST_CASE_STATUS = "updateTestCaseStatus";
  private final static String PARAM_VERIFY_EVALUATION_STATUS = "verifyEvalationStatus";
  private final static String PARAM_VERIFY_FORECAST_STATUS = "verifyForecastStatus";
  private final static String PARAM_VERIFY_FORECAST_DOSE = "verifyForecastDose";
  private final static String PARAM_VERIFY_FORECAST_VALID_DATE = "verifyForecastValidDate";
  private final static String PARAM_VERIFY_FORECAST_DUE_DATE = "verifyForecastDueDate";
  private final static String PARAM_VERIFY_FORECAST_OVERDUE_DATE = "verifyForecastOverdueDate";
  private final static String PARAM_VERIFY_FORECAST_FINISHED_DATE = "verifyForecastFinishedDate";
  private final static String PARAM_LABEL = "label";
  private final static String PARAM_SERVICE_URL = "serviceUrl";
  private final static String PARAM_SERVICE_TYPE = "serviceType";
  private final static String PARAM_SCHEDULE_NAME = "scheduleName";
  private final static String PARAM_SUPPORTS_FIXED = "supportsFixed";
  private final static String PARAM_OPTION_VALUE = "optionValue";

  private final static HoverText HOVER_TEXT_LABEL = new HoverText("Label")
      .add("<p>The label to display for this Software Service. </p>");
  private final static HoverText HOVER_TEXT_SERVICE_URL = new HoverText("Service URL")
      .add("<p>The web address of the service end point for requesting a forecast and evaluation. </p>");
  private final static HoverText HOVER_TEXT_SERVICE_TYPE = new HoverText("Service Type")
      .add("<p>The type of interface or system that is being connected to.  </p>");
  private final static HoverText HOVER_TEXT_SCHEDULE_NAME = new HoverText("Schedule Name")
      .add("<p>The name of the schedule that the system should use. ")
      .add("  The schedule name is passes as is to the software. ").add("Most software systems ignore this field. ")
      .add("  Consult the documentation for this system in order to determine supported schedules.  </p>");
  private final static HoverText HOVER_TEXT_SUPPORTS_FIXED = new HoverText("Supports Fixed")
      .add("<p>Indicates whether the Assessment Date can be passed to the software service. </p>")
      .add("<p>If this is true, this means that the software can be given Fixed test cases ")
      .add("  and they will be evaluated and forecasted as if it were the Assessment Date rather ")
      .add("  than the current date. Software systems that do not support fixed may return results ")
      .add("  that do not match expectations because the patient has ").add("aged out of receiving a vaccination. ")
      .add("  This is particularly the case with schedules such as Rotavirus which ")
      .add("  change dramatically as the test case ages. </p>");
  private final static HoverText HOVER_TEXT_TASK_GROUP_ASSIGNED = new HoverText("Task Group Assigned")
      .add("<p>The Task Group(s) that this software is currently assigned to as the primary software. </p>")
      .add("<p>While all test cases can be run against any publicly available software system, ")
      .add("  the assigned Software is the one that the test cases are designed to pass. Thus the Result Status ")
      .add("  that the Task Group assigns to the test case are in context of the assigned Software. ")
      .add("  A passing test case thus may match the results from the assigned Software ")
      .add("  but may or may not match results").add("  from other Software that may be run and compared here. </p>");
  private final static HoverText HOVER_TEXT_TASK_GROUP = new HoverText("Task Group")
      .add("<p>The currently selected Task Group. ")
      .add("A different Task Group can be selected by returning to the Test Cases and ")
      .add("selecting the Task Group in the navigation box on the left. </p> ");
  private final static HoverText HOVER_TEXT_TEST_PANEL = new HoverText("Test Panel")
      .add("<p> The currently selected Test Panel. ")
      .add("A different Test Panel can be selected by returning to the Test Cases and ")
      .add("selecting the Test Panel in the navigation box on the left. </p>");
  private final static HoverText HOVER_TEXT_RUN_FOR = new HoverText("Run For")
      .add("<p>Specify which categories to run now. </p>");
  private final static HoverText HOVER_TEXT_LIMIT_BY_CATEGORIES = new HoverText("Limit by Categories")
      .add("<p>Select which categories to run now. </p>");
  private final static HoverText HOVER_TEXT_FORECAST_COMPARISON = new HoverText("Forecast Comparison")
      .add("<p>Attributes of the forecast to compare. </p>");
  private final static HoverText HOVER_TEXT_UPDATE = new HoverText("Update").setHoverTitle("Update Test Case Status")
      .add("<p>There are two different concepts of pass/fail for test cases. ")
      .add("  The first concept, Result Status, can only be set by the task group ")
      .add("  experts to indicate whether their primary software ")
      .add("  is able to meet the expectations of the test case. ")
      .add("  The second concept is the Comparison that is done when ")
      .add("  a test case is run against a selected Software.  </p>")
      .add("<p>By clicking this option you can request that the Result Status should ")
      .add("  update by the results of this test run. If selected the following updates ")
      .add("  will be made when the comparison indicates: ")
      .add("  <ul><li><b>Same</b> If the Result Status is Fail it will be changed to Fixed. </li>")
      .add("    <li><b>Different</b> If the Result Status is Pass it will be changed to Fail. </li></ul></p>")
      .add("<p> If this option is disabled this means you are either not an expert on this Task Group or this  ")
      .add("  software is not the primary software that your Task Group is testing. </p>");
  private final static HoverText HOVER_TEST_CASES_UPDATED = new HoverText("Test Cases Updated")
      .add("<p>A summary of the test cases that had their Result Status updated based on the results that were returned.</p>");
  private final static HoverText HOVER_TEXT_TEST_CASE = new HoverText("Test Case")
      .add("<p>The test case being tested. </p>");
  private final static HoverText HOVER_TEXT_VACCINE_GROUP = new HoverText("Vaccine Group")
      .add("<p>The vaccine group that has a forecast expectation set. </p>");
  private final static HoverText HOVER_TEXT_STATUS = new HoverText("Status")
      .add("<p>The status as set by the Task Group for their target software system. ")
      .add("  Please note that the target system may not be the one that you are testing ")
      .add("  so the status and comparison may or may not align in these cases. </p>");
  private final static HoverText HOVER_TEXT_COMPARISON = new HoverText("Comparison")
      .add("<p>The comparison of the expected and actual results for the fields selected when making the request to Run Tests. </p>");

  @Override
  public String execute(HttpServletRequest req, HttpServletResponse resp, String action, String show)
      throws IOException {
    Session dataSession = applicationSession.getDataSession();
    User user = applicationSession.getUser();
    if (req.getParameter(PARAM_SOFTWARE_ID) != null) {
      int softwareId = Integer.parseInt(req.getParameter(PARAM_SOFTWARE_ID));
      Software selectedSoftware = (Software) dataSession.get(Software.class, softwareId);
      Transaction transaction = dataSession.beginTransaction();
      user.setSelectedSoftware(selectedSoftware);
      dataSession.update(user);
      transaction.commit();
    }
    if (req.getParameter(PARAM_TASK_GROUP_ID) != null) {
      int taskGroupId = Integer.parseInt(req.getParameter(PARAM_TASK_GROUP_ID));
      TaskGroup selectedTaskGroup = (TaskGroup) dataSession.get(TaskGroup.class, taskGroupId);
      Transaction transaction = dataSession.beginTransaction();
      user.setSelectedTaskGroup(selectedTaskGroup);
      user.setSelectedTestPanel(null);
      user.setSelectedTestPanelCase(null);
      user.setSelectedCategoryName(null);
      dataSession.update(user);
      transaction.commit();
    }
    if (req.getParameter(PARAM_TEST_PANEL_ID) != null) {
      int testPanelId = Integer.parseInt(req.getParameter(PARAM_TEST_PANEL_ID));
      TestPanel selectedTestPanel = (TestPanel) dataSession.get(TestPanel.class, testPanelId);
      Transaction transaction = dataSession.beginTransaction();
      user.setSelectedTaskGroup(selectedTestPanel.getTaskGroup());
      user.setSelectedTestPanel(selectedTestPanel);
      user.setSelectedTestPanelCase(null);
      user.setSelectedCategoryName(null);
      dataSession.update(user);
      transaction.commit();
    }
    if (action != null) {
      if (action.equals(ACTION_RUN_TESTS)) {
        TestPanel testPanel = user.getSelectedTestPanel();
        Software software = user.getSelectedSoftware();
        String problem = null;

        Set<String> categoryNameSet = null;
        if (req.getParameter(PARAM_RUN_ALL) == null) {
          categoryNameSet = new HashSet<String>();
          if (req.getParameterValues(PARAM_CATEGORY_NAME) != null) {
            for (String categoryName : req.getParameterValues(PARAM_CATEGORY_NAME)) {
              categoryNameSet.add(categoryName);
            }
          }
        }
        applicationSession.setForecastCompareCategoryNameSet(categoryNameSet);

        ForecastActualExpectedCompare.CompareCriteria compareCriteria = applicationSession.getCompareCriteria();
        compareCriteria.setVerifyEvaluationStatus(req.getParameter(PARAM_VERIFY_EVALUATION_STATUS) != null);
        compareCriteria.setVerifyForecastStatus(req.getParameter(PARAM_VERIFY_FORECAST_STATUS) != null);
        compareCriteria.setVerifyForecastDose(req.getParameter(PARAM_VERIFY_FORECAST_DOSE) != null);
        compareCriteria.setVerifyForecastValidDate(req.getParameter(PARAM_VERIFY_FORECAST_VALID_DATE) != null);
        compareCriteria.setVerifyForecastDueDate(req.getParameter(PARAM_VERIFY_FORECAST_DUE_DATE) != null);
        compareCriteria.setVerifyForecastOverdueDate(req.getParameter(PARAM_VERIFY_FORECAST_OVERDUE_DATE) != null);
        compareCriteria.setVerifyForecastFinishedDate(req.getParameter(PARAM_VERIFY_FORECAST_FINISHED_DATE) != null);

        if (categoryNameSet != null && categoryNameSet.size() == 0) {
          problem = "Unable to run tests, no categories selected. ";
        } else if (!compareCriteria.isVerifyEvaluationStatus() && !compareCriteria.isVerifyForecastDose()
            && !compareCriteria.isVerifyForecastDueDate() && !compareCriteria.isVerifyForecastFinishedDate()
            && !compareCriteria.isVerifyForecastOverdueDate() && !compareCriteria.isVerifyForecastStatus()
            && !compareCriteria.isVerifyForecastValidDate()) {
          problem = "Unable to run tests, no comparisons were selected to test. ";
        }

        if (problem != null) {
          applicationSession.setAlertError(problem);
          return SHOW_EXECUTE;
        }

        try {
          Set<TestPanelCase> updateSet = null;
          ForecastActualGenerator.runForecastActual(testPanel, software, categoryNameSet, dataSession, false);
          List<ForecastActualExpectedCompare> forecastCompareList = ForecastActualGenerator.createForecastComparison(
              testPanel, software, categoryNameSet, dataSession);
          boolean enableParamUpdateTestCaseStatus = checkEnableUpdateTestCase(user, testPanel, software, dataSession);
          if (enableParamUpdateTestCaseStatus) {
            if (req.getParameter(PARAM_UPDATE_TEST_CASE_STATUS) != null) {
              updateSet = ForecastActualGenerator.updateStatusOfTestPanel(dataSession, forecastCompareList);
            }
          }
          applicationSession.setForecastCompareTestPanelCaseUpdate(updateSet);

          Collections.sort(forecastCompareList, new ForecastActualExpectedCompare.ForecastCompareComparator());
          applicationSession.setForecastCompareList(forecastCompareList);
          Map<TestPanelCase, ForecastActualExpectedCompare> forecastCompareMap = new HashMap<TestPanelCase, ForecastActualExpectedCompare>();
          applicationSession.setForecastCompareCategoryHasProblemSet(new HashSet<String>());
          applicationSession.setForecastCompareTestPanelCaseHasProblemSet(new HashSet<TestPanelCase>());
          for (ForecastActualExpectedCompare forecastCompare : forecastCompareList) {
            forecastCompare.setCompareCriteria(compareCriteria);
            if (!forecastCompare.matchExactly()) {
              applicationSession.getForecastCompareCategoryHasProblemSet().add(
                  forecastCompare.getTestPanelCase().getCategoryName());
              applicationSession.getForecastCompareTestPanelCaseHasProblemSet().add(forecastCompare.getTestPanelCase());
            }
          }
          applicationSession.setForecastCompareTestPanel(testPanel);
          applicationSession.setAlertInformation("Forecast run, returned " + forecastCompareList.size() + " result(s)");
        } catch (Exception e) {
          applicationSession.setAlertError("Unable to forecast: " + e.getMessage());
        }
        return SHOW_EXECUTE;
      } else if (action.equals(ACTION_ADD_SOFTWARE) || action.equals(ACTION_UPDATE_SOFTWARE)) {
        Software software = user.getSelectedSoftware();
        if (action.equals(ACTION_ADD_SOFTWARE)) {
          software = new Software();
        }

        String label = req.getParameter(PARAM_LABEL);
        String serviceUrl = req.getParameter(PARAM_SERVICE_URL);
        String serviceType = action.equals(ACTION_ADD_SOFTWARE) ? req.getParameter(PARAM_SERVICE_TYPE) : software
            .getServiceType();
        String scheduleName = req.getParameter(PARAM_SCHEDULE_NAME);
        boolean supportsFixed = req.getParameter(PARAM_SUPPORTS_FIXED) != null;

        String problem = null;
        if (label.equals("")) {
          problem = "Label is required";
        } else if (serviceType.equals("")) {
          problem = "Service type is required";
        }
        if (problem != null) {
          applicationSession.setAlertError("Unable to save software settings: " + problem);
        } else {
          Transaction transaction = dataSession.beginTransaction();
          software.setLabel(label);
          software.setServiceUrl(serviceUrl);
          software.setServiceType(serviceType);
          software.setScheduleName(scheduleName);
          software.setSupportsFixed(supportsFixed);
          dataSession.saveOrUpdate(software);
          if (action.equals(ACTION_UPDATE_SOFTWARE)) {
            HashMap<ServiceOption, SoftwareSetting> softwareSettingsMap = getSoftwareSettingsMap(dataSession, software);
            Query query = dataSession.createQuery("from ServiceOption where serviceType = ? order by optionLabel");
            query.setParameter(0, serviceType);
            List<ServiceOption> serviceOptionList = query.list();

            for (ServiceOption serviceOption : serviceOptionList) {
              String optionValue = req.getParameter(PARAM_OPTION_VALUE + serviceOption.getOptionId());
              SoftwareSetting softwareSetting = softwareSettingsMap.get(serviceOption);
              if (optionValue.equals("")) {
                if (softwareSetting != null) {
                  dataSession.delete(softwareSetting);
                }
              } else {
                if (softwareSetting == null) {
                  softwareSetting = new SoftwareSetting();
                  softwareSetting.setSoftware(software);
                  softwareSetting.setServiceOption(serviceOption);
                }
                softwareSetting.setOptionValue(optionValue);
                dataSession.saveOrUpdate(softwareSetting);
              }
            }
          }
          transaction.commit();
          return SHOW_EXECUTE;
        }

      }
    }
    return show;
  }

  @Override
  protected void printPage(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, String show)
      throws ServletException, IOException {
    Session dataSession = applicationSession.getDataSession();
    User user = applicationSession.getUser();
    Software software = user.getSelectedSoftware();

    printSoftwareTree(out, dataSession, user);
    if (show == null || show.equals(SHOW_EXECUTE)) {

      out.println("<script>");
      out.println("  <!-- ");
      out.println("  function showRunAll() { ");
      out.println("    var form = document.getElementById('runTestsForm'); ");
      out.println("    var runAllTests = form." + PARAM_RUN_ALL + ".checked");
      out.println("    var rowToShow = document.getElementById('runSome'); ");
      out.println("    if (rowToShow != null) { ");
      out.println("      rowToShow.style.display = runAllTests ? 'none' : 'table-row'; ");
      out.println("    }");
      out.println("  }");
      out.println("  -->");
      out.println("</script>");
      out.println("<div class=\"centerLeftColumn\">");

      printSoftwareTable(out, dataSession, user, software);
      TestPanel testPanel = user.getSelectedTestPanel();
      if (testPanel != null) {
        printRunTestForm(out, dataSession, user, testPanel);
      }
      out.println("</div>");

      if (testPanel != null) {
        if (applicationSession.getForecastCompareList() != null
            && applicationSession.getForecastCompareTestPanel().equals(testPanel)) {
          printRunTestResults(out, user, testPanel);
        }
      }
    } else if (show.equals(SHOW_EDIT_SOFTWARE) || show.equals(SHOW_ADD_SOFTWARE)) {

      if (show.equals(SHOW_ADD_SOFTWARE)) {
        software = new Software();
      }
      String label = notNull(req.getParameter(PARAM_LABEL), software.getLabel());
      String serviceUrl = notNull(req.getParameter(PARAM_SERVICE_URL), software.getServiceUrl());
      String serviceType = notNull(req.getParameter(PARAM_SERVICE_TYPE), software.getServiceType());
      String scheduleName = notNull(req.getParameter(PARAM_SCHEDULE_NAME), software.getScheduleName());
      boolean supportsFixed = notNull(req.getParameter(PARAM_SUPPORTS_FIXED), software.isSupportsFixed());

      String cancelLink = "software?" + PARAM_SHOW + "=" + SHOW_EXECUTE;
      if (show.equals(SHOW_EDIT_SOFTWARE)) {
        cancelLink += "&" + PARAM_SOFTWARE_ID + "=" + software.getSoftwareId();
      }
      String cancelButton = "";
      if (user.isAdmin()) {
        cancelButton = " <a class=\"fauxbutton\" href=\"" + cancelLink + "\">Back</a>";
      }

      out.println("<div class=\"centerColumn\">");
      out.println("  <form method=\"POST\" action=\"software\">");
      if (show.equals(SHOW_EDIT_SOFTWARE)) {
        out.println("  <input type=\"hidden\" name=\"" + PARAM_SOFTWARE_ID + "\" value=\"" + software.getSoftwareId()
            + "\"/>");
      }

      out.println("  <h2>Software" + cancelButton + "</h2>");
      out.println("  <table width=\"100%\">");
      out.println("    <tr>");
      out.println("      <th>" + HOVER_TEXT_LABEL + "</th>");
      out.println("      <td><input type=\"text\" size=\"30\" name=\"" + PARAM_LABEL + "\" value=\"" + label
          + "\"/></td>");
      out.println("    </tr>");
      out.println("    <tr>");
      out.println("      <th>" + HOVER_TEXT_SERVICE_URL + "</th>");
      out.println("      <td><input type=\"text\" size=\"40\" name=\"" + PARAM_SERVICE_URL + "\" value=\"" + serviceUrl
          + "\"/></td>");
      out.println("    </tr>");

      out.println("    <tr>");
      out.println("      <th>" + HOVER_TEXT_SERVICE_TYPE + "</th>");
      out.println("      <td>");
      if (show.equals(SHOW_ADD_SOFTWARE)) {
        out.println("        <select type=\"text\" name=\"" + PARAM_SERVICE_TYPE + "\">");
        out.println("          <option value=\"\">--select--</option>");
        for (Service service : Service.valueList()) {
          if (service.getServiceType().equals(serviceType)) {
            out.println("              <option value=\"" + service.getServiceType() + "\" selected=\"selected\">"
                + service.getLabel() + "</option>");
          } else {
            out.println("              <option value=\"" + service.getServiceType() + "\">" + service.getLabel()
                + "</option>");
          }
        }
        out.println("        </select>");
      } else {
        out.println(software.getService().getLabel());
      }
      out.println("      </td>");
      out.println("    </tr>");
      out.println("    <tr>");
      out.println("      <th>" + HOVER_TEXT_SCHEDULE_NAME + "</th>");
      out.println("      <td><input type=\"text\" size=\"20\" name=\"" + PARAM_SCHEDULE_NAME + "\" value=\""
          + scheduleName + "\"/></td>");
      out.println("    </tr>");
      out.println("    <tr>");
      out.println("      <th>" + HOVER_TEXT_SUPPORTS_FIXED + "</th>");
      out.println("      <td><input type=\"checkbox\" name=\"" + PARAM_SUPPORTS_FIXED + "\" value=\"true\""
          + (supportsFixed ? " checked=\"true\"" : "") + "/></td>");
      out.println("    </tr>");

      if (show.equals(SHOW_EDIT_SOFTWARE)) {
        HashMap<ServiceOption, SoftwareSetting> softwareSettingsMap = getSoftwareSettingsMap(dataSession, software);

        Query query = dataSession.createQuery("from ServiceOption where serviceType = ? order by optionLabel");
        query.setParameter(0, serviceType);
        List<ServiceOption> serviceOptionList = query.list();

        for (ServiceOption serviceOption : serviceOptionList) {
          out.println("    <tr>");
          out.println("      <th>" + serviceOption.getOptionLabel() + "</th>");
          SoftwareSetting softwareSetting = softwareSettingsMap.get(serviceOption);
          String optionValue;
          if (softwareSetting == null) {
            optionValue = notNull(req.getParameter(PARAM_OPTION_VALUE + serviceOption.getOptionId()), "");
          } else {
            optionValue = notNull(req.getParameter(PARAM_OPTION_VALUE + serviceOption.getOptionId()),
                softwareSetting.getOptionValue());
          }
          if (serviceOption.getValidValues().equals("")) {
            out.println("      <td><input type=\"text\" size=\"20\" name=\"" + PARAM_OPTION_VALUE
                + serviceOption.getOptionId() + "\" value=\"" + optionValue + "\"/></td>");
          } else {
            out.println("      <td>");
            out.println("        <select name=\"" + PARAM_OPTION_VALUE + serviceOption.getOptionId() + "\">");
            out.println("          <option value=\"\">--select--</option>");
            for (String validValue : serviceOption.getValidValues().split("\\,")) {
              validValue = validValue.trim();
              if (optionValue.equalsIgnoreCase(validValue)) {
                out.println("              <option value=\"" + validValue + "\" selected=\"selected\">" + validValue
                    + "</option>");
              } else {
                out.println("              <option value=\"" + validValue + "\">" + validValue + "</option>");
              }
            }
            out.println("        </select>");
            out.println("      </td>");
          }
          out.println("    </tr>");
        }
      }

      out.println("    <tr>");
      if (show.equals(SHOW_ADD_SOFTWARE)) {
        out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION
            + "\" size=\"15\" value=\"" + ACTION_ADD_SOFTWARE + "\"/></td>");
      } else if (show.equals(SHOW_EDIT_SOFTWARE)) {
        out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION
            + "\" size=\"15\" value=\"" + ACTION_UPDATE_SOFTWARE + "\"/></td>");
      }
      out.println("    </tr>");
      out.println("  </table>");
      out.println("  </form>");
      out.println("</div>");
    }

  }

  public HashMap<ServiceOption, SoftwareSetting> getSoftwareSettingsMap(Session dataSession, Software software) {
    HashMap<ServiceOption, SoftwareSetting> softwareSettingsMap = new HashMap<ServiceOption, SoftwareSetting>();
    {
      Query query = dataSession
          .createQuery("from SoftwareSetting where software = ? order by serviceOption.optionLabel");
      query.setParameter(0, software);
      List<SoftwareSetting> softwareSettingList = query.list();
      for (SoftwareSetting softwareSetting : softwareSettingList) {
        softwareSettingsMap.put(softwareSetting.getServiceOption(), softwareSetting);
      }
    }
    return softwareSettingsMap;
  }

  public void printSoftwareTable(PrintWriter out, Session dataSession, User user, Software software) {
    String editLink = "software?" + PARAM_SHOW + "=" + SHOW_EDIT_SOFTWARE + "&" + PARAM_SOFTWARE_ID + "="
        + software.getSoftwareId();
    String editButton = "";
    if (user.isAdmin()) {
      editButton = " <a class=\"fauxbutton\" href=\"" + editLink + "\">Edit</a>";
    }

    out.println("  <h2>Software" + editButton + "</h2>");
    out.println("  <table width=\"100%\">");
    out.println("    <tr>");
    out.println("      <th>" + HOVER_TEXT_LABEL + "</th>");
    out.println("      <td>" + software.getLabel() + "</td>");
    out.println("    </tr>");
    out.println("    <tr>");
    out.println("      <th>" + HOVER_TEXT_SERVICE_URL + "</th>");
    out.println("      <td>" + software.getServiceUrl() + "</td>");
    out.println("    </tr>");
    if (software.getService() != null) {
      out.println("    <tr>");
      out.println("      <th>" + HOVER_TEXT_SERVICE_TYPE + "</th>");
      out.println("      <td>" + software.getService().getLabel() + "</td>");
      out.println("    </tr>");
    }
    if (software.getScheduleName() != null && !software.getScheduleName().equals("")) {
      out.println("    <tr>");
      out.println("      <th>" + HOVER_TEXT_SCHEDULE_NAME + "</th>");
      out.println("      <td>" + software.getScheduleName() + "</td>");
      out.println("    </tr>");
    }
    out.println("    <tr>");
    out.println("      <th>" + HOVER_TEXT_SUPPORTS_FIXED + "</th>");
    out.println("      <td>" + software.isSupportsFixed() + "</td>");
    out.println("    </tr>");

    Query query = dataSession.createQuery("from TaskGroup where primarySoftware = ?");
    query.setParameter(0, software);
    List<TaskGroup> taskGroupList = query.list();
    out.println("    <tr>");

    out.println("      <th>" + HOVER_TEXT_TASK_GROUP_ASSIGNED + "</th>");
    if (taskGroupList.size() == 0) {
      out.println("      <td><em>not assigned</em></td>");
    } else {
      out.println("      <td>");
      for (TaskGroup taskGroup : taskGroupList) {
        out.println(taskGroup.getLabel() + "<br/>");
      }
    }
    out.println("      </td>");
    out.println("    </tr>");
    query = dataSession.createQuery("from SoftwareSetting where software = ? order by serviceOption.optionLabel");
    query.setParameter(0, software);
    List<SoftwareSetting> softwareSettingList = query.list();
    for (SoftwareSetting softwareSetting : softwareSettingList) {
      if (!softwareSetting.getOptionValue().equals("")) {
        HoverText hoverText = new HoverText(softwareSetting.getServiceOption().getOptionLabel());
        hoverText.add("<p>");
        hoverText.add(softwareSetting.getServiceOption().getDescription());
        hoverText.add("</p>");
        hoverText.add("<p>This setting is specific to the "
            + softwareSetting.getServiceOption().getService().getLabel() + " software.</p>");
        out.println("    <tr>");
        out.println("      <th>" + hoverText + "</th>");
        out.println("      <td>" + softwareSetting.getOptionValue() + "</td>");
        out.println("    </tr>");
      }
    }

    out.println("  </table>");
  }

  public void printRunTestResults(PrintWriter out, User user, TestPanel testPanel) throws UnsupportedEncodingException {
    out.println("<div class=\"centerRightColumn\">");
    out.println("<h2>Test Results</h2>");
    out.println("  <table>");
    out.println("    <tr>");
    out.println("      <th>" + HOVER_TEXT_TASK_GROUP + "</th>");
    out.println("      <td>" + testPanel.getTaskGroup().getLabel() + "</td>");
    out.println("    </tr>");
    out.println("    <tr>");
    out.println("      <th>" + HOVER_TEXT_TEST_PANEL + "</th>");
    out.println("      <td>" + testPanel.getLabel() + "</td>");
    out.println("    </tr>");
    Set<String> categoryNameSet = applicationSession.getForecastCompareCategoryNameSet();
    out.println("    <tr>");
    out.println("      <th>" + HOVER_TEXT_RUN_FOR + "</th>");
    if (categoryNameSet == null) {
      out.println("      <td>All Categories</td>");
    } else {
      out.println("      <td>");
      List<String> categoryNameList = new ArrayList<String>(categoryNameSet);
      Collections.sort(categoryNameList);
      for (String categoryName : categoryNameList) {
        out.println("        " + categoryName + "<br/>");
      }
      out.println("      </td>");
    }
    out.println("    </tr>");
    Set<TestPanelCase> updateSet = applicationSession.getForecastCompareTestPanelCaseUpdate();
    if (updateSet != null) {
      out.println("    <tr>");
      out.println("      <th>" + HOVER_TEST_CASES_UPDATED + "</th>");
      out.println("      <td>");
      if (updateSet.size() == 0) {
        out.println("        <em>none</em>");
      } else {
        for (TestPanelCase tpc : updateSet) {
          String styleClass;
          Result result = tpc.getResult();
          if (result == Result.ACCEPT || result == Result.PASS || result == Result.FIXED) {
            styleClass = "passBox";
          } else {
            styleClass = "failBox";
          }
          out.println(tpc.getTestCase().getLabel());
          out.println("<span class=\"" + styleClass + "\">" + (result == null ? "-" : result.getLabel()));
          out.println("</span><br/>");
        }
      }
      out.println("      </td>");
      out.println("    </tr>");
    }

    ForecastActualExpectedCompare.CompareCriteria compareCriteria = applicationSession.getCompareCriteria();
    if (compareCriteria.isVerifyEvaluationStatus()) {
      out.println("    <tr>");
      out.println("      <th>Evaluation Comparison</th>");
      out.println("      <td>Status</td>");
      out.println("    </tr>");
    }
    out.println("    <tr>");
    out.println("      <th>" + HOVER_TEXT_FORECAST_COMPARISON + "</th>");
    out.println("      <td>");
    if (compareCriteria.isVerifyForecastStatus()) {
      out.println("        Status <br/>");
    }
    if (compareCriteria.isVerifyForecastStatus()) {
      out.println("        Dose <br/>");
    }
    if (compareCriteria.isVerifyForecastValidDate()) {
      out.println("        Earliest Date <br/>");
    }
    if (compareCriteria.isVerifyForecastDueDate()) {
      out.println("        Recommend Date <br/>");
    }
    if (compareCriteria.isVerifyForecastOverdueDate()) {
      out.println("        Past Due Date <br/>");
    }
    if (compareCriteria.isVerifyForecastFinishedDate()) {
      out.println("        Finished Date <br/>");
    }
    out.println("      </td>");
    out.println("    </tr>");
    out.println("  </table>");
    String categoryName = "";
    for (ForecastActualExpectedCompare forecastCompare : applicationSession.getForecastCompareList()) {
      if (!categoryName.equals(forecastCompare.getTestPanelCase().getCategoryName())) {
        if (!categoryName.equals("")) {
          out.println("  </table>");
        }
        out.println("  <h3>" + forecastCompare.getTestPanelCase().getCategoryName() + "</h3>");
        out.println("  <table width=\"100%\">");
        out.println("    <tr>");
        out.println("      <th width=\"50%\">" + HOVER_TEXT_TEST_CASE + "</th>");
        out.println("      <th width=\"20%\">" + HOVER_TEXT_VACCINE_GROUP + "</th>");
        out.println("      <th width=\"15%\">" + HOVER_TEXT_STATUS + "</th>");
        out.println("      <th width=\"15%\">" + HOVER_TEXT_COMPARISON + "</th>");
        out.println("    </tr>");
      }
      categoryName = forecastCompare.getTestPanelCase().getCategoryName();
      String styleClass = "";
      String link = "testCases?" + PARAM_ACTION + "="
          + URLEncoder.encode(TestCasesServlet.ACTION_SELECT_TEST_PANEL_CASE, "UTF-8") + "&"
          + TestCasesServlet.PARAM_TEST_PANEL_CASE_ID + "=" + forecastCompare.getTestPanelCase().getTestPanelCaseId();
      out.println("    <tr>");
      if (user.getSelectedTestPanelCase() != null
          && user.getSelectedTestPanelCase().equals(forecastCompare.getTestPanelCase())) {
        styleClass = "highlight";
      }
      String updateLabel = "";
      if (updateSet != null && updateSet.contains(forecastCompare.getTestPanelCase())) {
        updateLabel = " <span class=\"passBox\">Updated</span>";
      }
      out.println("      <td class=\"" + styleClass + "\"><a href=\"" + link + "\">"
          + forecastCompare.getForecastResultA().getTestCase().getLabel() + updateLabel + "</a></td>");
      out.println("      <td class=\"" + styleClass + "\"><a href=\"" + link + "\">"
          + forecastCompare.getForecastResultA().getVaccineGroup().getLabel() + "</a></td>");
      Result result = forecastCompare.getTestPanelCase().getResult();
      if (result != null) {
        if (result == Result.ACCEPT || result == Result.PASS) {
          styleClass = "pass";
        } else if (result == Result.RESEARCH || result == Result.FIXED) {
          styleClass = "research";
        } else {
          styleClass = "fail";
        }
      }

      out.println("      <td class=\"" + styleClass + "\">" + (result == null ? "-" : result.getLabel()) + "</td>");
      styleClass = forecastCompare.matchExactly() ? "pass" : "fail";
      if (forecastCompare.matchExactly()) {
        out.println("      <td class=\"" + styleClass + "\">" + forecastCompare.getMatchStatus() + "</td>");
      } else {
        out.println("      <td class=\"" + styleClass + "\">" + forecastCompare.getMatchStatus() + " ("
            + forecastCompare.getMatchDifference() + ")</td>");

      }
      out.println("    </tr>");
    }
    if (!categoryName.equals("")) {
      out.println("  </table>");
    }
    out.println("</div>");
  }

  public void printRunTestForm(PrintWriter out, Session dataSession, User user, TestPanel testPanel) {
    Software software = user.getSelectedSoftware();
    out.println("<h3>Run Tests</h3>");
    out.println("  <form method=\"POST\" action=\"software\" id=\"runTestsForm\">");
    out.println("  <input type=\"hidden\" name=\"" + PARAM_SOFTWARE_ID + "\" value=\"" + software.getSoftwareId()
        + "\"/>");
    out.println("  <table width=\"100%\"> ");
    out.println("    <tr>");
    out.println("      <th>" + HOVER_TEXT_TASK_GROUP + "</th>");
    out.println("      <td>" + testPanel.getTaskGroup().getLabel() + "</td>");
    out.println("    </tr>");
    out.println("    <tr>");
    out.println("      <th>" + HOVER_TEXT_TEST_PANEL + "</th>");
    out.println("      <td>" + testPanel.getLabel() + "</td>");
    out.println("    </tr>");
    Set<String> categoryNameSet = applicationSession.getForecastCompareCategoryNameSet();
    out.println("    <tr>");
    out.println("      <th>" + HOVER_TEXT_RUN_FOR + "</th>");
    out.println("      <td><input type=\"checkbox\" name=\"" + PARAM_RUN_ALL + "\" value=\"true\""
        + (categoryNameSet == null ? " checked=\"true\"" : "") + " onChange=\"showRunAll()\"/> All Categories</td>");
    out.println("    </tr>");
    out.println("    <tr" + (categoryNameSet == null ? " style=\"display: none;\"" : "") + " id=\"runSome\">");
    out.println("      <th>" + HOVER_TEXT_LIMIT_BY_CATEGORIES + "</th>");
    out.println("      <td>");
    Query query = dataSession.createQuery("from TestPanelCase where testPanel = ? order by categoryName");
    query.setParameter(0, testPanel);
    List<TestPanelCase> testPanelCaseList = query.list();
    String categoryName = "";
    for (TestPanelCase testPanelCase : testPanelCaseList) {
      if (!testPanelCase.getCategoryName().equals(categoryName)) {
        boolean checked = false;
        if (categoryNameSet != null) {
          checked = categoryNameSet.contains(testPanelCase.getCategoryName());
        } else {
          checked = user.getSelectedTestPanelCase() != null
              && user.getSelectedTestPanelCase().getCategoryName().equals(testPanelCase.getCategoryName());
        }
        if (checked) {
          out.println("        <input type=\"checkbox\" name=\"" + PARAM_CATEGORY_NAME + "\" value=\""
              + testPanelCase.getCategoryName() + "\" checked=\"true\"/>" + testPanelCase.getCategoryName() + "<br/>");
        } else {
          out.println("        <input type=\"checkbox\" name=\"" + PARAM_CATEGORY_NAME + "\" value=\""
              + testPanelCase.getCategoryName() + "\"/>" + testPanelCase.getCategoryName() + "<br/>");
        }
      }
      categoryName = testPanelCase.getCategoryName();
    }
    out.println("      </td>");
    out.println("    <tr>");
    out.println("      <th>" + HOVER_TEXT_UPDATE + "</th>");
    out.println("      <td>");
    boolean enableParamUpdateTestCaseStatus = checkEnableUpdateTestCase(user, testPanel, software, dataSession);
    if (enableParamUpdateTestCaseStatus) {
      out.println("        <input type=\"checkbox\" name=\"" + PARAM_UPDATE_TEST_CASE_STATUS
          + "\" value=\"false\"/> Test Case Status<br/>");
    } else {
      out.println("        <input type=\"checkbox\" name=\"" + PARAM_UPDATE_TEST_CASE_STATUS
          + "\" value=\"true\" disabled=\"true\"/> Test Case Status");
    }
    out.println("      </td>");
    out.println("    </tr>");
    out.println("    </tr>");
    ForecastActualExpectedCompare.CompareCriteria compareCriteria = applicationSession.getCompareCriteria();
    if (false) {
      out.println("    <tr>");
      out.println("      <th>Evaluation Comparison</th>");
      out.println("      <td>");
      out.println("        <input type=\"checkbox\" name=\"" + PARAM_VERIFY_EVALUATION_STATUS
          + "\" value=\"true\" disabled=\"true\"/> Status");
      out.println("      </td>");
      out.println("    </tr>");
    }
    out.println("    <tr>");
    out.println("      <th>" + HOVER_TEXT_FORECAST_COMPARISON + "</th>");
    out.println("      <td>");
    out.println("        <input type=\"checkbox\" name=\"" + PARAM_VERIFY_FORECAST_STATUS + "\" value=\"true\""
        + (compareCriteria.isVerifyForecastStatus() ? " checked=\"true\"" : "") + "/> Status <br/>");
    out.println("        <input type=\"checkbox\" name=\"" + PARAM_VERIFY_FORECAST_DOSE + "\" value=\"true\""
        + (compareCriteria.isVerifyForecastDose() ? " checked=\"true\"" : "") + "/> Dose <br/>");
    out.println("        <input type=\"checkbox\" name=\"" + PARAM_VERIFY_FORECAST_VALID_DATE + "\" value=\"true\""
        + (compareCriteria.isVerifyForecastValidDate() ? " checked=\"true\"" : "") + "/> Earliest Date <br/>");
    out.println("        <input type=\"checkbox\" name=\"" + PARAM_VERIFY_FORECAST_DUE_DATE + "\" value=\"true\""
        + (compareCriteria.isVerifyForecastDueDate() ? " checked=\"true\"" : "") + "/> Recommend Date <br/>");
    out.println("        <input type=\"checkbox\" name=\"" + PARAM_VERIFY_FORECAST_OVERDUE_DATE + "\" value=\"true\""
        + (compareCriteria.isVerifyForecastOverdueDate() ? " checked=\"true\"" : "") + "/> Past Due Date <br/>");
    out.println("        <input type=\"checkbox\" name=\"" + PARAM_VERIFY_FORECAST_FINISHED_DATE + "\" value=\"true\""
        + (compareCriteria.isVerifyForecastFinishedDate() ? " checked=\"true\"" : "") + "/> Finished Date <br/>");
    out.println("      </td>");
    out.println("    </tr>");
    out.println("  <tr>");
    out.println("    <td align=\"right\" colspan=\"2\"><input type=\"submit\" name=\"" + PARAM_ACTION
        + "\" size=\"15\" value=\"" + ACTION_RUN_TESTS + "\"/></td>");
    out.println("  </tr>");
    out.println("  </table> ");
    out.println("</form>");
  }

  public boolean checkEnableUpdateTestCase(User user, TestPanel testPanel, Software software, Session dataSession) {
    if (!testPanel.getTaskGroup().getPrimarySoftware().equals(software)) {
      return false;
    }
    Query query = dataSession.createQuery("from Expert where user = ? and taskGroup = ?");
    query.setParameter(0, user);
    query.setParameter(1, user.getSelectedTaskGroup());
    List<Expert> expertList = query.list();
    if (expertList.size() > 0) {
      Role role = expertList.get(0).getRole();
      if (role == Role.ADMIN || role == Role.EXPERT) {
        return true;
      }
    }
    return false;
  }

  public void printSoftwareTree(PrintWriter out, Session dataSession, User user) {
    out.println("<div class=\"leftColumn\">");

    Software selectedSoftware = user.getSelectedSoftware();
    if (selectedSoftware != null) {

      out.println("<h2>" + selectedSoftware.getLabel() + "</h2>");

      Query query = dataSession.createQuery("from TaskGroup order by label");
      // printTaskGroupList(out, dataSession, user, query);

    } else {
      out.println("<h2>Software</h2>");
    }
    out.println("  <ul class=\"selectLevel1\">");
    List<Software> softwareList = user.isAdmin() ? SoftwareManager.getListOfSoftware(dataSession) : SoftwareManager
        .getListOfUnrestrictedSoftware(user, dataSession);
    for (Software software : softwareList) {
      if (selectedSoftware == null || !selectedSoftware.equals(software)) {
        String link = "software?" + PARAM_SOFTWARE_ID + "=" + software.getSoftwareId();
        out.println("      <li class=\"selectLevel1\"><a href=\"" + link + "\">" + software.getLabel() + "</a>");
      }
    }
    if (user.isAdmin()) {
      out.println("      <li class=\"selectLevel1\"><a class=\"add\" href=\"software?" + PARAM_SHOW + "="
          + SHOW_ADD_SOFTWARE + "\">add new software</a>");
    } else {

    }
    out.println("  </ul>");
    out.println("</div>");
  }

  public void printTaskGroupList(PrintWriter out, Session dataSession, User user, Query query) {
    List<TaskGroup> taskGroupList = query.list();
    if (taskGroupList.size() > 0) {
      out.println("      <ul class=\"selectLevel1\">");
      TaskGroup selectedTaskGroup = user.getSelectedTaskGroup();
      if (selectedTaskGroup != null) {
        String link = "software?" + PARAM_TASK_GROUP_ID + "=" + selectedTaskGroup.getTaskGroupId();
        out.println("        <li class=\"selectLevel1\"><a href=\"" + link + "\">" + selectedTaskGroup.getLabel()
            + "</a>");
        query = dataSession.createQuery("from TestPanel where taskGroup = ? order by label");
        query.setParameter(0, selectedTaskGroup);
        List<TestPanel> testPanelList = query.list();
        if (testPanelList.size() > 0) {
          out.println("          <ul class=\"selectLevel2\">");
          TestPanel selectedTestPanel = user.getSelectedTestPanel();
          if (selectedTestPanel != null) {
            link = "software?" + PARAM_TEST_PANEL_ID + "=" + selectedTestPanel.getTestPanelId();
            out.println("            <li class=\"selectLevel2\"><a href=\"" + link + "\">"
                + selectedTestPanel.getLabel() + "</a></li>");
          }
          for (TestPanel testPanel : testPanelList) {
            link = "software?" + PARAM_TEST_PANEL_ID + "=" + testPanel.getTestPanelId();
            if (!testPanel.equals(selectedTestPanel)) {
              out.println("            <li class=\"selectLevel2\"><a href=\"" + link + "\">" + testPanel.getLabel()
                  + "</a></li>");
            }
          }
          out.println("          </ul>");
        }
        out.println("        </li>");
      }
      for (TaskGroup taskGroup : taskGroupList) {
        String link = "software?" + PARAM_TASK_GROUP_ID + "=" + taskGroup.getTaskGroupId();
        out.println("        <li class=\"selectLevel1\"><a href=\"" + link + "\">" + taskGroup.getLabel() + "</a></li>");
      }
      out.println("      </ul>");
    }
  }

}
