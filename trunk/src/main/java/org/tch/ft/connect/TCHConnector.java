package org.tch.ft.connect;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
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
import org.tch.ft.model.ForecastResult;
import org.tch.ft.model.Software;
import org.tch.ft.model.TestCase;
import org.tch.ft.model.TestEvent;

public class TCHConnector implements ConnectorInterface {

  private Map<String, ForecastItem> familyMapping = new HashMap<String, ForecastItem>();

  private Software software = null;

  public TCHConnector(Software software, List<ForecastItem> forecastItemList) {
    this.software = software;
    addForcastItem(forecastItemList, "Hib", 6);
    addForcastItem(forecastItemList, "HepB", 5);
    addForcastItem(forecastItemList, "DTaP", 2);
    addForcastItem(forecastItemList, "Td", 15);
    addForcastItem(forecastItemList, "Tdap", 15);
    addForcastItem(forecastItemList, "IPV", 11);
    addForcastItem(forecastItemList, "HepA", 4);
    addForcastItem(forecastItemList, "MMR", 9);
    addForcastItem(forecastItemList, "Var", 13);
    addForcastItem(forecastItemList, "Influenza", 3);
    addForcastItem(forecastItemList, "MCV4", 8);
    addForcastItem(forecastItemList, "HPV", 7);
    addForcastItem(forecastItemList, "Rota", 12);
    addForcastItem(forecastItemList, "PCV13", 10);
    addForcastItem(forecastItemList, "Zoster", 14);
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
    
    StringWriter sw = new StringWriter();
    PrintWriter logOut = new PrintWriter(sw);
    StringBuilder sb = new StringBuilder();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    sb.append("?evalDate=" + sdf.format(testCase.getEvalDate()));
    sb.append("&evalSchedule=");
    sb.append("&resultFormat=text");
    sb.append("&patientDob=" + sdf.format(testCase.getPatientDob()) + "");
    sb.append("&patientSex=" + testCase.getPatientSex() + "");
    int count = 0;
    for (TestEvent testEvent : testCase.getTestEventList()) {
      if (testEvent.getEvent().getEventType() == EventType.VACCINE) {
        count++;
        sb.append("&vaccineDate" + count + "=" + sdf.format(testEvent.getEventDate()));
        sb.append("&vaccineCvx" + count + "=" + testEvent.getEvent().getVaccineCvx());
        sb.append("&vaccineMvx" + count + "=" + testEvent.getEvent().getVaccineMvx());
      }
    }
    logOut.println("TCH Forecaster");
    logOut.println();
    logOut.println("Current time " + new Date());
    logOut.println("Connecting to " + software.getServiceUrl());
    logOut.println("Query " + software.getServiceUrl() + sb.toString());
    logOut.println();
    URLConnection urlConn;
    URL url = new URL(software.getServiceUrl() + sb.toString());
    urlConn = url.openConnection();
    urlConn.setDoInput(true);
    urlConn.setDoOutput(true);
    urlConn.setUseCaches(true);
    urlConn.setRequestProperty("Content-Type", "text/xml; charset=\"utf-8\"");
    urlConn.connect();

    InputStreamReader input = null;
    input = new InputStreamReader(urlConn.getInputStream());
    List<ForecastActual> list = new ArrayList<ForecastActual>();
    BufferedReader in = new BufferedReader(input);
    String line;
    logOut.println("Results:");
    while ((line = in.readLine()) != null) {
      logOut.println(line);
      line = line.trim();
      if (line.startsWith("Forecasting ")) {
        // Example lines
        // 0 1 2 3 4 5 6 7 8 9 10 11
        // Forecasting MMR dose 1 due 05/01/2066 valid 04/29/2006 overdue
        // 04/29/2006 finished 10/05/2009
        // Forecasting Hib complete
        String[] parts = line.split("\\s");
        if (parts.length > 2) {
          ForecastItem forecastItem = familyMapping.get(parts[1]);
          if (forecastItem != null) {
            ForecastActual forecastActual = new ForecastActual();
            forecastActual.setForecastItem(forecastItem);
            if ("complete".equalsIgnoreCase(parts[2])) {
              forecastActual.setDoseNumber(ForecastResult.DOSE_NUMBER_COMPLETE);
            } else {
              if (parts.length > 3 && "dose".equals(parts[2])) {
                forecastActual.setDoseNumber(parts[3]);
              }
              if (parts.length > 5 && "due".equals(parts[4])) {
                forecastActual.setDueDate(parseDate(parts[5]));
              }
              if (parts.length > 7 && "valid".equals(parts[6])) {
                forecastActual.setValidDate(parseDate(parts[7]));
              }
              if (parts.length > 9 && "overdue".equals(parts[8])) {
                forecastActual.setOverdueDate(parseDate(parts[9]));
              }
              if (parts.length > 11 && "finished".equals(parts[10])) {
                forecastActual.setFinishedDate(parseDate(parts[11]));
              }
            }
            list.add(forecastActual);
          }
        }
      }
    }
    input.close();
    logOut.close();
    for (ForecastActual forecastActual : list)
    {
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
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
      try {
        date = sdf.parse(s);
      } catch (ParseException pe) {
        // ignore
      }
    }
    return date;
  }

}
