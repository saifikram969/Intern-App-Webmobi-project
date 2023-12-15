package com.example.internapp_webmobi_project;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Create_Account_Activity extends AppCompatActivity {

    public EditText nameEdittext,Internship,contact,emailEditText, passwordEditText, confirmPasswordEditText;
    public Button createAccountBtn;
    public ProgressBar progressBar;
    public TextView loginBtnTextView;
    private FirebaseAuth mAuth;

    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //views linked id
        setContentView(R.layout.activity_create_account);
        mAuth = FirebaseAuth.getInstance();
        nameEdittext=findViewById(R.id.Name_edit_text);
        Internship=findViewById(R.id.internship_name_edit_text);
        contact=findViewById(R.id.contact_no_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        createAccountBtn = findViewById(R.id.create_account_button);
        progressBar = findViewById(R.id.progress_bar);
        loginBtnTextView = findViewById(R.id.login_text_view_btn);

        //for signup or for creating account
        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                // trim data for storing it
                String Name=nameEdittext.getText().toString().trim();
                String Email=emailEditText.getText().toString().trim();
                String num=contact.getText().toString().trim();
                String password=passwordEditText.toString().trim();
                String cnfrm=confirmPasswordEditText.toString().trim();
                String intern=Internship.getText().toString().trim();

                boolean error = false;

                if (TextUtils.isEmpty(Name)) {
                    nameEdittext.setError("Enter Your name");
                    error = true;
                }

                if (TextUtils.isEmpty(Email)) {
                    emailEditText.setError("Enter your email");
                    error = true;
                }

                if (TextUtils.isEmpty(num)) {
                    contact.setError("Enter your number");
                    error = true;
                }

                if (TextUtils.isEmpty(intern)) {
                    Internship.setError("Enter your internship");
                    error = true;
                }

                if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Enter a password");
                    error = true;
                }

                if (TextUtils.isEmpty(cnfrm)) {
                    confirmPasswordEditText.setError("Confirm your password");
                    error = true;
                } else if (!password.equals(cnfrm)) {
                    confirmPasswordEditText.setError("Passwords do not match");
                    error = true;
                }

                if (!error) {


                    // to register(create) the user credentials for login and storing value in database
                    mAuth.createUserWithEmailAndPassword(Email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // Create and display the custom progress dialog

                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Create_Account_Activity.this);
                            View dialogView = getLayoutInflater().inflate(R.layout.progress_dialog, null);
                            ProgressBar progressBarDialog = dialogView.findViewById(R.id.progressBar);
                            TextView textViewCustom = dialogView.findViewById(R.id.textViewCustom);
                            textViewCustom.setText("Account Creating ");
                            dialogBuilder.setView(dialogView);
                            //dialogBuilder.setTitle("Your data updating");

                            dialogBuilder.setCancelable(false);
                            AlertDialog dialog = dialogBuilder.create();
                            dialog.show();
                            if(task.isSuccessful())
                            {
                                HashMap<String,Object> hashMap=new HashMap<>();
                                hashMap.put("Name",Name);
                                hashMap.put("Email id",Email);
                                hashMap.put("Mobile Number",num);
                                hashMap.put("Internship",intern);
                                hashMap.put("Password",password);
                                userid=mAuth.getCurrentUser().getUid();

                                // to store user id in database
                                String UID=userid.getBytes(StandardCharsets.UTF_8).toString().trim();
                                hashMap.put("User id",userid);
                                FirebaseFirestore.getInstance().collection("User").document(Email).set(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                dialog.dismiss();

                                                Toast.makeText(Create_Account_Activity.this, "Register successfully", Toast.LENGTH_SHORT).show();
                                                Log.d(TAG,"Data Added successfully");
                                                startActivity(new Intent(getApplicationContext(),Login_Account_Activity.class ));
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                dialog.dismiss();
                                                Toast.makeText(Create_Account_Activity.this, "failed!!"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                Log.d(TAG,"OnFailure"+e.toString());
                                            }
                                        });


                            }
                            else {
                                Toast.makeText(Create_Account_Activity.this, "Error!!!"+task.getException(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), Login_Account_Activity.class));
                            }
                        }
                    });
                }

            }
        });
        loginBtnTextView.setOnClickListener(v-> {
            startActivity(new Intent(getApplicationContext(), Login_Account_Activity.class));
        });
    }

   /* void createAccount(){
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        boolean isValidated = validateData(email,password,confirmPassword);
        if (! isValidated){
            return;
        }
        createAccountInFirebase(email,password);

    }

    private void createAccountInFirebase(String email, String password) {
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(Create_Account_Activity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);

                        if (task.isSuccessful()){
                            //creating account is done
                            Utility.showToast(Create_Account_Activity.this,"Successfully create account,Check email to verify");
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }else {
                            //failure
                            Utility.showToast(Create_Account_Activity.this,task.getException().getLocalizedMessage());


                        }

                    }
                }
        );

    }
    void changeInProgress(boolean inProgress){
        if (inProgress){
            progressBar.setVisibility(View.VISIBLE);
            createAccountBtn.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            createAccountBtn.setVisibility(View.VISIBLE);
        }
    }


    boolean validateData(String email, String password, String confirmPassword){
        //validate the data that are input by user.
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is invalid");
            return false;
        }
        if (password.length()<6){
            passwordEditText.setError("Password length is invalid");
            return false;
        }
        if (!password.equals(confirmPassword)){
            confirmPasswordEditText.setError("Password not matched");
            return false;
        }
        return true;
    }*/
}