package org.immregistries.vfa.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.immregistries.vfa.connect.model.Software;
import org.immregistries.vfa.connect.model.TestCase;
import org.immregistries.vfa.manager.ForecastActualExpectedCompare;

public class User implements Serializable
{

  private static final long serialVersionUID = 1L;

  private int userId = 0;
  private String name = "";
  private String email = "";
  private String password = "";
  private String organization = "";
  private String position = "";
  private String phone = "";
  private boolean loggedIn = false;
  private boolean admin = false;
  private boolean memberOfGroup = false;
  private boolean canEditTestCase = false;
  private boolean canViewPrivate = false;
  private Agreement agreement = null;
  private Date agreementDate = null;
  private TaskGroup selectedTaskGroup = null;
  private TestPanel selectedTestPanel = null;
  private Software selectedSoftware = null;
  private TestPanelCase selectedTestPanelCase = null;
  private TestCase selectedTestCase = null;
  private SoftwareCompare selectedSoftwareCompare = null;
  private String selectedCategoryName = null;
  private boolean selectedCategoryNameExcluded = false;
  private boolean agreedToAgreement = false;
  private Expert selectedExpert = null;
  private List<Expert> expertList = null;

  public boolean isCanViewPrivate() {
    return canViewPrivate;
  }
  
  public void setCanViewPrivate(boolean canViewPrivate) {
    this.canViewPrivate = canViewPrivate;
  }
  
  public boolean isSelectedCategoryNameExcluded() {
    return selectedCategoryNameExcluded;
  }

  public void setSelectedCategoryNameExcluded(boolean selectedCategoryNameExcluded) {
    this.selectedCategoryNameExcluded = selectedCategoryNameExcluded;
  }

  public boolean isExpertOrAdmin(TaskGroup taskGroup) {
    for (Expert expert : expertList) {
      if (expert.getTaskGroup().equals(taskGroup)) {
        return expert.getRole() == Role.ADMIN || expert.getRole() == Role.EXPERT;
      }
    }
    return false;
  }

  public List<Expert> getExpertList() {
    return expertList;
  }

  public void setExpertList(List<Expert> expertList) {
    this.expertList = expertList;
  }

  public Expert getSelectedExpert() {
    return selectedExpert;
  }

  public void setSelectedExpert(Expert selectedExpert) {
    this.selectedExpert = selectedExpert;
  }

  public boolean isCanEditTestCase() {
    return canEditTestCase;
  }

  public void setCanEditTestCase(boolean canEditTestCase) {
    this.canEditTestCase = canEditTestCase;
  }

  public String getSelectedCategoryName() {
    return selectedCategoryName;
  }

  public void setSelectedCategoryName(String selectedCategoryName) {
    this.selectedCategoryName = selectedCategoryName;
  }

  public boolean isMemberOfGroup() {
    return memberOfGroup;
  }

  public void setMemberOfGroup(boolean memberOfGroup) {
    this.memberOfGroup = memberOfGroup;
  }

  public boolean isAdmin() {
    return admin;
  }

  public void setAdmin(boolean admin) {
    this.admin = admin;
  }

  public boolean isAgreedToAgreement() {
    return agreedToAgreement;
  }

  public void setAgreedToAgreement(boolean agreedToAgreement) {
    this.agreedToAgreement = agreedToAgreement;
  }

  public TaskGroup getSelectedTaskGroup() {
    return selectedTaskGroup;
  }

  public void setSelectedTaskGroup(TaskGroup selectedTaskGroup) {
    this.selectedTaskGroup = selectedTaskGroup;
  }

  public TestPanel getSelectedTestPanel() {
    return selectedTestPanel;
  }

  public void setSelectedTestPanel(TestPanel selectedTestPanel) {
    this.selectedTestPanel = selectedTestPanel;
  }

  public Software getSelectedSoftware() {
    return selectedSoftware;
  }

  public void setSelectedSoftware(Software selectedSoftware) {
    this.selectedSoftware = selectedSoftware;
  }

  public TestPanelCase getSelectedTestPanelCase() {
    return selectedTestPanelCase;
  }

  public void setSelectedTestPanelCase(TestPanelCase selectedTestPanelCase) {
    this.selectedTestPanelCase = selectedTestPanelCase;
  }

  public TestCase getSelectedTestCase() {
    return selectedTestCase;
  }

  public void setSelectedTestCase(TestCase seletectedTestCase) {
    this.selectedTestCase = seletectedTestCase;
  }

  public Agreement getAgreement() {
    return agreement;
  }

  public void setAgreement(Agreement agreement) {
    this.agreement = agreement;
  }

  public Date getAgreementDate() {
    return agreementDate;
  }

  public void setAgreementDate(Date agreementDate) {
    this.agreementDate = agreementDate;
  }

  public boolean isLoggedIn() {
    return loggedIn;
  }

  public void setLoggedIn(boolean loggedIn) {
    this.loggedIn = loggedIn;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getOrganization() {
    return organization;
  }

  public void setOrganization(String organization) {
    this.organization = organization;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public SoftwareCompare getSelectedSoftwareCompare() {
    return selectedSoftwareCompare;
  }

  public void setSelectedSoftwareCompare(SoftwareCompare selectedSoftwareCompare) {
    this.selectedSoftwareCompare = selectedSoftwareCompare;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof User) {
      User u = (User) obj;
      return u.getUserId() == this.getUserId();
    }
    return super.equals(obj);
  }

  @Override
  public String toString() {
    return name;
  }
}
