package org.tch.ft.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.tch.fc.model.Consideration;
import org.tch.fc.model.ConsiderationGuidance;
import org.tch.fc.model.EventType;
import org.tch.fc.model.Guidance;
import org.tch.fc.model.Rationale;
import org.tch.fc.model.RationaleGuidance;
import org.tch.fc.model.Recommend;
import org.tch.fc.model.RecommendGuidance;
import org.tch.fc.model.Resource;
import org.tch.fc.model.ResourceGuidance;
import org.tch.fc.model.TestCase;
import org.tch.fc.model.TestEvent;
import org.tch.fc.model.VaccineGroup;
import org.tch.ft.model.EvaluationExpected;
import org.tch.ft.model.ForecastExpected;
import org.tch.ft.model.GuidanceExpected;
import org.tch.ft.model.TestPanelCase;
import org.tch.ft.model.TestPanelEvaluation;
import org.tch.ft.model.TestPanelForecast;
import org.tch.ft.model.TestPanelGuidance;
import org.tch.ft.model.User;

public class ExpectationsManager
{

  private List<TestEvent> vaccinationEvents = null;
  private Map<TestEvent, EvaluationExpected> testEventMapToEvaluationExpected = null;
  private ForecastExpected forecastExpected = null;
  private GuidanceExpected guidanceExpected = null;
  private List<RecommendGuidance> recommendGuidanceList = null;
  private List<ConsiderationGuidance> considerationGuidanceList = null;
  private List<RationaleGuidance> rationaleGuidanceList = null;
  private List<ResourceGuidance> resourceGuidanceList = null;
  private List<RecommendGuidance> recommendGuidanceDeleteList = new ArrayList<RecommendGuidance>();
  private List<ConsiderationGuidance> considerationGuidanceDeleteList = new ArrayList<ConsiderationGuidance>();
  private List<RationaleGuidance> rationaleGuidanceDeleteList = new ArrayList<RationaleGuidance>();
  private List<ResourceGuidance> resourceGuidanceDeleteList = new ArrayList<ResourceGuidance>();

  public List<RecommendGuidance> getRecommendGuidanceDeleteList() {
    return recommendGuidanceDeleteList;
  }

  public List<ConsiderationGuidance> getConsiderationGuidanceDeleteList() {
    return considerationGuidanceDeleteList;
  }

  public List<RationaleGuidance> getRationaleGuidanceDeleteList() {
    return rationaleGuidanceDeleteList;
  }

  public List<ResourceGuidance> getResourceGuidanceDeleteList() {
    return resourceGuidanceDeleteList;
  }

  public List<TestEvent> getVaccinationEvents() {
    return vaccinationEvents;
  }

  public Map<TestEvent, EvaluationExpected> getTestEventMapToEvaluationExpected() {
    return testEventMapToEvaluationExpected;
  }

  public ForecastExpected getForecastExpected() {
    return forecastExpected;
  }

  public GuidanceExpected getGuidanceExpected() {
    return guidanceExpected;
  }

  public List<RecommendGuidance> getRecommendGuidanceList() {
    return recommendGuidanceList;
  }

  public List<ConsiderationGuidance> getConsiderationGuidanceList() {
    return considerationGuidanceList;
  }

  public List<RationaleGuidance> getRationaleGuidanceList() {
    return rationaleGuidanceList;
  }

  public List<ResourceGuidance> getResourceGuidanceList() {
    return resourceGuidanceList;
  }

  private User user = null;
  private VaccineGroup vaccineGroup = null;
  private Session dataSession = null;
  private boolean loadTestPanelDefault = false;
  private TestCase testCase = null;
  private TestPanelCase testPanelCase = null;

  public ExpectationsManager(User user, VaccineGroup vaccineGroup, boolean loadTestPanelDefault, Session dataSession) {

    this.user = user;
    this.vaccineGroup = vaccineGroup;
    this.loadTestPanelDefault = loadTestPanelDefault;
    this.dataSession = dataSession;

    testCase = user.getSelectedTestCase();
    testPanelCase = user.getSelectedTestPanelCase();
    if (testPanelCase == null || testCase == null) {
      throw new IllegalArgumentException("Unable to set expectations because user has not selected test case");
    }

    setupEvaluations();
    setupForecast();
    setupGuidance();
  }

  public void setupGuidance() {
    Query query;
    query = dataSession
        .createQuery("from GuidanceExpected where testCase = ? and author= ? and guidance.vaccineGroup = ? ");
    query.setParameter(0, testCase);
    query.setParameter(1, user);
    query.setParameter(2, vaccineGroup);
    List<GuidanceExpected> guidanceExpectedList = query.list();
    if (guidanceExpectedList.size() > 0) {
      guidanceExpected = guidanceExpectedList.get(0);
    } else if (loadTestPanelDefault) {
      query = dataSession
          .createQuery("from TestPanelGuidance where guidanceExpected.testCase = ? and testPanelCase = ? and guidanceExpected.guidance.vaccineGroup = ?");
      query.setParameter(0, testCase);
      query.setParameter(1, testPanelCase);
      query.setParameter(2, vaccineGroup);
      List<TestPanelGuidance> testPanelGuidanceList = query.list();
      if (testPanelGuidanceList.size() > 0) {
        guidanceExpected = testPanelGuidanceList.get(0).getGuidanceExpected();
      }
    }
    recommendGuidanceList = new ArrayList<RecommendGuidance>();
    considerationGuidanceList = new ArrayList<ConsiderationGuidance>();
    rationaleGuidanceList = new ArrayList<RationaleGuidance>();
    resourceGuidanceList = new ArrayList<ResourceGuidance>();
    if (guidanceExpected == null) {
      guidanceExpected = new GuidanceExpected();
      Guidance guidance = new Guidance();
      guidanceExpected.setGuidance(guidance);
      guidanceExpected.setTestCase(testCase);
      guidanceExpected.setAuthor(user);
      guidance.setVaccineGroup(vaccineGroup);
    } else {
      // Recommend
      query = dataSession
          .createQuery("from RecommendGuidance where guidance = ? order by recommend.recommendTypeCode, recommend.recommendText");
      query.setParameter(0, guidanceExpected.getGuidance());
      recommendGuidanceList = query.list();
      // Consideration
      query = dataSession
          .createQuery("from ConsiderationGuidance where guidance = ? order by consideration.considerationTypeCode, consideration.considerationText");
      query.setParameter(0, guidanceExpected.getGuidance());
      considerationGuidanceList = query.list();
      // Rationale
      query = dataSession.createQuery("from RationaleGuidance where guidance = ? order by rationale.rationaleText");
      query.setParameter(0, guidanceExpected.getGuidance());
      rationaleGuidanceList = query.list();
      // Resource
      query = dataSession.createQuery("from ResourceGuidance where resource = ? order by resource.resourceText");
      query.setParameter(0, guidanceExpected.getGuidance());
      resourceGuidanceList = query.list();
    }
  }

  public void setupForecast() {
    Query query;
    query = dataSession.createQuery("from ForecastExpected where testCase = ? and author = ? and vaccineGroup = ? ");
    query.setParameter(0, testCase);
    query.setParameter(1, user);
    query.setParameter(2, vaccineGroup);
    List<ForecastExpected> forecastExpectedList = query.list();
    if (forecastExpectedList.size() > 0) {
      forecastExpected = forecastExpectedList.get(0);
    } else if (loadTestPanelDefault) {
      query = dataSession
          .createQuery("from TestPanelForecast where forecastExpected.testCase = ? and testPanelCase = ? and forecastExpected.vaccineGroup = ? ");
      query.setParameter(0, testCase);
      query.setParameter(1, testPanelCase);
      query.setParameter(2, vaccineGroup);
      List<TestPanelForecast> testPanelForecastList = query.list();
      if (testPanelForecastList.size() > 0) {
        forecastExpected = testPanelForecastList.get(0).getForecastExpected();
      }
    }
    if (forecastExpected == null) {
      forecastExpected = new ForecastExpected();
      forecastExpected.setTestCase(testCase);
      forecastExpected.setAuthor(user);
      forecastExpected.setVaccineGroup(vaccineGroup);
    }
  }

  public void setupEvaluations() {
    Query query;

    testEventMapToEvaluationExpected = new HashMap<TestEvent, EvaluationExpected>();

    query = dataSession.createQuery("from TestEvent where testCase = ? and event.eventTypeCode = ?");
    query.setParameter(0, testCase);
    query.setParameter(1, EventType.VACCINATION.getEventTypeCode());
    vaccinationEvents = query.list();
    for (TestEvent vaccinationEvent : vaccinationEvents) {
      EvaluationExpected evaluationExpected = null;
      query = dataSession
          .createQuery("from EvaluationExpected where testCase = ? and author = ? and vaccineGroup = ? and testEvent = ?");
      query.setParameter(0, testCase);
      query.setParameter(1, user);
      query.setParameter(2, vaccineGroup);
      query.setParameter(3, vaccinationEvent);
      List<EvaluationExpected> evaluationExpectedList = query.list();
      if (evaluationExpectedList.size() > 0) {
        evaluationExpected = evaluationExpectedList.get(0);
      } else if (loadTestPanelDefault) {
        query = dataSession
            .createQuery("from TestPanelEvaluation where testPanelCase = ? and evaluationExpected.testCase = ? and evaluationExpected.vaccineGroup =  ? and evaluationExpected.testEvent = ?");
        query.setParameter(0, testPanelCase);
        query.setParameter(1, testCase);
        query.setParameter(2, vaccineGroup);
        query.setParameter(3, vaccinationEvent);
        List<TestPanelEvaluation> testPanelEvaluationList = query.list();
        if (testPanelEvaluationList.size() > 0) {
          evaluationExpected = testPanelEvaluationList.get(0).getEvaluationExpected();
        }
      }

      if (evaluationExpected == null) {
        evaluationExpected = new EvaluationExpected();
        evaluationExpected.setTestCase(testCase);
        evaluationExpected.setAuthor(user);
        evaluationExpected.setTestCase(testCase);
        evaluationExpected.setVaccineGroup(vaccineGroup);
        evaluationExpected.setTestEvent(vaccinationEvent);
      }
      testEventMapToEvaluationExpected.put(vaccinationEvent, evaluationExpected);
    }
  }
}
