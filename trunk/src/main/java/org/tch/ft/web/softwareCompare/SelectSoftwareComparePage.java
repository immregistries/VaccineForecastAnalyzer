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
package org.tch.ft.web.softwareCompare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.tch.ft.StyleClassLabel;
import org.tch.ft.manager.ForecastActualExpectedCompare;
import org.tch.ft.manager.SoftwareCompareManager;
import org.tch.ft.manager.SoftwareManager;
import org.tch.fc.model.ForecastActual;
import org.tch.ft.model.ForecastCompare;
import org.tch.ft.model.ForecastTarget;
import org.tch.fc.model.Software;
import org.tch.ft.model.SoftwareCompare;
import org.tch.ft.model.SoftwareTarget;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.TestPanelCase;
import org.tch.ft.model.User;
import org.tch.ft.web.FTBasePage;
import org.tch.ft.web.MenuSection;
import org.tch.ft.web.SecurePage;
import org.tch.ft.web.WebSession;

public class SelectSoftwareComparePage extends FTBasePage implements SecurePage {
  private static final long serialVersionUID = 1L;

  private Model<String> testPanelLabelModel = null;

  public SelectSoftwareComparePage() {
    this(new PageParameters());
  }

  public SelectSoftwareComparePage(final PageParameters pageParameters) {
    super(MenuSection.SOFTWARE_COMPARE, pageParameters);
    final ArrayList<Software> compareSoftwareSelect = new ArrayList<Software>();

    final WebSession webSession = (WebSession) getSession();
    final Session dataSession = webSession.getDataSession();
    final User user = ((WebSession) getSession()).getUser();

    final Software software = user.getSelectedSoftware();
    final TestPanel testPanel = user.getSelectedTestPanel();

    add(new Label("softwareLabel", software.getLabel()));

    List<SoftwareCompare> softwareCompareList;
    if (software != null && testPanel != null) {
      Query query = dataSession.createQuery("from SoftwareCompare where software = ? and testPanel = ?");
      query.setParameter(0, software);
      query.setParameter(1, testPanel);
      softwareCompareList = query.list();
    } else {
      softwareCompareList = new ArrayList<SoftwareCompare>();
    }

    ListView<SoftwareCompare> softwareCompareItems = new ListView<SoftwareCompare>("softwareCompareItems",
        softwareCompareList) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem<SoftwareCompare> item) {
        // TODO Auto-generated method stub
        final SoftwareCompare softwareCompare = item.getModelObject();

        final WebSession webSession = ((WebSession) getSession());
        if (softwareCompare.getSoftwareTargetList() == null) {
          Query query = dataSession.createQuery("from SoftwareTarget where softwareCompare = ?");
          query.setParameter(0, softwareCompare);
          softwareCompare.setSoftwareTargetList(query.list());
        }

        SoftwareCompare selectedSoftwareCompare = webSession.getUser().getSelectedSoftwareCompare();
        boolean selected = selectedSoftwareCompare != null
            && selectedSoftwareCompare.getSoftwareCompareId() == softwareCompare.getSoftwareCompareId();
        String styleClass = selected ? "highlight" : "";
        item.add(new StyleClassLabel("label", softwareCompare.getComparedToLabel(), styleClass));

        Link link = new Link("useSoftwareCompare") {

          private static final long serialVersionUID = 1L;

          @Override
          public void onClick() {
            WebSession webSession = ((WebSession) getSession());
            Session dataSession = webSession.getDataSession();
            Transaction trans = dataSession.beginTransaction();
            User user = ((WebSession) getSession()).getUser();
            user.setSelectedSoftwareCompare(softwareCompare);
            dataSession.update(user);
            trans.commit();
            setResponsePage(new SelectSoftwareComparePage(pageParameters));
          }

        };
        item.add(link);
      }
    };
    add(softwareCompareItems);

    WebMarkupContainer createSoftwareCompareSection = new WebMarkupContainer("createSoftwareCompareSection");
    createSoftwareCompareSection.setVisible(SoftwareManager.canEditSoftwareCompare(software, user, dataSession));
    add(createSoftwareCompareSection);

    Query query = dataSession.createQuery("from Software where softwareId != ? order by label");
    query.setParameter(0, software.getSoftwareId());
    List<Software> compareSoftwareList = query.list();

    final CheckBoxMultipleChoice<Software> checkBoxMultipleChoice = new CheckBoxMultipleChoice<Software>(
        "compareSoftwareCheckbox", new Model(compareSoftwareSelect), compareSoftwareList);

    Form<?> createdSoftwareCompareForm = new Form<Void>("createSoftwareCompareForm") {
      private static final long serialVersionUID = 1L;

      @Override
      protected void onSubmit() {

        SoftwareCompareManager softwareCompareManager = new SoftwareCompareManager(testPanel, software, compareSoftwareSelect);
        softwareCompareManager.start();
        webSession.setSoftwareCompareManager(softwareCompareManager);

        setResponsePage(new SelectSoftwareComparePage());
      }
    };

    createdSoftwareCompareForm.add(checkBoxMultipleChoice);

    createSoftwareCompareSection.add(createdSoftwareCompareForm);

  }
}
