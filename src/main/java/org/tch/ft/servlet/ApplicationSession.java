package org.tch.ft.servlet;

import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.tch.ft.CentralControl;
import org.tch.ft.manager.ForecastActualExpectedCompare;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.TestPanelCase;
import org.tch.ft.model.User;

public class ApplicationSession
{
  private Session dataSession = null;
  private String alertInformation = null;
  private String alertError = null;
  private String alertWarning = null;
  private User user = null;
  
  private Set<String> forecastCompareCategoryHasProblemSet = null;
  private Set<String> forecastCompareCategoryNameSet = null;
  private Set<TestPanelCase> forecastCompareTestPanelCaseHasProblemSet = null;
  private List<ForecastActualExpectedCompare> forecastCompareList = null;
  private TestPanel forecastCompareTestPanel = null;
  
  public Set<String> getForecastCompareCategoryNameSet() {
    return forecastCompareCategoryNameSet;
  }

  public void setForecastCompareCategoryNameSet(Set<String> forecastCompareCategoryNameSet) {
    this.forecastCompareCategoryNameSet = forecastCompareCategoryNameSet;
  }

  public Set<String> getForecastCompareCategoryHasProblemSet() {
    return forecastCompareCategoryHasProblemSet;
  }

  public void setForecastCompareCategoryHasProblemSet(Set<String> forecastCompareCategoryHasProblemSet) {
    this.forecastCompareCategoryHasProblemSet = forecastCompareCategoryHasProblemSet;
  }

  public Set<TestPanelCase> getForecastCompareTestPanelCaseHasProblemSet() {
    return forecastCompareTestPanelCaseHasProblemSet;
  }

  public void setForecastCompareTestPanelCaseHasProblemSet(Set<TestPanelCase> forecastCompareTestPanelCaseHasProblemSet) {
    this.forecastCompareTestPanelCaseHasProblemSet = forecastCompareTestPanelCaseHasProblemSet;
  }

  public List<ForecastActualExpectedCompare> getForecastCompareList() {
    return forecastCompareList;
  }

  public void setForecastCompareList(List<ForecastActualExpectedCompare> forecastCompareList) {
    this.forecastCompareList = forecastCompareList;
  }



  public TestPanel getForecastCompareTestPanel() {
    return forecastCompareTestPanel;
  }

  public void setForecastCompareTestPanel(TestPanel forecastCompareTestPanel) {
    this.forecastCompareTestPanel = forecastCompareTestPanel;
  }


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
