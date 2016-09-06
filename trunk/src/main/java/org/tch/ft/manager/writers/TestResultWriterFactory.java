package org.tch.ft.manager.writers;

public class TestResultWriterFactory
{

  public static WriterInterface createTestResultWriter(TestResultWriterFormatType formatType) {
    if (formatType == TestResultWriterFormatType.MCIR) {
      return new MicrTestResultWriter();
    }
    return null;
  }
}
