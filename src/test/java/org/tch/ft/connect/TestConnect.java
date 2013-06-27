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
package org.tch.ft.connect;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.tch.ft.CentralControl;
import org.tch.ft.model.ForecastActual;
import org.tch.ft.model.ForecastItem;
import org.tch.ft.model.Service;
import org.tch.ft.model.Software;
import org.tch.ft.model.TestCase;
import org.tch.ft.model.TestEvent;

public class TestConnect extends junit.framework.TestCase {

  public void testConnectSWP() throws Exception {
    SessionFactory factory = CentralControl.getSessionFactory();
    Session session = factory.openSession();

    Query query = session.createQuery("from TestCase");
    TestCase testCase = (TestCase) session.get(TestCase.class, 2);
    assertEquals("Hep B Test 1", testCase.getLabel());
    // assertEquals("3 Dose HIB Test 2", testCase.getLabel());
    query = session.createQuery("from TestEvent where testCase = ?");
    query.setParameter(0, testCase);
    List<TestEvent> testEventList = query.list();
    assertEquals(1, testEventList.size());
    testCase.setTestEventList(testEventList);
    Software software = (Software) session.get(Software.class, 7);
    assertEquals("TCH ImmSys Prod", software.getLabel());
    assertEquals(Service.TCH, software.getService());
//    assertEquals("MIIS Forecaster", software.getLabel());
//    assertEquals(Service.SWP, software.getService());

    query = session.createQuery("from ForecastItem");
    List<ForecastItem> forecastItemList = query.list();
    assertTrue(forecastItemList.size() >= 12);
    System.out.println("--> Software ");
    ConnectorInterface connector = ConnectFactory.createConnecter(software, forecastItemList);
    List<ForecastActual> forecastActualList = connector.queryForForecast(testCase);
    assertNotNull(forecastActualList);
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    boolean foundHepB = false;
    for (ForecastActual forecastActual : forecastActualList) {
      System.out.println("--> " + forecastActual.getForecastItem().getLabel() + " Dose "
          + forecastActual.getDoseNumber() + " Due " + sdf.format(forecastActual.getDueDate()) + " Valid " + sdf.format(forecastActual.getValidDate()) + " Overdue " + sdf.format(forecastActual.getOverdueDate()));
      if (forecastActual.getForecastItem().getForecastItemId() == 5) {
        assertEquals(sdf.parse("05/01/2006"), forecastActual.getDueDate());
        foundHepB = true;
      }
    }
    assertTrue("HepB forecast not found", foundHepB);
    session.close();
  }

  public void testConnectTCH() throws Exception {
    SessionFactory factory = CentralControl.getSessionFactory();
    Session session = factory.openSession();

    Query query = session.createQuery("from TestCase");
    TestCase testCase = (TestCase) session.get(TestCase.class, 2);
    assertEquals("Hep B Test 1", testCase.getLabel());
    // assertEquals("3 Dose HIB Test 2", testCase.getLabel());
    query = session.createQuery("from TestEvent where testCase = ?");
    query.setParameter(0, testCase);
    List<TestEvent> testEventList = query.list();
    assertEquals(1, testEventList.size());
    testCase.setTestEventList(testEventList);
    Software software = (Software) session.get(Software.class, 2);
    assertEquals("TCH Forecaster Validator", software.getLabel());
    assertEquals(Service.TCH, software.getService());

    query = session.createQuery("from ForecastItem");
    List<ForecastItem> forecastItemList = query.list();
    assertTrue(forecastItemList.size() >= 12);
    ConnectorInterface connector = ConnectFactory.createConnecter(software, forecastItemList);
    List<ForecastActual> forecastActualList = connector.queryForForecast(testCase);
    assertNotNull(forecastActualList);
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    boolean foundHepB = false;
    for (ForecastActual forecastActual : forecastActualList) {
      if (forecastActual.getForecastItem().getForecastItemId() == 5) {
        assertEquals(sdf.parse("05/01/2006"), forecastActual.getDueDate());
        assertNotNull(forecastActual.getLogText());
        System.out.print(forecastActual.getLogText().toString());
        foundHepB = true;
      }
    }
    assertTrue("HepB forecast not found", foundHepB);
    session.close();
    
  }
  
  public void testConnectSTC() throws Exception {
    SessionFactory factory = CentralControl.getSessionFactory();
    Session session = factory.openSession();

    Query query = session.createQuery("from TestCase");
    TestCase testCase = (TestCase) session.get(TestCase.class, 2);
    assertEquals("Hep B Test 1", testCase.getLabel());
    // assertEquals("3 Dose HIB Test 2", testCase.getLabel());
    query = session.createQuery("from TestEvent where testCase = ?");
    query.setParameter(0, testCase);
    List<TestEvent> testEventList = query.list();
    assertEquals(1, testEventList.size());
    testCase.setTestEventList(testEventList);
    Software software = (Software) session.get(Software.class, 9);
    assertEquals("STC Forecaster", software.getLabel());
    assertEquals(Service.STC, software.getService());

    query = session.createQuery("from ForecastItem");
    List<ForecastItem> forecastItemList = query.list();
    assertTrue(forecastItemList.size() >= 12);
    ConnectorInterface connector = ConnectFactory.createConnecter(software, forecastItemList);
    List<ForecastActual> forecastActualList = connector.queryForForecast(testCase);
    assertNotNull(forecastActualList);
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    boolean foundHepB = false;
    for (ForecastActual forecastActual : forecastActualList) {
      if (forecastActual.getForecastItem().getForecastItemId() == 5) {
        assertEquals(sdf.parse("04/01/2006"), forecastActual.getDueDate());
        assertNotNull(forecastActual.getLogText());
        System.out.print(forecastActual.getLogText().toString());
        foundHepB = true;
      }
    }
    assertTrue("HepB forecast not found", foundHepB);
    session.close();
    
  }

}
