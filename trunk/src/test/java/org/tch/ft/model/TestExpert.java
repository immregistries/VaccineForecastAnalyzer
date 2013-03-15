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

import junit.framework.TestCase;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.tch.ft.CentralControl;

public class TestExpert extends TestCase {
  public void testExperts()
  {
    SessionFactory factory = CentralControl.getSessionFactory();
    Session session = factory.openSession();

    Query query = session.createQuery("from Expert");
    List<Expert> experts = query.list();
    assertTrue(experts.size() >= 8);
    Expert expert = (Expert) session.get(Expert.class, 2);
    assertEquals("Nathan Bunker", expert.getUser().getName());
    assertEquals("Expert", expert.getRole().getLabel());
    assertEquals("TCH Expert Team", expert.getTaskGroup().getLabel());
    assertEquals("TCH Forecast Validator", expert.getTaskGroup().getPrimarySoftware().getLabel());
    expert = (Expert) session.get(Expert.class, 8);
    assertEquals("Expert", expert.getRole().getLabel());
    session.close();

  }
}
