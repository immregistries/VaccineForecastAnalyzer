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

        Transaction trans = dataSession.beginTransaction();
        SoftwareCompare softwareCompare = new SoftwareCompare();
        try {

          softwareCompare.setSoftware(software);
          softwareCompare.setTestPanel(testPanel);
          dataSession.save(softwareCompare);
          for (Software compareSoftware : compareSoftwareSelect) {
            SoftwareTarget softwareTarget = new SoftwareTarget();
            softwareTarget.setSoftwareCompare(softwareCompare);
            softwareTarget.setSoftware(compareSoftware);
            dataSession.save(softwareTarget);
          }
        } finally {
          trans.commit();
        }

        trans = dataSession.beginTransaction();

        try {

          Query query = dataSession.createQuery("from TestPanelCase where testPanel = ?");
          query.setParameter(0, testPanel);
          List<TestPanelCase> testPanelCaseList = query.list();
          for (TestPanelCase testPanelCase : testPanelCaseList) {

            query = dataSession
                .createQuery("from ForecastActual where software = ? and testCase = ? order by runDate desc");
            query.setParameter(0, software);
            query.setParameter(1, testPanelCase.getTestCase());
            List<ForecastActual> forecastActualList = query.list();
            if (forecastActualList.size() != 0) {
              for (ForecastActual forecastActual : forecastActualList) {
                ForecastCompare forecastCompare = new ForecastCompare();
                forecastCompare.setSoftwareCompare(softwareCompare);
                forecastCompare.setForecastActual(forecastActual);

                boolean allFound = compareSoftwareSelect.size() > 0;
                List<ForecastActual> compareWithList = new ArrayList<ForecastActual>();
                List<ForecastActualExpectedCompare> forecastActualExpectedCompareList = new ArrayList<ForecastActualExpectedCompare>();
                String compareLabel = "";
                for (Software compareSoftware : compareSoftwareSelect) {
                  query = dataSession
                      .createQuery("from ForecastActual where software = ? and testCase = ? and forecastItem = ? order by runDate desc");
                  query.setParameter(0, compareSoftware);
                  query.setParameter(1, testPanelCase.getTestCase());
                  query.setParameter(2, forecastActual.getForecastItem());
                  List<ForecastActual> forecastActualCompareList = query.list();
                  if (forecastActualCompareList.size() == 0) {
                    allFound = false;
                  } else {
                    ForecastTarget forecastTarget = new ForecastTarget();
                    forecastTarget.setForecastCompare(forecastCompare);
                    forecastTarget.setForecastActual(forecastActual);

                    compareWithList.add(forecastActualCompareList.get(0));
                    ForecastActualExpectedCompare forecastActualExcpectedCompare = new ForecastActualExpectedCompare();
                    forecastActualExcpectedCompare.setTestCase(testPanelCase.getTestCase());
                    forecastActualExcpectedCompare.setForecastResultA(forecastActual);
                    forecastActualExcpectedCompare.setForecastResultB(forecastActualCompareList.get(0));
                    forecastActualExcpectedCompare.setForecastItem(forecastActual.getForecastItem());
                    forecastActualExpectedCompareList.add(forecastActualExcpectedCompare);
                  }
                }
                if (!allFound) {
                  compareLabel = "X: Not all forecasts run";
                } else {
                  boolean matchAll = true;
                  int matchCount = 0;
                  for (ForecastActualExpectedCompare forecastActualExcpectedCompare : forecastActualExpectedCompareList) {
                    if (forecastActualExcpectedCompare.matchExactlyExlcudeOverdue()) {
                      matchCount++;
                    } else {
                      matchAll = false;
                    }
                  }
                  if (matchAll) {
                    compareLabel = "A: Same as all others";
                  } else if (matchCount > 0) {
                    compareLabel = "B: Same as at least " + matchCount + " other" + (matchCount == 1 ? "" : "s");
                  } else {
                    boolean completelyDisagree = true;
                    boolean completelyAgree = true;
                    for (int i = 1; i < compareWithList.size(); i++) {
                      for (int j = 0; j < i; j++) {
                        ForecastActualExpectedCompare forecastActualExcpectedCompare = new ForecastActualExpectedCompare();
                        forecastActualExcpectedCompare.setForecastResultA(compareWithList.get(j));
                        forecastActualExcpectedCompare.setForecastResultB(compareWithList.get(i));
                        if (forecastActualExcpectedCompare.matchExactlyExlcudeOverdue()) {
                          completelyDisagree = false;
                        } else {
                          completelyAgree = false;
                        }
                      }
                    }
                    if (completelyDisagree) {
                      compareLabel = "C: Different than all others and others don't agree";
                    } else if (completelyAgree) {
                      compareLabel = "E: Different than all others and others agree";
                    } else {
                      compareLabel = "D: Different than all others and others have mixed agreement";
                    }
                  }
                  forecastCompare.setCompareLabel(compareLabel);
                  dataSession.save(forecastCompare);
                  for (ForecastActual forecastActualToCompareWith : compareWithList) {
                    ForecastTarget forecastTarget = new ForecastTarget();
                    forecastTarget.setForecastCompare(forecastCompare);
                    forecastTarget.setForecastActual(forecastActualToCompareWith);
                    dataSession.save(forecastTarget);
                  }

                }

                dataSession.save(forecastCompare);

              }
            }
          }

        } finally {
          trans.commit();
        }

        setResponsePage(new SelectSoftwareComparePage());
      }
    };

    createdSoftwareCompareForm.add(checkBoxMultipleChoice);

    createSoftwareCompareSection.add(createdSoftwareCompareForm);

  }
}
