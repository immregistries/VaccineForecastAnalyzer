package org.immregistries.vfa.model;

import java.io.Serializable;

import org.immregistries.vfa.connect.model.GuidanceActual;

public class GuidanceActualRating implements Serializable
{
  private static final long serialVersionUID = 1L;

  private int guidanceActualRatingId = 0;
  private GuidanceActual guidanceActual = null;
  private ExpertRating expertRating = null;

  public int getGuidanceActualRatingId() {
    return guidanceActualRatingId;
  }

  public void setGuidanceActualRatingId(int guidanceActualRatingId) {
    this.guidanceActualRatingId = guidanceActualRatingId;
  }

  public GuidanceActual getGuidanceActual() {
    return guidanceActual;
  }

  public void setGuidanceActual(GuidanceActual guidanceActual) {
    this.guidanceActual = guidanceActual;
  }

  public ExpertRating getExpertRating() {
    return expertRating;
  }

  public void setExpertRating(ExpertRating expertRating) {
    this.expertRating = expertRating;
  }
}
