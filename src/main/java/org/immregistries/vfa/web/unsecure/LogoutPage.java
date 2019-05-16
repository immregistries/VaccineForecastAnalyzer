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
package org.immregistries.vfa.web.unsecure;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.immregistries.vfa.web.FTBasePage;
import org.immregistries.vfa.web.MenuSection;
import org.immregistries.vfa.web.WebSession;

public class LogoutPage extends FTBasePage {

  private static final long serialVersionUID = 1L;

  public LogoutPage(final PageParameters parameters) {
    super(MenuSection.UNSECURE);
    WebSession webSession = ((WebSession) getSession());
    webSession.signOut();
    webSession.invalidate();
    setResponsePage(new HomePage());
  }

}
