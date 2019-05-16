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
package org.immregistries.vfa.manager;

import java.text.SimpleDateFormat;
import org.immregistries.vfa.manager.AgeUtil;
import junit.framework.TestCase;

public class AgeUtilTest extends TestCase {
  
  public void testGetAge() throws Exception
  {
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    assertEquals("birth", AgeUtil.getAge(sdf.parse("12/01/2012"), sdf.parse("12/01/2012")));
    assertEquals("0 months", AgeUtil.getAge(sdf.parse("12/01/2012"), sdf.parse("12/02/2012")));
    assertEquals("0 months", AgeUtil.getAge(sdf.parse("12/01/2012"), sdf.parse("12/15/2012")));
    assertEquals("0 months", AgeUtil.getAge(sdf.parse("12/01/2012"), sdf.parse("12/31/2012")));
    assertEquals("1 month", AgeUtil.getAge(sdf.parse("11/01/2012"), sdf.parse("12/01/2012")));
    assertEquals("2 months", AgeUtil.getAge(sdf.parse("10/01/2012"), sdf.parse("12/01/2012")));
    assertEquals("3 months", AgeUtil.getAge(sdf.parse("09/01/2012"), sdf.parse("12/01/2012")));
    assertEquals("1 year", AgeUtil.getAge(sdf.parse("12/01/2011"), sdf.parse("12/01/2012")));
    assertEquals("1 year 1 month", AgeUtil.getAge(sdf.parse("11/01/2011"), sdf.parse("12/01/2012")));
    assertEquals("1 year 6 months", AgeUtil.getAge(sdf.parse("06/01/2011"), sdf.parse("12/01/2012")));
    assertEquals("2 years", AgeUtil.getAge(sdf.parse("12/01/2010"), sdf.parse("12/01/2012")));
    assertEquals("4 years 11 months", AgeUtil.getAge(sdf.parse("12/01/2007"), sdf.parse("11/01/2012")));
    assertEquals("5 years", AgeUtil.getAge(sdf.parse("12/01/2007"), sdf.parse("12/01/2012")));
    assertEquals("19 years", AgeUtil.getAge(sdf.parse("12/02/1992"), sdf.parse("12/01/2012")));
    assertEquals("20 years", AgeUtil.getAge(sdf.parse("12/01/1992"), sdf.parse("12/01/2012")));
    assertEquals("20 years", AgeUtil.getAge(sdf.parse("10/01/1992"), sdf.parse("12/01/2012")));
  }

}
