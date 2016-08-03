package org.tch.ft.servlet;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class ExternalTestServletTest {

  @Test
  public void test() throws ParseException {
    assertEquals(ExternalTestServlet.BABY, ExternalTestServlet.determineCategoryName(new Date()));
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    assertEquals(ExternalTestServlet.TODDLER, ExternalTestServlet.determineCategoryName(sdf.parse("08/01/2014")));
    assertEquals(ExternalTestServlet.YOUNG_CHILD, ExternalTestServlet.determineCategoryName(sdf.parse("08/01/2012")));
    assertEquals(ExternalTestServlet.CHILD, ExternalTestServlet.determineCategoryName(sdf.parse("08/01/2009")));
    assertEquals(ExternalTestServlet.TWEEN, ExternalTestServlet.determineCategoryName(sdf.parse("08/01/2006")));
    assertEquals(ExternalTestServlet.TEENAGER, ExternalTestServlet.determineCategoryName(sdf.parse("08/01/2003")));
    assertEquals(ExternalTestServlet.YOUNG_ADULT, ExternalTestServlet.determineCategoryName(sdf.parse("08/01/1996")));
    assertEquals(ExternalTestServlet.ADULT, ExternalTestServlet.determineCategoryName(sdf.parse("08/01/1991")));
    assertEquals(ExternalTestServlet.SENIOR, ExternalTestServlet.determineCategoryName(sdf.parse("08/01/1960")));
  }

}
