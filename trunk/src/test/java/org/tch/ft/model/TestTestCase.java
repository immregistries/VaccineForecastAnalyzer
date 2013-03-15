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
package org.tch.ft.model;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.tch.ft.CentralControl;

import junit.framework.TestCase;

public class TestTestCase extends TestCase {

  public void testTestCase()
  {
    SessionFactory factory = CentralControl.getSessionFactory();
    Session session = factory.openSession();

    Query query = session.createQuery("from TestCase");
    List<org.tch.ft.model.TestCase> testCaseList = query.list();
    assertTrue(testCaseList.size() >= 257);
    org.tch.ft.model.TestCase testCase = (org.tch.ft.model.TestCase) session.get(org.tch.ft.model.TestCase.class, 2);
    query = session.createQuery("from TestEvent where testCase = ?");
    query.setParameter(0, testCase);
    List<TestEvent> testEventList = query.list();
    assertEquals(1, testEventList.size());
    session.close();
  }
}
