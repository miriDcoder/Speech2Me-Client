package com.example.project;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

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
    public String res;

    public static User GetUserByMail(ArrayList<User> iUsers, String iEmail)
    {
        User userFound = null;

        for(User user : iUsers)
        {
            if(iEmail.equals(user.getmEmail()))
            {
                userFound = user;
                break;
            }
        }
        return userFound;
    }

    public static User GetUserById(ArrayList<User> iUsers, String iId)
    {
        User userFound = null;

        for(User user : iUsers)
        {
            if(iId.equals(user.getmId()))
            {
                userFound = user;
                break;
            }
        }
        return userFound;
    }

    public static void setScore(ArrayList<User> iUsers, String iId, int iScore){
        {
            User userFound = null;

            for(User user : iUsers)
            {
                if(iId.equals(user.getmId()))
                {
                    Student currStudent = (Student)user;
                    currStudent.setmScore(iScore);
                }
            }
        }
    }


    public static void InsertNewStudentToDataBase(User iUser, Context iContext)
    {
        try {
            JSONObject request = new JSONObject(new Gson().toJson(iUser, User.class));
            System.out.print(request);
            RequestQueue queue = Volley.newRequestQueue(iContext);
            String url ="http://10.100.102.19:8000/user_signup/";
// Request a string response from the provided URL.
            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, request,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Display the first 500 characters of the response string.
                            //textViewRequest.setText(response.substring(0,500));
                            //System.out.print(response.toString());
                            //iEditText.setText(response.toString());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print("ERROR!");
                }
            });
            queue.add(stringRequest);
            queue.start();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //System.out.print(reques);
        //JSONparser json = (JSONObject)parser.parse(request);

    }
}
