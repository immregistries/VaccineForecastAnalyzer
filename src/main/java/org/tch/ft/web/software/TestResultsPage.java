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
package org.tch.ft.web.software;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.tch.fc.model.ForecastActual;
import org.tch.ft.StyleClassLabel;
import org.tch.ft.manager.ForecastActualExpectedCompare;
import org.tch.ft.manager.ForecastActualGenerator;
import org.tch.ft.model.User;
import org.tch.ft.web.FTBasePage;
import org.tch.ft.web.MenuSection;
import org.tch.ft.web.SecurePage;
import org.tch.ft.web.WebSession;
import org.tch.ft.web.testCase.ActualVsExpectedPage;

public class TestResultsPage extends FTBasePage implements SecurePage {
  private static final long serialVersionUID = 1L;

  public TestResultsPage(final PageParameters pageParameters) {
    super(MenuSection.SOFTWARE, pageParameters);

    WebSession webSession = (WebSession) getSession();
    User user = webSession.getUser();

    if (user.getSelectedSoftware() == null) {
      throw new RestartResponseException(SoftwarePage.class);
    }
    
    String runForecastTestsResults = pageParameters.get("runForecastTestsResults").toString();
    if (runForecastTestsResults == null)
    {
      runForecastTestsResults = "";
    }
    Label runForecastTestsResultsLabel = new Label("runForecastTestsResults", runForecastTestsResults);
    runForecastTestsResultsLabel.setEscapeModelStrings(false);
    runForecastTestsResultsLabel.setVisible(!runForecastTestsResults.equals(""));
    add(runForecastTestsResultsLabel);

    WebMarkupContainer testResultsSection = new WebMarkupContainer("testResultsSection");

    List<ForecastActualExpectedCompare> forecastCompareList;
    if (user.getSelectedTestPanel() != null) {
      forecastCompareList = ForecastActualGenerator.createForecastComparison(user.getSelectedTestPanel(),
          user.getSelectedSoftware(), webSession.getDataSession());
      Collections.sort(forecastCompareList, new ForecastActualExpectedCompare.ForecastCompareComparator());
    } else {
      testResultsSection.setVisible(false);
      forecastCompareList = new ArrayList<ForecastActualExpectedCompare>();
    }
    add(testResultsSection);
    

    ListView<ForecastActualExpectedCompare> forecastCompareItems = new ListView<ForecastActualExpectedCompare>("forecastCompareItems",
        forecastCompareList) {
      @Override
      protected void populateItem(ListItem<ForecastActualExpectedCompare> item) {
        final ForecastActualExpectedCompare forecastCompare = item.getModelObject();

        item.add(new Label("testCaseLabel", forecastCompare.getForecastResultA().getTestCase().getLabel()));
        String runDate = "-";
        if (forecastCompare.getForecastResultB() != null) {
          ForecastActual forcastActual = (ForecastActual) forecastCompare.getForecastResultB();
          SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
          runDate = simpleDateFormat.format(forcastActual.getSoftwareResult().getRunDate());
        }
        String styleClass = forecastCompare.matchExactly() ? "pass" : "fail";
        item.add(new Label("runDate", runDate));
        item.add(new StyleClassLabel("matchStatus", forecastCompare.getMatchStatus(), styleClass));
        item.add(new StyleClassLabel("matchSimilurity", forecastCompare.getMatchSimilarity(), styleClass));
        item.add(new Link("selectTestCase") {

          @Override
          public void onClick() {
            selectTestCase(forecastCompare);
            setResponsePage(new ActualVsExpectedPage(pageParameters));
          }

        });
      }
    };
    testResultsSection.add(forecastCompareItems);
  }

}
