package org.tch.ft.manager.writers;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.tch.fc.model.ForecastActual;
import org.tch.fc.model.Software;
import org.tch.fc.model.TestCase;
import org.tch.ft.manager.SoftwareManager;
import org.tch.ft.model.TestPanelForecast;

public class MicrTestResultWriter extends GeneralWriterSupport implements WriterInterface
{

  public void write(PrintWriter out) {
    List<TestPanelForecast> testPanelForecastList = getTestPanelForecastList();

    List<Software> softwareList = user.isAdmin() ? SoftwareManager.getListOfSoftware(dataSession) : SoftwareManager
        .getListOfUnrestrictedSoftware(user, dataSession);
    Set<Software> softwareSet = null;
    if (!user.isAdmin()) {
      softwareSet = new HashSet<Software>(SoftwareManager.getListOfUnrestrictedSoftware(user, dataSession));
    }

    for (TestPanelForecast testPanelForecast : testPanelForecastList) {
      if (vaccineGroup != null && !testPanelForecast.getForecastExpected().getVaccineGroup().equals(vaccineGroup)) {
        continue;
      }
      TestCase testCase = testPanelForecast.getTestPanelCase().getTestCase();
      List<ForecastActual> forecastActualList;
      {
        Query query = dataSession
            .createQuery("from ForecastActual where softwareResult.testCase = ? and vaccineGroup = ? order by softwareResult.runDate desc");
        query.setParameter(0, testCase);
        query.setParameter(1, testPanelForecast.getForecastExpected().getVaccineGroup());
        forecastActualList = query.list();
      }
      out.println("Test Case Number,Patient Name,Patient DOB,Patient Sex,Evaluation Date,Software Label,Vaccine Group Label,Admin Label,Dose Number,Earliest Date,Recommended Date,Due Date,Past Due Date");
      for (ForecastActual forecastActual : forecastActualList) {
        if (softwareSet == null || softwareSet.contains(forecastActual.getSoftwareResult().getSoftware())) {
          out.print(f(testPanelForecast.getTestPanelCase().getTestCaseNumber()));
          out.print(f(testCase.getPatientFirst() + " " + testCase.getPatientLast()));
          out.print(f(testCase.getPatientDob()));
          out.print(f(testCase.getPatientSex()));
          out.print(f(testCase.getEvalDate()));
          out.print(f(forecastActual.getSoftwareResult().getSoftware().getLabel()));
          out.print(f(forecastActual.getVaccineGroup().getLabel()));
          out.print(f(forecastActual.getAdmin() == null ? "" : forecastActual.getAdmin().getLabel()));
          out.print(f(forecastActual.getDoseNumber()));
          out.print(f(forecastActual.getValidDate()));
          out.print(f(forecastActual.getDueDate()));
          out.print(f(forecastActual.getOverdueDate()));
          out.println();
        }
      }
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
