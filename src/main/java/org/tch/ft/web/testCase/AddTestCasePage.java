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
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.tch.fc.model.TestCase;
import org.tch.ft.model.Include;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.TestPanelCase;
import org.tch.ft.model.User;
import org.tch.ft.web.FTBasePage;
import org.tch.ft.web.MenuSection;
import org.tch.ft.web.SecurePage;
import org.tch.ft.web.WebSession;

public class AddTestCasePage extends FTBasePage implements SecurePage {
  private static final long serialVersionUID = 1L;

  public AddTestCasePage() {
    this(new PageParameters());
  }

  public AddTestCasePage(final PageParameters pageParameters) {
    super(MenuSection.TEST_PANEL, pageParameters);
    final Session dataSession = ((WebSession) getSession()).getDataSession();
    final WebSession webSession = (WebSession) getSession();
    final User user = webSession.getUser();
    final TestPanel testPanel = user.getSelectedTestPanel();

    add(new FeedbackPanel("feedback"));

    final TestCase testCase = new TestCase();
    testCase.setEvalDate(new Date());
    testCase.setPatientFirst(RandomNames.getRandomFirstName());
    testCase.setPatientLast(RandomNames.getRandomLastName());
    testCase.setPatientSex("F");
    
    Form<TestCase> testCaseForm = new Form<TestCase>("testCaseForm", new CompoundPropertyModel<TestCase>(testCase)) {
      @Override
      protected void onSubmit() {
        if (testCase.getTestCaseNumber() != null && testCase.getTestCaseNumber().length() > 0)
        {
          // make sure it's unique for test panel
          Query query = dataSession.createQuery("from TestPanelCase where testPanel = ? and testCaseNumber = ?");
          query.setParameter(0, testPanel);
          query.setParameter(1, testCase.getTestCaseNumber());
          if (query.list().size() > 0)
          {
            error("Test case number has already been used in this test panel");
            return;
          }
        }
        Transaction transaction = dataSession.beginTransaction();
        dataSession.save(testCase);
        TestPanelCase testPanelCase = new TestPanelCase();
        testPanelCase.setCategoryName(testCase.getCategoryName());
        testPanelCase.setInclude(Include.INCLUDED);
        testPanelCase.setTestCase(testCase);
        testPanelCase.setTestCaseNumber(testCase.getTestCaseNumber());
        testPanelCase.setTestPanel(testPanel);
        dataSession.save(testPanelCase);
        user.setSelectedTestCase(testCase);
        user.setSelectedTestPanelCase(testPanelCase);
        transaction.commit();
        setResponsePage(new CalendarPage());
      }
    };

    add(testCaseForm);

    TextField<String> testCaseNumberField = new TextField<String>("testCaseNumber");
    testCaseNumberField.add(StringValidator.maximumLength(120));
    testCaseForm.add(testCaseNumberField);

    TextField<String> categoryNameField = new TextField<String>("categoryName");
    categoryNameField.add(StringValidator.maximumLength(120));
    categoryNameField.setRequired(true);
    testCaseForm.add(categoryNameField);

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
