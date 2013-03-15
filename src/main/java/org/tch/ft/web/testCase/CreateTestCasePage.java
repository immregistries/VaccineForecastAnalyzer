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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.tch.ft.model.TaskGroup;
import org.tch.ft.model.TestCase;
import org.tch.ft.model.TestPanelCase;
import org.tch.ft.model.User;
import org.tch.ft.web.WebSession;
import org.tch.ft.web.FTBasePage;
import org.tch.ft.web.MenuSection;
import org.tch.ft.web.SecurePage;
import org.tch.ft.web.taskGroup.ExpertsAssignedPage;

public class CreateTestCasePage extends FTBasePage implements SecurePage {
  private static final long serialVersionUID = 1L;

  public CreateTestCasePage() {
    this(new PageParameters());
  }

  public CreateTestCasePage(final PageParameters pageParameters) {
    super(MenuSection.TEST_CASE, pageParameters);
    final WebSession webSession = ((WebSession) getSession());
    final TestCase testCase = new TestCase();
    final Session dataSession = webSession.getDataSession();
    final User user = webSession.getUser();
    
    testCase.setEvalDate(new Date());
    // TODO set first and last name automatically

    Form<TestCase> testCaseForm = new Form<TestCase>("testCaseForm", new CompoundPropertyModel<TestCase>(testCase)) {
      @Override
      protected void onSubmit() {
        Transaction transaction = dataSession.beginTransaction();
        dataSession.save(testCase);
        transaction.commit();
      }
    };
    add(testCaseForm);

    TextField<String> labelField = new TextField<String>("label");
    labelField.add(StringValidator.maximumLength(120));
    labelField.setRequired(true);
    testCaseForm.add(labelField);

    TextArea<String> descriptionField = new TextArea<String>("description");
    descriptionField.add(StringValidator.maximumLength(4000));
    descriptionField.setRequired(true);
    testCaseForm.add(descriptionField);

    TextField<String> patientFirstField = new TextField<String>("patientFirst");
    patientFirstField.add(StringValidator.maximumLength(30));
    patientFirstField.setRequired(true);
    testCaseForm.add(patientFirstField);

    TextField<String> patientLastField = new TextField<String>("patientLast");
    patientLastField.add(StringValidator.maximumLength(30));
    patientLastField.setRequired(true);
    testCaseForm.add(patientLastField);

    List<String> patientSexList = new ArrayList<String>();
    patientSexList.add(TestCase.PATIENT_SEX_FEMALE);
    patientSexList.add(TestCase.PATIENT_SEX_MALE);
    DropDownChoice<String> patientSexField = new DropDownChoice<String>("patientSex", patientSexList);
    patientSexField.setRequired(true);
    testCaseForm.add(patientSexField);

    TextField<Date> patientDobField = new TextField<Date>("patientDob");
    patientDobField.setRequired(true);
    patientDobField.add(new DatePicker());
    testCaseForm.add(patientDobField);

    TextField<Date> evalDateField = new TextField<Date>("evalDate");
    evalDateField.setRequired(true);
    evalDateField.add(new DatePicker());
    testCaseForm.add(evalDateField);


  }

}
