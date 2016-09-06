package org.tch.ft.manager.writers;


public class TestCaseWriterFactory
{
  
  public static WriterInterface createTestCaseWriter(TestCaseWriterFormatType formatType)
  {
    if (formatType == TestCaseWriterFormatType.CDC)
    {
      return new CdcTestCaseWriter();
    }
    else if (formatType == TestCaseWriterFormatType.EPIC)
    {
      return new EpicTestCaseWriter();
    }
    return null;
  }
}
