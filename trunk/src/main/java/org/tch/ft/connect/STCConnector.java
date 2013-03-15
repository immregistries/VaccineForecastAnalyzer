package org.tch.ft.connect;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tch.ft.model.EventType;
import org.tch.ft.model.ForecastActual;
import org.tch.ft.model.ForecastItem;
import org.tch.ft.model.Software;
import org.tch.ft.model.TestCase;
import org.tch.ft.model.TestEvent;

import com.stchome.saf.common.GenderType;
import com.stchome.saf.common.VaccMessageType;
import com.stchome.saf.common.VaccineCodeType;
import com.stchome.saf.messages.get._1_2.ForecastDetailsType;
import com.stchome.saf.messages.get._1_2.GetForecastRequestSoap11Stub;
import com.stchome.saf.messages.get._1_2.PersonDetailsType;
import com.stchome.saf.messages.get._1_2.ResponseDetailType;
import com.stchome.saf.messages.get._1_2.VaccinationType;

public class STCConnector extends GetForecastRequestSoap11Stub implements ConnectorInterface {

  private Map<String, ForecastItem> familyMapping = new HashMap<String, ForecastItem>();
  private Map<String, String> notSupported = new HashMap<String, String>();

  private Software software = null;

  public STCConnector(Software software, List<ForecastItem> forecastItemList) throws Exception {
    super(new URL(software.getServiceUrl()), null);
    this.software = software;
    addForcastItem(forecastItemList, "1", 2); // DTaP
    addForcastItem(forecastItemList, "2", 6); // Hib
    addForcastItem(forecastItemList, "3", 11); // IPV
    addForcastItem(forecastItemList, "4", 5); // HepB
    addForcastItem(forecastItemList, "5", 9); // MMR
    addForcastItem(forecastItemList, "6", 13); // Var
    addForcastItem(forecastItemList, "7", 8); // MCV4
    addForcastItem(forecastItemList, "9", 4); // HepA
    addForcastItem(forecastItemList, "10", 3); // Influenza
    addForcastItem(forecastItemList, "11", 10); // PCV13
    notSupported.put("12", "Hep-B 2 Dose");
    notSupported.put("13", "Anthrax");
    notSupported.put("14", "Smallpox shot/reading");
    notSupported.put("15", "Measles");
    notSupported.put("16", "Mumps");
    notSupported.put("17", "Rubella");
    notSupported.put("18", "Pneumo PPSV");
    addForcastItem(forecastItemList, "20", 12); // Rota
    addForcastItem(forecastItemList, "21", 7); // HPV
    notSupported.put("22", "Herpes Zoster");
    notSupported.put("23", "Novel Influenza H1N1");
    notSupported.put("24", "Tdap");
  }

  private void addForcastItem(List<ForecastItem> forecastItemList, String familyName, int forecastItemId) {
    for (ForecastItem forecastItem : forecastItemList) {
      if (forecastItem.getForecastItemId() == forecastItemId) {
        familyMapping.put(familyName, forecastItem);
        return;
      }
    }
  }

  public List<ForecastActual> queryForForecast(TestCase testCase) throws Exception {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    StringWriter sw = new StringWriter();
    PrintWriter logOut = new PrintWriter(sw);

    logOut.println("STC Forecaster");
    logOut.println();
    logOut.println("Current time " + new Date());
    logOut.println("Connecting to " + software.getServiceUrl());
    PersonDetailsType personDetailsType = new PersonDetailsType();
    personDetailsType.setDateOfBirth(sdf.format(testCase.getPatientDob()));
    personDetailsType.setGender("M".equals(testCase.getPatientSex()) ? GenderType.M : GenderType.F);
    personDetailsType.setPersonId(testCase.getTestCaseNumber());
    // sdf.format(testCase.getEvalDate())
    List<VaccinationType> vaccinationTypeList = new ArrayList<VaccinationType>();
    for (TestEvent testEvent : testCase.getTestEventList()) {
      if (testEvent.getEvent().getEventType() == EventType.VACCINE) {
        VaccinationType vaccinationType = new VaccinationType();
        String cvxCode = testEvent.getEvent().getVaccineCvx();
        if (cvxCode.startsWith("0")) {
          cvxCode = cvxCode.substring(1);
        }
        vaccinationType.setVaccCode(cvxCode);
        vaccinationType.setVaccDate(sdf.format(testEvent.getEventDate()));
        vaccinationType.setDoseSize(new BigDecimal(1.0));
        vaccinationType.setCodeType(VaccineCodeType.CVX);
        vaccinationTypeList.add(vaccinationType);
      }
    }
    personDetailsType.setVaccination(vaccinationTypeList.toArray(new VaccinationType[] {}));

    List<ForecastActual> list = new ArrayList<ForecastActual>();
    ResponseDetailType[] responseDetailTypes = getForecast(new PersonDetailsType[] { personDetailsType });
    if (responseDetailTypes.length == 0) {
      logOut.println("No results returned!");
    } else {
      ResponseDetailType responseDetailType = responseDetailTypes[0];
      personDetailsType = responseDetailType.getPersonDetails();
      logOut.println("Results returned");
      logOut.println("Information: " + responseDetailType.getInformation());

      if (personDetailsType.getVaccination() == null) {
        logOut.println("No vaccinations");
      } else {
        for (VaccinationType vaccinationType : personDetailsType.getVaccination()) {
          logOut.println("Vacc: " + vaccinationType.getVaccCode() + " given " + vaccinationType.getVaccDate());
          if (vaccinationType.getMessage() != null) {
            for (VaccMessageType message : vaccinationType.getMessage()) {
              logOut.println("  + " + message.getMessage() + " (" + message.getMessageType() + ")");
            }
          }
        }
      }

      for (ForecastDetailsType forecastDetailsType : responseDetailType.getForecastDetails()) {

        ForecastItem forecastItem = familyMapping.get(forecastDetailsType.getFamilyCode());
        if (forecastItem == null) {
          String label = notSupported.get(forecastDetailsType.getFamilyCode());
          if (label != null) {
            logOut.println("Unsupported family code " + forecastDetailsType.getFamilyCode() + " (" + label + ")");
          } else {
            logOut.println("Unrecognized family code " + forecastDetailsType.getFamilyCode() + " ");
          }
          logOut.println(" + Dose:     " + forecastDetailsType.getDoseNumber());
          logOut.println(" + Due:      " + forecastDetailsType.getRecommendedDate());
          logOut.println(" + Valid:    " + forecastDetailsType.getMinAllowableDate());
          logOut.println(" + Overdue:  " + forecastDetailsType.getPastDueDate());
          logOut.println(" + Finished: " + forecastDetailsType.getMaxAllowableDate());
        }
        if (forecastItem != null) {
          ForecastActual forecastActual = new ForecastActual();
          forecastActual.setForecastItem(forecastItem);
          forecastActual.setDoseNumber("" + forecastDetailsType.getDoseNumber());
          forecastActual.setDueDate(parseDate(forecastDetailsType.getRecommendedDate()));
          forecastActual.setValidDate(parseDate(forecastDetailsType.getMinAllowableDate()));
          forecastActual.setOverdueDate(parseDate(forecastDetailsType.getPastDueDate()));
          forecastActual.setFinishedDate(parseDate(forecastDetailsType.getMaxAllowableDate()));
          list.add(forecastActual);
        }
      }
    }

    logOut.close();
    for (ForecastActual forecastActual : list) {
      forecastActual.setLogText(sw.toString());
    }
    return list;
  }

  private Date parseDate(String s) {
    Date date = null;
    if (s == null) {
      return null;
    }

    if (s.length() > 0) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      try {
        date = sdf.parse(s);
      } catch (ParseException pe) {
        // ignore
      }
    }
    return date;
  }

}
