package org.tch.ft.manager;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.tch.fc.model.Software;
import org.tch.fc.model.SoftwareSetting;
import org.tch.ft.model.Expert;
import org.tch.ft.model.TaskGroup;
import org.tch.ft.model.User;

public class SoftwareManager {

  public static boolean isSoftwareAccessRestricted(Software software, User user, Session session) {
    boolean restricted = software.isVisibleStatusRestricted();
    Query query = session.createQuery("from TaskGroup where primarySoftware = ?");
    query.setParameter(0, software);
    List<TaskGroup> taskGroupList = query.list();
    for (TaskGroup taskGroup : taskGroupList) {
      query = session.createQuery("from Expert where taskGroup = ? and user = ?");
      query.setParameter(0, taskGroup);
      query.setParameter(1, user);
      List<Expert> expertList = query.list();
      if (expertList.size() > 0) {
        restricted = false;
      }
    }
    return restricted;
  }

  public static boolean canEditSoftwareCompare(Software software, User user, Session session) {
    boolean canEdit = false;
    Query query = session.createQuery("from TaskGroup where primarySoftware = ?");
    query.setParameter(0, software);
    List<TaskGroup> taskGroupList = query.list();
    for (TaskGroup taskGroup : taskGroupList) {
      query = session.createQuery("from Expert where taskGroup = ? and user = ?");
      query.setParameter(0, taskGroup);
      query.setParameter(1, user);
      List<Expert> expertList = query.list();
      if (expertList.size() > 0) {
        canEdit = true;
      }
    }
    return canEdit;

  }

  public static void initSoftware(Software software, Session session) {
    if (software != null) {
      Query query = session.createQuery("from SoftwareSetting where software = ?");
      query.setParameter(0, software);
      List<SoftwareSetting> softwareSettingList = query.list();
      software.setSoftwareSettingList(softwareSettingList);
    }
  }
}
