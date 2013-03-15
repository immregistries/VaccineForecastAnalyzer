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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.hibernate.Query;
import org.hibernate.Session;
import org.tch.ft.StyleClassLabel;
import org.tch.ft.manager.AgeUtil;
import org.tch.ft.manager.ForecastActualExpectedCompare;
import org.tch.ft.model.ForecastActual;
import org.tch.ft.model.ForecastItem;
import org.tch.ft.model.Software;
import org.tch.ft.model.TestCase;
import org.tch.ft.model.TestEvent;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.TestPanelCase;
import org.tch.ft.model.User;
import org.tch.ft.web.FTBasePage;
import org.tch.ft.web.MenuSection;
import org.tch.ft.web.SecurePage;
import org.tch.ft.web.WebSession;
import org.tch.ft.web.testCase.ActualVsExpectedPage;

public class CompareSoftwarePage extends FTBasePage implements SecurePage {
  private static final long serialVersionUID = 1L;

  private class ShowHideSection extends WebMarkupContainer {
    private String id = "";

    public ShowHideSection(String label, String id) {
      super(label);
      this.id = id;
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
      super.onComponentTag(tag);
      tag.put("onclick", "showHide('" + id + "')");
    }

  }

  public CompareSoftwarePage() {
    super(MenuSection.SOFTWARE);

    final WebSession webSession = (WebSession) getSession();
    final Session dataSession = webSession.getDataSession();
    final User user = webSession.getUser();
    final Software software = user.getSelectedSoftware();
    final TestPanel testPanel = user.getSelectedTestPanel();
    final ArrayList<Software> compareSoftwareSelect = new ArrayList<Software>();

    Map<String, List<CompareResults>> compareResultMap = webSession.getCompareResultMap();
    if (compareResultMap == null) {
      compareResultMap = new HashMap<String, List<CompareResults>>();
    }

    ListView<List<CompareResults>> compareResultsListView = new ListView<List<CompareResults>>("reportSection",
        new ArrayList<List<CompareResults>>(compareResultMap.values())) {
      @Override
      protected void populateItem(ListItem<List<CompareResults>> item) {
        CompareResults cr = item.getModel().getObject().get(0);
        List<CompareResults> compareResultsList = item.getModel().getObject();
        item.add(new Label("reportSectionTitle", cr.getStatus() + " (" + compareResultsList.size() + ")"));
        ListView<CompareResults> compareSection = new ListView<CompareResults>("compareSection", compareResultsList) {
          @Override
          protected void populateItem(ListItem<CompareResults> item) {
            final CompareResults compareResults = item.getModelObject();
            final ForecastActualExpectedCompare forecastCompare = compareResults.getForecastCompareList().size() > 0 ? compareResults
                .getForecastCompareList().get(0) : null;
            String label;
            TestCase testCase = null;
            if (forecastCompare != null && forecastCompare.getForecastResultA() != null) {
              testCase = forecastCompare.getForecastResultA().getTestCase();
              Query query = dataSession.createQuery("from TestEvent where testCase = ?");
              query.setParameter(0, testCase);
              testCase.setTestEventList(query.list());
            } else {
              testCase = new TestCase();
              testCase.setPatientDob(new Date());
              testCase.setTestEventList(new ArrayList<TestEvent>());
            }
            ForecastItem forecastItem = null;
            if (forecastCompare != null && forecastCompare.getForecastResultA() != null) {
              forecastItem = forecastCompare.getForecastResultA().getForecastItem();
            } else {
              forecastItem = new ForecastItem();
            }
            label = testCase.getLabel();
            String sectionId = "detailSection" + testCase.getTestCaseId() + "-" + forecastItem.getForecastItemId();
            item.add(new Label("testCaseLabel", label + " - " + forecastItem.getLabel()));
            ShowHideSection showHideSection = new ShowHideSection("sectionOpenLink", sectionId);
            item.add(showHideSection);
            WebMarkupContainer detailSection = new WebMarkupContainer("detailSection");
            item.add(detailSection);
            detailSection.setMarkupId(sectionId);
            detailSection.add(new Link("selectTestCase") {
              @Override
              public void onClick() {
                selectTestCase(forecastCompare);
                setResponsePage(new ActualVsExpectedPage());
              }
            });
            final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            final Date birth = testCase.getPatientDob();
            detailSection.add(new Label("dateOfBirth", sdf.format(testCase.getPatientDob())));
            ListView<TestEvent> testEventViewList = new ListView<TestEvent>("testEventViewList",
                testCase.getTestEventList()) {
              @Override
              protected void populateItem(ListItem<TestEvent> item) {
                TestEvent testEvent = item.getModelObject();
                Date eventDate = testEvent.getEventDate();
                item.add(new Label("label", testEvent.getEvent().getLabel()));
                item.add(new Label("eventDate", sdf.format(eventDate) + " " + AgeUtil.getAge(birth, eventDate)));
              }
            };
            detailSection.add(testEventViewList);
            detailSection.add(new Label("softwareLabel", software.getLabel()));
            final ForecastActual forecastActual;
            if (forecastCompare != null) {
              forecastActual = (ForecastActual) forecastCompare.getForecastResultA();
            } else {
              forecastActual = new ForecastActual();
            }
            detailSection.add(new Label("item", forecastActual.getForecastItem() == null ? "" : forecastActual
                .getForecastItem().getLabel()));
            detailSection.add(new Label("doseNumber", forecastActual.getDoseNumber()));
            detailSection.add(new Label("validDate", (forecastActual.getValidDate() == null ? "" : sdf
                .format(forecastActual.getValidDate()))));
            detailSection.add(new Label("dueDate", (forecastActual.getDueDate() == null ? "" : sdf
                .format(forecastActual.getDueDate()))));
            List<ForecastActual> forecastActualList = new ArrayList<ForecastActual>();
            if (forecastCompare != null) {
              for (ForecastActualExpectedCompare forecastCompareActual : compareResults.getForecastCompareList()) {
                forecastActualList.add((ForecastActual) forecastCompareActual.getForecastResultB());
              }
            }
            ListView<ForecastActual> forecastResultViewList = new ListView<ForecastActual>("forecastResultViewList",
                forecastActualList) {
              @Override
              protected void populateItem(ListItem<ForecastActual> item) {
                ForecastActual fac = item.getModelObject();
                item.add(new Label("softwareLabel", fac.getSoftware().getLabel()));
                item.add(new Label("item", fac.getForecastItem().getLabel()));
                String styleClass = ForecastActualExpectedCompare.same(forecastActual.getDoseNumber(),
                    fac.getDoseNumber()) ? "pass" : "fail";
                item.add(new StyleClassLabel("doseNumber", fac.getDoseNumber(), styleClass));
                styleClass = ForecastActualExpectedCompare.same(forecastActual.getValidDate(), fac.getValidDate()) ? "pass"
                    : "fail";
                item.add(new StyleClassLabel("validDate", (fac.getValidDate() == null ? "" : sdf.format(fac
                    .getValidDate())), styleClass));
                styleClass = ForecastActualExpectedCompare.same(forecastActual.getDueDate(), fac.getDueDate()) ? "pass"
                    : "fail";
                item.add(new StyleClassLabel("dueDate", (fac.getDueDate() == null ? "" : sdf.format(fac.getDueDate())),
                    styleClass));
              }
            };
            detailSection.add(forecastResultViewList);
          }
        };
        item.add(compareSection);
      }
    };
    add(compareResultsListView);

    Query query = dataSession.createQuery("from Software where softwareId != ? order by label");
    query.setParameter(0, software.getSoftwareId());
    List<Software> compareSoftwareList = query.list();

    final CheckBoxMultipleChoice<Software> checkBoxMultipleChoice = new CheckBoxMultipleChoice<Software>(
        "compareSoftwareCheckbox", new Model(compareSoftwareSelect), compareSoftwareList);

    Form<?> editSoftwareForm = new Form<Void>("editSoftwareForm") {
      private static final long serialVersionUID = 1L;

      @Override
      protected void onSubmit() {
        Map<String, List<CompareResults>> compareResultMap = new HashMap<String, List<CompareResults>>();
        webSession.setCompareResultMap(compareResultMap);
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
              CompareResults cr = new CompareResults();
              boolean allFound = compareSoftwareSelect.size() > 0;
              List<ForecastActual> compareWithList = new ArrayList<ForecastActual>();
              for (Software compareSoftware : compareSoftwareSelect) {
                query = dataSession
                    .createQuery("from ForecastActual where software = ? and testCase = ? and forecastItem = ? order by runDate desc");
                query.setParameter(0, compareSoftware);
                query.setParameter(1, testPanelCase.getTestCase());
                query.setParameter(2, forecastActual.getForecastItem());
                List<ForecastActual> forecastActualCompareList = query.list();
                if (forecastActualCompareList.size() == 0) {
                  cr.setStatus("X: Forecast not run on " + compareSoftware);
                  allFound = false;
                } else {
                  compareWithList.add(forecastActualCompareList.get(0));
                  ForecastActualExpectedCompare forecastCompare = new ForecastActualExpectedCompare();
                  forecastCompare.setTestCase(testPanelCase.getTestCase());
                  forecastCompare.setForecastResultA(forecastActual);
                  forecastCompare.setForecastResultB(forecastActualCompareList.get(0));
                  forecastCompare.setForecastItem(forecastActual.getForecastItem());
                  cr.getForecastCompareList().add(forecastCompare);
                }
              }
              if (allFound) {
                boolean matchAll = true;
                int matchCount = 0;
                for (ForecastActualExpectedCompare forecastCompare : cr.getForecastCompareList()) {
                  if (forecastCompare.matchExactlyExlcudeOverdue()) {
                    matchCount++;
                  } else {
                    matchAll = false;
                  }
                }
                if (matchAll) {
                  cr.setStatus("A: Same as all others");
                } else if (matchCount > 0) {
                  cr.setStatus("B: Same as at least " + matchCount + " other" + (matchCount == 1 ? "" : "s"));
                } else {
                  boolean completelyDisagree = true;
                  boolean completelyAgree = true;
                  for (int i = 1; i < compareWithList.size(); i++) {
                    for (int j = 0; j < i; j++) {
                      ForecastActualExpectedCompare forecastCompare = new ForecastActualExpectedCompare();
                      forecastCompare.setForecastResultA(compareWithList.get(j));
                      forecastCompare.setForecastResultB(compareWithList.get(i));
                      if (forecastCompare.matchExactlyExlcudeOverdue()) {
                        completelyDisagree = false;
                      } else {
                        completelyAgree = false;
                      }
                    }
                  }
                  if (completelyDisagree) {
                    cr.setStatus("C: Different than all others and others don't agree");
                  } else if (completelyAgree) {
                    cr.setStatus("E: Different than all others and others agree");
                  } else {
                    cr.setStatus("D: Different than all others and others have mixed agreement");
                  }
                }
              }
              List<CompareResults> compareResultsList = compareResultMap.get(cr.getStatus());
              if (compareResultsList == null) {
                compareResultsList = new ArrayList<CompareResults>();
                compareResultMap.put(cr.getStatus(), compareResultsList);
              }
              compareResultsList.add(cr);
            }
          }
        }

        setResponsePage(new CompareSoftwarePage());
      }
    };

    editSoftwareForm.add(checkBoxMultipleChoice);

    add(editSoftwareForm);

  }

}
