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
package org.immregistries.vfa.web.software;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.hibernate.Session;
import org.immregistries.vfa.connect.model.Software;
import org.immregistries.vfa.manager.ForecastActualExpectedCompare;
import org.immregistries.vfa.manager.ForecastActualGenerator;
import org.immregistries.vfa.model.TestPanel;
import org.immregistries.vfa.model.User;
import org.immregistries.vfa.web.FTBasePage;
import org.immregistries.vfa.web.MenuSection;
import org.immregistries.vfa.web.SecurePage;
import org.immregistries.vfa.web.WebSession;

public class RunForecastTestsPage extends FTBasePage implements SecurePage {
  private static final long serialVersionUID = 1L;

  public RunForecastTestsPage(final PageParameters pageParameters) {
    super(MenuSection.SOFTWARE, pageParameters);

    Link runLink = new Link("runLink") {

      @Override
      public void onClick() {
        // need a way to set the Software choice
        // probably need to use a model that can be seen by other objects
        WebSession webSession = ((WebSession) getSession());
        User user = webSession.getUser();
        TestPanel testPanel = user.getSelectedTestPanel();
        Software software = user.getSelectedSoftware();
        StringWriter stringWriter = new StringWriter();
        PrintWriter logOut = new PrintWriter(stringWriter);
        logOut.println("Running all forecasts");
        if (testPanel != null && software != null) {
          Session dataSession = webSession.getDataSession();
          try {
            ForecastActualGenerator.runForecastActual(testPanel, software,
                dataSession, false);
            logOut.println("Forecast returned with results");

          } catch (Exception e) {
            e.printStackTrace(logOut);
          }

        } else {
          logOut.println("Unable to run forecast, test panel and software is not selected");
        }

        logOut.flush();
        pageParameters.add("runForecastTestsResults", stringWriter.toString());
        // this causes the page to refresh
        setResponsePage(new TestResultsPage(pageParameters));
      }

    };
    add(runLink);

  }

}
