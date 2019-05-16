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
package org.immregistries.vfa.model;

import java.util.List;


import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.immregistries.vfa.CentralControl;
import org.immregistries.vfa.manager.UserManager;
import org.immregistries.vfa.model.User;
import junit.framework.TestCase;

public class TestUser extends TestCase
{
  public void testUsers()
  {
    SessionFactory factory = CentralControl.getSessionFactory();
    Session session = factory.openSession();

    Query query = session.createQuery("from User where name = ?");
    query.setString(0, "View Only");
    List<User> users = query.list();
    assertEquals(1, users.size());
    User user = new User();
    user.setName("View Only");
    user.setPassword("tub93");
    user = UserManager.login(user, session);
    assertTrue(user.isLoggedIn());
    session.close();
  }
  
}

