package com.example.project;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class AboutFragment extends Fragment {

    private TextView conditions;
    private TextView contactUs;
    private TextView aboutHeader;
    private TextView aboutText;
    private TextView backButton;
    private EditText msgHeader;
    private EditText msgText;
    private Button btnSend;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        conditions = (TextView)v.findViewById(R.id.textViewConditions);
        contactUs = (TextView)v.findViewById(R.id.textViewContact);
        aboutHeader = (TextView)v.findViewById(R.id.textViewAboutHeadline);
        aboutText = (TextView)v.findViewById(R.id.textViewAbout);
        backButton = (TextView)v.findViewById(R.id.buttonBack);
        msgHeader = (EditText) v.findViewById(R.id.txtSubject);
        msgText = (EditText) v.findViewById(R.id.txtMessage);
        btnSend = (Button)v.findViewById(R.id.btnOK);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.conditions);
        builder.setMessage(R.string.terms_and_conditions);
        final AlertDialog conditionsDialog = builder.create();

        conditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conditionsDialog.show();
            }
        });

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPageVisibility(View.INVISIBLE);
                setContactVisibilty(View.VISIBLE);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("&$$$$$$$$$$$$$$$$" + msgHeader.getText());
                String subject = msgHeader.getText().toString();
                System.out.println("&$$$$$$$$$$$$$$$$");
                String msg = msgText.getText().toString();
                System.out.println("&$$$$$$$$$$$$$$$$" + subject +","+ msg);
                if (subject != null && msg != null) {
                    setContactUs(subject, msg);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPageVisibility(View.VISIBLE);
                setContactVisibilty(View.INVISIBLE);
            }
        });


        return v;
    }

    private void setPageVisibility(int visibilty){
        conditions.setVisibility(visibilty);
        contactUs.setVisibility(visibilty);
        aboutHeader.setVisibility(visibilty);
        aboutText.setVisibility(visibilty);
    }

    private void setContactVisibilty(int visibilty){
        btnSend.setVisibility(visibilty);
        msgHeader.setVisibility(visibilty);
        msgText.setVisibility(visibilty);
        backButton.setVisibility(visibilty);
    }



    private void setContactUs(String subject, String msg){

        Intent mail = new Intent(Intent.ACTION_SEND);
        mail.putExtra(Intent.EXTRA_EMAIL,new String[]{"speech2me.app@gmail.com"});
        mail.putExtra(Intent.EXTRA_SUBJECT, subject);
        mail.putExtra(Intent.EXTRA_TEXT, msg);
        mail.setType("message/rfc822");
        startActivity(Intent.createChooser(mail, "Send email via:"));
    }
}
