package org.tch.ft.manager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AgeUtil {
  public static String getAge(Date dateOfBirth, Date eventDate) {
    if (dateOfBirth.equals(eventDate)) {
      return "birth";
    }
    Calendar age = Calendar.getInstance();
    age.setTime(dateOfBirth);
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    for (int y = 0; y < 5; y++) {
      for (int i = 0; i < 12; i++) {
        age.add(Calendar.MONTH, 1);
        if (age.getTime().after(eventDate)) {
          if (y == 0) {
            return i + " month" + (i == 1 ? "" : "s");
          } else {
            if (i == 0) {
              return y + " year" + (y == 1 ? "" : "s");
            }
            return y + " year" + (y == 1 ? " " : "s ") + i + " month" + (i == 1 ? "" : "s");
          }
        }
      }
    }
    int y = 4;
    while (!age.getTime().after(eventDate)) {
      age.add(Calendar.YEAR, 1);
      y++;
    }
    return y + " years";
  }
}
