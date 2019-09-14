package com.example.project;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

//This page shows the user's details and allows editing those details
public class AccountFragment extends Fragment {
    private String userId;
    private String userType;
    private String prevFirstName;
    private String prevLastName;
    private String prevPassword;
    private String newPassword;
    private String newPasswordAgain;
    private String prevEmail;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextEmail;
    private EditText editTextCurrPassword;
    private EditText editTextNewPassword;
    private EditText editTextNewPasswordAgain;
    private Button buttonSaveChanges;

    public AccountFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);
        editTextFirstName = (EditText)v.findViewById(R.id.editTextAccountFirstName);
        editTextLastName = (EditText)v.findViewById(R.id.editTextAccountLastName);
        editTextEmail = (EditText)v.findViewById(R.id.editTextAccountMail);
        editTextCurrPassword = (EditText)v.findViewById(R.id.editTextAccountCurrPassword);
        editTextNewPassword = (EditText)v.findViewById(R.id.editTextAccountNewPassword);
        editTextNewPasswordAgain = (EditText)v.findViewById(R.id.editTextAccountNewPasswordAgain);
        buttonSaveChanges = (Button)v.findViewById(R.id.buttonSaveChanges);
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start;i < end;i++) {
                    if (!Character.isLetter(source.charAt(i)) &&
                            !Character.toString(source.charAt(i)).equals("-") &&
                            !Character.toString(source.charAt(i)).matches("[\\u0590-\\u05ff]"))
                    {
                        return "";
                    }
                }

                return null;
            }
        };
        editTextFirstName.setFilters(new InputFilter[] {filter});
        editTextLastName.setFilters(new InputFilter[] {filter});
        if(getArguments() != null)
        {
            userId = getArguments().getString("user_id");
            userType = getArguments().getString("user_type");
            getUserDetails(false);
        }

        editTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    if(!AppUtils.IsValidMail(editTextEmail.getText().toString()))
                    {
                        editTextEmail.setText("");
                        messageToUser("אנא הזן מייל תקין");
                    }
                }
            }
        });

        buttonSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSendChanges();
            }
        });

        return v;
    }

    //this function shows the user a message
    private void messageToUser(CharSequence text)
    {
        Context context = getActivity();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    //when entering the fragment, we get the relevant user details to display
    private void getUserDetails(final boolean isNewDetails)
    {
        String url = "https://speech-rec-server.herokuapp.com/get_user/";
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id", userId);
            final RequestQueue queue = Volley.newRequestQueue(this.getContext());
            final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.has("first_name") &&
                                    response.has("last_name") &&
                                    response.has("email"))
                                {
                                    prevFirstName = response.getString("first_name");
                                    prevLastName = response.getString("last_name");
                                    prevEmail = response.getString("email");
                                    showDetails(isNewDetails);
                                }
                                else{
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },  new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            queue.add(jsonRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //displaying the details we've received from the server
    private void showDetails(boolean iIsNewDetails)
    {
        editTextFirstName.setText(prevFirstName);
        editTextLastName.setText(prevLastName);
        editTextEmail.setText(prevEmail);
        buttonSaveChanges.setEnabled(true);
        if(iIsNewDetails){
            messageToUser("הפרטים עודכנו בהצלחה");
        }
    }

    //in case the user wants to change some of his info, we need to validate some details-
    //if he wants to change the password - need to make sure that the new password he entered twice matches
    //and need to check what fields he wanted to update.
    //then we send the request to the server.
    private void validateAndSendChanges()
    {
        boolean isSendToServer = false;
        boolean isFirstNameChanged = false;
        boolean isLastNameChanged = false;
        boolean isWantToChangePassword = false;
        String currFirstName = editTextFirstName.getText().toString();
        String currLastName = editTextLastName.getText().toString();

        prevPassword = editTextCurrPassword.getText().toString();
        if(!prevFirstName.equals(currFirstName)){
            isFirstNameChanged = true;
            isSendToServer = true;
        }

        if(!prevLastName.equals(currLastName)){
            isLastNameChanged = true;
            isSendToServer = true;
        }

        if(!prevPassword.isEmpty()) {
            newPassword = editTextNewPassword.getText().toString();
            newPasswordAgain = editTextNewPasswordAgain.getText().toString();
            if (newPassword.isEmpty() || newPasswordAgain.isEmpty() || !newPassword.equals(newPasswordAgain)) {
                messageToUser("אין התאמה בין השדות של הסיסמה החדשה");
                isSendToServer = false;
            }
            else{
                isWantToChangePassword = true;
                isSendToServer = true;
            }
        }

        if(isSendToServer){
            String url = "https://speech-rec-server.herokuapp.com/user_update/";
            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("user_id", userId);
                jsonBody.put("user_type", userType);
                if(isFirstNameChanged){
                    jsonBody.put("first_name", currFirstName);
                }
                if(isLastNameChanged){
                    jsonBody.put("last_name", currLastName);
                }
                if(isWantToChangePassword){
                    jsonBody.put("old_password", prevPassword);
                    jsonBody.put("new_password", newPassword);
                }
                final RequestQueue queue = Volley.newRequestQueue(this.getContext());
                final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                buttonSaveChanges.setEnabled(true);
                                try {
                                    if(response.has("body") && response.getString("body").toLowerCase().contains("updated user"))
                                    {
                                        getUserDetails(true);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },  new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        messageToUser(getString(R.string.server_error_saving_details));
                    }
                });
                queue.add(jsonRequest);
                buttonSaveChanges.setEnabled(false);

            } catch (Exception e) {
                e.printStackTrace();
                messageToUser(getString(R.string.server_error_saving_details));
            }
        }
    }
}
