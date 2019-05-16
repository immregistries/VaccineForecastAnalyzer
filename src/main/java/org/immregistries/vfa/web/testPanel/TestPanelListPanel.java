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
package org.immregistries.vfa.web.testPanel;

import java.util.List;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.immregistries.vfa.StyleClassLabel;
import org.immregistries.vfa.model.TestPanel;
import org.immregistries.vfa.model.User;
import org.immregistries.vfa.web.WebSession;

public class TestPanelListPanel extends Panel {
  private static final long serialVersionUID = 1L;

  public TestPanelListPanel(String id, final PageParameters pageParameters, final Session dataSession, final User user,
      List<TestPanel> testPanelList) {
    super(id);
    ListView<TestPanel> testPanelItems = new ListView<TestPanel>("testPanelItems", testPanelList) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem<TestPanel> item) {
        // TODO Auto-generated method stub
        final TestPanel testPanel = item.getModelObject();

        WebSession webSession = ((WebSession) getSession());
        TestPanel selectedTestPanel = webSession.getUser().getSelectedTestPanel();
        boolean selected = selectedTestPanel != null && testPanel.equals(selectedTestPanel);
        String styleClass = selected ? "highlight" : "";
        item.add(new StyleClassLabel("label", testPanel.getLabel(), styleClass));
        

        Link link = new Link("useTestPanel") {

          private static final long serialVersionUID = 1L;

          @Override
          public void onClick() {
            WebSession webSession = ((WebSession) getSession());
            Session dataSession = webSession.getDataSession();
            Transaction trans = dataSession.beginTransaction();
            User user = ((WebSession) getSession()).getUser();
            user.setSelectedTestPanel(testPanel);
            dataSession.update(user);
            trans.commit();
            setResponsePage(new TestCaseListPage(pageParameters));
          }

        };
        item.add(link);
      }
    };
    add(testPanelItems);

  }
}
