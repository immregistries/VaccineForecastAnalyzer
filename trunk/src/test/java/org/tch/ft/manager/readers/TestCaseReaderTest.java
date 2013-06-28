package org.tch.ft.manager.readers;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.tch.fc.model.ForecastItem;

public class TestCaseReaderTest extends TestCase {

  public void testReadInputStream() throws Exception {
    TCR tcr = new TCR();
    tcr.read(this.getClass().getResourceAsStream("STCExample.txt"));
    assertNotNull(tcr.getTestCaseFieldListList());
    assertEquals(28, tcr.getTestCaseFieldListList().size());
  }
  
  private class TCR extends CsvTestCaseReader
  {
    public void setForecastItems(Map<Integer, ForecastItem> forecastItemListMap) {
      // do nothing
    }

    public void read(InputStream in) throws IOException {
      readInputStream(in);
    }
    
    public List<List<String>> getTestCaseFieldListList()
    {
      return testCaseFieldListList;
    }
  }

}
