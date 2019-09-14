package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

//About fragment - contains explanation about the app, credits, terms of agreement and contact.
public class AboutFragment extends Fragment {

    private TextView textConditions;
    private TextView textContactUs;
    private TextView textAboutHeader;
    private TextView textAbout;
    private ImageView imageBack;
    private EditText editTextMsgHeader;
    private EditText editTextMsgText;
    private Button buttonSend;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);

        //set XML elements
        textConditions = (TextView)v.findViewById(R.id.textViewConditions);
        textContactUs = (TextView)v.findViewById(R.id.textViewContact);
        textAboutHeader = (TextView)v.findViewById(R.id.textViewAboutHeadline);
        textAbout = (TextView)v.findViewById(R.id.textViewAbout);
        imageBack = (ImageView)v.findViewById(R.id.buttonBack);
        buttonSend = (Button)v.findViewById(R.id.btnOK);
        editTextMsgHeader = (EditText) v.findViewById(R.id.txtSubject);
        editTextMsgText = (EditText) v.findViewById(R.id.txtMessage);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.conditions);
        builder.setMessage(R.string.terms_and_conditions);
        final AlertDialog conditionsDialog = builder.create();

        textConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conditionsDialog.show();
            }
        });

        textContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPageVisibility(View.INVISIBLE);
                setContactVisibilty(View.VISIBLE);
            }
        });

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPageVisibility(View.VISIBLE);
                setContactVisibilty(View.INVISIBLE);
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = editTextMsgHeader.getText().toString();
                String msg = editTextMsgText.getText().toString();
                if (subject != null && msg != null) {
                    setContactUs(subject, msg);
                }
            }
        });

        return v;
    }

    private void setPageVisibility(int visibilty){
        textConditions.setVisibility(visibilty);
        textContactUs.setVisibility(visibilty);
        textAboutHeader.setVisibility(visibilty);
        textAbout.setVisibility(visibilty);
    }

    private void setContactVisibilty(int visibilty){
        buttonSend.setVisibility(visibilty);
        editTextMsgHeader.setVisibility(visibilty);
        editTextMsgText.setVisibility(visibilty);
        imageBack.setVisibility(visibilty);
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
