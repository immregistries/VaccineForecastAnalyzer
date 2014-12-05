package org.tch.ft.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.tch.ft.model.User;

public abstract class MainServlet extends HttpServlet
{

  public static final String PARAM_ACTION = "action";
  public static final String PARAM_SHOW = "show";

  private String title;
  private ServletProtection servletProtection = null;
  protected HttpSession webSession = null;
  protected ApplicationSession applicationSession = null;

  public MainServlet(String title, ServletProtection servletProtection) {
    this.title = title;
    this.servletProtection = servletProtection;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    setup(req);
    if (verifyOkayToExecute()) {
      String show = execute(req, resp);
      printPage(req, resp, show);
    } else {
      RequestDispatcher requestDispatcher = req.getRequestDispatcher("/s/home");
      requestDispatcher.forward(req, resp);
    }
  }

  public boolean verifyOkayToExecute() {
    boolean okayToExecute = false;

    if (servletProtection == ServletProtection.NONE) {
      okayToExecute = true;
    } else if (servletProtection == ServletProtection.ALL_USERS) {
      okayToExecute = applicationSession.getUser() != null && applicationSession.getUser().isLoggedIn();
    } else if (servletProtection == ServletProtection.ADMIN_ONLY) {
      // TODO Determine if user is admin
      okayToExecute = applicationSession.getUser() != null && applicationSession.getUser().isLoggedIn();
    }
    return okayToExecute;
  }

  public void setup(HttpServletRequest req) {
    webSession = req.getSession(true);
    applicationSession = (ApplicationSession) (ApplicationSession) webSession.getAttribute("applicationSession");
    if (applicationSession == null) {
      applicationSession = new ApplicationSession();
    }
    webSession.setAttribute("applicationSession", applicationSession);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    setup(req);
    if (verifyOkayToExecute()) {
      String show = execute(req, resp);
      printPage(req, resp, show);
    } else {
      RequestDispatcher requestDispatcher = req.getRequestDispatcher("/s/home");
      requestDispatcher.forward(req, resp);
    }
  }

  public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    return execute(req, resp, req.getParameter(PARAM_ACTION), req.getParameter(PARAM_SHOW));
  }

  public abstract String execute(HttpServletRequest req, HttpServletResponse resp, String action, String show) throws IOException;

  public void printPage(HttpServletRequest req, HttpServletResponse resp, String show) throws IOException {
    PrintWriter out = new PrintWriter(resp.getWriter());
    try {
      printHeader(out);
      printPage(req, resp, out, show);
      printFooter(out);
    } catch (Exception e) {
      out.println("<div class=\"oops\">");
      out.println("<h1>Oops!</h1>");
      out.println("<p>The TCH Forecast Tester has encountered an unexpected problem and is unable to proccess your "
          + "request. We apologize for the inconvience. You should not be seeing this issue. </p>");
      out.println("<p><a href=\"home\">Return to Home</a></p>");
      out.println("<h3>Technical Details</h3>");
      out.println("<p>Problem encountered: " + e.getMessage() + "</p>");
      out.println("<pre>");
      e.printStackTrace(out);
      out.println("</pre>");
      out.println("</div>");
      e.printStackTrace();
    }
    out.close();
  }

  public void printHeader(PrintWriter out) {
    out.println("<html>");
    out.println("  <head>");
    out.println("    <title>TCH Forecast Tester - " + title + "</title>");
    out.println("    <link rel=\"stylesheet\" type=\"text/css\" href=\"../s.css\" />");
    out.println("  </head>");
    out.println("  <body>");
    User user = applicationSession.getUser();
    if (user != null && user.isLoggedIn()) {
      if (user.isAgreedToAgreement() && user.isMemberOfGroup()) {
        out.println("    <table class=\"menu\">");
        out.println("      <tr>");
        out.println("        <td class=\"menuCell\">");
        out.println("          <a href=\"home\" class=\"menuLink\">Home</a>");
        out.println("        </td>");
        out.println("        <td class=\"menuCell\">");
        out.println("          <a href=\"testCases\" class=\"menuLink\">Test Cases</a>");
        out.println("        </td>");
        out.println("        <td class=\"menuCell\">");
        out.println("          <a href=\"home\" class=\"menuLink\">CDSi Services</a>");
        out.println("        </td>");
        out.println("        <td class=\"menuCell\">");
        out.println("          <a href=\"home\" class=\"menuLink\">Concepts</a>");
        out.println("        </td>");
        out.println("        <td class=\"menuCell\">");
        out.println("          <a href=\"home\" class=\"menuLink\">Reports</a>");
        out.println("        </td>");
        out.println("        <td class=\"menuCell\">");
        out.println("          <a href=\"home\" class=\"menuLink\">Tools</a>");
        out.println("        </td>");
        out.println("      </tr>");
        out.println("    </table>");
      }
    } else {
      out.println("    <h1>TCH Forecast Tester</h1>");
    }
    out.println("    <div class=\"content\">");
    if (applicationSession.getAlertError() != null)
    {
      out.println("      <p class=\"alertError\">Error: " + applicationSession.getAlertError() + "</p>");
      applicationSession.setAlertError(null);
    }
    if (applicationSession.getAlertWarning() != null)
    {
      out.println("      <p class=\"alertWarning\">Warning: " + applicationSession.getAlertWarning() + "</p>");
      applicationSession.setAlertWarning(null);
    }
    if (applicationSession.getAlertInformation() != null)
    {
      out.println("      <p class=\"alertInformation\">" + applicationSession.getAlertInformation() + "</p>");
      applicationSession.setAlertInformation(null);
    }
  }

  public void printFooter(PrintWriter out) {
    out.println("    </div>");
    out.println("  </body>");
    out.println("</html>");
  }

  protected abstract void printPage(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, String show)
      throws ServletException, IOException;

  protected static String notNull(String s) {
    return notNull(s, "");
  }

  protected static String notNull(String s, String defaultValue) {
    if (s == null) {
      return defaultValue;
    }
    return s;
  }
  
  protected static int notNull(String s, int defaultValue) {
    if (s == null) {
      return defaultValue;
    }
    return Integer.parseInt(s);
  }
  
  protected static SimpleDateFormat createSimpleDateFormat()
  {
    return new SimpleDateFormat("MM/dd/yyyy");
  }
  protected SimpleDateFormat sdf = createSimpleDateFormat();
  

}
