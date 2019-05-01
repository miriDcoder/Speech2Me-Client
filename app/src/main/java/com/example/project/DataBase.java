package com.example.project;

import java.util.ArrayList;


public class DataBase {
    ArrayList<User> users = new ArrayList<User>();

    public ArrayList<User> makeUserList() {
        users.add(new Teacher("shula@gmail.com", "1234", "שולה", "כץ", "Rehovot", "1001"));
        users.add(new Teacher("dana@gmail.com", "1234", "Dana", "Levi", "Tel Aviv", "1002"));

        users.add(new Student("dan@gmail.com", "1234", "Dan", "Gan", "Rehovot", "0001", 1, "12", 0, "1001"));
        users.add(new Student("ofer@gmail.com", "1234", "Ofer", "Feder", "Rehovot", "0002", 1, "12", 0, "1001"));
        users.add(new Student("miri@gmail.com", "1234", "מירי", "לוי", "Rehovot", "0003", 1, "11", 0, "1001"));
        users.add(new Student("daniel@gmail.com", "1234", "Daniel", "Levinson", "Tel Aviv", "0004", 1, "14", 0, "1002"));
        users.add(new Student("roni@gmail.com", "1234", "Roni", "Shaham", "Tel Aviv", "0005", 1, "15", 0, "1002"));
        users.add(new Student("gal@gmail.com", "1234", "Gal", "Cohen", "Tel Aviv", "0006", 1, "22", 0, "1002"));
        ((Teacher) users.get(0)).getmStudents().add((Student) users.get(2));
        ((Teacher) users.get(0)).getmStudents().add((Student) users.get(3));
        ((Teacher) users.get(0)).getmStudents().add((Student) users.get(4));
        ((Teacher) users.get(1)).getmStudents().add((Student) users.get(5));
        ((Teacher) users.get(1)).getmStudents().add((Student) users.get(6));
        ((Teacher) users.get(1)).getmStudents().add((Student) users.get(7));
        ((Teacher) users.get(0)).setmNumOfStudents(3);
        ((Teacher) users.get(1)).setmNumOfStudents(3);
        return users;
    }
}
