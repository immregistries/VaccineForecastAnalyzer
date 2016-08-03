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
import java.util.Date;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.tch.ft.model.Available;
import org.tch.ft.model.TaskGroup;
import org.tch.ft.model.TestNote;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.User;
import org.tch.ft.web.WebSession;
import org.tch.ft.web.FTBasePage;
import org.tch.ft.web.MenuSection;
import org.tch.ft.web.SecurePage;
import org.tch.ft.web.taskGroup.ExpertsAssignedPage;
import org.tch.ft.web.taskGroup.TaskGroupPage;

public class SelectTestPanelPage extends FTBasePage implements SecurePage {
  private static final long serialVersionUID = 1L;

  private Model<String> testPanelLabelModel = null;

  public SelectTestPanelPage() {
    this(new PageParameters());
  }

  public SelectTestPanelPage(final PageParameters pageParameters) {
    super(MenuSection.TEST_PANEL, pageParameters);
    WebSession webSession = (WebSession) getSession();
    final Session dataSession = webSession.getDataSession();
    final User user = ((WebSession) getSession()).getUser();

    List<TestPanel> testPanelList;
    if (user.getSelectedTaskGroup() != null) {
      Query query = dataSession.createQuery("from TestPanel where taskGroup = ? and availableCode = 'A' order by label");
      query.setParameter(0, user.getSelectedTaskGroup());
      testPanelList = query.list();
    } else {
      testPanelList = new ArrayList<TestPanel>();
    }

    add(new TestPanelListPanel("testPanelListPanel", pageParameters, dataSession, user, testPanelList));

    boolean canEdit = false;
    final TaskGroup taskGroup = user.getSelectedTaskGroup();
    if (taskGroup != null) {
      canEdit = ExpertsAssignedPage.determineCanEdit(user, dataSession, taskGroup);
    }

    WebMarkupContainer createTestPanelSection = new WebMarkupContainer("createTestPanelSection");
    createTestPanelSection.setVisible(canEdit);
    add(createTestPanelSection);

    Form<Void> createTestPanelForm = new Form<Void>("createTestPanelForm") {
      private static final long serialVersionUID = 1L;

      @Override
      protected void onSubmit() {
        TestPanel testPanel = new TestPanel();
        testPanel.setLabel(testPanelLabelModel.getObject());
        testPanel.setAvailable(Available.PUBLIC);
        testPanel.setTaskGroup(taskGroup);
        user.setSelectedSoftwareCompare(null);
        Transaction transaction = dataSession.beginTransaction();
        dataSession.save(testPanel);
        user.setSelectedTestPanel(testPanel);
        transaction.commit();
        testPanelLabelModel.setObject("");
        setResponsePage(new TestPanelPage());
      }
    };

    {
      testPanelLabelModel = new Model<String>("");
      TextArea<String> noteTextField = new TextArea<String>("testPanelLabelField", testPanelLabelModel);
      createTestPanelForm.add(noteTextField);
    }

    createTestPanelSection.add(createTestPanelForm);

  }
}
