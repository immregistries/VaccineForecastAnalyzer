package org.immregistries.vfa.model;

import java.io.Serializable;
import java.util.Date;

public class UseLog implements Serializable
{
  private int useLogId = 0;
  private Registration registration = null;
  private Module module = null;
  private Date useDate = null;
  private Date startupDate = null;
  private String softwareVersion = "";
  private int usageCount = 0;

  public int getUseLogId() {
    return useLogId;
  }

  public void setUseLogId(int useLogId) {
    this.useLogId = useLogId;
  }

  public Registration getRegistration() {
    return registration;
  }

  public void setRegistration(Registration registration) {
    this.registration = registration;
  }

  public Module getModule() {
    return module;
  }

  public void setModule(Module module) {
    this.module = module;
  }

  public String getModuleType() {
    return module == null ? "" : module.getModuleType();
  }

  public void setModuleType(String moduleType) {
    this.module = Module.getModule(moduleType);
  }

  public Date getUseDate() {
    return useDate;
  }

  public void setUseDate(Date useDate) {
    this.useDate = useDate;
  }

  public Date getStartupDate() {
    return startupDate;
  }

  public void setStartupDate(Date installDate) {
    this.startupDate = installDate;
  }

  public String getSoftwareVersion() {
    return softwareVersion;
  }

  public void setSoftwareVersion(String softwareVersion) {
    this.softwareVersion = softwareVersion;
  }

  public int getUsageCount() {
    return usageCount;
  }

  public void setUsageCount(int usageCount) {
    this.usageCount = usageCount;
  }
}
