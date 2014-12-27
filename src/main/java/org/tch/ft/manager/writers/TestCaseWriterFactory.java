package org.tch.ft.manager.writers;


public class TestCaseWriterFactory
{
  
  public static TestCaseWriter createTestCaseWriter(TestCaseWriter.FormatType formatType)
  {
    if (formatType == TestCaseWriter.FormatType.CDC)
    {
      return new CdcTestCaseWriter();
    }
    return null;
  }
}
