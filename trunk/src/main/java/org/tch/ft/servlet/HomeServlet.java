package org.tch.ft.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.tch.fc.model.Software;
import org.tch.ft.manager.UserManager;
import org.tch.ft.model.Expert;
import org.tch.ft.model.LoginAttempt;
import org.tch.ft.model.Role;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.User;

public class HomeServlet extends MainServlet
{
  public HomeServlet() {
    super("Home", ServletProtection.NONE);
  }

  public static final String ACTION_LOGIN = "Login";
  public static final String ACTION_LOGOUT = "Logout";

  public static final String PARAM_NAME = "name";
  public static final String PARAM_PASSWORD = "password";

  @Override
  public String execute(HttpServletRequest req, HttpServletResponse resp, String action, String show)
      throws IOException {
    if (action != null) {
      if (action.equals(ACTION_LOGIN)) {
        name = req.getParameter(PARAM_NAME);
        password = req.getParameter(PARAM_PASSWORD);

        Session dataSession = applicationSession.getDataSession();
        boolean authenticated = false;
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user = UserManager.login(user, dataSession);
        authenticated = user.isLoggedIn();
        Transaction transaction = dataSession.beginTransaction();
        try {
          LoginAttempt loginAttempt = new LoginAttempt();
          loginAttempt.setName(name);
          loginAttempt.setLoginDate(new Date());
          loginAttempt.setPassword(password);
          if (authenticated) {
            loginAttempt.setUser(user);
          }
          dataSession.save(loginAttempt);
        } finally {
          transaction.commit();
        }
        if (authenticated) {

          user.setAdmin(user.getName().equals("Nathan Bunker"));
          applicationSession.setUser(user);

          Query query = dataSession.createQuery("from Expert where user = ?");
          query.setParameter(0, user);
          List<Expert> expertList = query.list();
          user.setExpertList(expertList);
          user.setMemberOfGroup(expertList.size() > 0);

          boolean signedAgreement = false;
          if (user.getAgreement() != null && user.getAgreementDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -90);
            signedAgreement = user.getAgreementDate().after(calendar.getTime());
          }
          user.setAgreedToAgreement(signedAgreement);

          applicationSession.setAlertInformation("Welcome!");
          name = "";
          password = "";
        } else {
          applicationSession.setAlertError("Unrecognized name or password, unable to login. ");
          password = "";
        }

      } else if (action.equals(ACTION_LOGOUT)) {
        applicationSession.getDataSession().disconnect();
        applicationSession.getDataSession().close();
        applicationSession.setDataSession(null);
        applicationSession = new ApplicationSession();
      }
    }

    return show;
  }

  private String name = "";
  private String password = "";

  @Override
  protected void printPage(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, String show)
      throws ServletException, IOException {
    out.println("<div class=\"leftColumn\">");
    if (applicationSession.getUser() == null || !applicationSession.getUser().isLoggedIn()) {
      out.println("<p>Welcome, please login to continue. </p>");
      out.println("<h2>Login</h2>");
      out.println("<form method=\"POST\" action=\"home\"> ");
      out.println("  <table> ");
      out.println("    <tr>");
      out.println("      <td align=\"right\">Name: </td>");
      out.println("      <td><input type=\"text\" name=\"" + PARAM_NAME + "\" value=\"" + name + "\"/></td>");
      out.println("    </tr>");
      out.println("    <tr>");
      out.println("      <td align=\"right\">Password: </td>");
      out.println("      <td><input type=\"password\" name=\"" + PARAM_PASSWORD + "\" value=\"\"/></td>");
      out.println("    </tr>");
      out.println("    <tr>");
      out.println("      <td></td>");
      out.println("      <td>");
      out.println("        <input type=\"submit\" name=\"" + PARAM_ACTION + "\" value=\"" + ACTION_LOGIN + "\"/>");
      out.println("        <input type=\"reset\" value=\"Reset\"/>");
      out.println("      </td>");
      out.println("    </tr>");
      out.println("  </table> ");
      out.println("</form>");
    } else if (!applicationSession.getUser().isAgreedToAgreement()) {
      out.println("<p>User agreement not signed yet</p>");
    } else if (!applicationSession.getUser().isMemberOfGroup()) {
      out.println("<p>Not yet assigned to expert group</p>");
    } else {
      out.println("<h2>Logout</h2>");
      out.println("<form method=\"POST\" action=\"home\"> ");
      out.println("  <table> ");
      out.println("    <tr>");
      out.println("      <td align=\"right\">Name: </td>");
      out.println("      <td>" + applicationSession.getUser().getName() + "</td>");
      out.println("    </tr>");
      out.println("    <tr>");
      out.println("      <td></td>");
      out.println("      <td>");
      out.println("        <input type=\"submit\" name=\"" + PARAM_ACTION + "\" value=\"" + ACTION_LOGOUT + "\"/>");
      out.println("      </td>");
      out.println("    </tr>");
      out.println("  </table> ");
      out.println("</form>");
    }
    out.println("</div>");
    name = "";
    password = "";

  }
}
