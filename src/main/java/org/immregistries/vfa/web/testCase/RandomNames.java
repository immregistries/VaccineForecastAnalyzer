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
package org.immregistries.vfa.web.testCase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomNames {

  private List<String> firstNameList = new ArrayList<String>();
  private List<String> lastNameList = new ArrayList<String>();
  private Random random = new Random();

  private RandomNames() throws IOException {
    InputStreamReader in = new InputStreamReader(this.getClass().getResourceAsStream("RandomNames.properties"));
    BufferedReader reader = new BufferedReader(in);
    String line;
    while ((line = reader.readLine()) != null)
    {
      if (line.startsWith("FIRST=")) {
        firstNameList.add(line.substring(6).trim());
      } else if (line.startsWith("LAST=")) {
        lastNameList.add(line.substring(5).trim());
      }
    }
    in.close();
  }

  private static RandomNames randomNames = null;

  public static String getRandomLastName() {
    init();
    return randomNames.lastNameList.get(randomNames.random.nextInt(randomNames.lastNameList.size()));
  }

  public static String getRandomFirstName() {
    init();
    return randomNames.firstNameList.get(randomNames.random.nextInt(randomNames.firstNameList.size()));
  }
  private static void init() {
    if (randomNames == null) {
      try {
        randomNames = new RandomNames();
      } catch (IOException ioe) {
        throw new IllegalArgumentException("Unable to to initialize random names", ioe);
      }
    }
  }
}
