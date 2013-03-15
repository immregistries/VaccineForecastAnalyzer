package org.tch.ft.manager;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.tch.ft.model.Expert;
import org.tch.ft.model.Software;
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
  
  public static boolean canEditSoftwareCompare(Software software, User user, Session session)
  {
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
}
