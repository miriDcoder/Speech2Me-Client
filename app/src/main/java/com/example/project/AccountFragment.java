package com.example.project;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        EditText editTextLastName = (EditText)v.findViewById(R.id.editTextAccountLastName);
        EditText editTextEmail = (EditText)v.findViewById(R.id.editTextAccountMail);
        EditText editTextCity = (EditText)v.findViewById(R.id.editTextAccountCity);
        //-----------------NEED TO CHANGE AFTER CONNECTING REAL DB-------------
        ArrayList<User> users = tempDb.makeUserList();
        mUser = users.get(3);
    //----------------------------------------------------------------------
    updateFieldsFromDb(editTextFirstName, editTextLastName, editTextEmail, editTextCity);
        /*editTextFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    if(!AppUtils.IsLetters(editTextFirstName.getText().toString()))
                    {
                        editTextFirstName.setText("");
                        messageToUser("אנא הזן שם תקין");
                    }
                }
            }
        });*/

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
//
//    private void messageToUser(CharSequence text)
//    {
//        Context context = getApplicationContext();
//        int duration = Toast.LENGTH_SHORT;
//        Toast toast = Toast.makeText(context, text, duration);
//        toast.show();
//    }

    /*TextWatcher NameValidatorTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (filterLongEnough()) {
                populateList();
            }
        }

        private boolean filterLongEnough() {
            return tv_filter.getText().toString().trim().length() > 2;
        }
    };*/
    /*
    TODO:
    -->get USER from last activity
    stopped at: key change event, to validate
    1. display existing details
    2. validate new details that the user entered
    3. if all valid - update in db.
    */
}
