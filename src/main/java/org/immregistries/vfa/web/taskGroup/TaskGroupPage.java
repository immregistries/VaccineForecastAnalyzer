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
package org.immregistries.vfa.web.taskGroup;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.immregistries.vfa.model.TaskGroup;
import org.immregistries.vfa.web.FTBasePage;
import org.immregistries.vfa.web.MenuSection;
import org.immregistries.vfa.web.SecurePage;
import org.immregistries.vfa.web.WebSession;

public class TaskGroupPage extends FTBasePage implements SecurePage {
  private static final long serialVersionUID = 1L;

  public TaskGroupPage() {
    this(new PageParameters());
  }

  public TaskGroupPage(final PageParameters pageParameters) {
    super(MenuSection.TASK_GROUP, pageParameters);

    final WebSession webSession = (WebSession) getSession();
    final TaskGroup taskGroup = webSession.getUser().getSelectedTaskGroup();

    WebMarkupContainer taskGroupSection = new WebMarkupContainer("taskGroupSection");
    if (taskGroup == null) {
      taskGroupSection.setVisible(false);
    } else {
      taskGroupSection.add(new Label("label", taskGroup.getLabel()));
    }
    add(taskGroupSection);

  }

}
