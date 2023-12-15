package com.example.internapp_webmobi_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class Edit_Profile extends AppCompatActivity {

    TextView title, profileUpdate;
    public TextInputLayout pname, phone, pEmail, txtcity ;
    public TextInputEditText ename, eEmail, enumber, editcity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


//called textInputLayout id
        pname = findViewById(R.id.pname);
        phone = findViewById(R.id.pphn);
        pEmail = findViewById(R.id.pmail);
        txtcity = findViewById(R.id.txtcity);

        // called TextInputEditText id
        ename = findViewById(R.id.ename);
        eEmail = findViewById(R.id.eEmail);
        enumber = findViewById(R.id.Enum);
        editcity = findViewById(R.id.editcity);


    }
}
