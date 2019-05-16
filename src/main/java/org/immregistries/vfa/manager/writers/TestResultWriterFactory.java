package org.immregistries.vfa.manager.writers;

public class TestResultWriterFactory
{

  public static WriterInterface createTestResultWriter(TestResultWriterFormatType formatType) {
    if (formatType == TestResultWriterFormatType.MCIR) {
      return new MicrTestResultWriter();
    }
    if (formatType == TestResultWriterFormatType.MCIR_CONSOLIDATED) {
      return new MicrConslidatedTestResultWriter();
    }
    return null;
  }
}
