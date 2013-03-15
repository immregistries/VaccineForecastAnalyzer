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
package org.tch.ft.web.testCase;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.hibernate.Query;
import org.hibernate.Session;
import org.tch.ft.manager.ForecastActualExpectedCompare;
import org.tch.ft.model.ForecastActual;
import org.tch.ft.model.ForecastExpected;
import org.tch.ft.model.Software;
import org.tch.ft.model.TestCase;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.TestPanelExpected;
import org.tch.ft.model.User;
import org.tch.ft.web.WebSession;
import org.tch.ft.web.FTBasePage;
import org.tch.ft.web.MenuSection;
import org.tch.ft.web.SecurePage;

public class ForecastLogPage extends FTBasePage  implements SecurePage {
  private static final long serialVersionUID = 1L;

  public ForecastLogPage(final PageParameters pageParameters) {
    super(MenuSection.TEST_CASE, pageParameters);
    WebSession webSession = ((WebSession) getSession());
    final User user = webSession.getUser();
    final TestCase testCase = user.getSelectedTestCase();

    Query query;


    final Session dataSession = webSession.getDataSession();
    Software software = user.getSelectedSoftware();
    TestPanel testPanel = user.getSelectedTestPanel();
    ForecastActual forecastActual = null;
    ForecastExpected forecastExpected = null;
    List<ForecastActualExpectedCompare> forecastCompareList = new ArrayList<ForecastActualExpectedCompare>();
    if (testCase != null && testPanel != null) {
      query = dataSession
          .createQuery("from TestPanelExpected where testPanelCase.testCase = ? and testPanelCase.testPanel = ?");
      query.setParameter(0, testCase);
      query.setParameter(1, testPanel);
      List<TestPanelExpected> testPanelExpectedList = query.list();
      for (TestPanelExpected testPanelExpected : testPanelExpectedList) {
        forecastExpected = testPanelExpected.getForecastExpected();
        ForecastActualExpectedCompare forecastCompare = new ForecastActualExpectedCompare();
        forecastCompare.setForecastResultA(forecastExpected);
        forecastCompare.setForecastItem(forecastExpected.getForecastItem());
        forecastCompareList.add(forecastCompare);
        if (software != null) {
          query = dataSession
              .createQuery("from ForecastActual where software = ? and testCase = ? and forecastItem = ?");
          query.setParameter(0, software);
          query.setParameter(1, testCase);
          query.setParameter(2, forecastExpected.getForecastItem());
          List<ForecastActual> forecastActualList = query.list();
          if (forecastActualList.size() > 0) {
            forecastActual = forecastActualList.get(0);
          }
          forecastCompare.setForecastResultB(forecastActual);
        }
      }
    }

    ListView<ForecastActualExpectedCompare> forecastCompareItems = new ListView<ForecastActualExpectedCompare>("forecastCompareItems",
        forecastCompareList) {

      protected void populateItem(ListItem<ForecastActualExpectedCompare> item) {
        final ForecastActualExpectedCompare forecastCompare = item.getModelObject();
        ForecastActual forecastActual = (ForecastActual) forecastCompare.getForecastResultB();
        final ForecastExpected forecastExpected = (ForecastExpected) forecastCompare.getForecastResultA();
        
        String logText = "";
        if (forecastActual != null) {
          logText = forecastActual.getLogText();
        }
        item.add(new Label("forecastLineLabel", forecastExpected.getForecastItem().getLabel()));
        item.add(new MultiLineLabel("logText", logText));
      }
    };
    add(forecastCompareItems);
  }

}
