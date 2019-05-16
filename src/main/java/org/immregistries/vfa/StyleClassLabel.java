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
package org.immregistries.vfa;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;

public class StyleClassLabel extends Label {
  private String styleClass = "";

  public StyleClassLabel(String label, String value, String styleClass) {
    super(label, value);
    this.styleClass = styleClass;
  }

  @Override
  protected void onComponentTag(ComponentTag tag) {
    // TODO Auto-generated method stub
    super.onComponentTag(tag);
    tag.put("class", styleClass);
  }

}
