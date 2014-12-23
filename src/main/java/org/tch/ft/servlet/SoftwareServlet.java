package org.tch.ft.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.util.UrlEncoded;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.tch.fc.model.Software;
import org.tch.ft.manager.ForecastActualExpectedCompare;
import org.tch.ft.manager.ForecastActualGenerator;
import org.tch.ft.manager.SoftwareManager;
import org.tch.ft.model.Result;
import org.tch.ft.model.TaskGroup;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.TestPanelCase;
import org.tch.ft.model.User;

public class SoftwareServlet extends MainServlet
{

  public SoftwareServlet() {
    super("CDSI Services", ServletProtection.ALL_USERS);
  }

  private String ACTION_RUN_TESTS = "Run Tests";
  private String PARAM_SOFTWARE_ID = "softwareId";
  private String PARAM_TASK_GROUP_ID = "taskGroupId";
  private String PARAM_TEST_PANEL_ID = "testPanelId";
  private String PARAM_CATEGORY_NAME = "categoryName";
  private String PARAM_RUN_ALL = "runAll";
  private String PARAM_VERIFY_EVALUATION_STATUS = "verifyEvalationStatus";
  private String PARAM_VERIFY_FORECAST_STATUS = "verifyForecastStatus";
  private String PARAM_VERIFY_FORECAST_DOSE = "verifyForecastDose";
  private String PARAM_VERIFY_FORECAST_VALID_DATE = "verifyForecastValidDate";
  private String PARAM_VERIFY_FORECAST_DUE_DATE = "verifyForecastDueDate";
  private String PARAM_VERIFY_FORECAST_OVERDUE_DATE = "verifyForecastOverdueDate";
  private String PARAM_VERIFY_FORECAST_FINISHED_DATE = "verifyForecastFinishedDate";

  private String SHOW_EXECUTE = "execute";

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

        Set<String> categoryNameSet = null;
        if (req.getParameter(PARAM_RUN_ALL) == null) {
          categoryNameSet = new HashSet<String>();
          for (String categoryName : req.getParameterValues(PARAM_CATEGORY_NAME)) {
            categoryNameSet.add(categoryName);
          }
        }
        applicationSession.setForecastCompareCategoryNameSet(categoryNameSet);

        try {
          ForecastActualGenerator.runForecastActual(testPanel, software, categoryNameSet, dataSession, false);
          List<ForecastActualExpectedCompare> forecastCompareList = ForecastActualGenerator.createForecastComparison(
              testPanel, software, categoryNameSet, dataSession);
          Collections.sort(forecastCompareList, new ForecastActualExpectedCompare.ForecastCompareComparator());
          applicationSession.setForecastCompareList(forecastCompareList);
          Map<TestPanelCase, ForecastActualExpectedCompare> forecastCompareMap = new HashMap<TestPanelCase, ForecastActualExpectedCompare>();
          applicationSession.setForecastCompareCategoryHasProblemSet(new HashSet<String>());
          applicationSession.setForecastCompareTestPanelCaseHasProblemSet(new HashSet<TestPanelCase>());
          for (ForecastActualExpectedCompare forecastCompare : forecastCompareList) {
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
      }
    }
    return SHOW_EXECUTE;
  }

  @Override
  protected void printPage(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, String show)
      throws ServletException, IOException {
    Session dataSession = applicationSession.getDataSession();
    User user = applicationSession.getUser();

    if (show.equals(SHOW_EXECUTE)) {
      printSoftwareTree(out, dataSession, user);

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
      TestPanel testPanel = user.getSelectedTestPanel();
      if (testPanel != null) {
        out.println("<h2>" + testPanel.getTaskGroup().getLabel() + ": " + testPanel.getLabel() + "</h2>");
        out.println("  <form method=\"POST\" action=\"software\" id=\"runTestsForm\">");
        out.println("  <input type=\"hidden\" name=\"" + PARAM_SOFTWARE_ID + "\" value=\""
            + user.getSelectedSoftware().getSoftwareId() + "\"/>");
        out.println("  <table width=\"100%\"> ");
        out.println("    <tr>");
        out.println("      <th>Run Tests</th>");
        out.println("    </tr>");
        out.println("    <tr>");
        out.println("      <td><input type=\"checkbox\" name=\"" + PARAM_RUN_ALL
            + "\" value=\"true\" checked=\"true\"  onChange=\"showRunAll()\"/> run all categories</td>");
        out.println("    </tr>");
        out.println("  <tr style=\"display: none;\" id=\"runSome\">");
        out.println("    <td>");
        Query query = dataSession.createQuery("from TestPanelCase where testPanel = ? order by categoryName");
        query.setParameter(0, testPanel);
        List<TestPanelCase> testPanelCaseList = query.list();
        String categoryName = "";
        for (TestPanelCase testPanelCase : testPanelCaseList) {
          if (!testPanelCase.getCategoryName().equals(categoryName)) {
            if (user.getSelectedTestPanelCase() != null
                && user.getSelectedTestPanelCase().getCategoryName().equals(testPanelCase.getCategoryName())) {
              out.println("<input type=\"checkbox\" name=\"" + PARAM_CATEGORY_NAME + "\" value=\""
                  + UrlEncoded.encodeString(testPanelCase.getCategoryName(), "UTF-8") + "\" checked=\"true\"/>"
                  + testPanelCase.getCategoryName() + "<br/>");
            } else {
              out.println("<input type=\"checkbox\" name=\"" + PARAM_CATEGORY_NAME + "\" value=\""
                  + UrlEncoded.encodeString(testPanelCase.getCategoryName(), "UTF-8") + "\"/>"
                  + testPanelCase.getCategoryName() + "<br/>");
            }
          }
          categoryName = testPanelCase.getCategoryName();
        }
        out.println("    </td>");
        out.println("  </tr>");
        if (false) {
          out.println("    <tr>");
          out.println("      <td>");
          out.println("        Verify Evaluation <br/>");
          out.println("        <input type=\"checkbox\" name=\"" + PARAM_VERIFY_EVALUATION_STATUS
              + "\" value=\"true\" checked=\"true\" disabled=\"true\"/> status <br/>");
          out.println("        Verify Forecast <br/>");
          out.println("        <input type=\"checkbox\" name=\"" + PARAM_VERIFY_FORECAST_STATUS
              + "\" value=\"true\" checked=\"true\"/> status <br/>");
          out.println("        <input type=\"checkbox\" name=\"" + PARAM_VERIFY_FORECAST_DOSE
              + "\" value=\"true\" checked=\"true\"/> dose <br/>");
          out.println("        <input type=\"checkbox\" name=\"" + PARAM_VERIFY_FORECAST_VALID_DATE
              + "\" value=\"true\" checked=\"true\"/> earliest date <br/>");
          out.println("        <input type=\"checkbox\" name=\"" + PARAM_VERIFY_FORECAST_DUE_DATE
              + "\" value=\"true\" checked=\"true\"/> recommend date <br/>");
          out.println("        <input type=\"checkbox\" name=\"" + PARAM_VERIFY_FORECAST_OVERDUE_DATE
              + "\" value=\"true\" checked=\"true\"/> past due date <br/>");
          out.println("        <input type=\"checkbox\" name=\"" + PARAM_VERIFY_FORECAST_FINISHED_DATE
              + "\" value=\"true\" checked=\"true\"/> finished date <br/>");
          out.println("      </td>");
          out.println("    </tr>");
        }
        out.println("  <tr>");
        out.println("    <td align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION + "\" size=\"15\" value=\""
            + ACTION_RUN_TESTS + "\"/></td>");
        out.println("  </tr>");
        out.println("  </table> ");
        out.println("</form>");
      }
      out.println("</div>");

      if (applicationSession.getForecastCompareList() != null
          && applicationSession.getForecastCompareTestPanel().equals(testPanel)) {
        out.println("<div class=\"centerRightColumn\">");
        out.println("<h2>Test Results</h2>");
        out.println("  <table width=\"100%\">");
        out.println("    <tr>");
        out.println("      <th>Category</th>");
        out.println("      <th>Test Case</th>");
        out.println("      <th>Status</th>");
        out.println("      <th>Comparison</th>");
        out.println("    </tr>");
        for (ForecastActualExpectedCompare forecastCompare : applicationSession.getForecastCompareList()) {
          String styleClass = "";
          String link = "testCases?" + PARAM_ACTION + "="
              + UrlEncoded.encodeString(TestCasesServlet.ACTION_SELECT_TEST_PANEL_CASE, "UTF-8") + "&"
              + TestCasesServlet.PARAM_TEST_PANEL_CASE_ID + "="
              + forecastCompare.getTestPanelCase().getTestPanelCaseId();
          out.println("    <tr>");
          if (user.getSelectedTestPanelCase() != null
              && user.getSelectedTestPanelCase().equals(forecastCompare.getTestPanelCase())) {
            styleClass = "highlight";
          }
          out.println("      <td class=\"" + styleClass + "\"><a href=\"" + link + "\">"
              + forecastCompare.getTestPanelCase().getCategoryName() + "</a></td>");
          out.println("      <td class=\"" + styleClass + "\"><a href=\"" + link + "\">"
              + forecastCompare.getForecastResultA().getTestCase().getLabel() + "</a></td>");
          Result result = forecastCompare.getTestPanelCase().getResult();
          if (result != null) {
            if (result == Result.ACCEPT || result == Result.PASS) {
              styleClass = "pass";
            } else {
              styleClass = "fail";
            }
          }

          out.println("      <td class=\"" + styleClass + "\">" + (result == null ? "-" : result.getLabel()) + "</td>");
          styleClass = forecastCompare.matchExactly() ? "pass" : "fail";
          if (forecastCompare.getMatchSimilarity().equals("100%")) {
            out.println("      <td class=\"" + styleClass + "\">" + forecastCompare.getMatchStatus() + "</td>");
          } else {
            out.println("      <td class=\"" + styleClass + "\">" + forecastCompare.getMatchStatus() + " ("
                + forecastCompare.getMatchDifference() + ")</td>");

          }
          out.println("    </tr>");
        }
        out.println("  </table>");
        out.println("</div>");
      }

    }

  }

  public void printSoftwareTree(PrintWriter out, Session dataSession, User user) {
    out.println("<div class=\"leftColumn\">");

    Software selectedSoftware = user.getSelectedSoftware();
    if (selectedSoftware != null) {
      out.println("<h2>CDSi Service</h2>");
      out.println("  <ul class=\"selectLevel1\">");
      String link = "software?" + PARAM_SOFTWARE_ID + "=" + selectedSoftware.getSoftwareId();
      if (selectedSoftware != null) {
        out.println("      <li class=\"selectLevel1\"><a href=\"" + link + "\">" + selectedSoftware.getLabel() + "</a>");
      }

      Query query = dataSession.createQuery("from TaskGroup order by label");
      List<TaskGroup> taskGroupList = query.list();
      if (taskGroupList.size() > 0) {
        out.println("      <ul class=\"selectLevel2\">");
        TaskGroup selectedTaskGroup = user.getSelectedTaskGroup();
        if (selectedTaskGroup != null) {
          link = "software?" + PARAM_TASK_GROUP_ID + "=" + selectedTaskGroup.getTaskGroupId();
          out.println("        <li class=\"selectLevel2\"><a href=\"" + link + "\">" + selectedTaskGroup.getLabel()
              + "</a>");
          query = dataSession.createQuery("from TestPanel where taskGroup = ? order by label");
          query.setParameter(0, selectedTaskGroup);
          List<TestPanel> testPanelList = query.list();
          if (testPanelList.size() > 0) {
            out.println("          <ul class=\"selectLevel2\">");
            TestPanel selectedTestPanel = user.getSelectedTestPanel();
            if (selectedTestPanel != null) {
              link = "software?" + PARAM_TEST_PANEL_ID + "=" + selectedTestPanel.getTestPanelId();
              out.println("            <li class=\"selectLevel3\"><a href=\"" + link + "\">"
                  + selectedTestPanel.getLabel() + "</a></li>");
            }
            for (TestPanel testPanel : testPanelList) {
              link = "software?" + PARAM_TEST_PANEL_ID + "=" + testPanel.getTestPanelId();
              if (!testPanel.equals(selectedTestPanel)) {
                out.println("            <li class=\"selectLevel3\"><a href=\"" + link + "\">" + testPanel.getLabel()
                    + "</a></li>");
              }
            }
            out.println("          </ul>");
          }
          out.println("        </li>");
        }
        for (TaskGroup taskGroup : taskGroupList) {
          link = "software?" + PARAM_TASK_GROUP_ID + "=" + taskGroup.getTaskGroupId();
          out.println("        <li class=\"selectLevel2\"><a href=\"" + link + "\">" + taskGroup.getLabel()
              + "</a></li>");
        }
        out.println("      </ul>");
      }

      out.println("    </li>");
      out.println("  </ul>");
      out.println("<h3>Other CDSi Services</h3>");
    } else {
      out.println("<h3>CDSi Services</h3>");
    }
    out.println("  <ul class=\"selectLevel1\">");
    List<Software> softwareList = SoftwareManager.getListOfUnrestrictedSoftware(user, dataSession);
    for (Software software : softwareList) {
      if (selectedSoftware == null || !selectedSoftware.equals(software)) {
        String link = "software?" + PARAM_SOFTWARE_ID + "=" + software.getSoftwareId();
        out.println("      <li class=\"selectLevel1\"><a href=\"" + link + "\">" + software.getLabel() + "</a>");
      }
    }
    out.println("  </ul>");
    out.println("</div>");
  }

}
