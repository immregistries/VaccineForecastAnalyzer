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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.hibernate.Query;
import org.tch.fc.TCHConnector;
import org.tch.fc.model.TestCase;
import org.tch.fc.model.TestEvent;
import org.tch.ft.model.User;
import org.tch.ft.web.FTBasePage;
import org.tch.ft.web.MenuSection;
import org.tch.ft.web.SecurePage;
import org.tch.ft.web.WebSession;

public class TestCaseDetail extends FTBasePage implements SecurePage
{
  private static final long serialVersionUID = 1L;

  public TestCaseDetail() {
    this(new PageParameters());
  }

  public TestCaseDetail(final PageParameters pageParameters) {
    super(MenuSection.TEST_CASE, pageParameters);
    WebSession webSession = ((WebSession) getSession());
    final User user = webSession.getUser();
    final TestCase testCase = user.getSelectedTestCase();
    setupTestCaseDetail(webSession, testCase);
  }

  public void setupTestCaseDetail(WebSession webSession, final TestCase testCase) {
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    add(new Label("label", testCase.getLabel()));
    add(new Label("description", testCase.getDescription()));
    add(new Label("patientFirst", testCase.getPatientFirst()));
    add(new Label("patientLast", testCase.getPatientLast()));
    add(new Label("patientSex", testCase.getPatientSex()));
    add(new Label("patientDob", sdf.format(testCase.getPatientDob())));
    add(new Label("evalDate", sdf.format(testCase.getEvalDate())));
    add(new Label("testCaseId", String.valueOf(testCase.getTestCaseId())));

    {
      Query query = webSession.getDataSession().createQuery("from TestEvent where testCase = ?");
      query.setParameter(0, testCase);
      List<TestEvent> testEventList = query.list();
      testCase.setTestEventList(testEventList);

      ListView<TestEvent> testEventItems = new ListView<TestEvent>("testEventItems", testEventList) {

        protected void populateItem(ListItem<TestEvent> item) {
          final TestEvent testEvent = item.getModelObject();

          SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
          item.add(new Label("eventType", testEvent.getEvent().getEventType().getLabel()));
          item.add(new Label("label", testEvent.getEvent().getLabel()));
          item.add(new Label("vacineCvx", testEvent.getEvent().getVaccineCvx()));
          item.add(new Label("vacineMvx", testEvent.getEvent().getVaccineMvx()));
          item.add(new Label("eventDate", sdf.format(testEvent.getEventDate())));
          item.add(new Label("eventAge", testEvent.getAgeAlmost(testCase)));
        }
      };
      add(testEventItems);
    }

  }

}
