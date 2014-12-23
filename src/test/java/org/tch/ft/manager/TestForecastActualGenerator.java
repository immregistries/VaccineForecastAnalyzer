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

import java.util.List;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.tch.fc.model.ForecastActual;
import org.tch.fc.model.Software;
import org.tch.ft.CentralControl;
import org.tch.ft.model.TestPanel;

public class TestForecastActualGenerator extends TestCase {
  public void testRunForecastActual() throws Exception
  {
    SessionFactory factory = CentralControl.getSessionFactory();
    Session session = factory.openSession();

    TestPanel testPanel = (TestPanel) session.get(TestPanel.class, 2);
    assertNotNull(testPanel);
    Software software = (Software) session.get(Software.class, 5); // 2 == TCH, 3 == MIIS
    assertNotNull(software);
    ForecastActualGenerator.runForecastActual(testPanel, software, session, false);
    List<ForecastActualExpectedCompare> forecastCompareList = ForecastActualGenerator.createForecastComparison(testPanel, software, null, session);
        
    System.out.println("Test run foreast count = " + forecastCompareList.size());
    int totalCount = forecastCompareList.size();
    int errorCount = 0;
    for (ForecastActualExpectedCompare forecastCompare : forecastCompareList)
    {
      if (forecastCompare.getRunStatus() == ForecastActualExpectedCompare.RunStatus.ERRORED)
      {
        errorCount++;
        System.err.println("Unable to run forecast compare for test case " + forecastCompare.getForecastResultA().getTestCase().getTestCaseId());
        forecastCompare.getRunException().printStackTrace(System.err);
      }
      System.out.println("Test " + forecastCompare.getTestCase().getLabel());
      ForecastActual forecastActual = (ForecastActual) forecastCompare.getForecastResultB();
      assertNotNull(forecastActual.getSoftwareResult().getLogText());
      assertTrue(forecastActual.getSoftwareResult().getLogText().length() > 0);
    }
    System.out.println("Queries made: " + totalCount);
    assertEquals(0, errorCount);
    assertTrue(totalCount > 0);
    session.close();
  }
}
