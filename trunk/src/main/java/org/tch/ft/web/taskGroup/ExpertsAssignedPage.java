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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.tch.ft.model.Expert;
import org.tch.ft.model.Role;
import org.tch.ft.model.TaskGroup;
import org.tch.ft.model.User;
import org.tch.ft.web.WebSession;
import org.tch.ft.web.FTBasePage;
import org.tch.ft.web.MenuSection;
import org.tch.ft.web.SecurePage;

public class ExpertsAssignedPage extends FTBasePage  implements SecurePage {
  private static final long serialVersionUID = 1L;
  private Model<User> addUserModel;
  private Model<Role> addRoleModel;

  public ExpertsAssignedPage() {
    this(new PageParameters());
  }

  public ExpertsAssignedPage(final PageParameters pageParameters) {
    super(MenuSection.TASK_GROUP, pageParameters);

    final WebSession webSession = (WebSession) getSession();
    final Session dataSession = webSession.getDataSession();
    final User user = webSession.getUser();
    final TaskGroup taskGroup = webSession.getUser().getSelectedTaskGroup();
    final boolean canEdit = determineCanEdit(user, dataSession, taskGroup);
    
    Query query;
    WebMarkupContainer taskGroupSection = new WebMarkupContainer("taskGroupSection");
    
    if (taskGroup == null) {
      taskGroupSection.setVisible(false);
    } else {
      taskGroupSection.add(new Label("label", taskGroup.getLabel()));
      query = dataSession.createQuery("from Expert where taskGroup = ? order by user.name");
      query.setParameter(0, taskGroup);
      final List<Expert> expertList = query.list();
      ListView<Expert> expertListItems = new ListView<Expert>("expertListItems", expertList) {
        private static final long serialVersionUID = 1L;

        @Override
        protected void populateItem(ListItem<Expert> item) {
          final Expert expert = item.getModelObject();
          item.add(new Label("name", expert.getUser().getName()));
          item.add(new Label("role", expert.getRole().getLabel()));
          item.add(new Label("organization", expert.getUser().getOrganization()));
          item.add(new Label("position", expert.getUser().getPosition()));
          item.add(new Label("email", expert.getUser().getEmail()));
          item.add(new Label("phone", expert.getUser().getPhone()));
          Link<Void> link = new Link<Void>("removeExpert") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
              Transaction transaction = dataSession.beginTransaction();
              try {
                dataSession.delete(expert);
              } finally {
                transaction.commit();
              }
              setResponsePage(new ExpertsAssignedPage());
            }
          };
          link.setVisible(canEdit);
          item.add(link);
        }
      };
      taskGroupSection.add(expertListItems);
      WebMarkupContainer addExpertSection = new WebMarkupContainer("addExpertSection");
      if (canEdit) {
        Form<Void> addExpertForm = new Form<Void>("addExpertForm") {
          @Override
          protected void onSubmit() {
            Transaction transaction = dataSession.beginTransaction();
            try {
              Expert expert = new Expert();
              expert.setUser(addUserModel.getObject());
              expert.setTaskGroup(taskGroup);
              expert.setRole(addRoleModel.getObject());
              dataSession.save(expert);
            } finally {
              transaction.commit();
            }
            setResponsePage(new ExpertsAssignedPage());
          }
        };
        query = dataSession.createQuery("from User order by name");
        List<User> userList = query.list();
        for (Iterator<User> it = userList.iterator(); it.hasNext();) {
          User u = it.next();
          for (Expert expert : expertList) {
            if (expert.getUser() == u) {
              it.remove();
              break;
            }
          }
        }

        List<Role> roleList = new ArrayList<Role>();
        roleList.add(Role.ADMIN);
        roleList.add(Role.EXPERT);
        roleList.add(Role.VIEW);
        addUserModel = new Model<User>();
        addRoleModel = new Model<Role>(Role.EXPERT);
        DropDownChoice<User> addUser = new DropDownChoice<User>("user", addUserModel, userList);
        DropDownChoice<Role> addRole = new DropDownChoice<Role>("role", addRoleModel, roleList);
        addUser.setRequired(true);
        addRole.setRequired(true);
        addExpertForm.add(addUser);
        addExpertForm.add(addRole);

        addExpertSection.add(addExpertForm);
        // TODO
      } else {
        addExpertSection.setVisible(false);
      }
      taskGroupSection.add(addExpertSection);
    }
    add(taskGroupSection);

    
  }

  public static boolean determineCanEdit(final User user, final Session dataSession, TaskGroup taskGroup) {
    Query query = dataSession.createQuery("from Expert where user = ? and taskGroup = ?");
    query.setParameter(0, user);
    query.setParameter(1, taskGroup);
    List<Expert> expertList = query.list();
    if (expertList.size() > 0) {
      Expert expert = expertList.get(0);
      if (expert.getRole() == Role.EXPERT || expert.getRole() == Role.ADMIN) {
        return true;
      }
    }
    return false;
  }
}
