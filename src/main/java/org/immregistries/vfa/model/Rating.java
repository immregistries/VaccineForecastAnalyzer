package org.immregistries.vfa.model;

import java.io.Serializable;

public enum Rating implements Serializable {
  LIKE("L", "Like"), COMMENT("C", "Comment"), PROBLEM("P", "Problem"), ;

  private static final long serialVersionUID = 1L;

  private String ratingStatus = "";
  private String label = "";

  Rating(String ratingStatus, String label) {
    this.ratingStatus = ratingStatus;
    this.label = label;
  }

  public String getRatingStatus() {
    return ratingStatus;
  }

  public void setRatingStatus(String ratingStatus) {
    this.ratingStatus = ratingStatus;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public static Rating getRating(String ratingStatus) {
    for (Rating rating : values()) {
      if (rating.getRatingStatus().equalsIgnoreCase(ratingStatus)) {
        return rating;
      }
    }
    return null;
  }

}
