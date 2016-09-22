package org.tch.ft.manager.writers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.tch.fc.model.EventType;
import org.tch.fc.model.Software;
import org.tch.fc.model.TestCase;
import org.tch.fc.model.TestEvent;
import org.tch.fc.model.VaccineGroup;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.TestPanelForecast;
import org.tch.ft.model.User;

public class GeneralWriterSupport 
{
  protected TestPanel testPanel = null;
  protected Set<String> categoryNameSet = null;
  protected Session dataSession = null;
  protected VaccineGroup vaccineGroup = null;
  protected User user = null;
  protected Set<Software> softwareSet = null;

  public void setSoftwareSet(Set<Software> softwareSet) {
    this.softwareSet = softwareSet;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public VaccineGroup getVaccineGroup() {
    return vaccineGroup;
  }

  public void setVaccineGroup(VaccineGroup vaccineGroup) {
    this.vaccineGroup = vaccineGroup;
  }

  public void setDataSession(Session dataSession) {
    this.dataSession = dataSession;
  }

  public void setCategoryNameSet(Set<String> categoryNameSet) {
    this.categoryNameSet = categoryNameSet;
  }

  public void setTestPanel(TestPanel testPanel) {
    this.testPanel = testPanel;
  }

  public String createFilename() {
    if (vaccineGroup != null) {
      return testPanel.getLabel() + "-" + vaccineGroup.getLabel() + ".csv";
    }
    return testPanel.getLabel() + ".csv";
  }

  protected List<TestEvent> getTextEventList(TestCase testCase) {
    List<TestEvent> testEventList;
    {
      Query query = dataSession.createQuery("from TestEvent where testCase = ? and event.eventTypeCode = ? order by eventDate");
      query.setParameter(0, testCase);
      query.setParameter(1, EventType.VACCINATION.getEventTypeCode());
      testEventList = query.list();
    }
    return testEventList;
  }

  protected List<TestPanelForecast> getTestPanelForecastList() {
    List<TestPanelForecast> testPanelForecastList;
    {
      Query query = dataSession.createQuery(
          "from TestPanelForecast where testPanelCase.testPanel = ? and testPanelCase.resultStatus <> 'E' order by testPanelCase.categoryName, testPanelCase.testCase.label");
      query.setParameter(0, testPanel);
      if (categoryNameSet == null) {
        testPanelForecastList = query.list();
      } else {
        testPanelForecastList = new ArrayList<TestPanelForecast>();
        for (TestPanelForecast testPanelForecast : (List<TestPanelForecast>) query.list()) {
          if (categoryNameSet.contains(testPanelForecast.getTestPanelCase().getCategoryName())) {
            testPanelForecastList.add(testPanelForecast);
          }
        }
      }
    }
    return testPanelForecastList;
  }


}
