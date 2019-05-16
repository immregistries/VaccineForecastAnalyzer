package org.immregistries.vfa.manager;

public enum AdverseOutcome {
  DOES_NOT_COMPLETE("A", "Patient does not complete series."),
  GIVEN_INVALID("B", "Patient given an invalid dose."),
  NOT_GIVEN_VALID("C", "Patient NOT given a valid dose, scheduled to receive valid dose later."),
  TOO_MANY_DOSES("D", "Patient receives too many doses."),
  GIVEN_EARLY("E", "Patient given a valid dose earlier than recommended. "),
  SCHEDULED_FOR_INVALID("F", "Patient scheduled to receive an invalid dose. "),
  SCHEDULED_FOR_LATE("G", "Patient scheduled for a valid dose later than recommended. "),
  SCHEDULED_FOR_EARLY("H", "Patient scheduled for a valid dose earlier than recommended."),
  ;
  private String id = "";
  private String text = "";
  public String getId() {
    return id;
  }
  public String getText() {
    return text;
  }
  private AdverseOutcome(String id, String text)
  {
    this.id = id;
    this.text = text;
  }
  
  @Override
  public String toString() {
    return text;
  }
}
