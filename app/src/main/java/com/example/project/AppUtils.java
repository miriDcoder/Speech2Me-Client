package com.example.project;

public class AppUtils {

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
}
