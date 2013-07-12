package org.tch.ft.manager.readers;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tch.fc.model.Event;
import org.tch.fc.model.EventType;
import org.tch.fc.model.Software;
import org.tch.ft.model.TestCaseWithExpectations;
import org.tch.ft.model.User;

public abstract class CsvTestCaseReader implements TestCaseReader {

  protected SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
  protected List<List<String>> testCaseFieldListList = new ArrayList<List<String>>();
  protected List<TestCaseWithExpectations> testCaseList = new ArrayList<TestCaseWithExpectations>();
  protected List<String> headerFields = null;
  protected Map<String, Event> cvxToEventMap = new HashMap<String, Event>();
  protected User user = null;
  
  protected Software loadExpectationsSoftware;
  
  public Software getLoadExpectationsSoftware() {
    return loadExpectationsSoftware;
  }

  public void setLoadExpectationsSoftware(Software loadExpecationsSoftware) {
    this.loadExpectationsSoftware = loadExpecationsSoftware;
  }

  protected String errorMessage = null;
  
  public String getErrorMessage()
  {
    return errorMessage;
  }

  public User getUser() {
    return user;
  }

  public List<TestCaseWithExpectations> getTestCaseList() {
    return testCaseList;
  }

  public void setEventList(List<Event> eventList) {
    for (Event event : eventList) {
      if (event.getEventType() == EventType.VACCINE) {
        cvxToEventMap.put(event.getVaccineCvx(), event);
      }
    }
  }

  public void setUser(User user) {
    this.user = user;
  }

  protected String readField(int position, List<String> testCaseFieldList) {
    if (position < testCaseFieldList.size()) {
      return testCaseFieldList.get(position).trim();
    }
    return "";
  }

  protected Date readDateField(int position, List<String> testCaseFieldList, TestCaseWithExpectations testCaseWithExpectations) {
    String dateValue = readField(position, testCaseFieldList);
    if (dateValue.equals("")) {
      return null;
    }
    try {
      return sdf.parse(dateValue);
    } catch (ParseException parseException) {
      throw new IllegalArgumentException("Unable to parse date '" + dateValue + "' for test case "
          + testCaseWithExpectations.getTestCase().getTestCaseNumber() + "");
    }
  }

  protected int findFieldPos(String fieldName) {
    fieldName = fieldName.toUpperCase();
    for (int i = 0; i < headerFields.size(); i++) {
      String headerName = headerFields.get(i).toUpperCase();
      // System.out.print("[" + headerName + "]-");
      if (headerName.startsWith(fieldName)) {
        // System.out.println();
        return i;
      }
    }
    throw new IllegalArgumentException("Unable to find field that starts with '" + fieldName + "'");
  }

  protected void readInputStream(InputStream in) throws IOException {
    List<String> testCaseFieldList = new ArrayList<String>();
    StringBuilder fieldValue = new StringBuilder();
    boolean quoted = false;
    boolean justQuoted = false;
    int nextChar = in.read();
    while (nextChar != -1) {
      if (nextChar == '"') {
        if (justQuoted) {
          fieldValue.append((char) nextChar);
          justQuoted = false;
          quoted = true;
        } else if (quoted) {
          quoted = false;
          justQuoted = true;
        } else {
          quoted = true;
        }
      } else if (!quoted && nextChar == ',') {
        testCaseFieldList.add(fieldValue.toString());
        fieldValue.setLength(0);
        justQuoted = false;
      } else {
        justQuoted = false;
        if (!quoted && nextChar == '\r') {
          testCaseFieldList.add(fieldValue.toString());
          fieldValue.setLength(0);
          if (testCaseFieldList.size() > 0 && testCaseFieldList.get(0).length() > 0) {
            testCaseFieldListList.add(testCaseFieldList);
          }
          testCaseFieldList = new ArrayList<String>();
        } else {
          if (nextChar >= ' ') {
            fieldValue.append((char) nextChar);
          } else {
            fieldValue.append(' ');
          }
        }
      }
      nextChar = in.read();
    }
    in.close();
  }


}
