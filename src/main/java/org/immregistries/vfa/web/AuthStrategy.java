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
package org.immregistries.vfa.web;

import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.request.component.IRequestableComponent;
import org.immregistries.vfa.web.unsecure.HomePage;

public class AuthStrategy implements IAuthorizationStrategy
{
  

  public <T extends IRequestableComponent> boolean isInstantiationAuthorized(Class<T> componentClass)
  {
    if (SecurePage.class.isAssignableFrom(componentClass))
    {
      WebSession webSession = (WebSession) Session.get();
      if (!webSession.isSignedIn() || webSession.getUser() == null || !webSession.getUser().isLoggedIn())
      {
        throw new RestartResponseAtInterceptPageException(HomePage.class);
      }
    }
    return true;
  }

  public boolean isActionAuthorized(Component component, Action action)
  {
    return true;
  }

}
