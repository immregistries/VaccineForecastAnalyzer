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

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.tch.ft.StyleClassLabel;
import org.tch.ft.manager.SoftwareManager;
import org.tch.fc.model.Software;
import org.tch.ft.model.User;
import org.tch.ft.web.FTBasePage;
import org.tch.ft.web.MenuSection;
import org.tch.ft.web.SecurePage;
import org.tch.ft.web.WebSession;

public class SelectSoftwarePage extends FTBasePage implements SecurePage {
  private static final long serialVersionUID = 1L;

  public SelectSoftwarePage(final PageParameters pageParameters) {
    super(MenuSection.SOFTWARE, pageParameters);

    WebSession webSession = (WebSession) getSession();
    List<Software> softwareList = getSoftwareList(webSession);
    setOutputMarkupId(true);

    ListView<Software> softwareItems = new ListView<Software>("softwareItems", softwareList) {
      @Override
      protected void populateItem(ListItem<Software> item) {
        final Software software = item.getModelObject();

        WebSession webSession = ((WebSession) getSession());
        Software selectedSoftware = webSession.getUser().getSelectedSoftware();
        boolean selected = selectedSoftware != null && software.getSoftwareId() == selectedSoftware.getSoftwareId();
        String styleClass = selected ? "highlight" : "";
        item.add(new StyleClassLabel("label", software.getLabel(), styleClass));
        item.add(new StyleClassLabel("serviceUrl", software.getServiceUrl(), styleClass));
        item.add(new StyleClassLabel("scheduleName", software.getScheduleName(), styleClass));

        Link link = new Link("useSoftware") {

          @Override
          public void onClick() {
            WebSession webSession = ((WebSession) getSession());
            Session dataSession = webSession.getDataSession();
            Transaction trans = dataSession.beginTransaction();
            User user = ((WebSession) getSession()).getUser();
            user.setSelectedSoftware(software);
            user.setSelectedSoftwareCompare(null);
            dataSession.update(user);
            trans.commit();
            setResponsePage(new SoftwarePage(pageParameters));
          }

        };
        item.add(link);
      }
    };
    add(softwareItems);

  }

  public static List<Software> getSoftwareList(WebSession webSession) {
    List<Software> softwareList = makeListObjects(webSession.getDataSession());
    for (Iterator<Software> it = softwareList.iterator(); it.hasNext();) {
      Software software = it.next();
      if (SoftwareManager.isSoftwareAccessRestricted(software, webSession.getUser(), webSession.getDataSession())) {
        it.remove();
      }
    }
    return softwareList;
  }

  private static List<Software> makeListObjects(Session session) {
    Query query = session.createQuery("from Software order by label");
    List<Software> softwareList = query.list();
    return softwareList;
  }

}
