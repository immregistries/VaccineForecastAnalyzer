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
package org.immregistries.vfa.web.testCase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.immregistries.vfa.connect.model.Event;
import org.immregistries.vfa.connect.model.ServiceOption;
import org.immregistries.vfa.connect.model.Software;
import org.immregistries.vfa.connect.model.TestCase;
import org.immregistries.vfa.connect.model.TestCaseSetting;
import org.immregistries.vfa.connect.model.TestEvent;
import org.immregistries.vfa.model.Include;
import org.immregistries.vfa.model.Result;
import org.immregistries.vfa.model.TestPanelCase;
import org.immregistries.vfa.model.User;
import org.immregistries.vfa.web.FTBasePage;
import org.immregistries.vfa.web.MenuSection;
import org.immregistries.vfa.web.SecurePage;
import org.immregistries.vfa.web.WebSession;

public class CalendarPage extends FTBasePage implements SecurePage {
  private static final long serialVersionUID = 1L;
  
  public CalendarPage() {
    this(new PageParameters());
  }

  public CalendarPage(final PageParameters pageParameters) {
    super(MenuSection.TEST_CASE, pageParameters);
    final WebSession webSession = ((WebSession) getSession());
    final User user = webSession.getUser();
    final TestCase testCase = user.getSelectedTestCase();
    final Session dataSession = webSession.getDataSession();
    

    add(new FeedbackPanel("feedback"));


  }


}
