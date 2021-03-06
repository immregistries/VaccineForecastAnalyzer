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
package org.immregistries.vfa.web.software;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.immregistries.vfa.connect.model.Service;
import org.immregistries.vfa.connect.model.Software;
import org.immregistries.vfa.connect.model.ServiceOption;
import org.immregistries.vfa.connect.model.SoftwareSetting;
import org.immregistries.vfa.model.User;
import org.immregistries.vfa.web.FTBasePage;
import org.immregistries.vfa.web.MenuSection;
import org.immregistries.vfa.web.SecurePage;
import org.immregistries.vfa.web.WebSession;

public class EditSoftwarePage extends FTBasePage implements SecurePage {
  private static final long serialVersionUID = 1L;

  public EditSoftwarePage() {
    super(MenuSection.SOFTWARE);

    final WebSession webSession = (WebSession) getSession();
    final Session dataSession = webSession.getDataSession();
    final User user = webSession.getUser();
    final Software software = user.getSelectedSoftware();

    Form<Software> editSoftwareForm = new Form<Software>("editSoftwareForm", new CompoundPropertyModel<Software>(
        software)) {
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

    Query query = dataSession.createQuery("from SoftwareSetting where software = ? order by serviceOption.optionLabel");
    query.setParameter(0, software);
    List<SoftwareSetting> softwareSettingList = query.list();

    ListView<SoftwareSetting> softwareSettingItems = new ListView<SoftwareSetting>("softwareSettingItems",
        softwareSettingList) {

      @Override
      protected void populateItem(ListItem<SoftwareSetting> item) {
        final SoftwareSetting softwareSetting = item.getModelObject();

        Form<SoftwareSetting> editSoftwareSettingForm = new Form<SoftwareSetting>("editSoftwareSettingForm",
            new CompoundPropertyModel<SoftwareSetting>(softwareSetting)) {
          @Override
          protected void onSubmit() {
            Transaction transaction = dataSession.beginTransaction();
            dataSession.update(softwareSetting);
            transaction.commit();
            setResponsePage(new SoftwareDetailsPage());
          }
        };

        TextField<String> optionValue = new TextField<String>("optionValue");
        optionValue.setRequired(true);
        editSoftwareSettingForm.add(optionValue);
        editSoftwareSettingForm.add(new Label("optionLabel", softwareSetting.getServiceOption().getOptionLabel()));
        editSoftwareSettingForm.add(new Label("description", softwareSetting.getServiceOption().getDescription()));
        item.add(editSoftwareSettingForm);
      }
    };
    add(softwareSettingItems);

    // Add form
    final SoftwareSetting softwareSetting = new SoftwareSetting();
    softwareSetting.setSoftware(software);

    Form<SoftwareSetting> addSoftwareSettingForm = new Form<SoftwareSetting>("addSoftwareSettingForm",
        new CompoundPropertyModel<SoftwareSetting>(softwareSetting)) {
      @Override
      protected void onSubmit() {
        Transaction transaction = dataSession.beginTransaction();
        dataSession.save(softwareSetting);
        transaction.commit();
        setResponsePage(new SoftwareDetailsPage());
      }
    };

    TextField<String> optionValue = new TextField<String>("optionValue");
    optionValue.setRequired(true);
    addSoftwareSettingForm.add(optionValue);

    List<ServiceOption> serviceOptionList = getServiceOptionList(dataSession, software, softwareSettingList);
    DropDownChoice<ServiceOption> serviceOption = new DropDownChoice<ServiceOption>("serviceOption", serviceOptionList);
    serviceOption.setRequired(true);
    addSoftwareSettingForm.add(serviceOption);

    add(addSoftwareSettingForm);

  }

  public List<ServiceOption> getServiceOptionList(final Session dataSession, final Software software,
      List<SoftwareSetting> softwareSettingList) {

    Query query = dataSession.createQuery("from ServiceOption where serviceType = ?");
    query.setParameter(0, software.getServiceType());
    List<ServiceOption> serviceOptionList = query.list();
    for (Iterator<ServiceOption> it = serviceOptionList.iterator(); it.hasNext();) {
      ServiceOption serviceOption = it.next();
      for (SoftwareSetting softwareSettingCompare : softwareSettingList) {
        if (softwareSettingCompare.getServiceOption().equals(serviceOption)) {
          it.remove();
          break;
        }
      }
    }
    return serviceOptionList;
  }

}
