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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.tch.fc.model.ForecastActual;
import org.tch.fc.model.VaccineGroup;
import org.tch.fc.model.Software;
import org.tch.fc.model.TestCase;
import org.tch.ft.StyleClassLabel;
import org.tch.ft.manager.ForecastActualExpectedCompare;
import org.tch.ft.manager.SoftwareManager;
import org.tch.ft.model.ForecastCompare;
import org.tch.ft.model.ForecastTarget;
import org.tch.ft.model.Result;
import org.tch.ft.model.TestNote;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.TestPanelCase;
import org.tch.ft.model.User;
import org.tch.ft.web.SecurePage;
import org.tch.ft.web.WebSession;
import org.tch.ft.web.testCase.TestCaseDetail;

public class CompareForecastPage extends TestCaseDetail implements SecurePage
{
  private static final long serialVersionUID = 1L;

  private ForecastCompare forecastCompare;
  private Model<String> noteTextModel = null;

  public CompareForecastPage() {
    this(new PageParameters());
  }

  public CompareForecastPage(final PageParameters pageParameters) {
    super(pageParameters);
    WebSession webSession = ((WebSession) getSession());
    final User user = webSession.getUser();
    final TestCase testCase = user.getSelectedTestCase();
    final Software software = user.getSelectedSoftware();
    final TestPanel testPanel = user.getSelectedTestPanel();
    forecastCompare = webSession.getForecastCompare();

    Query query;

    final Session dataSession = webSession.getDataSession();
    final TestPanelCase testPanelCase = findTestPanel(testCase, dataSession, testPanel);
    final boolean canEdit = testPanelCase != null
        && SoftwareManager.canEditSoftwareCompare(software, user, dataSession);

    final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    ForecastActual forecastActual = forecastCompare.getForecastActual();
    final VaccineGroup vaccineGroup = forecastActual.getVaccineGroup();
    final String actualDoseNumber = forecastActual.getDoseNumber() != null ? forecastActual.getDoseNumber() : "-";
    final String actualValidDateString = forecastActual.getValidDate() != null ? sdf.format(forecastActual
        .getValidDate()) : "-";
    final String actualDueDateString = forecastActual.getDueDate() != null ? sdf.format(forecastActual.getDueDate())
        : "-";

    add(new Label("forecastLineLabel", vaccineGroup.getLabel()));
    add(new Label("compareLabel", forecastCompare.getCompareLabel()));

    add(new Label("actualLabel", forecastActual.getSoftwareResult().getSoftware().getLabel()));
    add(new Label("actualDoseNumber", actualDoseNumber));
    add(new Label("actualValidDate", actualValidDateString));
    add(new Label("actualDueDate", actualDueDateString));

    List<ForecastTarget> otherActualList;
    query = webSession.getDataSession().createQuery("from ForecastTarget where forecastCompare = ?");
    query.setParameter(0, forecastCompare);
    otherActualList = query.list();
    ListView<ForecastTarget> otherActualItems = new ListView<ForecastTarget>("forecastActualItems", otherActualList) {
      @Override
      protected void populateItem(ListItem<ForecastTarget> item) {
        final ForecastTarget compareForecastTarget = item.getModelObject();
        final ForecastActual compareForecastActual = compareForecastTarget.getForecastActual();
        ForecastActualExpectedCompare forecastActualExpectedCompare = new ForecastActualExpectedCompare();
        forecastActualExpectedCompare.setForecastResultA(forecastCompare.getForecastActual());
        forecastActualExpectedCompare.setForecastResultB(compareForecastActual);

        final String compareDoseNumber = compareForecastActual.getDoseNumber() != null ? compareForecastActual
            .getDoseNumber() : "-";
        final String compareValidDateString = compareForecastActual.getValidDate() != null ? sdf
            .format(compareForecastActual.getValidDate()) : "-";
        final String compareDueDateString = compareForecastActual.getDueDate() != null ? sdf
            .format(compareForecastActual.getDueDate()) : "-";

        String styleClass = forecastActualExpectedCompare.matchExactlyExlcudeOverdue() ? "pass" : "fail";
        item.add(new StyleClassLabel("actualLabel", (compareForecastActual != null ? compareForecastActual
            .getSoftwareResult().getSoftware().getLabel() : ""), styleClass));

        styleClass = compareDoseNumber.equals(actualDoseNumber) ? "pass" : "fail";
        item.add(new StyleClassLabel("actualDoseNumber", compareDoseNumber, styleClass));

        styleClass = compareValidDateString.equals(actualValidDateString) ? "pass" : "fail";
        item.add(new StyleClassLabel("actualValidDate", compareValidDateString, styleClass));

        styleClass = compareDueDateString.equals(actualDueDateString) ? "pass" : "fail";
        item.add(new StyleClassLabel("actualDueDate", compareDueDateString, styleClass));
      }

    };
    add(otherActualItems);

    WebMarkupContainer changeTestStatus = new WebMarkupContainer("changeTestStatus");
    Result result = forecastCompare.getResult();
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
      forecastCompare.setResult(result);
      dataSession.update(forecastCompare);
      boolean nextOne = false;
      int nextForecastCompareId = webSession.getForecastCompareIdList().size() > 0 ? webSession
          .getForecastCompareIdList().get(0) : 0;
      for (int forecastCompareId : webSession.getForecastCompareIdList()) {
        if (nextOne) {
          nextForecastCompareId = forecastCompareId;
          break;
        }
        if (forecastCompareId == forecastCompare.getForecastCompareId()) {
          nextOne = true;
        }
      }
      if (nextForecastCompareId > 0) {
        forecastCompare = (ForecastCompare) dataSession.get(ForecastCompare.class, nextForecastCompareId);
        User user = ((WebSession) getSession()).getUser();
        user.setSelectedTestCase((TestCase) forecastCompare.getForecastActual().getTestCase());
        webSession.setForecastCompare(forecastCompare);
      }

      transaction.commit();
      setResponsePage(new CompareForecastPage());
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
