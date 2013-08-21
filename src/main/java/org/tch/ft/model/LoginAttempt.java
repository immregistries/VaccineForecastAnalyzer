package org.tch.ft.model;

import java.io.Serializable;
import java.util.Date;

public class LoginAttempt implements Serializable {

  private static final long serialVersionUID = 1L;

  private int loginAttemptId = 0;
  private Date loginDate = null;
  private String name = "";
  private String password = "";
  private User user = null;

  public int getLoginAttemptId() {
    return loginAttemptId;
  }

  public void setLoginAttemptId(int loginAttemptId) {
    this.loginAttemptId = loginAttemptId;
  }

  public Date getLoginDate() {
    return loginDate;
  }

  public void setLoginDate(Date loginDate) {
    this.loginDate = loginDate;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
