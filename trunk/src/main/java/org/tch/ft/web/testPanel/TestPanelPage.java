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

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.hibernate.Session;
import org.tch.ft.model.TaskGroup;
import org.tch.ft.model.User;
import org.tch.ft.web.FTBasePage;
import org.tch.ft.web.MenuSection;
import org.tch.ft.web.SecurePage;
import org.tch.ft.web.WebSession;
import org.tch.ft.web.taskGroup.ExpertsAssignedPage;

public class TestPanelPage extends FTBasePage implements SecurePage {
  private static final long serialVersionUID = 1L;

  public TestPanelPage() {
    this(new PageParameters());
  }

  public TestPanelPage(final PageParameters pageParameters) {
    super(MenuSection.TEST_PANEL, pageParameters);
    final WebSession webSession = (WebSession) getSession();
    final Session dataSession = webSession.getDataSession();
    final User user = webSession.getUser();
    final TaskGroup taskGroup = user.getSelectedTaskGroup();

    WebMarkupContainer selectTestPanelSection = new WebMarkupContainer("selectTestPanelSection");
    add(selectTestPanelSection);

    WebMarkupContainer canEditSection = new WebMarkupContainer("canEdit");
    selectTestPanelSection.add(canEditSection);

    if (taskGroup == null) {
      selectTestPanelSection.setVisible(false);
    }
    canEditSection.setVisible(taskGroup != null && user.getSelectedTestPanel() != null
        && ExpertsAssignedPage.determineCanEdit(user, dataSession, taskGroup));

  }
}
