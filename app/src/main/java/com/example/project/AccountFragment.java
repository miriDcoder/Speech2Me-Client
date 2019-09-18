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

import java.io.UnsupportedEncodingException;

//This page shows the user's details and allows editing those details
public class AccountFragment extends Fragment {
    private String mUserId;
    private String mUserType;
    private String mPrevFirstName;
    private String mPrevLastName;
    private String mPrevPassword;
    private String mNewPassword;
    private String mNewPasswordAgain;
    private String mPrevEmail;
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
            System.out.println("getArguments not null");
            mUserId = getArguments().getString("user_id");
            mUserType = getArguments().getString("user_type");
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
            jsonBody.put("user_id", mUserId);
            final RequestQueue queue = Volley.newRequestQueue(this.getContext());
            final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
//                                if(response.has("first_name") &&
//                                        response.has("last_name") &&
//                                        response.has("email"))
//                                {
                                    System.out.println(response);
                                    mPrevFirstName = response.getString("first_name");
                                    mPrevLastName = response.getString("last_name");
                                    mPrevEmail = response.getString("email");
                                    showDetails(isNewDetails);
                                //}
                            } catch (JSONException e) {
                                System.out.println("In exception of on response");
                                buttonSaveChanges.setText(getString(R.string.save_changes));
                                buttonSaveChanges.setEnabled(true);
                                messageToUser(getString(R.string.error_server_try_later));
                                e.printStackTrace();
                            }
                        }
                    },  new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("In on error response");
                    buttonSaveChanges.setText(getString(R.string.save_changes));
                    buttonSaveChanges.setEnabled(true);
                    parseVolleyError(error);
                }
            });
            queue.add(jsonRequest);
            System.out.println("Sent request");
        } catch (Exception e) {
            System.out.println("In general exception");
            buttonSaveChanges.setText(getString(R.string.save_changes));
            buttonSaveChanges.setEnabled(true);
            messageToUser(getString(R.string.error_server_try_later));
            e.printStackTrace();
        }
    }

    //displaying the details we've received from the server
    private void showDetails(boolean iIsNewDetails)
    {
        editTextFirstName.setText(mPrevFirstName);
        editTextLastName.setText(mPrevLastName);
        editTextEmail.setText(mPrevEmail);
        buttonSaveChanges.setEnabled(true);
        buttonSaveChanges.setText(getString(R.string.save_changes));
        if(iIsNewDetails){
            messageToUser("הפרטים עודכנו בהצלחה");
        }
    }

    //in case the user wants to change some of his info, we need to validate some details-
    //if he wants to change the editTextPassword - need to make sure that the new editTextPassword he entered twice matches
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

        mPrevPassword = editTextCurrPassword.getText().toString();
        if(!mPrevFirstName.equals(currFirstName)){
            isFirstNameChanged = true;
            isSendToServer = true;
        }

        if(!mPrevLastName.equals(currLastName)){
            isLastNameChanged = true;
            isSendToServer = true;
        }

        if(!mPrevPassword.isEmpty()) {
            mNewPassword = editTextNewPassword.getText().toString();
            mNewPasswordAgain = editTextNewPasswordAgain.getText().toString();
            if (mNewPassword.isEmpty() || mNewPasswordAgain.isEmpty() || !mNewPassword.equals(mNewPasswordAgain)) {
                messageToUser("אין התאמה בין השדות של הסיסמה החדשה");
                isSendToServer = false;
            }
            else{
                isWantToChangePassword = true;
                isSendToServer = true;
            }
        }

        if(isSendToServer){
            buttonSaveChanges.setEnabled(false);
            buttonSaveChanges.setText(getString(R.string.saving));
            String url = "https://speech-rec-server.herokuapp.com/user_update/";
            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("user_id", mUserId);
                jsonBody.put("user_type", mUserType);
                if(isFirstNameChanged){
                    jsonBody.put("first_name", currFirstName);
                }
                if(isLastNameChanged){
                    jsonBody.put("last_name", currLastName);
                }
                if(isWantToChangePassword){
                    jsonBody.put("old_password", mPrevPassword);
                    jsonBody.put("new_password", mNewPassword);
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
                                    buttonSaveChanges.setText(getString(R.string.save_changes));
                                    buttonSaveChanges.setEnabled(true);
                                    messageToUser(getString(R.string.server_error_saving_details));
                                    e.printStackTrace();
                                }
                            }
                        },  new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        buttonSaveChanges.setText(getString(R.string.save_changes));
                        buttonSaveChanges.setEnabled(true);
                        parseVolleyError(error);
                    }
                });
                queue.add(jsonRequest);
            } catch (Exception e) {
                e.printStackTrace();
                buttonSaveChanges.setText(getString(R.string.save_changes));
                buttonSaveChanges.setEnabled(true);
                messageToUser(getString(R.string.server_error_saving_details));
            }
        }
    }

    private void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("error");
            translateErrorToMessageForClient(message);
        } catch (JSONException e) {
            messageToUser(getResources().getString(R.string.error_server));
        } catch (UnsupportedEncodingException exceptionError) {
            messageToUser(getResources().getString(R.string.error_server));
        } catch(Exception ex){
            messageToUser(getResources().getString(R.string.error_server));
        }
    }

    private void translateErrorToMessageForClient(String iErrorMsg)
    {
        String message = "";
        switch(iErrorMsg.toLowerCase())
        {
            case "old password does not match new password":
                message = getString(R.string.error_old_password);
                break;
            case "no user type":
                message = getString(R.string.error_user_type);
                break;
            default:
                message = getString(R.string.error_server);
                break;
        }

        messageToUser(message);
    }
}
