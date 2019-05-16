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
package org.immregistries.vfa.web.expert;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.hibernate.Query;
import org.hibernate.Session;
import org.immregistries.vfa.model.Expert;
import org.immregistries.vfa.model.TaskGroup;
import org.immregistries.vfa.model.User;
import org.immregistries.vfa.web.FTBasePage;
import org.immregistries.vfa.web.MenuSection;
import org.immregistries.vfa.web.SecurePage;
import org.immregistries.vfa.web.WebSession;
import org.immregistries.vfa.web.taskGroup.TaskGroupListPanel;

public class TaskGroupsAssignedPage extends FTBasePage implements SecurePage {
  private static final long serialVersionUID = 1L;

  public TaskGroupsAssignedPage() {
     this(new PageParameters());
  }

  public TaskGroupsAssignedPage(final PageParameters pageParameters) {
    super(MenuSection.EXPERT, pageParameters);
    WebSession webSession = ((WebSession) getSession());
    final User user = webSession.getUser();
    final Session dataSession = webSession.getDataSession();

    List<TaskGroup> taskGroupList = new ArrayList<TaskGroup>();
    Query query = dataSession.createQuery("from Expert where user = ? order by taskGroup.label");
    query.setParameter(0, user);
    List<Expert> expertList = query.list();
    for (Expert expert : expertList)
    {
      taskGroupList.add(expert.getTaskGroup());
    }
    add(new TaskGroupListPanel("taskGroupListPanel", pageParameters, dataSession, user, taskGroupList));

  }
}
