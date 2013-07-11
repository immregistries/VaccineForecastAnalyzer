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
package org.tch.ft.web.testPanel;

import java.util.ArrayList;
import java.util.Collections;
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
import org.tch.fc.model.TestCase;
import org.tch.ft.StyleClassLabel;
import org.tch.ft.model.Include;
import org.tch.ft.model.Result;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.TestPanelCase;
import org.tch.ft.model.User;
import org.tch.ft.web.FTBasePage;
import org.tch.ft.web.MenuSection;
import org.tch.ft.web.SecurePage;
import org.tch.ft.web.WebSession;
import org.tch.ft.web.testCase.ActualVsExpectedPage;
import org.tch.ft.web.testCase.TestCaseDetailPage;

public class TestCaseListPage extends FTBasePage implements SecurePage {
  private static final long serialVersionUID = 1L;

  public TestCaseListPage() {
    this(new PageParameters());
  }

  public TestCaseListPage(final PageParameters pageParameters) {
    super(MenuSection.TEST_PANEL, pageParameters);
    final Session dataSession = ((WebSession) getSession()).getDataSession();
    final WebSession webSession = (WebSession) getSession();
    final User user = webSession.getUser();

    WebMarkupContainer testPanelSection = new WebMarkupContainer("testPanelSection");
    add(testPanelSection);

    List<TestPanelCase> testPanelCaseList;
    final TestPanel testPanel = user.getSelectedTestPanel();
    if (testPanel == null) {
      testPanelSection.setVisible(false);
      testPanelCaseList = new ArrayList<TestPanelCase>();
    } else {
      Query query = dataSession
          .createQuery("from TestPanelCase where testPanel = ? order by categoryName, testCase.label");
      query.setParameter(0, testPanel);
      testPanelCaseList = query.list();
      Collections.sort(testPanelCaseList, new TestPanelCase.TestPanelCaseComparator());
    }

    List<List<TestPanelCase>> categoryList = new ArrayList<List<TestPanelCase>>();
    String lastCategoryName = "";
    List<TestPanelCase> currentTestPanelCaseList = new ArrayList<TestPanelCase>();
    for (TestPanelCase testPanelCase : testPanelCaseList) {
      if (!testPanelCase.getCategoryName().equals(lastCategoryName)) {
        if (currentTestPanelCaseList.size() > 0) {
          categoryList.add(currentTestPanelCaseList);
        }
        currentTestPanelCaseList = new ArrayList<TestPanelCase>();
      }
      currentTestPanelCaseList.add(testPanelCase);
      lastCategoryName = testPanelCase.getCategoryName();
    }
    if (currentTestPanelCaseList.size() > 0) {
      categoryList.add(currentTestPanelCaseList);
    }
    
    webSession.setCategoryList(categoryList);

    testPanelSection.add(new Label("label", testPanel == null ? "" : testPanel.getLabel()));
    ListView<List<TestPanelCase>> categoryItems = new ListView<List<TestPanelCase>>("categoryItems", categoryList) {
      @Override
      protected void populateItem(ListItem<List<TestPanelCase>> item) {
        final List<TestPanelCase> testPanelCaseList = item.getModelObject();

        item.add(new Label("categoryName", testPanelCaseList.size() > 0 ? testPanelCaseList.get(0).getCategoryName()
            : ""));
        ListView<TestPanelCase> testPanelCaseItems = new ListView<TestPanelCase>("testPanelCaseItems",
            testPanelCaseList) {
          @Override
          protected void populateItem(ListItem<TestPanelCase> item) {
            final TestPanelCase testPanelCase = item.getModelObject();
            TestCase testCase = testPanelCase.getTestCase();
            item.add(new Label("testCaseNumber", testPanelCase.getTestCaseNumber()));
            item.add(new Label("testCaseLabel", testCase.getLabel()));
            String description = testCase.getDescription();
            if (description.length() > 30) {
              description = description.substring(0, 30) + "...";
            }
            item.add(new Label("testCaseDescription", description));
            String patient = testCase.getPatientFirst() + " " + testCase.getPatientLast();

            item.add(new Label("testCasePatient", patient));
            item.add(new Label("include", testPanelCase.getInclude() == null ? "" : testPanelCase.getInclude()
                .getLabel()));

            String styleClass = "none";
            if (testPanelCase.getResult() != null
                && (testPanelCase.getInclude() == null || testPanelCase.getInclude() == Include.INCLUDED)) {
              if (testPanelCase.getResult() == Result.FAIL || testPanelCase.getResult() == Result.RESEARCH
                  || testPanelCase.getResult() == Result.FIXED) {
                styleClass = "fail";
              } else if (testPanelCase.getResult() == Result.PASS || testPanelCase.getResult() == Result.ACCEPT) {
                styleClass = "pass";
              }
            }

            item.add(new StyleClassLabel("status", testPanelCase.getResult() == null ? "" : testPanelCase.getResult()
                .getLabel(), styleClass));
            item.add(new Link("selectTestCase") {
              @Override
              public void onClick() {
                WebSession webSession = ((WebSession) getSession());
                Transaction trans = webSession.getDataSession().beginTransaction();
                User user = ((WebSession) getSession()).getUser();
                user.setSelectedTestCase(testPanelCase.getTestCase());
                user.setSelectedTestPanelCase(testPanelCase);
                trans.commit();
                setResponsePage(new ActualVsExpectedPage(pageParameters));
              }
            });
          }

        };
        item.add(testPanelCaseItems);
      }
    };
    testPanelSection.add(categoryItems);
    
   


  }
}
