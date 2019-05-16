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
package org.immregistries.vfa.web.testCase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.immregistries.vfa.connect.model.TestCase;
import org.immregistries.vfa.model.TestNote;
import org.immregistries.vfa.model.User;
import org.immregistries.vfa.web.FTBasePage;
import org.immregistries.vfa.web.MenuSection;
import org.immregistries.vfa.web.SecurePage;
import org.immregistries.vfa.web.WebSession;

public class CommentsPage extends FTBasePage implements SecurePage {
  private static final long serialVersionUID = 1L;

  private Model<String> noteTextModel = null;

  public CommentsPage(final PageParameters pageParameters) {
    super(MenuSection.TEST_CASE, pageParameters);
    WebSession webSession = ((WebSession) getSession());
    final User user = webSession.getUser();
    final TestCase testCase = user.getSelectedTestCase();

    final Session dataSession = webSession.getDataSession();

    Query query = webSession.getDataSession().createQuery("from TestNote where testCase = ?");
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

}
