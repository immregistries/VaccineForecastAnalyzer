package org.tch.ft.model;

import java.io.Serializable;
import java.util.Date;

public class Registration implements Serializable
{
  private int registrationId = 0;
  private String registrationKey = "";
  private User user = null;
  private String name = "";
  private String title = "";
  private String position = "";
  private String phone = "";
  private String email = "";
  private TaskGroup taskGroup = null;
  private String facility = "";
  private Date acceptedDate = null;

  public int getRegistrationId() {
    return registrationId;
  }

  public void setRegistrationId(int registrationId) {
    this.registrationId = registrationId;
  }

  public String getRegistrationKey() {
    return registrationKey;
  }

  public void setRegistrationKey(String registrationKey) {
    this.registrationKey = registrationKey;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public TaskGroup getTaskGroup() {
    return taskGroup;
  }

  public void setTaskGroup(TaskGroup taskGroup) {
    this.taskGroup = taskGroup;
  }

  public String getFacility() {
    return facility;
  }

  public void setFacility(String facility) {
    this.facility = facility;
  }

  public Date getAcceptedDate() {
    return acceptedDate;
  }

  public void setAcceptedDate(Date acceptedDate) {
    this.acceptedDate = acceptedDate;
  }
}
