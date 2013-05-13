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
package org.tch.ft.web.testPanel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.file.Folder;
import org.apache.wicket.util.lang.Bytes;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.tch.ft.CentralControl;
import org.tch.ft.WicketApplication;
import org.tch.ft.manager.TestCaseImporter;
import org.tch.ft.manager.readers.MiisTestCaseReader;
import org.tch.ft.manager.readers.TestCaseReader;
import org.tch.ft.manager.readers.TestCaseReaderFactory;
import org.tch.ft.model.Event;
import org.tch.ft.model.ForecastItem;
import org.tch.ft.model.Role;
import org.tch.ft.model.Software;
import org.tch.ft.model.TaskGroup;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.User;
import org.tch.ft.web.FTBasePage;
import org.tch.ft.web.MenuSection;
import org.tch.ft.web.SecurePage;
import org.tch.ft.web.WebSession;
import org.tch.ft.web.software.SelectSoftwarePage;
import org.tch.ft.web.softwareCompare.SelectSoftwareComparePage;

public class UploadTestCasesPage extends FTBasePage implements SecurePage {

  private Model<TestCaseReader.FormatType> fileFormat;
  private Model<Software> softwareModel;

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Form for uploads.
   */
  private class FileUploadForm extends Form<Void> {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    FileUploadField fileUploadField;
    private WebSession webSession;

    /**
     * Construct.
     * 
     * @param name
     *          Component name
     */
    public FileUploadForm(String name, WebSession webSession) {
      super(name);

      // set this form to multipart mode (always needed for uploads!)
      setMultiPart(true);

      // Add one file input field
      add(fileUploadField = new FileUploadField("fileInput"));

      // Set maximum size to 2MB
      setMaxSize(Bytes.megabytes(2));

      this.webSession = webSession;
    }

    /**
     * @see org.apache.wicket.markup.html.form.Form#onSubmit()
     */
    @Override
    protected void onSubmit() {
      final List<FileUpload> uploads = fileUploadField.getFileUploads();
      if (uploads != null) {
        for (FileUpload upload : uploads) {
          // Create a new file
          File newFile = new File(getUploadFolder(), upload.getClientFileName());

          // Check new file, delete if it already existed
          checkFileExists(newFile);
          try {
            // Save to new file
            newFile.createNewFile();
            upload.writeTo(newFile);

            String filename = newFile.getName();
            int i = filename.lastIndexOf('.');
            if (i != -1) {
              filename = filename.substring(0, i);
            }
            InputStream in = new FileInputStream(newFile);
            Session dataSession = webSession.getDataSession();
            User user = webSession.getUser();

            Transaction transaction = dataSession.beginTransaction();
            Query query = dataSession.createQuery("from Event");
            List<Event> eventList = query.list();
            query = dataSession.createQuery("from ForecastItem");
            List<ForecastItem> forecastItemList = query.list();
            Map<Integer, ForecastItem> forecastItemListMap = new HashMap<Integer, ForecastItem>();
            for (ForecastItem forecastItem : forecastItemList) {
              forecastItemListMap.put(forecastItem.getForecastItemId(), forecastItem);
            }
            TestCaseReader.FormatType formatType = (TestCaseReader.FormatType) fileFormat.getObject();
            TestCaseReader testCaseReader = TestCaseReaderFactory.createTestCaseReader(formatType);
            testCaseReader.setLoadExpectationsSoftware((Software) softwareModel.getObject());

            testCaseReader.setEventList(eventList);
            testCaseReader.setUser(user);
            testCaseReader.setForecastItems(forecastItemListMap);
            testCaseReader.read(in);

            TestCaseImporter tci = new TestCaseImporter();

            // find testPanelCase
            TaskGroup taskGroup = user.getSelectedTaskGroup();
            query = dataSession.createQuery("from TestPanel where taskGroup = ? and label = ?");
            query.setParameter(0, taskGroup);
            query.setParameter(1, filename);
            List<TestPanel> testPanelList = query.list();
            TestPanel testPanel = null;
            if (testPanelList.size() > 0) {
              testPanel = testPanelList.get(0);
            } else {
              testPanel = new TestPanel();
              testPanel.setLabel(filename);
              testPanel.setTaskGroup(taskGroup);
              dataSession.save(testPanel);
            }
            tci.importTestCases(testCaseReader, testPanel, dataSession);

            transaction.commit();

            if (testCaseReader.getErrorMessage() != null) {
              UploadTestCasesPage.this.info("Imported " + testCaseReader.getTestCaseList().size()
                  + " test cases into test panel: " + filename + ", reader encountered problem: "
                  + testCaseReader.getErrorMessage());
            } else {
              UploadTestCasesPage.this.info("Imported " + testCaseReader.getTestCaseList().size()
                  + " test cases into test panel: " + filename);
            }

          } catch (Exception e) {
            throw new IllegalStateException("Unable to write file", e);
          }
        }
      }
    }
  }

  private Folder getUploadFolder() {
    return ((WicketApplication) Application.get()).getUploadFolder();
  }

  /**
   * Check whether the file allready exists, and if so, try to delete it.
   * 
   * @param newFile
   *          the file to check
   */
  private void checkFileExists(File newFile) {
    if (newFile.exists()) {
      // Try to delete the file
      if (!Files.remove(newFile)) {
        throw new IllegalStateException("Unable to overwrite " + newFile.getAbsolutePath());
      }
    }
  }

  public UploadTestCasesPage() {
    super(MenuSection.TEST_PANEL, new PageParameters());

    final WebSession webSession = (WebSession) getSession();

    final FeedbackPanel uploadFeedback = new FeedbackPanel("uploadFeedback");
    add(uploadFeedback);

    final FileUploadForm simpleUploadForm = new FileUploadForm("simpleUpload", webSession);

    fileFormat = new Model<TestCaseReader.FormatType>();
    List<TestCaseReader.FormatType> fileFormatOptions = new ArrayList<TestCaseReader.FormatType>();
    fileFormatOptions.add(TestCaseReader.FormatType.CDC);
    fileFormatOptions.add(TestCaseReader.FormatType.IHS);
    fileFormatOptions.add(TestCaseReader.FormatType.MIIS);
    DropDownChoice<TestCaseReader.FormatType> format = new DropDownChoice<TestCaseReader.FormatType>("format",
        fileFormat, fileFormatOptions);
    format.setRequired(true);
    simpleUploadForm.add(format);
    softwareModel = new Model<Software>();
    DropDownChoice<Software> software = new DropDownChoice<Software>("software", softwareModel,
        SelectSoftwarePage.getSoftwareList(webSession));
    simpleUploadForm.add(software);

    add(simpleUploadForm);
  }
}
