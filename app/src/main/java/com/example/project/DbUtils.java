package com.example.project;

import java.util.ArrayList;

public class DbUtils {

    /*
    TODO:
    1. insert to db- all functions retrieves the id they got from db
    1.1 teacher
    1.2 student
    1.3 student to teacher
    2. check if given teacher id is in db
    */
    public static User GetUserByMail(ArrayList<User> iUsers, String iEmail)
    {
        User userFound = null;

        for(User user : iUsers)
        {
            if(iEmail == user.getmEmail())
            {
                userFound = user;
                break;
            }
        }

        return userFound;
    }
}
