package org.tch.ft.model;

import java.io.Serializable;

public class ExpertRating implements Serializable {
  private static final long serialVersionUID = 1L;

	private int expertRatingId = 0;
	private Expert expert = null;
	private TestNote testNote = null;
	private Rating rating = null;

	public int getExpertRatingId() {
		return expertRatingId;
	}

	public void setExpertRatingId(int expertRatingId) {
		this.expertRatingId = expertRatingId;
	}

	public Expert getExpert() {
		return expert;
	}

	public void setExpert(Expert expert) {
		this.expert = expert;
	}

	public TestNote getTestNote() {
		return testNote;
	}

	public void setTestNote(TestNote testNote) {
		this.testNote = testNote;
	}

	public Rating getRating() {
		return rating;
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}

  public String getRatingStatus() {
    return rating == null ? null : rating.getRatingStatus();
  }

  public void setRatingStatus(String ratingStatus) {
    this.rating = Rating.getRating(ratingStatus);
  }
}
