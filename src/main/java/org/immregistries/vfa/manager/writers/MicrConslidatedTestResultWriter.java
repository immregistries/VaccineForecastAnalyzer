package org.immregistries.vfa.manager.writers;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.immregistries.vfa.connect.model.ForecastActual;
import org.immregistries.vfa.connect.model.Software;
import org.immregistries.vfa.connect.model.TestCase;
import org.immregistries.vfa.connect.model.VaccineGroup;
import org.immregistries.vfa.manager.SoftwareManager;
import org.immregistries.vfa.model.TestPanelCase;
import org.immregistries.vfa.model.TestPanelForecast;

public class MicrConslidatedTestResultWriter extends GeneralWriterSupport implements WriterInterface {

  private class Row {
    public Map<VaccineGroup, Map<Software, ForecastActual>> map = null;
    TestPanelCase testPanelCase = null;
  }

  public void write(PrintWriter out) {
    List<TestPanelForecast> testPanelForecastList = getTestPanelForecastList();

    Set<Software> softwareSetAccess = null;
    if (softwareSet != null) {
      softwareSetAccess = new HashSet<Software>(softwareSet);
    }
    if (!user.isAdmin()) {
      List<Software> softwareList = SoftwareManager.getListOfUnrestrictedSoftware(user, dataSession);
      if (softwareSetAccess == null) {
        softwareSetAccess = new HashSet<Software>(softwareList);
      } else {
        softwareSetAccess.retainAll(softwareList);
      }
    }

    Set<VaccineGroup> vaccineGroupSet = new HashSet<VaccineGroup>();
    Set<Software> softwareSetToInclude = new HashSet<Software>();
    List<Row> rowList = new ArrayList<Row>();
    {
      Row row = null;
      for (TestPanelForecast testPanelForecast : testPanelForecastList) {
        if (vaccineGroup != null && !testPanelForecast.getForecastExpected().getVaccineGroup().equals(vaccineGroup)) {
          continue;
        }
        TestCase testCase = testPanelForecast.getTestPanelCase().getTestCase();
        if (row == null || !row.testPanelCase.equals(testPanelForecast.getTestPanelCase())) {
          row = new Row();
          Map<VaccineGroup, Map<Software, ForecastActual>> vaccineGroupSoftwareForecastActualMap = new HashMap<VaccineGroup, Map<Software, ForecastActual>>();
          row.map = vaccineGroupSoftwareForecastActualMap;
          row.testPanelCase = testPanelForecast.getTestPanelCase();
          rowList.add(row);
        }
        List<ForecastActual> forecastActualList;
        {
          Query query = dataSession
              .createQuery("from ForecastActual where softwareResult.testCase = ? and vaccineGroup = ? order by softwareResult.runDate desc");
          query.setParameter(0, testCase);
          query.setParameter(1, testPanelForecast.getForecastExpected().getVaccineGroup());
          forecastActualList = query.list();
        }
        for (ForecastActual forecastActual : forecastActualList) {
          if (softwareSetAccess == null || softwareSetAccess.contains(forecastActual.getSoftwareResult().getSoftware())) {
            VaccineGroup vaccineGroup = forecastActual.getVaccineGroup();
            Software software = forecastActual.getSoftwareResult().getSoftware();
            vaccineGroupSet.add(vaccineGroup);
            softwareSetToInclude.add(software);
            Map<Software, ForecastActual> softwareForecastActualMap = row.map.get(vaccineGroup);
            if (softwareForecastActualMap == null) {
              softwareForecastActualMap = new HashMap<Software, ForecastActual>();
              row.map.put(vaccineGroup, softwareForecastActualMap);
            }
            softwareForecastActualMap.put(software, forecastActual);
          }
        }
      }
    }
    List<VaccineGroup> vaccineGroupList = new ArrayList<VaccineGroup>(vaccineGroupSet);
    List<Software> softwareList = new ArrayList<Software>(softwareSetToInclude);
    out.print("Test Case Number,Patient Name,Patient DOB,Patient Sex,Evaluation Date,");
    for (VaccineGroup vaccineGroup : vaccineGroupList) {
      for (Software software : softwareList) {
        String baseLabel = "\"" + vaccineGroup.getLabel() + " - " + software.getLabel() + " - ";
        out.print(baseLabel + "Admin Label\",");
        out.print(baseLabel + "Dose Number\",");
        out.print(baseLabel + "Earliest Date\",");
        out.print(baseLabel + "Recommended Date\",");
        out.print(baseLabel + "Past Due Date\",");
      }
    }
    out.println();
    for (Row row : rowList) {
      TestCase testCase = row.testPanelCase.getTestCase();
      out.print(f(row.testPanelCase.getTestCaseNumber()));
      out.print(f(testCase.getPatientFirst() + " " + testCase.getPatientLast()));
      out.print(f(testCase.getPatientDob()));
      out.print(f(testCase.getPatientSex()));
      out.print(f(testCase.getEvalDate()));
      for (VaccineGroup vaccineGroup : vaccineGroupList) {
        Map<Software, ForecastActual> softwareForecastActualMap = row.map.get(vaccineGroup);
        for (Software software : softwareList) {
          ForecastActual forecastActual = softwareForecastActualMap == null ? null : softwareForecastActualMap.get(software);
          out.print(f(forecastActual == null || forecastActual.getAdmin() == null ? "" : forecastActual.getAdmin().getLabel()));
          out.print(f(forecastActual == null ? "" : forecastActual.getDoseNumber()));
          out.print(f(forecastActual == null ? null : forecastActual.getValidDate()));
          out.print(f(forecastActual == null ? null : forecastActual.getDueDate()));
          out.print(f(forecastActual == null ? null : forecastActual.getOverdueDate()));
        }
      }
      out.println();
    }
  }

  private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

  private String f(Date d) {
    if (d == null) {
      return f("");
    }
    return f(sdf.format(d));
  }

  private String f(String s) {
    if (s == null) {
      s = "";
    }
    return "\"" + s + "\",";
  }

}
