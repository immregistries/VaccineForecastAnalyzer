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

import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.tch.ft.manager.ForecastActualExpectedCompare;
import org.tch.ft.model.TestPanelCase;
import org.tch.ft.model.User;

public class FTBasePage extends WebPage {
  private static final long serialVersionUID = 1L;

  public FTBasePage(MenuSection menuSection) {
    this(menuSection, new PageParameters());
  }

  public FTBasePage(MenuSection menuSection, final PageParameters parameters) {
    final WebSession webSession = (WebSession) getSession();
    webSession.setMenuSection(menuSection);
    add(new NavigationPanel("navigationPanel"));
  }
  
  public void selectTestCase(final ForecastActualExpectedCompare forecastCompare) {
    WebSession webSession = ((WebSession) getSession());
    Transaction trans = webSession.getDataSession().beginTransaction();
    User user = ((WebSession) getSession()).getUser();
    user.setSelectedTestCase(forecastCompare.getTestCase());
    if (user.getSelectedTestPanel() != null) {
      Query query = webSession.getDataSession().createQuery(
          "from TestPanelCase where testPanel = ? and testCase = ?");
      query.setParameter(0, user.getSelectedTestPanel());
      query.setParameter(1, forecastCompare.getTestCase());
      List<TestPanelCase> testPanelCaseList = query.list();
      if (testPanelCaseList.size() > 0) {
        user.setSelectedTestPanelCase(testPanelCaseList.get(0));
      }
    }
    trans.commit();
  }
}
