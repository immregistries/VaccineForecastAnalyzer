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
package org.immregistries.vfa.web.softwareCompare;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.immregistries.vfa.StyleClassLabel;
import org.immregistries.vfa.connect.model.TestCase;
import org.immregistries.vfa.model.ForecastCompare;
import org.immregistries.vfa.model.Result;
import org.immregistries.vfa.model.SoftwareCompare;
import org.immregistries.vfa.model.User;
import org.immregistries.vfa.web.FTBasePage;
import org.immregistries.vfa.web.MenuSection;
import org.immregistries.vfa.web.SecurePage;
import org.immregistries.vfa.web.WebSession;

public class ComparisonListPage extends FTBasePage implements SecurePage {
  private static final long serialVersionUID = 1L;

  public ComparisonListPage() {
    this(new PageParameters());
  }

  public ComparisonListPage(final PageParameters pageParameters) {
    super(MenuSection.SOFTWARE_COMPARE, pageParameters);
    final Session dataSession = ((WebSession) getSession()).getDataSession();
    final WebSession webSession = (WebSession) getSession();
    final User user = webSession.getUser();
    
    final List<Integer> forecastCompareIdList = new ArrayList<Integer>();
    webSession.setForecastCompareIdList(forecastCompareIdList);

    WebMarkupContainer compareListSection = new WebMarkupContainer("compareListSection");
    add(compareListSection);

    List<ForecastCompare> forecastCompareList;
    final SoftwareCompare softwareCompare = user.getSelectedSoftwareCompare();
    if (softwareCompare == null) {
      compareListSection.setVisible(false);
      forecastCompareList = new ArrayList<ForecastCompare>();
    } else {
      Query query = dataSession
          .createQuery("from ForecastCompare where softwareCompare = ? order by compareLabel, forecastActual.softwareResult.testCase.label");
      query.setParameter(0, softwareCompare);
      forecastCompareList = query.list();
    }

    List<List<ForecastCompare>> categoryList = new ArrayList<List<ForecastCompare>>();
    String lastCompareLabel = "";
    List<ForecastCompare> currentForecastCompareList = new ArrayList<ForecastCompare>();
    for (ForecastCompare forecastCompare : forecastCompareList) {
      if (!forecastCompare.getCompareLabel().equals(lastCompareLabel)) {
        if (currentForecastCompareList.size() > 0) {
          categoryList.add(currentForecastCompareList);
        }
        currentForecastCompareList = new ArrayList<ForecastCompare>();
      }
      currentForecastCompareList.add(forecastCompare);
      forecastCompareIdList.add(forecastCompare.getForecastCompareId());
      lastCompareLabel = forecastCompare.getCompareLabel();
    }
    if (currentForecastCompareList.size() > 0) {
      categoryList.add(currentForecastCompareList);
    }

    compareListSection.add(new Label("label", softwareCompare == null ? "" : softwareCompare.getComparedToLabel()));
    ListView<List<ForecastCompare>> categoryItems = new ListView<List<ForecastCompare>>("compareLabelItems",
        categoryList) {
      @Override
      protected void populateItem(ListItem<List<ForecastCompare>> item) {
        final List<ForecastCompare> forecastCompareList = item.getModelObject();

        item.add(new Label("compareLabel", forecastCompareList.size() > 0 ? forecastCompareList.get(0)
            .getCompareLabel() : ""));
        ListView<ForecastCompare> testPanelCaseItems = new ListView<ForecastCompare>("forecastCompareItems",
            forecastCompareList) {
          @Override
          protected void populateItem(ListItem<ForecastCompare> item) {
            final ForecastCompare forecastCompareItem = item.getModelObject();
            TestCase testCase = (TestCase) forecastCompareItem.getForecastActual().getTestCase();
            item.add(new Label("testCaseLabel", testCase.getLabel()));
            String description = testCase.getDescription();
            if (description.length() > 30) {
              description = description.substring(0, 30) + "...";
            }
            item.add(new Label("testCaseDescription", description));
            String patient = testCase.getPatientFirst() + " " + testCase.getPatientLast();

            String styleClass = "none";
            if (forecastCompareItem.getResult() != null) {
              if (forecastCompareItem.getResult() == Result.FAIL || forecastCompareItem.getResult() == Result.RESEARCH
                  || forecastCompareItem.getResult() == Result.FIXED) {
                styleClass = "fail";
              } else if (forecastCompareItem.getResult() == Result.PASS
                  || forecastCompareItem.getResult() == Result.ACCEPT) {
                styleClass = "pass";
              }
            }

            item.add(new StyleClassLabel("status", forecastCompareItem.getResult() == null ? "" : forecastCompareItem
                .getResult().getLabel(), styleClass));
            item.add(new Link("selectTestCase") {
              @Override
              public void onClick() {
                WebSession webSession = ((WebSession) getSession());
                Transaction trans = webSession.getDataSession().beginTransaction();
                User user = ((WebSession) getSession()).getUser();
                user.setSelectedTestCase((TestCase) forecastCompareItem.getForecastActual().getTestCase());
                webSession.setForecastCompare(forecastCompareItem);
                trans.commit();
                setResponsePage(new CompareForecastPage(pageParameters));
              }
            });
          }

        };
        item.add(testPanelCaseItems);
      }
    };
    compareListSection.add(categoryItems);

  }
}
