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

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.hibernate.Query;
import org.hibernate.Session;
import org.tch.fc.model.Software;
import org.tch.fc.model.SoftwareSetting;
import org.tch.ft.model.User;
import org.tch.ft.web.FTBasePage;
import org.tch.ft.web.MenuSection;
import org.tch.ft.web.SecurePage;
import org.tch.ft.web.WebSession;

public class SoftwareDetailsPage extends FTBasePage implements SecurePage {
  private static final long serialVersionUID = 1L;

  public SoftwareDetailsPage() {
    this(new PageParameters());
  }

  public SoftwareDetailsPage(final PageParameters pageParameters) {
    super(MenuSection.SOFTWARE, pageParameters);

    WebSession webSession = (WebSession) getSession();
    User user = webSession.getUser();
    Software software = user.getSelectedSoftware();
    if (software == null) {
      throw new RestartResponseException(SoftwarePage.class);
    }

    add(new Label("softwareLabel", software.getLabel()));
    add(new Label("serviceType", software.getServiceType()));
    add(new Label("serviceUrl", software.getServiceUrl()));
    add(new Label("scheduleName", software.getScheduleName()));
    
    Session dataSession = webSession.getDataSession();
    Query query = dataSession.createQuery("from SoftwareSetting where software = ? order by serviceOption.optionLabel");
    query.setParameter(0, software);
    List<SoftwareSetting> softwareSettingList = query.list();
    
    ListView<SoftwareSetting> softwareSettingItems = new ListView<SoftwareSetting>("softwareSettingItems", softwareSettingList) {
      
      @Override
      protected void populateItem(ListItem<SoftwareSetting> item) {
        final SoftwareSetting softwareSetting = item.getModelObject();
        item.add(new Label("optionLabel", softwareSetting.getServiceOption().getOptionLabel()));
        item.add(new Label("optionValue", softwareSetting.getOptionValue()));
        item.add(new Label("description", softwareSetting.getServiceOption().getDescription()));
      }
    };
    add(softwareSettingItems);

    

  }

}
