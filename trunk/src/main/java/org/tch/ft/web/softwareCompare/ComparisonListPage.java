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
package org.tch.ft.web.softwareCompare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.tch.ft.StyleClassLabel;
import org.tch.ft.model.ForecastCompare;
import org.tch.ft.model.Include;
import org.tch.ft.model.Result;
import org.tch.ft.model.SoftwareCompare;
import org.tch.ft.model.TestCase;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.TestPanelCase;
import org.tch.ft.model.User;
import org.tch.ft.web.FTBasePage;
import org.tch.ft.web.MenuSection;
import org.tch.ft.web.SecurePage;
import org.tch.ft.web.WebSession;
import org.tch.ft.web.testCase.EditTestCasePage;
import org.tch.ft.web.testCase.TestCaseDetailPage;

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
          .createQuery("from ForecastCompare where softwareCompare = ? order by compareLabel, forecastActual.testCase.label");
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
            TestCase testCase = forecastCompareItem.getForecastActual().getTestCase();
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
                user.setSelectedTestCase(forecastCompareItem.getForecastActual().getTestCase());
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
