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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.axis.utils.ArrayUtil;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.tch.fc.TCHConnector;
import org.tch.fc.model.Admin;
import org.tch.fc.model.Event;
import org.tch.fc.model.ForecastActual;
import org.tch.fc.model.VaccineGroup;
import org.tch.fc.model.Software;
import org.tch.fc.model.TestCase;
import org.tch.fc.model.TestEvent;
import org.tch.ft.StyleClassLabel;
import org.tch.ft.manager.ForecastActualExpectedCompare;
import org.tch.ft.manager.SoftwareManager;
import org.tch.ft.model.Expert;
import org.tch.ft.model.ForecastExpected;
import org.tch.ft.model.Include;
import org.tch.ft.model.Result;
import org.tch.ft.model.TaskGroup;
import org.tch.ft.model.TestNote;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.TestPanelCase;
import org.tch.ft.model.TestPanelForecast;
import org.tch.ft.model.User;
import org.tch.ft.web.SecurePage;
import org.tch.ft.web.WebSession;
import org.tch.ft.web.taskGroup.ExpertsAssignedPage;
import org.tch.ft.web.testPanel.TestCaseListPage;

public class ActualVsExpectedPage extends TestCaseDetail implements SecurePage
{
  private static final long serialVersionUID = 1L;
  private Model<String> noteTextModel = null;

  private Model<VaccineGroup> addExpectedForeastItemModel;
  private Model<String> addExpectedDoseNumberModel;
  private Model<Date> addExpectedValidDateModel;
  private Model<Date> addExpectedDueDateModel;
  private Model<Date> addExpectedOverdueDateModel;
  private Model<Admin> addExpectedAdminStatusModel;
  private TestPanelCase testPanelCase;

  public ActualVsExpectedPage() {
    this(new PageParameters());
  }

  public ActualVsExpectedPage(final PageParameters pageParameters) {
    super(pageParameters);
    final WebSession webSession = ((WebSession) getSession());
    final User user = webSession.getUser();
    final TestCase testCase = user.getSelectedTestCase();

    Query query;

    final Session dataSession = webSession.getDataSession();
    Software software = user.getSelectedSoftware();
    SoftwareManager.initSoftware(software, dataSession);
    final TaskGroup taskGroup = user.getSelectedTaskGroup();
    final TestPanel testPanel = user.getSelectedTestPanel();
    testPanelCase = findTestPanel(testCase, dataSession, testPanel);
    final boolean canEdit = testPanelCase != null && determineIfCanEdit(user, dataSession, testPanel);
    ForecastActual forecastActual = null;
    ForecastExpected forecastExpected = null;
    List<ForecastActualExpectedCompare> forecastCompareList = new ArrayList<ForecastActualExpectedCompare>();
    if (testCase != null && testPanel != null) {
      query = dataSession
          .createQuery("from TestPanelForecast where testPanelCase.testCase = ? and testPanelCase.testPanel = ?");
      query.setParameter(0, testCase);
      query.setParameter(1, testPanel);
      List<TestPanelForecast> testPanelForecastList = query.list();
      for (TestPanelForecast testPanelForecast : testPanelForecastList) {
        forecastExpected = testPanelForecast.getForecastExpected();
        ForecastActualExpectedCompare forecastCompare = new ForecastActualExpectedCompare();
        forecastCompare.setForecastResultA(forecastExpected);
        forecastCompare.setVaccineGroup(forecastExpected.getVaccineGroup());
        forecastCompareList.add(forecastCompare);
        if (software != null) {
          query = dataSession
              .createQuery("from ForecastActual where softwareResult.software = ? and softwareResult.testCase = ? and vaccineGroup = ?");
          query.setParameter(0, software);
          query.setParameter(1, testCase);
          query.setParameter(2, forecastExpected.getVaccineGroup());
          List<ForecastActual> forecastActualList = query.list();
          if (forecastActualList.size() > 0) {
            forecastActual = forecastActualList.get(0);
          }
          forecastCompare.setForecastResultB(forecastActual);

        }

      }
    }

    ListView<ForecastActualExpectedCompare> forecastCompareItems = new ListView<ForecastActualExpectedCompare>(
        "forecastCompareItems", forecastCompareList) {

      private static final long serialVersionUID = 1L;

      protected void populateItem(ListItem<ForecastActualExpectedCompare> item) {

        final ForecastActualExpectedCompare forecastCompare = item.getModelObject();
        final VaccineGroup vaccineGroup = forecastCompare.getVaccineGroup();
        ForecastActual forecastActual = (ForecastActual) forecastCompare.getForecastResultB();
        final ForecastExpected forecastExpected = (ForecastExpected) forecastCompare.getForecastResultA();
        String expectedDoseNumber = "-";
        String expectedValidDateString = "-";
        String expectedDueDateString = "-";
        String expectedOverdueDateString = "-";
        String actualDoseNumber = "-";
        String actualValidDateString = "-";
        String actualDueDateString = "-";
        String actualOverdueDateString = "-";
        Date expectedValidDate = null;
        Date expectedDueDate = null;
        Date expectedOverdueDate = null;
        Admin expectedAdmin = null;
        Admin actualAdmin = null;

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        if (forecastExpected != null) {
          expectedDoseNumber = forecastExpected.getDoseNumber() != null ? forecastExpected.getDoseNumber() : "-";
          expectedValidDateString = forecastExpected.getValidDate() != null ? sdf.format(forecastExpected
              .getValidDate()) : "-";
          expectedDueDateString = forecastExpected.getDueDate() != null ? sdf.format(forecastExpected.getDueDate())
              : "-";
          expectedOverdueDateString = forecastExpected.getOverdueDate() != null ? sdf.format(forecastExpected
              .getOverdueDate()) : "-";
          expectedValidDate = forecastExpected.getValidDate();
          expectedDueDate = forecastExpected.getDueDate();
          expectedOverdueDate = forecastExpected.getOverdueDate();
          expectedAdmin = forecastExpected.getAdmin();
        }

        if (forecastActual != null) {
          actualDoseNumber = forecastActual.getDoseNumber() != null ? forecastActual.getDoseNumber() : "-";
          actualValidDateString = forecastActual.getValidDate() != null ? sdf.format(forecastActual.getValidDate())
              : "-";
          actualDueDateString = forecastActual.getDueDate() != null ? sdf.format(forecastActual.getDueDate()) : "-";
          actualOverdueDateString = forecastActual.getOverdueDate() != null ? sdf.format(forecastActual
              .getOverdueDate()) : "-";
          actualAdmin = forecastActual.getAdmin();
        }
        List<ForecastExpected> otherExpectedList;
        List<ForecastActual> otherActualList;

        WebSession webSession = ((WebSession) getSession());
        Query query = webSession.getDataSession().createQuery(
            "from ForecastExpected where testCase = ? and vaccineGroup = ?");
        query.setParameter(0, testCase);
        query.setParameter(1, forecastExpected.getVaccineGroup());
        otherExpectedList = query.list();
        if (forecastExpected != null) {
          for (Iterator<ForecastExpected> it = otherExpectedList.iterator(); it.hasNext();) {
            ForecastExpected fe = it.next();
            if (fe.getForecastExpectedId() == forecastExpected.getForecastExpectedId()) {
              it.remove();
            }
          }
        }
        query = webSession.getDataSession().createQuery(
            "from ForecastActual where softwareResult.testCase = ? and vaccineGroup = ?");
        query.setParameter(0, testCase);
        query.setParameter(1, forecastExpected.getVaccineGroup());
        otherActualList = query.list();
        for (Iterator<ForecastActual> it = otherActualList.iterator(); it.hasNext();) {
          ForecastActual fa = it.next();
          if (SoftwareManager.isSoftwareAccessRestricted(fa.getSoftwareResult().getSoftware(), user,
              webSession.getDataSession())) {
            it.remove();
          } else if (forecastActual != null) {
            if (fa.getForecastActualId() == forecastActual.getForecastActualId()) {
              it.remove();
            }
          }
        }

        ListView<ForecastExpected> otherExpectedItems = new ListView<ForecastExpected>("forecastExpectedItems",
            otherExpectedList) {

          private static final long serialVersionUID = 1L;

          @Override
          protected void populateItem(ListItem<ForecastExpected> item) {
            // TODO Auto-generated method stub
            final ForecastExpected forecastExpected = item.getModelObject();
            item.add(new Label("expectedLabel", "Expected by " + forecastExpected.getAuthor().getName() + " at "
                + forecastExpected.getAuthor().getOrganization()));
            item.add(new Label("expectedAdmin", (forecastExpected.getAdmin() == null ? Admin.UNKNOWN : forecastExpected
                .getAdmin()).getLabel()));
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            String expectedDoseNumber = forecastExpected.getDoseNumber() != null ? forecastExpected.getDoseNumber()
                : "-";
            String expectedValidDate = forecastExpected.getValidDate() != null ? sdf.format(forecastExpected
                .getValidDate()) : "-";
            String expectedDueDate = forecastExpected.getDueDate() != null ? sdf.format(forecastExpected.getDueDate())
                : "-";
            String expectedOverdueDate = forecastExpected.getOverdueDate() != null ? sdf.format(forecastExpected
                .getOverdueDate()) : "-";
            item.add(new Label("expectedDoseNumber", expectedDoseNumber));
            item.add(new Label("expectedValidDate", expectedValidDate));
            item.add(new Label("expectedDueDate", expectedDueDate));
            item.add(new Label("expectedOverdueDate", expectedOverdueDate));
          }
        };

        ListView<ForecastActual> otherActualItems = new ListView<ForecastActual>("forecastActualItems", otherActualList) {
          @Override
          protected void populateItem(ListItem<ForecastActual> item) {
            // TODO Auto-generated method stub

            final ForecastActual forecastActual = item.getModelObject();
            item.add(new Label("actualLabel", "Actual from "
                + forecastActual.getSoftwareResult().getSoftware().getLabel()));
            item.add(new Label("actualAdmin", (forecastActual.getAdmin() == null ? Admin.UNKNOWN : forecastExpected
                .getAdmin()).getLabel()));
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            String actualDoseNumber = forecastActual.getDoseNumber() != null ? forecastActual.getDoseNumber() : "-";
            String actualValidDate = forecastActual.getValidDate() != null ? sdf.format(forecastActual.getValidDate())
                : "-";
            String actualDueDate = forecastActual.getDueDate() != null ? sdf.format(forecastActual.getDueDate()) : "-";
            String actualOverdueDate = forecastActual.getOverdueDate() != null ? sdf.format(forecastActual
                .getOverdueDate()) : "-";
            item.add(new Label("actualDoseNumber", actualDoseNumber));
            item.add(new Label("actualValidDate", actualValidDate));
            item.add(new Label("actualDueDate", actualDueDate));
            item.add(new Label("actualOverdueDate", actualOverdueDate));
          }

        };

        ExpectedValuesForm editExpectedform = new ExpectedValuesForm("editExpectedform", dataSession, vaccineGroup,
            testCase, user, canEdit);

        {
          editExpectedform.setExpectedAdmin(expectedAdmin);
          editExpectedform.setExpectedDoseNumber(expectedDoseNumber);
          editExpectedform.setExpectedValidDate(expectedValidDate);
          editExpectedform.setExpectedDueDate(expectedDueDate);
          editExpectedform.setExpectedOverdueDate(expectedOverdueDate);

          List<Admin> expectedAdminList = createAdminList();
          DropDownChoice<Admin> expectedAdminDropDown = new DropDownChoice<Admin>("expectedAdmin",
              editExpectedform.getExpectedAdminModel(), expectedAdminList);
          expectedAdminDropDown.setRequired(true);
          editExpectedform.add(expectedAdminDropDown);

          TextField<String> expectedDoseNumberField = new TextField<String>("expectedDoseNumberField",
              editExpectedform.getExpectedDoseNumberModel());
          expectedDoseNumberField.setRequired(true);
          editExpectedform.add(expectedDoseNumberField);

          TextField<Date> expectedDueDateField = new TextField<Date>("expectedDueDateField",
              editExpectedform.getExpectedDueDateModel(), Date.class);
          expectedDueDateField.add(new DatePicker());
          editExpectedform.add(expectedDueDateField);

          TextField<Date> expectedOverdueDateField = new TextField<Date>("expectedOverdueDateField",
              editExpectedform.getExpectedOverdueDateModel(), Date.class);
          expectedOverdueDateField.add(new DatePicker());
          editExpectedform.add(expectedOverdueDateField);

          TextField<Date> expectedValidDateField = new TextField<Date>("expectedValidDateField",
              editExpectedform.getExpectedValidDateModel(), Date.class);
          expectedValidDateField.add(new DatePicker());
          editExpectedform.add(expectedValidDateField);
        }

        item.add(new Label("forecastLineLabel",
            forecastExpected != null && forecastExpected.getVaccineGroup() != null ? forecastExpected.getVaccineGroup()
                .getLabel() : ""));

        item.add(otherExpectedItems);

        String styleClass = forecastCompare.matchExactly() ? "pass" : "fail";
        item.add(new StyleClassLabel("expectedLabel", "Expected by " + taskGroup.getLabel(), styleClass));
        item.add(new StyleClassLabel("actualLabel", (forecastActual != null ? "Actual from "
            + forecastActual.getSoftwareResult().getSoftware().getLabel() : "No Results"), styleClass));

        styleClass = (expectedAdmin != null && actualAdmin != null && expectedAdmin.equals(actualAdmin)) ? "pass"
            : "fail";
        item.add(new StyleClassLabel("expectedAdmin", (expectedAdmin == null ? Admin.UNKNOWN : expectedAdmin)
            .getLabel(), styleClass));
        item.add(new StyleClassLabel("actualAdmin", (actualAdmin == null ? Admin.UNKNOWN : actualAdmin).getLabel(),
            styleClass));

        styleClass = expectedDoseNumber.equals(actualDoseNumber) ? "pass" : "fail";
        item.add(new StyleClassLabel("expectedDoseNumber", expectedDoseNumber, styleClass));
        item.add(new StyleClassLabel("actualDoseNumber", actualDoseNumber, styleClass));

        styleClass = expectedValidDateString.equals(actualValidDateString) ? "pass" : "fail";
        item.add(new StyleClassLabel("expectedValidDate", expectedValidDateString, styleClass));
        item.add(new StyleClassLabel("actualValidDate", actualValidDateString, styleClass));

        styleClass = expectedDueDateString.equals(actualDueDateString) ? "pass" : "fail";
        item.add(new StyleClassLabel("expectedDueDate", expectedDueDateString, styleClass));
        item.add(new StyleClassLabel("actualDueDate", actualDueDateString, styleClass));

        styleClass = expectedOverdueDateString.equals(actualOverdueDateString) ? "pass" : "fail";
        item.add(new StyleClassLabel("expectedOverdueDate", expectedOverdueDateString, styleClass));
        item.add(new StyleClassLabel("actualOverdueDate", actualOverdueDateString, styleClass));

        item.add(otherActualItems);

        item.add(editExpectedform);

        item.add(new Label("setExpectedValuesLabel", canEdit ? "Set Expected Values for Task Group"
            : "Set Expected Values"));

      }

    };
    add(forecastCompareItems);

    WebMarkupContainer allResultsForForecaster = new WebMarkupContainer("allResultsForForecaster");
    List<ForecastActual> forecastActualListAll = null;
    if (software == null) {
      allResultsForForecaster.setVisible(false);
      forecastActualListAll = new ArrayList<ForecastActual>();
    } else {
      allResultsForForecaster.setVisible(true);
      query = dataSession
          .createQuery("from ForecastActual where softwareResult.software = ? and softwareResult.testCase = ?");
      query.setParameter(0, software);
      query.setParameter(1, testCase);
      forecastActualListAll = query.list();
    }
    allResultsForForecaster.add(new Label("allResultsReturned", "All Results for "
        + (software == null ? "" : software.getLabel())));

    ListView<ForecastActual> allForecastActualItems = new ListView<ForecastActual>("allForecastActualItems",
        forecastActualListAll) {
      @Override
      protected void populateItem(ListItem<ForecastActual> item) {
        // TODO Auto-generated method stub

        final ForecastActual forecastActual = item.getModelObject();
        item.add(new Label("actualLabel", forecastActual.getVaccineGroup().getLabel()));
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String actualDoseNumber = forecastActual.getDoseNumber() != null ? forecastActual.getDoseNumber() : "-";
        String actualValidDate = forecastActual.getValidDate() != null ? sdf.format(forecastActual.getValidDate())
            : "-";
        String actualDueDate = forecastActual.getDueDate() != null ? sdf.format(forecastActual.getDueDate()) : "-";
        String actualOverdueDate = forecastActual.getOverdueDate() != null ? sdf
            .format(forecastActual.getOverdueDate()) : "-";
        item.add(new Label("actualDoseNumber", actualDoseNumber));
        item.add(new Label("actualValidDate", actualValidDate));
        item.add(new Label("actualDueDate", actualDueDate));
        item.add(new Label("actualOverdueDate", actualOverdueDate));
      }

    };
    allResultsForForecaster.add(allForecastActualItems);

    add(allResultsForForecaster);

    WebMarkupContainer changeTestStatus = new WebMarkupContainer("changeTestStatus");
    Result result = testPanelCase != null ? testPanelCase.getResult() : null;
    addResultLine("pass", Result.PASS, changeTestStatus, result);
    addResultLine("accept", Result.ACCEPT, changeTestStatus, result);
    addResultLine("fixed", Result.FIXED, changeTestStatus, result);
    addResultLine("research", Result.RESEARCH, changeTestStatus, result);
    addResultLine("fail", Result.FAIL, changeTestStatus, result);
    changeTestStatus.setVisible(testPanelCase != null && canEdit);
    add(changeTestStatus);
    String testPanelLabel = testPanelCase != null ? testPanelCase.getTestPanel().getTaskGroup().getPrimarySoftware()
        .getLabel() : "";
    changeTestStatus.add(new Label("primarySoftware", testPanelLabel));

    Form<Void> addExpectedForm = new Form<Void>("addExpectedForm") {
      private static final long serialVersionUID = 2L;

      @Override
      protected void onSubmit() {

        ForecastExpected forecastExpectedByUser = null;
        Transaction trans = dataSession.beginTransaction();
        try {

          forecastExpectedByUser = new ForecastExpected();
          forecastExpectedByUser.setTestCase(testCase);
          forecastExpectedByUser.setAuthor(user);
          forecastExpectedByUser.setVaccineGroup(addExpectedForeastItemModel.getObject());

          forecastExpectedByUser.setDoseNumber(addExpectedDoseNumberModel.getObject());
          VaccineGroup vaccineGroup = addExpectedForeastItemModel.getObject();
          forecastExpectedByUser.setVaccineGroup(vaccineGroup);
          forecastExpectedByUser.setAdmin(addExpectedAdminStatusModel.getObject());
          forecastExpectedByUser.setValidDate(addExpectedValidDateModel.getObject());
          forecastExpectedByUser.setDueDate(addExpectedDueDateModel.getObject());
          forecastExpectedByUser.setOverdueDate(addExpectedOverdueDateModel.getObject());
          forecastExpectedByUser.setAdmin(addExpectedAdminStatusModel.getObject());
          dataSession.saveOrUpdate(forecastExpectedByUser);
          if (canEdit) {
            TestPanelForecast testPanelForecast = null;
            Query query = dataSession
                .createQuery("from TestPanelForecast where testPanelCase = ? and forecastExpected.vaccineGroup = ?");
            query.setParameter(0, testPanelCase);
            query.setParameter(1, vaccineGroup);
            List<TestPanelForecast> testPanelForecastList = query.list();
            if (testPanelForecastList != null && testPanelForecastList.size() > 0) {
              testPanelForecast = testPanelForecastList.get(0);
            } else {
              testPanelForecast = new TestPanelForecast();
            }
            testPanelForecast.setTestPanelCase(testPanelCase);
            testPanelForecast.setForecastExpected(forecastExpectedByUser);
            dataSession.saveOrUpdate(testPanelForecast);
          }
          setResponsePage(new ActualVsExpectedPage());
        } finally {
          trans.commit();
        }
      }

    };

    {
      query = dataSession.createQuery("from VaccineGroup order by label");
      List<VaccineGroup> vaccineGroupList = query.list();
      addExpectedForeastItemModel = new Model<VaccineGroup>();
      DropDownChoice<VaccineGroup> vaccineGroupDropDown = new DropDownChoice<VaccineGroup>("vaccineGroup",
          addExpectedForeastItemModel, vaccineGroupList);
      vaccineGroupDropDown.setRequired(true);
      addExpectedForm.add(vaccineGroupDropDown);

      List<Admin> adminList = createAdminList();
      addExpectedAdminStatusModel = new Model<Admin>();
      DropDownChoice<Admin> adminDropDown = new DropDownChoice<Admin>("admin", addExpectedAdminStatusModel, adminList);
      adminDropDown.setRequired(true);
      addExpectedForm.add(adminDropDown);

      addExpectedDoseNumberModel = new Model<String>();
      TextField<String> addExpectedDoseNumberField = new TextField<String>("expectedDoseNumberField",
          addExpectedDoseNumberModel);
      addExpectedDoseNumberField.setRequired(true);
      addExpectedForm.add(addExpectedDoseNumberField);

      addExpectedValidDateModel = new Model<Date>();
      TextField<Date> addExpectedValidDateField = new TextField<Date>("expectedValidDateField",
          addExpectedValidDateModel, Date.class);
      addExpectedValidDateField.add(new DatePicker());
      addExpectedForm.add(addExpectedValidDateField);

      addExpectedDueDateModel = new Model<Date>();
      TextField<Date> addExpectedDueDateField = new TextField<Date>("expectedDueDateField", addExpectedDueDateModel,
          Date.class);
      addExpectedDueDateField.add(new DatePicker());
      addExpectedForm.add(addExpectedDueDateField);

      addExpectedOverdueDateModel = new Model<Date>();
      TextField<Date> addExpectedOverdueDateField = new TextField<Date>("expectedOverdueDateField",
          addExpectedOverdueDateModel, Date.class);
      addExpectedOverdueDateField.add(new DatePicker());
      addExpectedForm.add(addExpectedOverdueDateField);
    }
    add(addExpectedForm);

    query = webSession.getDataSession().createQuery("from TestNote where testCase = ?");
    query.setParameter(0, testCase);
    final List<TestNote> testNoteList = query.list();
    ListView<TestNote> testNoteItems = new ListView<TestNote>("testNoteItems", testNoteList) {
      protected void populateItem(ListItem<TestNote> item) {
        final TestNote testNote = item.getModelObject();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        item.add(new Label("userName", testNote.getUser().getName()));
        item.add(new Label("userOrganization", testNote.getUser().getOrganization()));
        item.add(new Label("noteDate", sdf.format(testNote.getNoteDate())));
        item.add(new MultiLineLabel("noteText", testNote.getNoteText()));
      }
    };
    add(testNoteItems);

    Form<Void> commentForm = new Form<Void>("commentform") {
      private static final long serialVersionUID = 1L;

      @Override
      protected void onSubmit() {
        TestNote testNote = new TestNote();
        testNote.setNoteText(noteTextModel.getObject());
        testNote.setUser(user);
        testNote.setTestCase(testCase);
        testNote.setNoteDate(new Date());
        Transaction trans = dataSession.beginTransaction();
        try {
          dataSession.save(testNote);
        } finally {
          trans.commit();
        }
        testNoteList.add(testNote);
        noteTextModel.setObject("");
      }
    };

    {
      noteTextModel = new Model<String>("");
      TextArea<String> noteTextField = new TextArea<String>("noteTextField", noteTextModel);
      commentForm.add(noteTextField);
    }

    add(commentForm);

    WebMarkupContainer editTestCaseLink = new WebMarkupContainer("editTestCaseLink");
    editTestCaseLink.setVisible(canEdit);
    add(editTestCaseLink);

    ExternalLink forecastLink = new ExternalLink("forecastLink", "http://tchforecasttester.org/fv/forecast"
        + TCHConnector.createQueryString(testCase, software, "html"));
    add(forecastLink);
    ExternalLink stepLink = new ExternalLink("stepLink", "http://tchforecasttester.org/fv/fv/step"
        + TCHConnector.createQueryString(testCase, software, "text"));
    add(stepLink);

    WebMarkupContainer testPanelSection = new WebMarkupContainer("testPanelSection");
    add(testPanelSection);

    List<List<TestPanelCase>> categoryList = webSession.getCategoryList();
    if (categoryList == null) {
      categoryList = TestCaseListPage.initTestPanelCategories(dataSession, webSession, testPanelSection, testPanel);
    }
    ListView<List<TestPanelCase>> categoryItems = new ListView<List<TestPanelCase>>("categoryItems", categoryList) {
      @Override
      protected void populateItem(ListItem<List<TestPanelCase>> item) {
        final List<TestPanelCase> testPanelCaseList = item.getModelObject();

        item.add(new Label("categoryName", testPanelCaseList.size() > 0 ? testPanelCaseList.get(0).getCategoryName()
            : ""));
        ListView<TestPanelCase> testPanelCaseItems = new ListView<TestPanelCase>("testPanelCaseItems",
            testPanelCaseList) {
          @Override
          protected void populateItem(ListItem<TestPanelCase> item) {
            final TestPanelCase testPanelCase = item.getModelObject();
            TestCase testCase = testPanelCase.getTestCase();
            item.add(new Label("testCaseLabel", testCase.getLabel()));

            String styleClass = "none";
            if (testPanelCase.getResult() != null
                && (testPanelCase.getInclude() == null || testPanelCase.getInclude() == Include.INCLUDED)) {
              if (testPanelCase.getResult() == Result.FAIL || testPanelCase.getResult() == Result.RESEARCH
                  || testPanelCase.getResult() == Result.FIXED) {
                styleClass = "fail";
              } else if (testPanelCase.getResult() == Result.PASS || testPanelCase.getResult() == Result.ACCEPT) {
                styleClass = "pass";
              }
            }

            item.add(new StyleClassLabel("status", testPanelCase.getResult() == null ? "" : testPanelCase.getResult()
                .getLabel(), styleClass));
            item.add(new Link("selectTestCase") {
              @Override
              public void onClick() {
                WebSession webSession = ((WebSession) getSession());
                Transaction trans = webSession.getDataSession().beginTransaction();
                User user = ((WebSession) getSession()).getUser();
                user.setSelectedTestCase(testPanelCase.getTestCase());
                user.setSelectedTestPanelCase(testPanelCase);
                trans.commit();
                setResponsePage(new ActualVsExpectedPage(pageParameters));
              }
            });
          }

        };
        item.add(testPanelCaseItems);
      }
    };
    testPanelSection.add(categoryItems);

    {
      List<TestPanel> testPanelList = new ArrayList<TestPanel>();

      query = dataSession.createQuery("from TestPanelCase where testCase = ? order by testPanel.label");
      query.setParameter(0, testCase);
      List<TestPanelCase> testPanelCaseList = query.list();
      for (TestPanelCase tpc : testPanelCaseList) {
        testPanelList.add(tpc.getTestPanel());
      }

      final TestPanelCase testPanelCaseToAdd = new TestPanelCase();
      testPanelCaseToAdd.setTestPanel(webSession.getLastTestPanelAssignment());
      testPanelCaseToAdd.setTestCase(testCase);
      testPanelCaseToAdd.setCategoryName(testPanelCase == null ? "" : testPanelCase.getCategoryName());
      testPanelCaseToAdd.setInclude(Include.INCLUDED);
      testPanelCaseToAdd.setTestCaseNumber(testPanelCase == null ? "" : testPanelCase.getTestCaseNumber());

      Form<TestPanelCase> assignToTestPanel = new Form<TestPanelCase>("assignToTestPanel",
          new CompoundPropertyModel<TestPanelCase>(testPanelCaseToAdd)) {
        @Override
        protected void onSubmit() {
          Transaction transaction = dataSession.beginTransaction();
          dataSession.save(testPanelCaseToAdd);

          Query query = dataSession.createQuery("from TestPanelForecast where testPanelCase = ?");
          query.setParameter(0, testPanelCase);
          List<TestPanelForecast> testPanelForecastList = query.list();
          for (TestPanelForecast testPanelForecast : testPanelForecastList) {
            TestPanelForecast testPanelForecastCopy = new TestPanelForecast();
            testPanelForecastCopy.setTestPanelCase(testPanelCaseToAdd);
            testPanelForecastCopy.setForecastExpected(testPanelForecast.getForecastExpected());
            dataSession.save(testPanelForecastCopy);
          }
          transaction.commit();
          webSession.setLastTestPanelAssignment(testPanelCaseToAdd.getTestPanel());
          setResponsePage(new ActualVsExpectedPage());
        }
      };

      List<TestPanel> testPanelListForAdding = new ArrayList<TestPanel>();

      query = dataSession.createQuery("from TaskGroup order by label");
      List<TaskGroup> taskGroupList = query.list();
      for (TaskGroup tg : taskGroupList) {
        query = dataSession.createQuery("from Expert where taskGroup = ? and user = ?");
        query.setParameter(0, tg);
        query.setParameter(1, user);
        List<Expert> expertList = query.list();
        if (expertList.size() > 0) {
          query = dataSession.createQuery("from TestPanel where taskGroup = ? order by label");
          query.setParameter(0, tg);
          List<TestPanel> tpl = query.list();
          for (TestPanel tp : tpl) {
            boolean okayToAdd = true;
            for (TestPanelCase tpcCheck : testPanelCaseList) {
              if (tpcCheck.getTestPanel().equals(tp)) {
                okayToAdd = false;
                break;
              }
            }
            if (okayToAdd) {
              testPanelListForAdding.add(tp);
            }
          }
        }
      }

      DropDownChoice<TestPanel> testPanelField = new DropDownChoice<TestPanel>("testPanel", testPanelListForAdding);
      testPanelField.setRequired(true);
      assignToTestPanel.add(testPanelField);

      ListView<TestPanel> testPanelAssignments = new ListView<TestPanel>("testPanelAssignments", testPanelList) {
        protected void populateItem(org.apache.wicket.markup.html.list.ListItem<TestPanel> item) {
          TestPanel testPanel = item.getModelObject();
          item.add(new Label("taskGroupLabel", testPanel.getTaskGroup().getLabel()));
          item.add(new Label("testPanelLabel", testPanel.getLabel()));
        };
      };
      assignToTestPanel.add(testPanelAssignments);

      add(assignToTestPanel);
    }
  }

  public List<Admin> createAdminList() {
    List<Admin> adminList = new ArrayList<Admin>();
    for (Admin admin : Admin.values()) {
      adminList.add(admin);
    }
    return adminList;
  }

  protected static boolean determineIfCanEdit(final User user, final Session dataSession, TestPanel testPanel) {
    // Checking to see if user has the right to set the expectations
    // for the task group. Any other user will just add their comment
    // but will not change the official expected answer
    if (testPanel != null) {
      TaskGroup taskGroup = testPanel.getTaskGroup();
      return ExpertsAssignedPage.determineCanEdit(user, dataSession, taskGroup);
    }
    return false;
  }

  private TestPanelCase findTestPanel(final TestCase testCase, final Session dataSession, final TestPanel testPanel) {
    Query query;
    query = dataSession.createQuery("from TestPanelCase where testCase = ? and testPanel = ?");
    query.setParameter(0, testCase);
    query.setParameter(1, testPanel);
    List<TestPanelCase> testPanelCaseList = query.list();
    if (testPanelCaseList.size() > 0) {
      return testPanelCaseList.get(0);
    }
    return null;
  }

  private class ResultLink extends Link<Result>
  {
    public ResultLink(String arg0, Result result) {
      super(arg0, new Model<Result>(result));
    }

    @Override
    public void onClick() {
      Result result = (Result) getModelObject();
      WebSession webSession = ((WebSession) getSession());
      Session dataSession = webSession.getDataSession();
      Transaction transaction = dataSession.beginTransaction();
      testPanelCase.setResult(result);
      dataSession.update(testPanelCase);
      transaction.commit();
      setResponsePage(new ActualVsExpectedPage());
    }
  }

  private void addResultLine(String s, Result result, WebMarkupContainer changeTestStatus, Result currentResult) {
    WebMarkupContainer part1 = new WebMarkupContainer(s + "1");
    changeTestStatus.add(part1);
    part1.add(new ResultLink(s, result));
    WebMarkupContainer part2 = new WebMarkupContainer(s + "2");
    changeTestStatus.add(part2);
    if (result == currentResult) {
      String style = "pass";
      if (result == Result.FAIL) {
        style = "fail";
      }
      part1.add(AttributeModifier.replace("class", style));
      part2.add(AttributeModifier.replace("class", style));
    }
  }

  private class ExpectedValuesForm extends Form<Void>
  {
    public ExpectedValuesForm(String id, Session dataSession, VaccineGroup vaccineGroup, TestCase testCase, User user,
        boolean canEdit) {
      super(id);
      this.dataSession = dataSession;
      this.vaccineGroup = vaccineGroup;
      this.testCase = testCase;
      this.user = user;
      this.canEdit = canEdit;
    }

    private Session dataSession;
    private VaccineGroup vaccineGroup = null;
    private TestCase testCase = null;
    private User user = null;
    private boolean canEdit;

    private static final long serialVersionUID = 2L;

    protected Model<String> expectedDoseNumberModel;
    protected Model<Date> expectedValidDateModel;
    protected Model<Date> expectedDueDateModel;
    protected Model<Date> expectedOverdueDateModel;
    protected Model<Admin> expectedAdminModel;

    public Model<Admin> getExpectedAdminModel() {
      return expectedAdminModel;
    }

    public void setExpectedAdminModel(Model<Admin> expectedAdminModel) {
      this.expectedAdminModel = expectedAdminModel;
    }

    public Model<String> getExpectedDoseNumberModel() {
      return expectedDoseNumberModel;
    }

    public Model<Date> getExpectedValidDateModel() {
      return expectedValidDateModel;
    }

    public Model<Date> getExpectedDueDateModel() {
      return expectedDueDateModel;
    }

    public Model<Date> getExpectedOverdueDateModel() {
      return expectedOverdueDateModel;
    }

    public void setExpectedDoseNumber(String expectedDoseNumber) {
      this.expectedDoseNumberModel = new Model<String>(expectedDoseNumber);
    }

    public void setExpectedValidDate(Date expectedValidDate) {
      this.expectedValidDateModel = new Model<Date>(expectedValidDate);
    }

    public void setExpectedDueDate(Date expectedDueDate) {
      this.expectedDueDateModel = new Model<Date>(expectedDueDate);
    }

    public void setExpectedOverdueDate(Date expectedOverdueDate) {
      this.expectedOverdueDateModel = new Model<Date>(expectedOverdueDate);
    }

    public void setExpectedAdmin(Admin admin) {
      this.expectedAdminModel = new Model<Admin>(admin);
    }

    @Override
    protected void onSubmit() {
      ForecastExpected forecastExpectedByUser = null;
      Transaction trans = dataSession.beginTransaction();
      try {
        Query query = dataSession
            .createQuery("from ForecastExpected where testCase = ? and vaccineGroup = ? and author = ?");
        query.setParameter(0, testCase);
        query.setParameter(1, vaccineGroup);
        query.setParameter(2, user);
        List<ForecastExpected> forecastExpectedByUserList = query.list();
        if (forecastExpectedByUserList.size() > 0) {
          forecastExpectedByUser = (ForecastExpected) forecastExpectedByUserList.get(0);
        } else {
          forecastExpectedByUser = new ForecastExpected();
          forecastExpectedByUser.setTestCase(testCase);
          forecastExpectedByUser.setVaccineGroup(vaccineGroup);
          forecastExpectedByUser.setAuthor(user);
        }
        forecastExpectedByUser.setUpdatedDate(new Date());
        forecastExpectedByUser.setAdmin(expectedAdminModel.getObject());
        forecastExpectedByUser.setDoseNumber(expectedDoseNumberModel.getObject());
        forecastExpectedByUser.setValidDate(expectedValidDateModel.getObject());
        forecastExpectedByUser.setDueDate(expectedDueDateModel.getObject());
        forecastExpectedByUser.setOverdueDate(expectedOverdueDateModel.getObject());
        dataSession.saveOrUpdate(forecastExpectedByUser);
        if (canEdit) {
          TestPanelForecast testPanelForecast = null;
          query = dataSession
              .createQuery("from TestPanelForecast where testPanelCase = ? and forecastExpected.vaccineGroup = ?");
          query.setParameter(0, testPanelCase);
          query.setParameter(1, vaccineGroup);
          List<TestPanelForecast> testPanelForecastList = query.list();
          if (testPanelForecastList != null && testPanelForecastList.size() > 0) {
            testPanelForecast = testPanelForecastList.get(0);
          } else {
            testPanelForecast = new TestPanelForecast();
          }
          testPanelForecast.setTestPanelCase(testPanelCase);
          testPanelForecast.setForecastExpected(forecastExpectedByUser);
          dataSession.saveOrUpdate(testPanelForecast);
        }
        setResponsePage(new ActualVsExpectedPage());
      } finally {
        trans.commit();
      }
    }

  };

}
