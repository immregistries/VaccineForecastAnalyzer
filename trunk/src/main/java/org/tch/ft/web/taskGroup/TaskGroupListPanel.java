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

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.tch.ft.StyleClassLabel;
import org.tch.ft.model.Expert;
import org.tch.ft.model.TaskGroup;
import org.tch.ft.model.User;
import org.tch.ft.web.WebSession;
import org.tch.ft.web.testPanel.SelectTestPanelPage;

public class TaskGroupListPanel extends Panel {
  public TaskGroupListPanel(String id, final PageParameters pageParameters, final Session dataSession, final User user,
      List<TaskGroup> taskGroupList) {
    super(id);

    ListView<TaskGroup> taskGroupItems = new ListView<TaskGroup>("taskGroupItems", taskGroupList) {
      @Override
      protected void populateItem(ListItem<TaskGroup> item) {
        // TODO Auto-generated method stub
        final TaskGroup taskGroup = item.getModelObject();

        WebSession webSession = ((WebSession) getSession());
        String role = "";
        Query query = dataSession.createQuery("from Expert where user = ? and taskGroup = ?");
        query.setParameter(0, user);
        query.setParameter(1, taskGroup);
        List<Expert> expertList = query.list();
        if (expertList.size() > 0) {
          Expert expert = expertList.get(0);
          role = expert.getRole().getLabel();
        }
        TaskGroup selectedTaskGroup = webSession.getUser().getSelectedTaskGroup();
        boolean selected = selectedTaskGroup != null && taskGroup.equals(selectedTaskGroup);
        String styleClass = selected ? "highlight" : "";
        item.add(new StyleClassLabel("label", taskGroup.getLabel(), styleClass));
        item.add(new StyleClassLabel("primarySoftware", taskGroup.getPrimarySoftware().getLabel(), styleClass));
        item.add(new StyleClassLabel("role", role, styleClass));
        Link link = new Link("useTaskGroup") {

          @Override
          public void onClick() {
            WebSession webSession = ((WebSession) getSession());
            Transaction trans = webSession.getDataSession().beginTransaction();
            User user = ((WebSession) getSession()).getUser();
            user.setSelectedTaskGroup(taskGroup);
            user.setSelectedTestPanel(null);
            user.setSelectedTestPanelCase(null);
            user.setSelectedSoftware(taskGroup.getPrimarySoftware());
            user.setSelectedSoftwareCompare(null);
            dataSession.update(user);
            trans.commit();
            setResponsePage(new TaskGroupPage());
          }

        };
        item.add(link);
      }
    };
    add(taskGroupItems);
  }
}
