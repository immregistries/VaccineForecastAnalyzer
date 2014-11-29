package org.tch.ft.servlet;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.tch.ft.CentralControl;
import org.tch.ft.model.User;

public class ApplicationSession
{
  private Session dataSession = null;
  private String alertInformation = null;
  private String alertError = null;
  private String alertWarning = null;
  private User user = null;

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Session getDataSession() {
    if (dataSession == null) {
      SessionFactory factory = CentralControl.getSessionFactory();
      dataSession = factory.openSession();
    }
    return dataSession;
  }

  public void setDataSession(Session dataSession) {
    this.dataSession = dataSession;
  }

  public String getAlertInformation() {
    return alertInformation;
  }

  public void setAlertInformation(String alertInformation) {
    this.alertInformation = alertInformation;
  }

  public String getAlertError() {
    return alertError;
  }

  public void setAlertError(String alertError) {
    this.alertError = alertError;
  }

  public String getAlertWarning() {
    return alertWarning;
  }

  public void setAlertWarning(String alertWarning) {
    this.alertWarning = alertWarning;
  }

}
