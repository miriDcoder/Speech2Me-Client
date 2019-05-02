package com.example.project;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppUtils {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean IsLetters(String str)
    {
        boolean isAllLetters = true;
        char[] letters = str.toCharArray();

        for(char c:letters){
            if(!Character.isLetter(c)){
                isAllLetters = false;
                break;
            }
        }

        return isAllLetters;
    }

    public static boolean IsValidMail(String str)
    {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(str);
        return matcher.find();
    }
}
