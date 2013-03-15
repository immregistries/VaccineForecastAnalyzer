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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
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
import org.tch.ft.manager.UserManager;
import org.tch.ft.model.ForecastActual;
import org.tch.ft.model.ForecastExpected;
import org.tch.ft.model.ForecastItem;
import org.tch.ft.model.Result;
import org.tch.ft.model.Software;
import org.tch.ft.model.TaskGroup;
import org.tch.ft.model.TestCase;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.TestPanelCase;
import org.tch.ft.model.TestPanelExpected;
import org.tch.ft.model.User;
import org.tch.ft.web.SecurePage;
import org.tch.ft.web.WebSession;
import org.tch.ft.web.taskGroup.ExpertsAssignedPage;

public class ActualVsExpectedPage extends TestCaseDetail implements SecurePage {
  private static final long serialVersionUID = 1L;

  private Model<ForecastItem> addExpectedForeastItemModel;
  private Model<String> addExpectedDoseNumberModel;
  private Model<Date> addExpectedValidDateModel;
  private Model<Date> addExpectedDueDateModel;
  private Model<Date> addExpectedOverdueDateModel;
  private TestPanelCase testPanelCase;

  public ActualVsExpectedPage() {
    this(new PageParameters());
  }

  public ActualVsExpectedPage(final PageParameters pageParameters) {
    super(pageParameters);
    WebSession webSession = ((WebSession) getSession());
    final User user = webSession.getUser();
    final TestCase testCase = user.getSelectedTestCase();

    Query query;

    final Session dataSession = webSession.getDataSession();
    Software software = user.getSelectedSoftware();
    final TaskGroup taskGroup = user.getSelectedTaskGroup();
    final TestPanel testPanel = user.getSelectedTestPanel();
    testPanelCase = findTestPanel(testCase, dataSession, testPanel);
    final boolean canEdit = testPanelCase != null && determineIfCanEdit(user, dataSession, testPanel);
    ForecastActual forecastActual = null;
    ForecastExpected forecastExpected = null;
    List<ForecastActualExpectedCompare> forecastCompareList = new ArrayList<ForecastActualExpectedCompare>();
    if (testCase != null && testPanel != null) {
      query = dataSession
          .createQuery("from TestPanelExpected where testPanelCase.testCase = ? and testPanelCase.testPanel = ?");
      query.setParameter(0, testCase);
      query.setParameter(1, testPanel);
      List<TestPanelExpected> testPanelExpectedList = query.list();
      for (TestPanelExpected testPanelExpected : testPanelExpectedList) {
        forecastExpected = testPanelExpected.getForecastExpected();
        ForecastActualExpectedCompare forecastCompare = new ForecastActualExpectedCompare();
        forecastCompare.setForecastResultA(forecastExpected);
        forecastCompare.setForecastItem(forecastExpected.getForecastItem());
        forecastCompareList.add(forecastCompare);
        if (software != null) {
          query = dataSession
              .createQuery("from ForecastActual where software = ? and testCase = ? and forecastItem = ?");
          query.setParameter(0, software);
          query.setParameter(1, testCase);
          query.setParameter(2, forecastExpected.getForecastItem());
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

      private Model<String> expectedDoseNumberModel;
      private Model<Date> expectedValidDateModel;
      private Model<Date> expectedDueDateModel;
      private Model<Date> expectedOverdueDateModel;

      protected void populateItem(ListItem<ForecastActualExpectedCompare> item) {

        final ForecastActualExpectedCompare forecastCompare = item.getModelObject();
        final ForecastItem forecastItem = forecastCompare.getForecastItem();
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
        }

        if (forecastActual != null) {
          actualDoseNumber = forecastActual.getDoseNumber() != null ? forecastActual.getDoseNumber() : "-";
          actualValidDateString = forecastActual.getValidDate() != null ? sdf.format(forecastActual.getValidDate())
              : "-";
          actualDueDateString = forecastActual.getDueDate() != null ? sdf.format(forecastActual.getDueDate()) : "-";
          actualOverdueDateString = forecastActual.getOverdueDate() != null ? sdf.format(forecastActual
              .getOverdueDate()) : "-";
        }
        List<ForecastExpected> otherExpectedList;
        List<ForecastActual> otherActualList;

        WebSession webSession = ((WebSession) getSession());
        Query query = webSession.getDataSession().createQuery(
            "from ForecastExpected where testCase = ? and forecastItem = ?");
        query.setParameter(0, testCase);
        query.setParameter(1, forecastExpected.getForecastItem());
        otherExpectedList = query.list();
        if (forecastExpected != null) {
          for (Iterator<ForecastExpected> it = otherExpectedList.iterator(); it.hasNext();) {
            ForecastExpected fe = it.next();
            if (fe.getForecastExpectedId() == forecastExpected.getForecastExpectedId()) {
              it.remove();
            }
          }
        }
        query = webSession.getDataSession().createQuery("from ForecastActual where testCase = ? and forecastItem = ?");
        query.setParameter(0, testCase);
        query.setParameter(1, forecastExpected.getForecastItem());
        otherActualList = query.list();
        for (Iterator<ForecastActual> it = otherActualList.iterator(); it.hasNext();) {
          ForecastActual fa = it.next();
          if (SoftwareManager.isSoftwareAccessRestricted(fa.getSoftware(), user, webSession.getDataSession())) {
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
            item.add(new Label("actualLabel", "Actual from " + forecastActual.getSoftware().getLabel()));
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

        Form<Void> editExpectedform = new Form<Void>("editExpectedform") {
          private static final long serialVersionUID = 2L;

          @Override
          protected void onSubmit() {
            ForecastExpected forecastExpectedByUser = null;
            Transaction trans = dataSession.beginTransaction();
            try {
              Query query = dataSession
                  .createQuery("from ForecastExpected where testCase = ? and forecastItem = ? and author = ?");
              query.setParameter(0, testCase);
              query.setParameter(1, forecastItem);
              query.setParameter(2, user);
              List<ForecastExpected> forecastExpectedByUserList = query.list();
              if (forecastExpectedByUserList.size() > 0) {
                forecastExpectedByUser = (ForecastExpected) forecastExpectedByUserList.get(0);
              } else {
                forecastExpectedByUser = new ForecastExpected();
                forecastExpectedByUser.setTestCase(testCase);
                forecastExpectedByUser.setForecastItem(forecastItem);
                forecastExpectedByUser.setAuthor(user);
              }
              forecastExpectedByUser.setDoseNumber(expectedDoseNumberModel.getObject());
              forecastExpectedByUser.setValidDate(expectedValidDateModel.getObject());
              forecastExpectedByUser.setDueDate(expectedDueDateModel.getObject());
              forecastExpectedByUser.setOverdueDate(expectedOverdueDateModel.getObject());
              dataSession.saveOrUpdate(forecastExpectedByUser);
              if (canEdit) {
                TestPanelExpected testPanelExpected = null;
                query = dataSession
                    .createQuery("from TestPanelExpected where testPanelCase = ? and forecastExpected.forecastItem = ?");
                query.setParameter(0, testPanelCase);
                query.setParameter(1, forecastItem);
                List<TestPanelExpected> testPanelExpectedList = query.list();
                if (testPanelExpectedList != null && testPanelExpectedList.size() > 0) {
                  testPanelExpected = testPanelExpectedList.get(0);
                } else {
                  testPanelExpected = new TestPanelExpected();
                }
                testPanelExpected.setTestPanelCase(testPanelCase);
                testPanelExpected.setForecastExpected(forecastExpectedByUser);
                dataSession.saveOrUpdate(testPanelExpected);
              }
              setResponsePage(new ActualVsExpectedPage());
            } finally {
              trans.commit();
            }
          }

        };

        {
          expectedDoseNumberModel = new Model<String>(expectedDoseNumber);
          expectedValidDateModel = new Model<Date>(expectedValidDate);
          expectedDueDateModel = new Model<Date>(expectedDueDate);
          expectedOverdueDateModel = new Model<Date>(expectedOverdueDate);
          TextField<String> expectedDoseNumberField = new TextField<String>("expectedDoseNumberField",
              expectedDoseNumberModel);
          expectedDoseNumberField.setRequired(true);
          editExpectedform.add(expectedDoseNumberField);

          TextField<Date> expectedDueDateField = new TextField<Date>("expectedDueDateField", expectedDueDateModel,
              Date.class);
          expectedDueDateField.add(new DatePicker());
          editExpectedform.add(expectedDueDateField);

          TextField<Date> expectedOverdueDateField = new TextField<Date>("expectedOverdueDateField",
              expectedOverdueDateModel, Date.class);
          expectedOverdueDateField.add(new DatePicker());
          editExpectedform.add(expectedOverdueDateField);

          TextField<Date> expectedValidDateField = new TextField<Date>("expectedValidDateField",
              expectedValidDateModel, Date.class);
          expectedValidDateField.add(new DatePicker());
          editExpectedform.add(expectedValidDateField);
        }

        item.add(new Label("forecastLineLabel",
            forecastExpected != null && forecastExpected.getForecastItem() != null ? forecastExpected.getForecastItem()
                .getLabel() : ""));

        item.add(otherExpectedItems);

        String styleClass = forecastCompare.matchExactly() ? "pass" : "fail";
        item.add(new StyleClassLabel("expectedLabel", "Expected by " + taskGroup.getLabel(), styleClass));
        item.add(new StyleClassLabel("actualLabel", "Actual from "
            + (forecastActual != null ? forecastActual.getSoftware().getLabel() : ""), styleClass));

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
          forecastExpectedByUser.setForecastItem(addExpectedForeastItemModel.getObject());

          forecastExpectedByUser.setDoseNumber(addExpectedDoseNumberModel.getObject());
          ForecastItem forecastItem = addExpectedForeastItemModel.getObject();
          forecastExpectedByUser.setForecastItem(forecastItem);
          forecastExpectedByUser.setValidDate(addExpectedValidDateModel.getObject());
          forecastExpectedByUser.setDueDate(addExpectedDueDateModel.getObject());
          forecastExpectedByUser.setOverdueDate(addExpectedOverdueDateModel.getObject());
          dataSession.saveOrUpdate(forecastExpectedByUser);
          if (canEdit) {
            TestPanelExpected testPanelExpected = null;
            Query query = dataSession
                .createQuery("from TestPanelExpected where testPanelCase = ? and forecastExpected.forecastItem = ?");
            query.setParameter(0, testPanelCase);
            query.setParameter(1, forecastItem);
            List<TestPanelExpected> testPanelExpectedList = query.list();
            if (testPanelExpectedList != null && testPanelExpectedList.size() > 0) {
              testPanelExpected = testPanelExpectedList.get(0);
            } else {
              testPanelExpected = new TestPanelExpected();
            }
            testPanelExpected.setTestPanelCase(testPanelCase);
            testPanelExpected.setForecastExpected(forecastExpectedByUser);
            dataSession.saveOrUpdate(testPanelExpected);
          }
          setResponsePage(new ActualVsExpectedPage());
        } finally {
          trans.commit();
        }
      }

    };

    {
      query = dataSession.createQuery("from ForecastItem order by label");
      List<ForecastItem> forecastItemList = query.list();
      addExpectedForeastItemModel = new Model<ForecastItem>();
      DropDownChoice<ForecastItem> forecastItemDropDown = new DropDownChoice<ForecastItem>("forecastItem",
          addExpectedForeastItemModel, forecastItemList);
      forecastItemDropDown.setRequired(true);
      addExpectedForm.add(forecastItemDropDown);

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

  private class ResultLink extends Link<Result> {
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

}