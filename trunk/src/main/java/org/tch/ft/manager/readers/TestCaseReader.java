package org.tch.ft.manager.readers;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.tch.ft.model.Event;
import org.tch.ft.model.ForecastItem;
import org.tch.ft.model.Software;
import org.tch.ft.model.TestCase;
import org.tch.ft.model.User;

public interface TestCaseReader {
  
  public static enum FormatType {MIIS, IHS, CDC}

  public void setForecastItems(Map<Integer, ForecastItem> forecastItemListMap);
  
  public User getUser() ;

  public List<TestCase> getTestCaseList();

  public void setEventList(List<Event> eventList) ;

  public void setUser(User user) ;

  public void read(InputStream in) throws IOException ;
  
  public void setLoadExpectationsSoftware(Software s);
  
  public Software getLoadExpectationsSoftware();
  
  public String getErrorMessage();

}
