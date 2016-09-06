package org.tch.ft.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.util.file.FileDeleteStrategy;
import org.apache.wicket.util.file.IFileCleaner;
import org.apache.wicket.util.upload.DiskFileItemFactory;
import org.apache.wicket.util.upload.FileItem;
import org.apache.wicket.util.upload.ServletFileUpload;
import org.hibernate.Query;
import org.hibernate.Session;
import org.tch.fc.model.Software;
import org.tch.fc.model.VaccineGroup;
import org.tch.ft.manager.readers.TestCaseReader;
import org.tch.ft.manager.writers.TestCaseWriterFactory;
import org.tch.ft.manager.writers.TestCaseWriterFormatType;
import org.tch.ft.manager.writers.TestResultWriterFactory;
import org.tch.ft.manager.writers.TestResultWriterFormatType;
import org.tch.ft.manager.writers.WriterInterface;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.TestPanelCase;
import org.tch.ft.model.User;

public class ToolsServlet extends MainServlet
{

  public ToolsServlet() {
    super("Tools", ServletProtection.ALL_USERS);
  }

  private final static String ACTION_IMPORT = "Import";
  private final static String ACTION_EXPORT_TEST_CASES = "Export Test Cases";
  private final static String ACTION_EXPORT_TEST_RESULTS = "Export Test Results";

  private final static String SHOW_IMPORT_TEST_CASES = "importTestCases";
  private final static String SHOW_EXPORT_TEST_CASES = "exportTestCases";
  private final static String SHOW_EXPORT_TEST_RESULTS = "exportTestResults";

  private final static String PARAM_FILE1 = "file1";
  private final static String PARAM_FORMAT_TYPE = "formatType";
  private final static String PARAM_SOFTWARE_ID = "softwareId";
  private final static String PARAM_TEST_PANEL_ID = "testPanelId";
  private final static String PARAM_VACCINE_GROUP_ID = "vaccineGroupId";

  private final static String PARAM_CATEGORY_NAME = "categoryName";
  private final static String PARAM_RUN_ALL = "runAll";

  private final static HoverText HOVER_TEXT_TEST_CASE_FILE = new HoverText("Test Case File")
      .add("<p>Select the file that you want to upload. </p>");
  private final static HoverText HOVER_TEXT_FORMAT = new HoverText("Format")
      .add("<p>Select the format of the data in the file. </p>")
      .add("<ul><li><b>CDC</b>CSV formatted data from the CDSi test case spreadsheets. </li>")
      .add("<li><b>IHS</b> A simple format used by IHS to upload example test cases. </li>")
      .add("<li><b>MIIS</b> Massachusetts Immunization Information System CSV format.</li>")
      .add("<li><b>STC</b> Scientific Technologies Corporation CSV format. </li></ul>");
  private final static HoverText HOVER_TEXT_FORMAT_EXPORT = new HoverText("Format")
      .add("<p>Select the format of the test case data. </p>")
      .add("<ul><li><b>CDC</b>CSV formatted data from the CDSi test case spreadsheets. </li>")
      .add("<li><b>Epic</b>Epic's Immunization Scheduling Tester </li></ul>");
  private final static HoverText HOVER_TEXT_VACCINE_GROUP = new HoverText("Vaccine Group")
      .add("<p>Exclude all vaccine groups except this in the export.  </p>");
  private final static HoverText HOVER_TEXT_SET_EXPECTATIONS = new HoverText("Set Expectations")
      .add("<p>Set expectations for each test case based on actuals from the selected software. ")
      .add("  This option is used for test case formats that don't include expectations, such ")
      .add("  as those that are random samples from patient histories. </p>");
  private final static HoverText HOVER_TEXT_TASK_GROUP = new HoverText("Task Group")
      .add("<p>The currently selected Task Group. ")
      .add("A different Task Group can be selected by returning to the Test Cases and ")
      .add("selecting the Task Group in the navigation box on the left. </p> ");
  private final static HoverText HOVER_TEXT_TEST_PANEL = new HoverText("Test Panel")
      .add("<p> The currently selected Test Panel. ")
      .add("A different Test Panel can be selected by returning to the Test Cases and ")
      .add("selecting the Test Panel in the navigation box on the left. </p>");
  private final static HoverText HOVER_TEXT_EXPORT_FOR = new HoverText("Export For")
      .add("<p>Specify which categories to export. </p>");
  private final static HoverText HOVER_TEXT_LIMIT_BY_CATEGORIES = new HoverText("Limit by Categories")
      .add("<p>Select which categories to export. </p>");
  private final static HoverText HOVER_TEXT_ = new HoverText("").add("<p></p>");

  @Override
  public String execute(HttpServletRequest req, HttpServletResponse resp, String action, String show)
      throws IOException {
    Session dataSession = applicationSession.getDataSession();
    User user = applicationSession.getUser();

    if (action != null) {

      if (action.equals(ACTION_IMPORT)) {

        IFileCleaner fileCleaner = new IFileCleaner() {

          public void track(File file, Object marker, FileDeleteStrategy deleteStrategy) {
            // TODO Auto-generated method stub

          }

          public void track(File file, Object marker) {
            // TODO Auto-generated method stub

          }

          public void destroy() {
            // TODO Auto-generated method stub

          }
        };

        DiskFileItemFactory fileItemFactory = new DiskFileItemFactory(fileCleaner);
        fileItemFactory.setSizeThreshold(1 * 1024 * 1024); // 1 MB
        String uploadDirString = ""; // ksm.getKeyedValue(KeyedSetting.UPLOAD_DIR,
                                     // ".");
        File uploadDir = new File(uploadDirString);
        if (!uploadDir.exists()) {
          throw new IllegalArgumentException("Upload directory not found, unable to upload");
        }
        File uploadDirTemp = new File(uploadDir, "/temp");
        if (!uploadDirTemp.exists()) {
          uploadDirTemp.mkdir();
        }
        File uploadDirDest = new File(uploadDirTemp, "/process");
        if (!uploadDirDest.exists()) {
          uploadDirDest.mkdir();
        }
        String formatType = null;
        int softwareId = 0;
        fileItemFactory.setRepository(uploadDirTemp);
        ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
        File file = null;
        String filename = "";
        try {
          List<FileItem> items = uploadHandler.parseRequest(req);
          for (FileItem item : items) {
            /*
             * Handle Form Fields.
             */
            if (item.isFormField()) {
              if (item.getFieldName().equals(PARAM_FORMAT_TYPE)) {
                formatType = item.getString();
              } else if (item.getFieldName().equals(PARAM_SOFTWARE_ID)) {
                if (item.getString() != null) {
                  softwareId = Integer.parseInt(item.getString());
                }
              }
            } else {
              file = new File(uploadDirDest, item.getName());
              filename = item.getName();
              item.write(file);
            }
          }
        } catch (Exception ex) {
          // throw new ServletException("Unable to upload file", ex);
        }

      } else if (action.equals(ACTION_EXPORT_TEST_CASES) || action.equals(ACTION_EXPORT_TEST_RESULTS)) {
        TestPanel testPanel = user.getSelectedTestPanel();

        Set<String> categoryNameSet = null;
        if (req.getParameter(PARAM_RUN_ALL) == null) {
          categoryNameSet = new HashSet<String>();
          if (req.getParameterValues(PARAM_CATEGORY_NAME) != null) {
            for (String categoryName : req.getParameterValues(PARAM_CATEGORY_NAME)) {
              categoryNameSet.add(categoryName);
            }
          }
        }

        String problem = null;

        if (categoryNameSet != null && categoryNameSet.size() == 0) {
          problem = "Unable to export tests, no categories selected. ";
        }

        if (req.getParameter(PARAM_FORMAT_TYPE).equals("")) {
          problem = "Format was not specified";
        }

        if (problem != null) {
          applicationSession.setAlertError(problem);
          if (action.equals(ACTION_EXPORT_TEST_CASES)) {
            return SHOW_EXPORT_TEST_CASES;
          } else {
            return SHOW_EXPORT_TEST_RESULTS;
          }
        }
        VaccineGroup vaccineGroup = null;
        if (!req.getParameter(PARAM_VACCINE_GROUP_ID).equals("")) {
          vaccineGroup = (VaccineGroup) dataSession.get(VaccineGroup.class,
              Integer.parseInt(req.getParameter(PARAM_VACCINE_GROUP_ID)));
        }
        WriterInterface writer;
        if (action.equals(ACTION_EXPORT_TEST_CASES)) {
          TestCaseWriterFormatType formatType = TestCaseWriterFormatType.valueOf(req.getParameter(PARAM_FORMAT_TYPE));
          writer = TestCaseWriterFactory.createTestCaseWriter(formatType);
        } else {
          TestResultWriterFormatType formatType = TestResultWriterFormatType.valueOf(req
              .getParameter(PARAM_FORMAT_TYPE));
          writer = TestResultWriterFactory.createTestResultWriter(formatType);
        }

        writer.setVaccineGroup(vaccineGroup);
        writer.setCategoryNameSet(categoryNameSet);
        writer.setDataSession(dataSession);
        writer.setTestPanel(testPanel);
        writer.setUser(user);

        resp.setContentType("text/plain");
        resp.setHeader("Content-Disposition", "Attachment;filename=\"" + writer.createFilename() + "\"");
        PrintWriter out = new PrintWriter(resp.getOutputStream());

        writer.write(out);
        out.close();
        return SHOW_NOTHING;
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
    TestPanel testPanel = user.getSelectedTestPanel();

    out.println("<div class=\"leftColumn\">");
    out.println("<h2>Tools</h2>");
    out.println("<ul class=\"selectLevel1\">");
    out.println("  <li class=\"selectLevel1\"><a href=\"tools?" + PARAM_SHOW + "=" + SHOW_EXPORT_TEST_CASES
        + "\">Export Test Cases</a></li>");
    out.println("  <li class=\"selectLevel1\"><a href=\"tools?" + PARAM_SHOW + "=" + SHOW_EXPORT_TEST_RESULTS
        + "\">Export Test Results</a></li>");
    out.println("</ul>");
    out.println("</div>");
    if (show != null) {
      if (show.equals(SHOW_IMPORT_TEST_CASES)) {

        out.println("<div class=\"centerLeftColumn\">");
        out.println("  <form enctype=\"multipart/form-data\" method=\"POST\" action=\"tools\">");
        out.println("  <table>");
        out.println("    <tr>");
        out.println("      <th>" + HOVER_TEXT_TEST_CASE_FILE + "</th>");
        out.println("      <th><input type=\"file\" name=\"file1\"></th>");
        out.println("    </tr>");
        out.println("    <tr>");
        out.println("      <th>" + HOVER_TEXT_FORMAT + "</th>");
        out.println("      <td>");
        out.println("        <select name=\"" + PARAM_FORMAT_TYPE + "\">");
        out.println("          <option value=\"\">--select--</option>");
        for (TestCaseReader.FormatType formatType : TestCaseReader.FormatType.values()) {
          out.println("              <option value=\"" + formatType + "\">" + formatType + "</option>");
        }
        out.println("        </select>");
        out.println("      </td>");
        out.println("    </tr>");
        out.println("    <tr>");
        out.println("      <th>" + HOVER_TEXT_SET_EXPECTATIONS + "</th>");
        out.println("        <select name=\"" + PARAM_SOFTWARE_ID + "\">");
        out.println("          <option value=\"\">--select--</option>");
        out.println("        </select>");
        out.println("    </tr>");
        out.println("          <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\"" + PARAM_ACTION
            + "\" size=\"15\" value=\"" + ACTION_IMPORT + "\"/></td>");
        out.println("  </table>");
        out.println("  </form>");
        out.println("</div>");
      } else if (show.equals(SHOW_EXPORT_TEST_CASES)) {
        showExportTestCases(out, dataSession, user, software, testPanel);
      } else if (show.equals(SHOW_EXPORT_TEST_RESULTS)) {
        showExportTestResults(out, dataSession, user, software, testPanel);
      }
    }

  }

  private void showExportTestCases(PrintWriter out, Session dataSession, User user, Software software,
      TestPanel testPanel) {
    printShowExportAllScript(out);
    out.println("<div class=\"centerLeftColumn\">");
    out.println("<h3>Export Test Cases</h3>");
    if (testPanel == null) {
      out.println("<p>Please select a test panel in the Test Cases area first before exporting here. </p>");
    } else {

      out.println("  <form method=\"POST\" action=\"tools\" id=\"exportTestsForm\">");
      out.println("  <input type=\"hidden\" name=\"" + PARAM_SOFTWARE_ID + "\" value=\"" + software.getSoftwareId()
          + "\"/>");
      out.println("  <table width=\"100%\"> ");
      printTaskGroupAndTestPanel(out, testPanel);
      out.println("    <tr>");
      out.println("      <th>" + HOVER_TEXT_FORMAT_EXPORT + "</th>");
      out.println("      <td>");
      out.println("        <select name=\"" + PARAM_FORMAT_TYPE + "\">");
      out.println("          <option value=\"\">--select--</option>");
      for (TestCaseWriterFormatType formatType : TestCaseWriterFormatType.values()) {
        out.println("              <option value=\"" + formatType + "\">" + formatType + "</option>");
      }
      out.println("        </select>");
      out.println("      </td>");
      out.println("    </tr>");
      printFormatAndVaccineGroupRows(out, dataSession, user, testPanel);
      out.println("  <tr>");
      out.println("    <td align=\"right\" colspan=\"2\"><input type=\"submit\" name=\"" + PARAM_ACTION
          + "\" size=\"15\" value=\"" + ACTION_EXPORT_TEST_CASES + "\"/></td>");
      out.println("  </tr>");
      out.println("  </table> ");
      out.println("</form>");
    }
    out.println("</div>");
  }

  private void showExportTestResults(PrintWriter out, Session dataSession, User user, Software software,
      TestPanel testPanel) {
    printShowExportAllScript(out);
    out.println("<div class=\"centerLeftColumn\">");
    out.println("<h3>Export Test Results</h3>");
    if (testPanel == null) {
      out.println("<p>Please select a test panel in the Test Cases area first before exporting here. </p>");
    } else {

      out.println("  <form method=\"POST\" action=\"tools\" id=\"exportTestsForm\">");
      out.println("  <input type=\"hidden\" name=\"" + PARAM_SOFTWARE_ID + "\" value=\"" + software.getSoftwareId()
          + "\"/>");
      out.println("  <table width=\"100%\"> ");
      printTaskGroupAndTestPanel(out, testPanel);
      out.println("    <tr>");
      out.println("      <th>" + HOVER_TEXT_FORMAT_EXPORT + "</th>");
      out.println("      <td>");
      out.println("        <select name=\"" + PARAM_FORMAT_TYPE + "\">");
      out.println("          <option value=\"\">--select--</option>");
      for (TestResultWriterFormatType formatType : TestResultWriterFormatType.values()) {
        out.println("              <option value=\"" + formatType + "\">" + formatType + "</option>");
      }
      out.println("        </select>");
      out.println("      </td>");
      out.println("    </tr>");
      printFormatAndVaccineGroupRows(out, dataSession, user, testPanel);
      out.println("  <tr>");
      out.println("    <td align=\"right\" colspan=\"2\"><input type=\"submit\" name=\"" + PARAM_ACTION
          + "\" size=\"15\" value=\"" + ACTION_EXPORT_TEST_RESULTS + "\"/></td>");
      out.println("  </tr>");
      out.println("  </table> ");
      out.println("</form>");
    }
    out.println("</div>");
  }

  private void printFormatAndVaccineGroupRows(PrintWriter out, Session dataSession, User user, TestPanel testPanel) {
    out.println("    <tr>");
    out.println("      <th>" + HOVER_TEXT_VACCINE_GROUP + "</th>");
    out.println("      <td>");
    out.println("        <select name=\"" + PARAM_VACCINE_GROUP_ID + "\">");
    out.println("          <option value=\"\">--select--</option>");
    {
      Query query = dataSession.createQuery("from VaccineGroup order by label");
      List<VaccineGroup> vaccineGroupList = query.list();
      for (VaccineGroup vaccineGroup : vaccineGroupList) {
        out.println("              <option value=\"" + vaccineGroup.getVaccineGroupId() + "\">"
            + vaccineGroup.getLabel() + "</option>");
      }
    }
    out.println("        </select>");
    out.println("      </td>");
    out.println("    </tr>");

    Set<String> categoryNameSet = applicationSession.getForecastCompareCategoryNameSet();
    out.println("    <tr>");
    out.println("      <th>" + HOVER_TEXT_EXPORT_FOR + "</th>");
    out.println("      <td><input type=\"checkbox\" name=\"" + PARAM_RUN_ALL + "\" value=\"true\""
        + (categoryNameSet == null ? " checked=\"true\"" : "") + " onChange=\"showExportAll()\"/> All Categories</td>");
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
    out.println("    </tr>");
  }

  private void printTaskGroupAndTestPanel(PrintWriter out, TestPanel testPanel) {
    out.println("    <tr>");
    out.println("      <th>" + HOVER_TEXT_TASK_GROUP + "</th>");
    out.println("      <td>" + testPanel.getTaskGroup().getLabel() + "</td>");
    out.println("    </tr>");
    out.println("    <tr>");
    out.println("      <th>" + HOVER_TEXT_TEST_PANEL + "</th>");
    out.println("      <td>" + testPanel.getLabel() + "</td>");
    out.println("    </tr>");
  }

  private void printShowExportAllScript(PrintWriter out) {
    out.println("<script>");
    out.println("  <!-- ");
    out.println("  function showExportAll() { ");
    out.println("    var form = document.getElementById('exportTestsForm'); ");
    out.println("    var runAllTests = form." + PARAM_RUN_ALL + ".checked");
    out.println("    var rowToShow = document.getElementById('runSome'); ");
    out.println("    if (rowToShow != null) { ");
    out.println("      rowToShow.style.display = runAllTests ? 'none' : 'table-row'; ");
    out.println("    }");
    out.println("  }");
    out.println("  -->");
    out.println("</script>");
  }

}
