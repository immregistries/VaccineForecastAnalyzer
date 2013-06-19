package org.tch.ft.manager.readers;

public class TestCaseReaderFactory {
  public static TestCaseReader createTestCaseReader(TestCaseReader.FormatType formatType)
  {
    if (formatType == TestCaseReader.FormatType.MIIS)
    {
      return new MiisTestCaseReader();
    }
    else if (formatType == TestCaseReader.FormatType.IHS)
    {
      return new IhsTestCaseReader();
    }
    else if (formatType == TestCaseReader.FormatType.CDC)
    {
      return new CdcTestCaseReader();
    }
    else if (formatType == TestCaseReader.FormatType.STC)
    {
      return new StcTestCaseReader();
    }
    return null;
  }
}
