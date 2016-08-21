package org.tch.ft.manager.writers;


public class TestCaseWriterFactory
{
  
  public static TestCaseWriter createTestCaseWriter(TestCaseWriter.FormatType formatType)
  {
    if (formatType == TestCaseWriter.FormatType.CDC)
    {
      return new CdcTestCaseWriter();
    }
    else if (formatType == TestCaseWriter.FormatType.EPIC)
    {
      return new EpicTestCaseWriter();
    }
    return null;
  }
}
