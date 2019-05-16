package org.immregistries.vfa.model;

import java.io.Serializable;
import java.util.Date;

public class AccessRequest implements Serializable
{
  private static final long serialVersionUID = 1L;
  
  private int accessRequestId = 0;
  private Registration registration = null;
  private Module module = null;
  private Access access = null;
  private Date requestDate = null;
  private User user = null;
  private String userComment = "";

  public String getModuleType() {
    return module == null ? "" : module.getModuleType();
  }

  public void setModuleType(String moduleType) {
    this.module = Module.getModule(moduleType);
  }

  public String getAccessStatus() {
    return access == null ? "" : access.getAccessStatus();
  }

  public void setAccessStatus(String accessStatus) {
    this.access = Access.getAccess(accessStatus);
  }

  public int getAccessRequestId() {
    return accessRequestId;
  }

  public void setAccessRequestId(int accessRequestId) {
    this.accessRequestId = accessRequestId;
  }

  public Registration getRegistration() {
    return registration;
  }

  public void setRegistration(Registration registration) {
    this.registration = registration;
  }

  public Module getModule() {
    return module;
  }

  public void setModule(Module module) {
    this.module = module;
  }

  public Access getAccess() {
    return access;
  }

  public void setAccess(Access access) {
    this.access = access;
  }

  public Date getRequestDate() {
    return requestDate;
  }

  public void setRequestDate(Date requestDate) {
    this.requestDate = requestDate;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getUserComment() {
    return userComment;
  }

  public void setUserComment(String userComment) {
    this.userComment = userComment;
  }

}
