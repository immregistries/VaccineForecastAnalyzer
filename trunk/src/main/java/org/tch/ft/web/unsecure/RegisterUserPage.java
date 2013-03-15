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

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.tch.ft.manager.Mailer;
import org.tch.ft.model.Agreement;
import org.tch.ft.model.User;
import org.tch.ft.web.WebSession;
import org.tch.ft.web.FTBasePage;
import org.tch.ft.web.MenuSection;
import org.tch.ft.web.expert.ExpertPage;

public class RegisterUserPage extends FTBasePage {
  private static final long serialVersionUID = 1L;
  private static Random random = new Random();

  public RegisterUserPage(final PageParameters pageParameters) {
    super(MenuSection.UNSECURE);
    add(new FeedbackPanel("feedback"));
    final User user = new User();
    final Agreement agreement = getCurrentAgreement(((WebSession) getSession()).getDataSession());
    Form<User> registerUserForm = new Form<User>("registerUserForm", new CompoundPropertyModel<User>(user)) {

      private static final long serialVersionUID = 1L;

      @Override
      protected void onSubmit() {
        WebSession webSession = ((WebSession) getSession());
        final Session dataSession = webSession.getDataSession();
        Query query = dataSession.createQuery("from User where name = ? or email = ?");
        query.setParameter(0, user.getName());
        query.setParameter(1, user.getEmail());
        List<User> conflictUserList = query.list();
        if (conflictUserList.size() > 0) {
          error("Name or email address already registered");
          return;
        }
        if (!looksLikeEmail(user.getEmail())) {
          error("Email appears to be invalid");
          return;
        }

        if (user.isAgreedToAgreement()) {
          user.setAgreementDate(new Date());
          user.setAgreement(agreement);
        } else {
          error("Must accept agreement to register");
          return;
        }

        Transaction transaction = dataSession.beginTransaction();
        try {
          // force email to lower case for matching
          user.setEmail(user.getEmail().toLowerCase());
          user.setPassword(generateRandomPassword());
          dataSession.save(user);
        } finally {
          transaction.commit();
        }
        webSession.signIn(user.getName(), user.getPassword());
        setResponsePage(new ExpertPage());
        Mailer mailer = new Mailer();
        StringBuilder sb = new StringBuilder();
        sb.append("You have been registered on the Texas Children's Hospital ");
        sb.append("Forecast Testing system and are awaiting approval to be ");
        sb.append("accepted on an expert team. Once you are approved you can use the following ");
        sb.append("access credentials: \r\r");
        sb.append("User Name: " + user.getName() + "\r");
        sb.append("Password: " + user.getPassword() + "\r");
        sb.append("\r");
        sb.append("");
        mailer.sendEmail(user.getEmail(), "TCH Forecast Testing Registration", sb.toString());
      }

      private boolean looksLikeEmail(String email) {
        int posAt = email.indexOf("@");
        if (posAt == -1) {
          return false;
        }
        int posPeriod = email.indexOf('.', posAt);
        if (posPeriod == -1) {
          return false;
        }
        if (email.length() <= 6) {
          return false;
        }
        return true;
      }

      private String generateRandomPassword() {
        String password = "";
        password += (char) (((int) 'A') + random.nextInt(26));
        password += (char) (((int) 'A') + random.nextInt(26));
        password += (char) (((int) '0') + random.nextInt(10));
        password += (char) (((int) '0') + random.nextInt(10));
        password += (char) (((int) 'A') + random.nextInt(26));
        password += (char) (((int) 'A') + random.nextInt(26));
        password += (char) (((int) '0') + random.nextInt(10));
        password += (char) (((int) '0') + random.nextInt(10));
        return password;
      }
    };
    add(registerUserForm);
    TextField<String> nameField = new TextField<String>("name");
    nameField.setLabel(Model.of("Name"));
    nameField.add(StringValidator.maximumLength(30));
    TextField<String> emailField = new TextField<String>("email");
    nameField.setLabel(Model.of("Email"));
    nameField.add(StringValidator.maximumLength(120));
    TextField<String> organizationField = new TextField<String>("organization");
    nameField.setLabel(Model.of("Organization"));
    nameField.add(StringValidator.maximumLength(120));
    TextField<String> positionField = new TextField<String>("position");
    nameField.setLabel(Model.of("Position"));
    nameField.add(StringValidator.maximumLength(120));
    TextField<String> phoneField = new TextField<String>("phone");
    nameField.setLabel(Model.of("Phone"));
    nameField.add(StringValidator.maximumLength(30));
    CheckBox agreeCheckBox = new CheckBox("agreedToAgreement");
    nameField.setRequired(true);
    emailField.setRequired(true);
    organizationField.setRequired(true);
    positionField.setRequired(true);
    phoneField.setRequired(true);
    
    registerUserForm.add(nameField);
    registerUserForm.add(emailField);
    registerUserForm.add(organizationField);
    registerUserForm.add(positionField);
    registerUserForm.add(phoneField);
    registerUserForm.add(agreeCheckBox);
    registerUserForm.add(new Label("agreementText", agreement.getAgreementText()).setEscapeModelStrings(false));

  }

  public static Agreement getCurrentAgreement(Session dataSession) {
    Query query = dataSession.createQuery("from Agreement order by versionDate desc");
    List<Agreement> agreementList = query.list();
    if (agreementList.size() > 0) {
      return agreementList.get(0);
    }
    return null;
  }
}
