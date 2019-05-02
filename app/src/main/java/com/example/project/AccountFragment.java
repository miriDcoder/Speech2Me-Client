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

import java.util.ArrayList;

public class AccountFragment extends Fragment {
    DataBase tempDb = new DataBase();
    private User mUser;

    public AccountFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);
        final EditText editTextFirstName = (EditText)v.findViewById(R.id.editTextAccountFirstName);
        final EditText editTextLastName = (EditText)v.findViewById(R.id.editTextAccountLastName);
        final EditText editTextEmail = (EditText)v.findViewById(R.id.editTextAccountMail);
        EditText editTextCity = (EditText)v.findViewById(R.id.editTextAccountCity);
        final EditText editTextCurrPassword = (EditText)v.findViewById(R.id.editTextAccountCurrPassword);
        final EditText editTextNewPassword = (EditText)v.findViewById(R.id.editTextAccountNewPassword);
        final EditText editTextNewPasswordAgain = (EditText)v.findViewById(R.id.editTextAccountNewPasswordAgain);
        Button buttonSaveChanges = (Button)v.findViewById(R.id.buttonSaveChanges);
        //-----------------NEED TO CHANGE AFTER CONNECTING REAL DB-------------
        ArrayList<User> users = tempDb.makeUserList();
        mUser = users.get(3);
    //----------------------------------------------------------------------
        updateFieldsFromDb(editTextFirstName, editTextLastName, editTextEmail, editTextCity);
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start;i < end;i++) {
                    if (!Character.isLetter(source.charAt(i)) &&
                            !Character.toString(source.charAt(i)).equals("-") &&
                            !Character.toString(source.charAt(i)).matches("[\\u0590-\\u05ff]"))
                    {
                        //display toast
                        return "";
                    }
                }

                return null;
            }
        };
        editTextFirstName.setFilters(new InputFilter[] {filter});
        editTextLastName.setFilters(new InputFilter[] {filter});
        editTextCity.setFilters(new InputFilter[] {filter});

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
                boolean isChangePassword = false;
                String currPass = editTextCurrPassword.getText().toString();
                if(!currPass.isEmpty())
                {
                    int compareCurrPass = currPass.compareTo(mUser.getmPassword());
                    if(compareCurrPass != 0)
                    {
                        messageToUser("ססמה נוכחית שגויה.");
                    }
                    else {
                        String newPass = editTextNewPassword.getText().toString();
                        String newPassAgain = editTextNewPasswordAgain.getText().toString();
                        int compare = newPass.compareTo(newPassAgain);
                        if (!newPass.isEmpty() && !newPassAgain.isEmpty() && compare == 0) {
                            //TODO: update new password in database
                        } else {
                            messageToUser("ססמה חדשה שגויה. יש להזין את אותה הססמה בשדות המתאימים.");
                        }
                    }
                }

                //TODO: UPDATE FIELDS IN DB
            }
        });

        return v;
    }

    private void updateFieldsFromDb(EditText iFirstName, EditText iLastName, EditText iEmail, EditText iCity)
    {
        iFirstName.setText(mUser.getmFirstName());
        iLastName.setText(mUser.getmLastName());
        iEmail.setText(mUser.getmEmail());
        if(!mUser.getmCity().isEmpty())
        {
            iCity.setText(mUser.getmCity());
        }
    }

    private void messageToUser(CharSequence text)
    {
        Context context = getActivity();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
