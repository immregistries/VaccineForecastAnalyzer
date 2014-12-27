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

public class ConceptsServlet extends MainServlet
{

  public ConceptsServlet() {
    super("Concepts", ServletProtection.ALL_USERS);
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
