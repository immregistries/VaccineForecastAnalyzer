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
package org.immregistries.vfa;

import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.file.Folder;
import org.immregistries.vfa.web.AuthStrategy;
import org.immregistries.vfa.web.WebSession;
import org.immregistries.vfa.web.expert.ExpertPage;
import org.immregistries.vfa.web.expert.PersonalDetailsPage;
import org.immregistries.vfa.web.expert.TaskGroupsAssignedPage;
import org.immregistries.vfa.web.software.EditSoftwarePage;
import org.immregistries.vfa.web.software.RunForecastTestsPage;
import org.immregistries.vfa.web.software.SelectSoftwarePage;
import org.immregistries.vfa.web.software.SoftwareDetailsPage;
import org.immregistries.vfa.web.software.SoftwarePage;
import org.immregistries.vfa.web.software.TestResultsPage;
import org.immregistries.vfa.web.softwareCompare.CompareForecastPage;
import org.immregistries.vfa.web.softwareCompare.ComparisonListPage;
import org.immregistries.vfa.web.softwareCompare.SelectSoftwareComparePage;
import org.immregistries.vfa.web.taskGroup.ExpertsAssignedPage;
import org.immregistries.vfa.web.taskGroup.SelectTaskPage;
import org.immregistries.vfa.web.taskGroup.TaskGroupDetailsPage;
import org.immregistries.vfa.web.taskGroup.TaskGroupPage;
import org.immregistries.vfa.web.taskGroup.TestPanelsAssociatedPage;
import org.immregistries.vfa.web.testCase.ActualVsExpectedPage;
import org.immregistries.vfa.web.testCase.AddTestCasePage;
import org.immregistries.vfa.web.testCase.CommentsPage;
import org.immregistries.vfa.web.testCase.EditTestCasePage;
import org.immregistries.vfa.web.testCase.ForecastLogPage;
import org.immregistries.vfa.web.testCase.ForecastNowPage;
import org.immregistries.vfa.web.testCase.TestCaseDetailPage;
import org.immregistries.vfa.web.testCase.TestCasePage;
import org.immregistries.vfa.web.testPanel.SelectTestPanelPage;
import org.immregistries.vfa.web.testPanel.TestCaseListPage;
import org.immregistries.vfa.web.testPanel.TestPanelPage;
import org.immregistries.vfa.web.testPanel.UploadTestCasesPage;
import org.immregistries.vfa.web.unsecure.ForgotPasswordPage;
import org.immregistries.vfa.web.unsecure.HomePage;
import org.immregistries.vfa.web.unsecure.LoginPage;
import org.immregistries.vfa.web.unsecure.LogoutPage;
import org.immregistries.vfa.web.unsecure.RegisterUserPage;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start
 * class.
 * 
 * @see org.immregistries.vfa.StartForecastTester#main(String[])
 */
public class WicketApplication extends AuthenticatedWebApplication
{

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
    mountPage("ForecastNowPage", ForecastNowPage.class);
    mountPage("CommentsPage", CommentsPage.class);

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
