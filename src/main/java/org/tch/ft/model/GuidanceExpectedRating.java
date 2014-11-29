package org.tch.ft.model;

import java.io.Serializable;

public class GuidanceExpectedRating implements Serializable
{
  private static final long serialVersionUID = 1L;

  private int guidanceExpectedRatingId = 0;
  private GuidanceExpected guidanceExpected = null;
  private ExpertRating expertRating = null;

  public int getGuidanceExpectedRatingId() {
    return guidanceExpectedRatingId;
  }

  public void setGuidanceExpectedRatingId(int guidanceExpectedRatingId) {
    this.guidanceExpectedRatingId = guidanceExpectedRatingId;
  }

  public GuidanceExpected getGuidanceExpected() {
    return guidanceExpected;
  }

  public void setGuidanceExpected(GuidanceExpected guidanceExpected) {
    this.guidanceExpected = guidanceExpected;
  }

  public ExpertRating getExpertRating() {
    return expertRating;
  }

  public void setExpertRating(ExpertRating expertRating) {
    this.expertRating = expertRating;
  }

}
