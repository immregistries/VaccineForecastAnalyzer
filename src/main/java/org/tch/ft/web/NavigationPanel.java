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
package org.tch.ft.web;

import java.util.Date;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.tch.ft.SoftwareVersion;
import org.tch.ft.model.Agreement;
import org.tch.ft.model.User;
import org.tch.ft.web.expert.ExpertPage;
import org.tch.ft.web.taskGroup.ExpertsAssignedPage;
import org.tch.ft.web.unsecure.RegisterUserPage;

public class NavigationPanel extends Panel {

  private static final long serialVersionUID = 1L;

  private AuthenticatedWebSession authenticatedWebSession;

  public NavigationPanel(String id) {
    super(id);

    if (authenticatedWebSession == null) {
      authenticatedWebSession = AuthenticatedWebSession.get();
    }
    final WebSession webSession = (WebSession) getSession();
    WebMarkupContainer loggedInMenu = new WebMarkupContainer("loggedInMenu");
    setUsernameLabel(loggedInMenu);
    setTaskGroupLabel(loggedInMenu);
    setTestPanelLabel(loggedInMenu);
    setSoftwareLabel(loggedInMenu);
    loggedInMenu.add(new Label("version", SoftwareVersion.VERSION));
    
    setTestCaseLabel(loggedInMenu);
    final User user = webSession.getUser();
    add(loggedInMenu);

    WebMarkupContainer loggedOutMenu = new WebMarkupContainer("loggedOutMenu");
    add(loggedOutMenu);

    WebMarkupContainer limitedMenu = new WebMarkupContainer("limitedMenu");
    setUsernameLabel(limitedMenu);
    add(limitedMenu);

    WebMarkupContainer signAgreementSection = new WebMarkupContainer("signAgreementSection");
    final Agreement agreement = RegisterUserPage.getCurrentAgreement(((WebSession) getSession()).getDataSession());
    signAgreementSection.add(new Label("agreementText", agreement.getAgreementText()).setEscapeModelStrings(false));
    Link acceptLink = new Link("acceptLink") {
      @Override
      public void onClick() {
        WebSession webSession = ((WebSession) getSession());
        Session dataSession = webSession.getDataSession();
        Transaction trans = dataSession.beginTransaction();
        user.setAgreementDate(new Date());
        user.setAgreement(agreement);
        dataSession.update(user);
        trans.commit();
        webSession.setSignedAgreement(true);
        setResponsePage(new ExpertPage());
      }
    };
    signAgreementSection.add(acceptLink);

    add(signAgreementSection);

    WebMarkupContainer notAssignedToGroupSection = new WebMarkupContainer("notAssignedToGroupSection");
    add(notAssignedToGroupSection);

    loggedOutMenu.setVisible(false);
    loggedInMenu.setVisible(false);
    limitedMenu.setVisible(false);
    signAgreementSection.setVisible(false);
    notAssignedToGroupSection.setVisible(false);

    if (user != null && user.isLoggedIn()) {
      if (!webSession.isMemberOfGroup()) {
        limitedMenu.setVisible(true);
        notAssignedToGroupSection.setVisible(true);
      } else if (!webSession.isSignedAgreement()) {
        limitedMenu.setVisible(true);
        signAgreementSection.setVisible(true);
      } else {
        loggedInMenu.setVisible(true);
      }
    } else {
      loggedOutMenu.setVisible(true);
    }
  }

  private void setUsernameLabel(WebMarkupContainer loggedInMenu) {
    String label = "";

    if (authenticatedWebSession instanceof WebSession) {
      if (authenticatedWebSession.isSignedIn()) {
        WebSession authenticator = (WebSession) AuthenticatedWebSession.get();
        label = authenticator.getUsername();
      } else {
        label = "";
      }
    }

    loggedInMenu.add(new Label("userName", label));
  }

  private void setTaskGroupLabel(WebMarkupContainer loggedInMenu) {
    String label = "";

    WebMarkupContainer taskGroupSelected = new WebMarkupContainer("taskGroupSelected");
    loggedInMenu.add(taskGroupSelected);
    WebMarkupContainer taskGroupNotSelected = new WebMarkupContainer("taskGroupNotSelected");
    loggedInMenu.add(taskGroupNotSelected);

    boolean selected = false;
    if (authenticatedWebSession instanceof WebSession) {
      if (authenticatedWebSession.isSignedIn()) {
        WebSession authenticator = (WebSession) AuthenticatedWebSession.get();
        if (authenticator.getUser().getSelectedTaskGroup() != null) {
          label = authenticator.getUser().getSelectedTaskGroup().getLabel();
          selected = true;
        }
      }
    }
    taskGroupSelected.setVisible(selected);
    taskGroupNotSelected.setVisible(!selected);
    taskGroupSelected.add(new Label("selectedTaskGroupLabel", label));
  }

  private void setTestPanelLabel(WebMarkupContainer loggedInMenu) {
    String label = "";

    WebMarkupContainer testPanelSelected = new WebMarkupContainer("testPanelSelected");
    loggedInMenu.add(testPanelSelected);
    WebMarkupContainer testPanelNotSelected = new WebMarkupContainer("testPanelNotSelected");
    loggedInMenu.add(testPanelNotSelected);
    WebMarkupContainer canEditSection = new WebMarkupContainer("canEdit");
    testPanelSelected.add(canEditSection);

    boolean selected = false;
    if (authenticatedWebSession instanceof WebSession) {
      if (authenticatedWebSession.isSignedIn()) {
        WebSession authenticator = (WebSession) AuthenticatedWebSession.get();
        if (authenticator.getUser().getSelectedTestPanel() != null) {
          label = authenticator.getUser().getSelectedTestPanel().getLabel();
          selected = true;
        }
      }
    }
    boolean canSelect = false;
    boolean canEdit = false;
    if (authenticatedWebSession instanceof WebSession) {
      if (authenticatedWebSession.isSignedIn()) {
        WebSession authenticator = (WebSession) AuthenticatedWebSession.get();
        if (authenticator.getUser().getSelectedTaskGroup() != null) {
          canSelect = true;
          canEdit = ExpertsAssignedPage.determineCanEdit(authenticator.getUser(), authenticator.getDataSession(),
              authenticator.getUser().getSelectedTaskGroup());
        }
      }
    }
    testPanelSelected.setVisible(selected);
    testPanelNotSelected.setVisible(!selected && canSelect);
    testPanelSelected.add(new Label("selectedTestPanelLabel", label));
    canEditSection.setVisible(canEdit);
  }

  private void setSoftwareLabel(WebMarkupContainer loggedInMenu) {
    String label = "";

    WebMarkupContainer softwareSelected = new WebMarkupContainer("softwareSelected");
    loggedInMenu.add(softwareSelected);
    WebMarkupContainer softwareNotSelected = new WebMarkupContainer("softwareNotSelected");
    loggedInMenu.add(softwareNotSelected);
    WebMarkupContainer testPanelSelected = new WebMarkupContainer("testPanelSelected");
    softwareSelected.add(testPanelSelected);

    boolean selected = false;
    if (authenticatedWebSession instanceof WebSession) {
      if (authenticatedWebSession.isSignedIn()) {
        WebSession authenticator = (WebSession) AuthenticatedWebSession.get();
        if (authenticator.getUser().getSelectedSoftware() != null) {
          label = authenticator.getUser().getSelectedSoftware().getLabel();
          selected = true;
        }
      }
    }
    boolean canSelect = false;
    if (!selected) {
      if (authenticatedWebSession instanceof WebSession) {
        if (authenticatedWebSession.isSignedIn()) {
          WebSession authenticator = (WebSession) AuthenticatedWebSession.get();
          if (authenticator.getUser().getSelectedTaskGroup() != null) {
            canSelect = true;
          }
        }
      }
    } else {
      if (authenticatedWebSession instanceof WebSession) {
        if (authenticatedWebSession.isSignedIn()) {
          WebSession authenticator = (WebSession) AuthenticatedWebSession.get();
          if (authenticator.getUser().getSelectedTestPanel() == null) {
            testPanelSelected.setVisible(false);
          }
        }
      }

    }

    softwareSelected.setVisible(selected);
    softwareNotSelected.setVisible(!selected & canSelect);
    softwareSelected.add(new Label("selectedSoftwareLabel", label));
    
    WebMarkupContainer softwareCompare = new WebMarkupContainer("softwareCompare");
    setSoftwareCompareLabel(softwareCompare);
    loggedInMenu.add(softwareCompare);
  }

  private void setSoftwareCompareLabel(WebMarkupContainer softwareCompare) {
    String label = "";

    WebMarkupContainer softwareSelected = new WebMarkupContainer("softwareCompareSelected");
    softwareCompare.add(softwareSelected);
    WebMarkupContainer softwareNotSelected = new WebMarkupContainer("softwareCompareNotSelected");
    softwareCompare.add(softwareNotSelected);
    WebMarkupContainer testPanelSelected = new WebMarkupContainer("testPanelSelected");
    softwareSelected.add(testPanelSelected);

    boolean selected = false;
    if (authenticatedWebSession instanceof WebSession) {
      if (authenticatedWebSession.isSignedIn()) {
        WebSession authenticator = (WebSession) AuthenticatedWebSession.get();
        if (authenticator.getUser().getSelectedSoftwareCompare() != null) {
          if (authenticator.getUser().getSelectedSoftwareCompare().getSoftwareTargetList() == null) {
            Query query = authenticator.getDataSession().createQuery("from SoftwareTarget where softwareCompare = ?");
            query.setParameter(0, authenticator.getUser().getSelectedSoftwareCompare());
            authenticator.getUser().getSelectedSoftwareCompare().setSoftwareTargetList(query.list());
          }
          label = authenticator.getUser().getSelectedSoftwareCompare().getComparedToLabel();
          selected = true;
        }
      }
    }
    boolean canSelect = false;
    if (!selected) {
      if (authenticatedWebSession instanceof WebSession) {
        if (authenticatedWebSession.isSignedIn()) {
          WebSession authenticator = (WebSession) AuthenticatedWebSession.get();
          if (authenticator.getUser().getSelectedTaskGroup() != null) {
            canSelect = true;
          }
        }
      }
    } else {
      if (authenticatedWebSession instanceof WebSession) {
        if (authenticatedWebSession.isSignedIn()) {
          WebSession authenticator = (WebSession) AuthenticatedWebSession.get();
          if (authenticator.getUser().getSelectedTestPanel() == null) {
            testPanelSelected.setVisible(false);
          }
        }
      }

    }

    softwareSelected.setVisible(selected);
    softwareNotSelected.setVisible(!selected & canSelect);
    softwareSelected.add(new Label("selectedSoftwareCompareLabel", label));
  }

  private void setTestCaseLabel(WebMarkupContainer loggedInMenu) {
    String label = "";

    WebMarkupContainer testCaseSelected = new WebMarkupContainer("testCaseSelected");
    loggedInMenu.add(testCaseSelected);

    boolean selected = false;
    if (authenticatedWebSession instanceof WebSession) {
      if (authenticatedWebSession.isSignedIn()) {
        WebSession authenticator = (WebSession) AuthenticatedWebSession.get();
        if (authenticator.getUser().getSelectedTestCase() != null) {
          label = authenticator.getUser().getSelectedTestCase().getLabel();
          selected = true;
        }
      }
    }
    testCaseSelected.setVisible(selected);
    testCaseSelected.add(new Label("selectedTestCaseLabel", label));
  }

}
