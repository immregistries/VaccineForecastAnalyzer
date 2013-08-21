/*
 * Copyright 2013 - Texas Children's Hospital
 * 
 *   Texas Children's Hospital licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */
package org.tch.ft.web;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.tch.ft.CentralControl;
import org.tch.ft.manager.UserManager;
import org.tch.ft.model.ForecastCompare;
import org.tch.ft.model.LoginAttempt;
import org.tch.ft.model.TestPanelCase;
import org.tch.ft.model.User;
import org.tch.ft.web.softwareCompare.CompareResults;

/**
 * Authenticates the session based on role
 */
public class WebSession extends AuthenticatedWebSession {

  private static final long serialVersionUID = 1L;

  private Roles authenticatedRole;

  private String username = "";
  private MenuSection menuSection = MenuSection.UNSECURE;
  private boolean signedAgreement = false;
  private boolean memberOfGroup = false;
  private Map<String, List<CompareResults>> compareResultMap;
  private ForecastCompare forecastCompare = null;

  private List<Integer> forecastCompareIdList = null;
  private List<List<TestPanelCase>> categoryList = null;

  public List<List<TestPanelCase>> getCategoryList() {
    return categoryList;
  }

  public void setCategoryList(List<List<TestPanelCase>> categoryList) {
    this.categoryList = categoryList;
  }

  public List<Integer> getForecastCompareIdList() {
    return forecastCompareIdList;
  }

  public void setForecastCompareIdList(List<Integer> forecastCompareIdList) {
    this.forecastCompareIdList = forecastCompareIdList;
  }

  public ForecastCompare getForecastCompare() {
    return forecastCompare;
  }

  public void setForecastCompare(ForecastCompare forecastCompare) {
    this.forecastCompare = forecastCompare;
  }

  public Map<String, List<CompareResults>> getCompareResultMap() {
    return compareResultMap;
  }

  public void setCompareResultMap(Map<String, List<CompareResults>> compareResultMap) {
    this.compareResultMap = compareResultMap;
  }

  public boolean isSignedAgreement() {
    return signedAgreement;
  }

  public void setSignedAgreement(boolean signedAgreement) {
    this.signedAgreement = signedAgreement;
  }

  public boolean isMemberOfGroup() {
    return memberOfGroup;
  }

  public void setMemberOfGroup(boolean memberOfGroup) {
    this.memberOfGroup = memberOfGroup;
  }

  public MenuSection getMenuSection() {
    return menuSection;
  }

  public void setMenuSection(MenuSection menuSection) {
    this.menuSection = menuSection;
  }

  public WebSession(Request request) {
    super(request);
  }

  @Override
  public boolean authenticate(final String username, final String password) {
    boolean authenticated = false;
    Session session = getDataSession();
    User user = new User();
    user.setName(username);
    user.setPassword(password);
    user = UserManager.login(user, session);
    authenticated = user.isLoggedIn();
    Transaction transaction = session.beginTransaction();
    try {
      LoginAttempt loginAttempt = new LoginAttempt();
      loginAttempt.setName(username);
      loginAttempt.setLoginDate(new Date());
      loginAttempt.setPassword(password);
      if (authenticated) {
        loginAttempt.setUser(user);
      }
      session.save(loginAttempt);
    } finally {
      transaction.commit();
    }
    if (authenticated) {
      this.username = username;
      if (user.getName().equals("Nathan Bunker") || user.getName().equals("Gordon Chamberlin")) {
        authenticatedRole = new Roles(Roles.ADMIN);
      } else {
        authenticatedRole = new Roles(Roles.USER);
      }
      this.user = user;

      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.DAY_OF_MONTH, -90);

      signedAgreement = user.getAgreement() != null && user.getAgreementDate() != null
          && user.getAgreementDate().after(calendar.getTime());

      Query query = session.createQuery("from Expert where user = ?");
      query.setParameter(0, user);
      memberOfGroup = query.list().size() > 0;

    } else {
      this.username = "";
      this.user = null;
    }

    return authenticated;
  }

  @Override
  public Roles getRoles() {
    if (!isSignedIn()) {
      authenticatedRole = null;
    }
    return authenticatedRole;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  private User user = null;
  private transient Session dataSession = null;

  @Override
  public void invalidate() {
    if (dataSession != null && dataSession.isOpen()) {
      dataSession.close();
    }
    dataSession = null;
    super.invalidate();
  }

  public Session getDataSession() {
    if (dataSession == null) {
      SessionFactory factory = CentralControl.getSessionFactory();
      dataSession = factory.openSession();
    }
    return dataSession;
  }

  public synchronized User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
    dirty();
  }

}
