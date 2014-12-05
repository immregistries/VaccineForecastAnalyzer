package org.tch.ft.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.tch.fc.model.DateSet;
import org.tch.fc.model.RelativeRule;
import org.tch.fc.model.TestCase;
import org.tch.fc.model.TestEvent;

public class RelativeRuleManager
{

  private static class LoopDetected extends Exception
  {
    // default class
  }

  public static void updateFixedDatesForRelativeRules(TestCase testCase, Session dataSession, boolean force) {
    if (testCase.getDateSetCode().equals(DateSet.RELATIVE.getDateSetCode())) {
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
      Date today = new Date();
      String todayString = sdf.format(new Date());
      if (force || testCase.getPatientDob() == null || testCase.getEvalDate() == null
          || !sdf.format(testCase.getEvalDate()).equals(todayString)) {
        Transaction transaction = dataSession.beginTransaction();
        testCase.calculateFixedDates(today);
        Query query = dataSession.createQuery("from TestEvent where testCase = ? order by eventDate");
        query.setParameter(0, testCase);
        List<TestEvent> testEventList = query.list();
        List<TestEvent> needsToBeCalculatedList = new ArrayList<TestEvent>();
        RelativeRule parentEventRule = null;
        for (TestEvent testEvent : testEventList) {
          findCalculationNeeds(needsToBeCalculatedList, parentEventRule, testEvent);
        }
        
        for (TestEvent testEvent : needsToBeCalculatedList) {
          testEvent.calculateFixedDates();
          dataSession.update(testEvent);
        }

        dataSession.update(testCase);
        transaction.commit();
      }
    }
  }

  public static void findCalculationNeeds(List<TestEvent> needsToBeCalculatedList, RelativeRule parentEventRule,
      TestEvent testEvent) {
    {
      boolean found = false;
      for (TestEvent te : needsToBeCalculatedList) {
        if (te.equals(testEvent)) {
          found = true;
        }
      }
      if (!found) {
        needsToBeCalculatedList.add(0, testEvent);
      }
    }
    try {
      RelativeRule eventRule = testEvent.getEventRule();
      if (eventRule != null) {
        eventRule.getDependentTestEventSet().clear();
        if (parentEventRule != null) {
          if (parentEventRule.getDependentTestEventSet().contains(eventRule)) {
            throw new LoopDetected();
          }
          eventRule.getDependentTestEventSet().addAll(parentEventRule.getDependentTestEventSet());
        }

        eventRule.getDependentTestEventSet().add(testEvent);
        while (eventRule != null) {
          if (eventRule.getTestEvent() != null) {
            findCalculationNeeds(needsToBeCalculatedList, eventRule, eventRule.getTestEvent());
          }
          eventRule = eventRule.getAndRule();
        }
      }
    } catch (LoopDetected ld) {
      // continue
    }
  }
}
