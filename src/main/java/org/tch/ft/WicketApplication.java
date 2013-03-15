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
package org.tch.ft;

import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.file.Folder;
import org.tch.ft.web.AuthStrategy;
import org.tch.ft.web.WebSession;
import org.tch.ft.web.expert.ExpertPage;
import org.tch.ft.web.expert.PersonalDetailsPage;
import org.tch.ft.web.expert.TaskGroupsAssignedPage;
import org.tch.ft.web.software.EditSoftwarePage;
import org.tch.ft.web.software.RunForecastTestsPage;
import org.tch.ft.web.software.SelectSoftwarePage;
import org.tch.ft.web.software.SoftwareDetailsPage;
import org.tch.ft.web.software.SoftwarePage;
import org.tch.ft.web.software.TestResultsPage;
import org.tch.ft.web.softwareCompare.CompareForecastPage;
import org.tch.ft.web.softwareCompare.CompareSoftwarePage;
import org.tch.ft.web.softwareCompare.ComparisonListPage;
import org.tch.ft.web.softwareCompare.SelectSoftwareComparePage;
import org.tch.ft.web.taskGroup.ExpertsAssignedPage;
import org.tch.ft.web.taskGroup.SelectTaskPage;
import org.tch.ft.web.taskGroup.TaskGroupDetailsPage;
import org.tch.ft.web.taskGroup.TaskGroupPage;
import org.tch.ft.web.taskGroup.TestPanelsAssociatedPage;
import org.tch.ft.web.testCase.ActualVsExpectedPage;
import org.tch.ft.web.testCase.AddTestCasePage;
import org.tch.ft.web.testCase.CommentsPage;
import org.tch.ft.web.testCase.EditTestCasePage;
import org.tch.ft.web.testCase.ForecastLogPage;
import org.tch.ft.web.testCase.TestCaseDetailPage;
import org.tch.ft.web.testCase.TestCasePage;
import org.tch.ft.web.testPanel.SelectTestPanelPage;
import org.tch.ft.web.testPanel.TestCaseListPage;
import org.tch.ft.web.testPanel.TestPanelPage;
import org.tch.ft.web.testPanel.UploadTestCasesPage;
import org.tch.ft.web.unsecure.ForgotPasswordPage;
import org.tch.ft.web.unsecure.HomePage;
import org.tch.ft.web.unsecure.LoginPage;
import org.tch.ft.web.unsecure.LogoutPage;
import org.tch.ft.web.unsecure.RegisterUserPage;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 * 
 * @see org.tch.ft.StartForecastTester#main(String[])
 */
public class WicketApplication extends AuthenticatedWebApplication {

  private Folder uploadFolder = null;

  public Folder getUploadFolder() {
    return uploadFolder;
  }

  /**
   * @see org.apache.wicket.Application#getHomePage()
   */
  @Override
  public Class<HomePage> getHomePage() {
    return HomePage.class;
  }

  /**
   * @see org.apache.wicket.Application#init()
   */
  @Override
  public void init() {
    super.init();
    getDebugSettings().setDevelopmentUtilitiesEnabled(true);

    // unsecure
    mountPage("LoginPage", LoginPage.class);
    mountPage("LogoutPage", LogoutPage.class);
    mountPage("HomePage", HomePage.class);

    mountPage("ExpertPage", ExpertPage.class);
    mountPage("PersonalDetailsPage", PersonalDetailsPage.class);
    mountPage("TaskGroupsAssignedPage", TaskGroupsAssignedPage.class);

    mountPage("TaskGroupPage", TaskGroupPage.class);
    mountPage("TaskGroupDetailsPage", TaskGroupDetailsPage.class);
    mountPage("ExpertsAssignedPage", ExpertsAssignedPage.class);
    mountPage("SelectTaskPage", SelectTaskPage.class);
    mountPage("TestPanelsAssociatedPage", TestPanelsAssociatedPage.class);

    mountPage("TestPanelPage", TestPanelPage.class);
    mountPage("TestCaseListPage", TestCaseListPage.class);
    mountPage("TaskGroupDetailsPage", TaskGroupDetailsPage.class);
    mountPage("SelectTestPanelPage", SelectTestPanelPage.class);
    mountPage("AddTestCasePage", AddTestCasePage.class);
    mountPage("UploadTestCasesPage", UploadTestCasesPage.class);
    
    mountPage("SoftwarePage", SoftwarePage.class);
    mountPage("SoftwareDetailsPage", SoftwareDetailsPage.class);
    mountPage("EditSoftwarePage", EditSoftwarePage.class);
    mountPage("SelectSoftwarePage", SelectSoftwarePage.class);
    mountPage("TestResultsPage", TestResultsPage.class);
    mountPage("RunForecastTestsPage", RunForecastTestsPage.class);

    mountPage("TestCasePage", TestCasePage.class);
    mountPage("TestCaseDetailPage", TestCaseDetailPage.class);
    mountPage("EditTestCasePage", EditTestCasePage.class);
    mountPage("ActualVsExpectedPage", ActualVsExpectedPage.class);
    mountPage("ForecastLogPage", ForecastLogPage.class);
    mountPage("CommentsPage", CommentsPage.class);

    mountPage("CompareSoftwarePage", CompareSoftwarePage.class);
    mountPage("SelectSoftwareComparePage", SelectSoftwareComparePage.class);
    mountPage("ComparisonListPage", ComparisonListPage.class);
    mountPage("CompareForecastPage", CompareForecastPage.class);
    
    mountPage("RegisterUserPage", RegisterUserPage.class);
    mountPage("ForgotPasswordPage", ForgotPasswordPage.class);

    getSecuritySettings().setAuthorizationStrategy(new AuthStrategy());
    getApplicationSettings().setAccessDeniedPage(HomePage.class);
    
    
    getResourceSettings().setThrowExceptionOnMissingResource(false);

    uploadFolder = new Folder(System.getProperty("java.io.tmpdir"), "wicket-uploads");
    // Ensure folder exists
    uploadFolder.mkdirs();

  }

  @Override
  protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
    return WebSession.class;
  }

  @Override
  protected Class<? extends WebPage> getSignInPageClass() {
    return LoginPage.class;
  }

  static {
    System.setProperty("java.awt.headless", "true");
  }
  
}
