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
package org.tch.ft.web.software;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.tch.ft.model.User;
import org.tch.ft.web.FTBasePage;
import org.tch.ft.web.MenuSection;
import org.tch.ft.web.SecurePage;
import org.tch.ft.web.WebSession;

public class SoftwareDetailsPage extends FTBasePage implements SecurePage {
  private static final long serialVersionUID = 1L;

  public SoftwareDetailsPage() {
    this(new PageParameters());
  }

  public SoftwareDetailsPage(final PageParameters pageParameters) {
    super(MenuSection.SOFTWARE, pageParameters);

    WebSession webSession = (WebSession) getSession();
    User user = webSession.getUser();
    if (user.getSelectedSoftware() == null) {
      throw new RestartResponseException(SoftwarePage.class);
    }

    add(new Label("softwareLabel", user.getSelectedSoftware().getLabel()));
    add(new Label("serviceType", user.getSelectedSoftware().getServiceType()));
    add(new Label("serviceUrl", user.getSelectedSoftware().getServiceUrl()));
    add(new Label("scheduleName", user.getSelectedSoftware().getScheduleName()));

  }

}
