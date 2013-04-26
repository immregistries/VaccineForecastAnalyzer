package org.tch.ft.manager.readers;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.tch.ft.model.Event;
import org.tch.ft.model.ForecastExpected;
import org.tch.ft.model.ForecastItem;
import org.tch.ft.model.TestCase;
import org.tch.ft.model.TestEvent;
import org.tch.ft.model.User;

public interface TestCaseReader {
  
  public static enum FormatType {MIIS, IHS}

  public void setForecastItems(Map<Integer, ForecastItem> forecastItemListMap);
  
  public Map<TestCase, List<TestEvent>> getTestEventListMap() ;

  public User getUser() ;

  public List<TestCase> getTestCaseList();

  public Map<TestCase, List<ForecastExpected>> getForecastExpectedListMap();

  public void setEventList(List<Event> eventList) ;

  public void setUser(User user) ;

  public void read(InputStream in) throws IOException ;

}
