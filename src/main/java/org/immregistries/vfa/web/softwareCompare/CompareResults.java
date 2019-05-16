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
package org.immregistries.vfa.web.softwareCompare;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.immregistries.vfa.manager.ForecastActualExpectedCompare;

public class CompareResults implements Serializable {
  private String status = "";
  private List<ForecastActualExpectedCompare> forecastCompareList = new ArrayList<ForecastActualExpectedCompare>();
  public String getStatus() {
    return status;
  }
  public void setStatus(String status) {
    this.status = status;
  }
  public List<ForecastActualExpectedCompare> getForecastCompareList() {
    return forecastCompareList;
  }
  public void setForecastCompareList(List<ForecastActualExpectedCompare> forecastCompareList) {
    this.forecastCompareList = forecastCompareList;
  }
}
