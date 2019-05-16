package org.immregistries.vfa.manager.readers;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.immregistries.vfa.connect.model.Event;
import org.immregistries.vfa.connect.model.VaccineGroup;
import org.immregistries.vfa.model.TestCaseWithExpectations;
import org.immregistries.vfa.model.User;
import org.immregistries.vfa.connect.model.Software;

public interface TestCaseReader {
  
  public static enum FormatType {MIIS, IHS, CDC, STC}

  public void setVaccineGroupss(Map<Integer, VaccineGroup> vaccineGroupListMap);
  
  public User getUser() ;

  public List<TestCaseWithExpectations> getTestCaseList();

  public void setEventList(List<Event> eventList) ;

  public void setUser(User user) ;

  public void read(InputStream in) throws IOException ;
  
  public void setLoadExpectationsSoftware(Software s);
  
  public Software getLoadExpectationsSoftware();
  
  public String getErrorMessage();

}
