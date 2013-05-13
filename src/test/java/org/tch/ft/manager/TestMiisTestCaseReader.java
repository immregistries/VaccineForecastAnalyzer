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
package org.tch.ft.manager;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.tch.ft.CentralControl;
import org.tch.ft.manager.readers.MiisTestCaseReader;
import org.tch.ft.model.Event;
import org.tch.ft.model.ForecastExpected;
import org.tch.ft.model.ForecastItem;
import org.tch.ft.model.TaskGroup;
import org.tch.ft.model.TestEvent;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.User;

public class TestMiisTestCaseReader extends TestCase{
  public void testConstruct() throws Exception
  {
   
    InputStream in = this.getClass().getResourceAsStream("/IFMTestCasesMaster20120813.csv");
    assertNotNull(in);
    SessionFactory factory = CentralControl.getSessionFactory();
    Session dataSession = factory.openSession();
    User user = (User) dataSession.get(User.class, 1);
    Query query = dataSession.createQuery("from Event");
    List<Event> eventList = query.list();
    query = dataSession.createQuery("from ForecastItem");
    List<ForecastItem> forecastItemList = query.list();
    Map<Integer, ForecastItem> forecastItemListMap = new HashMap<Integer, ForecastItem>();
    for (ForecastItem forecastItem : forecastItemList)
    {
      forecastItemListMap.put(forecastItem.getForecastItemId(), forecastItem);
    }
    MiisTestCaseReader miisTestCaseReader = new MiisTestCaseReader();
    miisTestCaseReader.setEventList(eventList);
    miisTestCaseReader.setUser(user);
    miisTestCaseReader.setForecastItems(forecastItemListMap);
    miisTestCaseReader.read(in);
    assertEquals(840, miisTestCaseReader.getTestCaseList().size());
    
    org.tch.ft.model.TestCase testCase = miisTestCaseReader.getTestCaseList().get(3);
    assertNotNull(testCase);
    assertEquals("1003", testCase.getTestCaseNumber());
    assertEquals("HepA", testCase.getCategoryName());
    List<ForecastExpected> forecastExpectedList = testCase.getForecastExpectedList();
    assertNotNull(forecastExpectedList);
    List<TestEvent> testEventList = testCase.getTestEventList();
    assertNotNull(testEventList);
    for (TestEvent testEvent : testEventList)
    {
      System.out.println("--> " + testEvent.getEvent().getVaccineCvx() + " " + testEvent.getEventDate());
    }
    assertEquals(1, testEventList.size());
    assertEquals("83", testEventList.get(0).getEvent().getVaccineCvx());
    
    dataSession.close();
    factory.close();
  }
  
  public void testImport() throws Exception
  {
    String filename = "IFMTestCasesMaster20120813";
    InputStream in = this.getClass().getResourceAsStream("/" + filename + ".csv");
    assertNotNull(in);
    SessionFactory factory = CentralControl.getSessionFactory();
    Session dataSession = factory.openSession();
    Transaction transaction = dataSession.beginTransaction();
    
    User user = (User) dataSession.get(User.class, 1);
    Query query = dataSession.createQuery("from Event");
    List<Event> eventList = query.list();
    query = dataSession.createQuery("from ForecastItem");
    List<ForecastItem> forecastItemList = query.list();
    Map<Integer, ForecastItem> forecastItemListMap = new HashMap<Integer, ForecastItem>();
    for (ForecastItem forecastItem : forecastItemList)
    {
      forecastItemListMap.put(forecastItem.getForecastItemId(), forecastItem);
    }
    MiisTestCaseReader miisTestCaseReader = new MiisTestCaseReader();
    miisTestCaseReader.setEventList(eventList);
    miisTestCaseReader.setUser(user);
    miisTestCaseReader.setForecastItems(forecastItemListMap);
    miisTestCaseReader.read(in);
    TestCaseImporter tci = new TestCaseImporter();
    
    // find testPanelCase
    TaskGroup taskGroup = (TaskGroup) dataSession.get(TaskGroup.class, 4);
    query = dataSession.createQuery("from TestPanel where taskGroup = ? and label = ?");
    query.setParameter(0, taskGroup);
    query.setParameter(1, filename);
    List<TestPanel> testPanelList = query.list();
    TestPanel testPanel = null;
    if (testPanelList.size() > 0)
    {
      testPanel = testPanelList.get(0);
    }
    else
    {
      testPanel = new TestPanel();
      testPanel.setLabel(filename);
      testPanel.setTaskGroup(taskGroup);
      dataSession.save(testPanel);
    }
    tci.importTestCases(miisTestCaseReader, testPanel, dataSession);
    
    transaction.commit();
    dataSession.close();
    factory.close();
    
  }
}
