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
package org.tch.ft.web.unsecure;

import java.util.List;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.hibernate.Query;
import org.hibernate.Session;
import org.tch.ft.manager.Mailer;
import org.tch.ft.model.User;
import org.tch.ft.web.WebSession;
import org.tch.ft.web.FTBasePage;
import org.tch.ft.web.MenuSection;

public class ForgotPasswordPage extends FTBasePage {

  public ForgotPasswordPage() {
    super(MenuSection.UNSECURE);
    add(new FeedbackPanel("feedback"));
    final User user = new User();

    Form<User> forgotPasswordForm = new Form<User>("forgotPasswordForm", new CompoundPropertyModel<User>(user)) {

      private static final long serialVersionUID = 1L;

      @Override
      protected void onSubmit() {
        WebSession webSession = ((WebSession) getSession());
        final Session dataSession = webSession.getDataSession();
        // First get record where both match, if that is not found then get
        // record where either matches
        Query query = dataSession.createQuery("from User where name = ? and email = ?");
        query.setParameter(0, user.getName());
        query.setParameter(1, user.getEmail().toLowerCase());
        List<User> matchingUserList = query.list();
        if (matchingUserList.size() == 0) {
          query = dataSession.createQuery("from User where name = ? or email = ?");
          query.setParameter(0, user.getName());
          query.setParameter(1, user.getEmail().toLowerCase());
          matchingUserList = query.list();
        }
        if (matchingUserList.size() == 0) {
          error("Name or email address are not registered");
          return;
        }

        User matchingUser = matchingUserList.get(0);
        Mailer mailer = new Mailer();
        StringBuilder sb = new StringBuilder();
        sb.append("You have been registered as an expert on the Texas Children's Hospital ");
        sb.append("Forecast Testing system.  Here are your access credentials: \r\r");
        sb.append("User Name: " + matchingUser.getName() + "\r");
        sb.append("Password: " + matchingUser.getPassword() + "\r");
        mailer.sendEmail(user.getEmail(), "TCH Forecast Testing Registration", sb.toString());
        info("Email was sent with password to '" + user.getEmail() + "' for Expert named '" + user.getName() + "'");
      }

    };
    add(forgotPasswordForm);
    TextField<String> nameField = new TextField<String>("name");
    nameField.setLabel(Model.of("Name"));
    TextField<String> emailField = new TextField<String>("email");
    nameField.setLabel(Model.of("Email"));
    nameField.setRequired(true);
    emailField.setRequired(true);
    forgotPasswordForm.add(nameField);
    forgotPasswordForm.add(emailField);

  }
}
