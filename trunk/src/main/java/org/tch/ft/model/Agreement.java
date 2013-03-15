package org.tch.ft.model;

import java.io.Serializable;
import java.util.Date;

public class Agreement implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  private int agreementId = 0;
  private Date versionDate = null;
  private String agreementText = "";

  public int getAgreementId() {
    return agreementId;
  }

  public void setAgreementId(int agreementId) {
    this.agreementId = agreementId;
  }

  public Date getVersionDate() {
    return versionDate;
  }

  public void setVersionDate(Date versionDate) {
    this.versionDate = versionDate;
  }

  public String getAgreementText() {
    return agreementText;
  }

  public void setAgreementText(String agreementText) {
    this.agreementText = agreementText;
  }
}
