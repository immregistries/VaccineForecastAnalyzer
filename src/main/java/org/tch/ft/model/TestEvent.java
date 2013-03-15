package org.tch.ft.model;

import java.io.Serializable;
import java.util.Date;

public class TestEvent implements Serializable {

  private static final long serialVersionUID = 1L;

	private int testEventId = 0;
	private TestCase testCase = null;
	private Event event = null;
	private Date eventDate = null;

	public int getTestEventId() {
		return testEventId;
	}

	public void setTestEventId(int testEventId) {
		this.testEventId = testEventId;
	}

	public TestCase getTestCase() {
		return testCase;
	}

	public void setTestCase(TestCase testCase) {
		this.testCase = testCase;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}
}
