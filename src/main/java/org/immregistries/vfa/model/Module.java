package org.immregistries.vfa.model;

import java.io.Serializable;

public enum Module implements Serializable {
  API("A", "API"), IHS("I", "IHS"), WEB_ACCESS("W", "Web Access");
  private String moduleType = "";
  private String label = "";

  public String getModuleType() {
    return moduleType;
  }

  public void setModuleType(String moduleType) {
    this.moduleType = moduleType;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  private Module(String moduleType, String label) {
    this.moduleType = moduleType;
    this.label = label;
  }

  public static Module getModule(String moduleType) {
    for (Module module : Module.values()) {
      if (module.getModuleType().equalsIgnoreCase(moduleType)) {
        return module;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return label;
  }
}
