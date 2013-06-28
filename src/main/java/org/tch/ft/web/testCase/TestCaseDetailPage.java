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

import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.tch.fc.TCHConnector;
import org.tch.fc.model.TestCase;
import org.tch.ft.model.Include;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.TestPanelCase;
import org.tch.ft.model.User;
import org.tch.ft.web.SecurePage;
import org.tch.ft.web.WebSession;

public class TestCaseDetailPage extends TestCaseDetail implements SecurePage {
  private static final long serialVersionUID = 1L;

  public TestCaseDetailPage() {
    this(new PageParameters());
  }

  public TestCaseDetailPage(final PageParameters pageParameters) {
    super(pageParameters);
    WebSession webSession = ((WebSession) getSession());
    final User user = webSession.getUser();
    final Session dataSession = webSession.getDataSession();
    final TestCase testCase = user.getSelectedTestCase();
    final TestPanel testPanel = user.getSelectedTestPanel();

    WebMarkupContainer testPanelTable = new WebMarkupContainer("testPanelTable");
    if (user.getSelectedTestPanelCase() == null && testPanel != null) {
      // check to see if there is an assigned test panel case
      Query query = dataSession.createQuery("from TestPanelCase where testPanel = ? and testCase = ?");
      query.setParameter(0, testPanel);
      query.setParameter(1, testCase);
      List<TestPanelCase> testPanelCaseList = query.list();
      if (testPanelCaseList.size() > 0) {
        Transaction transaction = dataSession.beginTransaction();
        user.setSelectedTestPanelCase(testPanelCaseList.get(0));
        dataSession.update(user);
        transaction.commit();
      }
    }
    final TestPanelCase testPanelCase = user.getSelectedTestPanelCase();
    if (testPanelCase == null) {
      add(new Label("testPanelTitle", testPanel == null ? "Test Panel Not Selected" : "Not Assigned to Test Panel"));
      testPanelTable.setVisible(false);
    } else {
      if (testPanelCase.getInclude() == Include.EXCLUDED) {
        add(new Label("testPanelTitle", "Excluded from Test Panel"));
      } else if (testPanelCase.getInclude() == Include.PROPOSED) {
        add(new Label("testPanelTitle", "Proposed for Test Panel"));
      } else {
        add(new Label("testPanelTitle", "Assigned to Test Panel"));
      }
      testPanelTable.setVisible(true);
      testPanelTable.add(new Label("categoryName", testPanelCase.getCategoryName()));
      testPanelTable.add(new Label("testCaseNumber", testPanelCase.getTestCaseNumber()));
      testPanelTable.add(new Label("includeStatus", testPanelCase.getInclude().getLabel()));
      testPanelTable.add(new Label("resultStatus", testPanelCase.getResult() != null ? testPanelCase.getResult()
          .getLabel() : ""));
    }
    add(testPanelTable);



    final boolean canEdit = testPanelCase != null
        && ActualVsExpectedPage.determineIfCanEdit(user, dataSession, testPanel);

    WebMarkupContainer editTestCaseLink = new WebMarkupContainer("editTestCaseLink");
    editTestCaseLink.setVisible(canEdit);
    add(editTestCaseLink);

  }


}
