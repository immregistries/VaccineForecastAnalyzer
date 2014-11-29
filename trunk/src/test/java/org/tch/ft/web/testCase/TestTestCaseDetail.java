package org.tch.ft.web.testCase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.tch.fc.model.TestEvent;

import junit.framework.TestCase;

public class TestTestCaseDetail extends TestCase
{

  public void testCreateAgeAlmost() throws Exception {
    assertAge("0 Months", "01/01/2011", "01/01/2011");
    assertAge("1 Month", "02/01/2011", "01/01/2011");
    assertAge("2 Months", "03/01/2011", "01/01/2011");
    assertAge("3 Months", "04/01/2011", "01/01/2011");
    assertAge("3 Months", "04/26/2011", "01/01/2011");
    assertAge("4 Months", "05/01/2011", "01/01/2011");
    assertAge("5 Months", "06/01/2011", "01/01/2011");
    assertAge("6 Months", "07/01/2011", "01/01/2011");
    assertAge("7 Months", "08/01/2011", "01/01/2011");
    assertAge("8 Months", "09/01/2011", "01/01/2011");
    assertAge("9 Months", "10/01/2011", "01/01/2011");
    assertAge("10 Months", "11/01/2011", "01/01/2011");
    assertAge("11 Months", "12/01/2011", "01/01/2011");
    assertAge("12 Months", "01/01/2012", "01/01/2011");
    assertAge("13 Months", "02/01/2012", "01/01/2011");
    assertAge("14 Months", "03/01/2012", "01/01/2011");
    assertAge("15 Months", "04/01/2012", "01/01/2011");
    assertAge("16 Months", "05/01/2012", "01/01/2011");
    assertAge("17 Months", "06/01/2012", "01/01/2011");
    assertAge("18 Months", "07/01/2012", "01/01/2011");
    assertAge("19 Months", "08/01/2012", "01/01/2011");
    assertAge("20 Months", "09/01/2012", "01/01/2011");
    assertAge("21 Months", "10/01/2012", "01/01/2011");
    assertAge("22 Months", "11/01/2012", "01/01/2011");
    assertAge("23 Months", "12/01/2012", "01/01/2011");
    assertAge("2 Years", "01/01/2013", "01/01/2011");
    assertAge("2 Years", "02/01/2013", "01/01/2011");
    assertAge("4 Years", "01/01/2015", "01/01/2011");
    assertAge("4 Years", "01/02/2015", "01/01/2011");
    assertAge("4 Years", "01/28/2015", "01/01/2011");
    assertAge("4 Years", "03/28/2015", "01/01/2011");
    assertAge("4 Years", "07/28/2015", "01/01/2011");
    assertAge("4 Years", "12/27/2015", "01/01/2011");
    assertAge("8 Years", "01/01/2019", "01/01/2011");
    assertAge("Almost 1 Month", "01/30/2011", "01/01/2011");
    assertAge("Almost 12 Months", "12/30/2011", "01/01/2011");
    assertAge("Almost 2 Years", "12/30/2012", "01/01/2011");
    assertAge("Almost 4 Years", "12/30/2014", "01/01/2011");

  }

  public void assertAge(String age, String eventDateString, String referenceDateString) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    Date eventDate = sdf.parse(eventDateString);
    Date referenceDate = sdf.parse(referenceDateString);
    TestEvent testEvent = new TestEvent();
    testEvent.setEventDate(eventDate);
    org.tch.fc.model.TestCase tc = new org.tch.fc.model.TestCase();
    tc.setPatientDob(referenceDate);
    assertEquals(age, testEvent.getAgeAlmost(tc));
  }

}
