package org.tch.ft.manager;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.tch.ft.model.User;

public class UserManager {
  public static User login(User userCredentials, Session session) {
    userCredentials.setLoggedIn(false);
    String name = userCredentials.getName();
    Query query = session.createQuery("from User where name = ?");
    query.setString(0, name);
    List<User> users = query.list();
    if (users.size() > 0) {
      for (User user : users) {
        if (user.getPassword().equals(userCredentials.getPassword())) {
          user.setLoggedIn(true);
          return user;
        }
      }
    }
    return userCredentials;
  }

}
