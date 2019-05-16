package org.immregistries.vfa.servlet;

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
import org.immregistries.vfa.connect.model.Service;
import org.immregistries.vfa.connect.model.ServiceOption;
import org.immregistries.vfa.connect.model.Software;
import org.immregistries.vfa.connect.model.SoftwareSetting;
import org.immregistries.vfa.manager.ForecastActualExpectedCompare;
import org.immregistries.vfa.manager.ForecastActualGenerator;
import org.immregistries.vfa.manager.SoftwareManager;
import org.immregistries.vfa.model.Expert;
import org.immregistries.vfa.model.Result;
import org.immregistries.vfa.model.Role;
import org.immregistries.vfa.model.TaskGroup;
import org.immregistries.vfa.model.TestPanel;
import org.immregistries.vfa.model.TestPanelCase;
import org.immregistries.vfa.model.User;

public class ReportsServlet extends MainServlet
{

  public ReportsServlet() {
    super("Reports", ServletProtection.ALL_USERS);
  }

  private final static String ACTION_ = "";

  private final static String SHOW_ = "";

  private final static String PARAM_ = "";

  private final static HoverText HOVER_TEXT_ = new HoverText("")
      .add("<p></p>");

  @Override
  public String execute(HttpServletRequest req, HttpServletResponse resp, String action, String show)
      throws IOException {
    Session dataSession = applicationSession.getDataSession();
    User user = applicationSession.getUser();
    return show;
  }

  @Override
  protected void printPage(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, String show)
      throws ServletException, IOException {
    Session dataSession = applicationSession.getDataSession();
    User user = applicationSession.getUser();
    Software software = user.getSelectedSoftware();

    out.println("<div class=\"leftColumn\">");
    out.println("</div>");
    if (show == null || show.equals(SHOW_)) {

      out.println("<div class=\"centerLeftColumn\">");

      out.println("</div>");
    }

  }

 
}
