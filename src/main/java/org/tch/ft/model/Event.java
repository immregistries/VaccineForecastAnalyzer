package org.tch.ft.model;

import java.io.Serializable;

public class Event implements Serializable {

  private static final long serialVersionUID = 1L;

  private int eventId = 0;
  private String label = "";
  private EventType eventType = null;
  private String vaccineCvx = "";
  private String vaccineMvx = "";

  public int getEventId() {
    return eventId;
  }

  public void setEventId(int eventId) {
    this.eventId = eventId;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public EventType getEventType() {
    return eventType;
  }

  public void setEventType(EventType eventType) {
    this.eventType = eventType;
  }

  public void setEventTypeCode(String eventTypeCode) {
    this.eventType = EventType.getEventType(eventTypeCode);
  }

  public String getEventTypeCode() {
    return eventType == null ? null : eventType.getEventTypeCode();
  }

  public String getVaccineCvx() {
    return vaccineCvx;
  }

  public void setVaccineCvx(String vaccineCvx) {
    this.vaccineCvx = vaccineCvx;
  }

  public String getVaccineMvx() {
    return vaccineMvx;
  }

  public void setVaccineMvx(String vaccineMvx) {
    this.vaccineMvx = vaccineMvx;
  }

  @Override
  public String toString() {
    return (eventType == null ? "" : eventType.getLabel()) + ": " + label;
  }
}
