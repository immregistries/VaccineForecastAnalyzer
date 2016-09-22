package org.tch.ft.manager.writers;

import java.io.PrintWriter;
import java.util.Set;

import org.hibernate.Session;
import org.tch.fc.model.Software;
import org.tch.fc.model.VaccineGroup;
import org.tch.ft.model.TestPanel;
import org.tch.ft.model.User;

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
