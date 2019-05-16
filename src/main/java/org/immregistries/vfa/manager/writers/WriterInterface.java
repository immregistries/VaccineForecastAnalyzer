package org.immregistries.vfa.manager.writers;

import java.io.PrintWriter;
import java.util.Set;

import org.hibernate.Session;
import org.immregistries.vfa.connect.model.Software;
import org.immregistries.vfa.connect.model.VaccineGroup;
import org.immregistries.vfa.model.TestPanel;
import org.immregistries.vfa.model.User;

public interface WriterInterface
{
  public void setDataSession(Session dataSession);

  public void setCategoryNameSet(Set<String> categoryNameSet);

  public void setTestPanel(TestPanel testPanel);

  public void setVaccineGroup(VaccineGroup vaccineGroup);

  public void setUser(User user);

  public void write(PrintWriter out);

  public String createFilename();
  
  public void setSoftwareSet(Set<Software> softwareSet);
}
