package org.immregistries.vfa.model;

import java.io.Serializable;
import java.util.Date;

import org.immregistries.vfa.connect.model.TestCase;

public class TestNote implements Serializable {

  private static final long serialVersionUID = 1L;

	private int testNoteId = 0;
	private TestCase testCase = null;
	private User user = null;
	private String noteText = "";
	private Date noteDate = null;

	public int getTestNoteId() {
		return testNoteId;
	}

	public void setTestNoteId(int testNoteId) {
		this.testNoteId = testNoteId;
	}

	public TestCase getTestCase() {
		return testCase;
	}

	public void setTestCase(TestCase testCase) {
		this.testCase = testCase;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getNoteText() {
		return noteText;
	}

	public void setNoteText(String noteText) {
		this.noteText = noteText;
	}

	public Date getNoteDate() {
		return noteDate;
	}

	public void setNoteDate(Date noteDate) {
		this.noteDate = noteDate;
	}

}
