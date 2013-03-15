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
package org.tch.ft.web.taskGroup;

import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.hibernate.Query;
import org.hibernate.Session;
import org.tch.ft.model.Role;
import org.tch.ft.model.TaskGroup;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.User;
import org.tch.ft.web.WebSession;
import org.tch.ft.web.FTBasePage;
import org.tch.ft.web.MenuSection;
import org.tch.ft.web.SecurePage;
import org.tch.ft.web.testPanel.TestPanelListPanel;

public class TestPanelsAssociatedPage extends FTBasePage  implements SecurePage {
  private static final long serialVersionUID = 1L;
  public TestPanelsAssociatedPage() {
    this(new PageParameters());
  }

  public TestPanelsAssociatedPage(final PageParameters pageParameters) {
    super(MenuSection.TASK_GROUP, pageParameters);

    final WebSession webSession = (WebSession) getSession();
    final Session dataSession = webSession.getDataSession();
    final User user = webSession.getUser();
    final TaskGroup taskGroup = webSession.getUser().getSelectedTaskGroup();
    
    Query query;
    WebMarkupContainer taskGroupSection = new WebMarkupContainer("taskGroupSection");
    if (taskGroup == null) {
      taskGroupSection.setVisible(false);
    } else {
      query = dataSession.createQuery("from TestPanel where taskGroup = ? order by label");
      query.setParameter(0, taskGroup);
      List<TestPanel> testPanelList = query.list();

      taskGroupSection.add(new TestPanelListPanel("testPanelListPanel", pageParameters, dataSession, user, testPanelList));
    }
    add(taskGroupSection);

  }

}
