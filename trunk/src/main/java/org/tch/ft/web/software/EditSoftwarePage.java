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
package org.tch.ft.web.software;

import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.tch.fc.model.Service;
import org.tch.fc.model.Software;
import org.tch.ft.model.User;
import org.tch.ft.web.FTBasePage;
import org.tch.ft.web.MenuSection;
import org.tch.ft.web.SecurePage;
import org.tch.ft.web.WebSession;

public class EditSoftwarePage extends FTBasePage implements SecurePage  {
  private static final long serialVersionUID = 1L;

  public EditSoftwarePage() {
    super(MenuSection.SOFTWARE);

    final WebSession webSession = (WebSession) getSession();
    final Session dataSession = webSession.getDataSession();  
    final User user = webSession.getUser();
    final Software software = user.getSelectedSoftware();
    
    Form<Software> editSoftwareForm = new Form<Software>("editSoftwareForm", new CompoundPropertyModel<Software>(software)) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void onSubmit() {
        Transaction transaction = dataSession.beginTransaction();
        dataSession.update(software);
        transaction.commit();
        setResponsePage(new SoftwareDetailsPage());
      }
    };
    
    TextField<String> softwareLabel = new TextField<String>("label");
    softwareLabel.setRequired(true);
    editSoftwareForm.add(softwareLabel);
    
    List<Service> serviceList = Service.valueList();
    DropDownChoice<Service> serviceType = new DropDownChoice<Service>("service", serviceList);
    serviceType.setRequired(true);
    editSoftwareForm.add(serviceType);

    TextField<String> serviceUrl = new TextField<String>("serviceUrl");
    editSoftwareForm.add(serviceUrl);

    TextField<String> scheduleName = new TextField<String>("scheduleName");
    editSoftwareForm.add(scheduleName);
    
    add(editSoftwareForm);

  }

}
