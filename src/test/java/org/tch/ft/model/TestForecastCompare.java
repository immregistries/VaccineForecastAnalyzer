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
package org.tch.ft.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.tch.ft.CentralControl;
import org.tch.ft.manager.ForecastActualExpectedCompare;

import junit.framework.TestCase;

public class TestForecastCompare extends TestCase{

  public void testMatchExactly()
  {
    SessionFactory factory = CentralControl.getSessionFactory();
    Session dataSession = factory.openSession();
    
    org.tch.ft.model.TestCase testCase = (org.tch.ft.model.TestCase) dataSession.get(org.tch.ft.model.TestCase.class, 62);
    Software software = (Software) dataSession.get(Software.class, 2);
    TaskGroup taskGroup = (TaskGroup) dataSession.get(TaskGroup.class, 2);
    TestPanel testPanel = (TestPanel) dataSession.get(TestPanel.class, 2);
    TestPanelExpected testPanelExpected = null;
    ForecastActual forecastActual = null;
    ForecastExpected forecastExpected = null;
    assertNotNull(testCase);
    assertNotNull(testPanel);
      Query query = dataSession.createQuery("from TestPanelExpected where testPanelCase.testCase = ? and testPanelCase.testPanel = ?");
      query.setParameter(0, testCase);
      query.setParameter(1, testPanel);
      List<TestPanelExpected> testPanelExpectedList = query.list();
      if (testPanelExpectedList.size() > 0) {
        testPanelExpected = testPanelExpectedList.get(0);
        forecastExpected = testPanelExpected.getForecastExpected();
      }
      if (software != null) {
        query = dataSession.createQuery("from ForecastActual where software = ? and testCase = ?");
        query.setParameter(0, software);
        query.setParameter(1, testCase);
        List<ForecastActual> forecastActualList = query.list();
        if (forecastActualList.size() > 0) {
          forecastActual = forecastActualList.get(0);
        }
      }
    ForecastActualExpectedCompare forecastCompare = new ForecastActualExpectedCompare();
    forecastCompare.setForecastResultA(forecastExpected);
    forecastCompare.setForecastResultB(forecastActual);
    
    assertNotNull(forecastExpected);
    assertNotNull(forecastExpected.getDoseNumber());
    assertNotNull(forecastActual);
    assertNotNull(forecastActual.getDoseNumber());
    
    assertTrue("Should match", forecastCompare.matchExactly());
    dataSession.close();

  }
  
  public void testComparator()
  {
    ForecastActualExpectedCompare compare1 = new ForecastActualExpectedCompare();
    ForecastActualExpectedCompare compare2 = new ForecastActualExpectedCompare();
    ForecastExpected expected1 = new ForecastExpected();
    ForecastExpected expected2 = new ForecastExpected();
    org.tch.ft.model.TestCase testCase1 = new org.tch.ft.model.TestCase();
    org.tch.ft.model.TestCase testCase2 = new org.tch.ft.model.TestCase();
    testCase1.setLabel("This Test 2");
    testCase2.setLabel("This Test 1");
    expected1.setTestCase(testCase1);
    expected2.setTestCase(testCase2);
    compare1.setForecastResultA(expected1);
    compare2.setForecastResultA(expected2);
    List<ForecastActualExpectedCompare> forecastCompareList = new ArrayList<ForecastActualExpectedCompare>();
    forecastCompareList.add(compare1);
    forecastCompareList.add(compare2);
    Collections.sort(forecastCompareList, new ForecastActualExpectedCompare.ForecastCompareComparator());
    assertEquals(compare2, forecastCompareList.get(0));
    testCase1.setLabel("This Test 1");
    testCase2.setLabel("This Test 2");
    Collections.sort(forecastCompareList, new ForecastActualExpectedCompare.ForecastCompareComparator());
    assertEquals(compare2, forecastCompareList.get(1));
    testCase1.setLabel("Test A 2");
    testCase2.setLabel("Test B 1");
    Collections.sort(forecastCompareList, new ForecastActualExpectedCompare.ForecastCompareComparator());
    assertEquals(compare2, forecastCompareList.get(1));
    testCase1.setLabel("3 Dose HIB Test 3");
    testCase2.setLabel("3 Dose HIB Test 1");
    Collections.sort(forecastCompareList, new ForecastActualExpectedCompare.ForecastCompareComparator());
    assertEquals(compare2, forecastCompareList.get(0));
  }
}
