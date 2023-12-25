package com.example.internapp_webmobi_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.test.espresso.remote.EspressoRemoteMessage;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login_Account_Activity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginBtn, loginGoggle;

     GoogleSignInClient gsc;
     GoogleSignInOptions gso;
    ProgressBar progressBar;
    TextView createAccountBtnTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_account);
        //views linked id

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginBtn = findViewById(R.id.login_btn);
        loginGoggle = findViewById(R.id.goggle_login_btn);
        progressBar = findViewById(R.id.progress_bar);
        createAccountBtnTextView = findViewById(R.id.create_account_text_view_btn);
        //goggle gso
         gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();


        // Initialize sign in client
        gsc = GoogleSignIn.getClient(this,gso);

        //goggle login
        loginGoggle.setOnClickListener((View.OnClickListener) view -> {
           signIn();
        });



        loginBtn.setOnClickListener((v)-> loginUser());
        createAccountBtnTextView.setOnClickListener((v)->startActivity(new Intent(Login_Account_Activity.this,Create_Account_Activity.class)));
    }

    private void signIn() {

        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent,1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                navigateToSecondActivity();
            }catch (ApiException e){
                Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                     }
                            }
                             }

     void navigateToSecondActivity() {
        finish();
        Intent intent = new Intent(Login_Account_Activity.this,MainActivity.class);
        startActivity(intent);
    }


    void loginUser() {

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        boolean isValidated = validateData(email,password);
        if (! isValidated){
            return;
        }
        loginAccountInFirebase(email,password);

    }

    void loginAccountInFirebase(String email,String password){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if (task.isSuccessful()){
                    //login is success
                    //dialog.dismiss();
                    Toast.makeText(Login_Account_Activity.this, "Logged in Successfull!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                    /*if (firebaseAuth.getCurrentUser().isEmailVerified()){
                        //go to mainActivity
                        startActivity(new Intent(Login_Account_Activity.this,MainActivity.class));
                        finish();
                    }else {
                            Utility.showToast(Login_Account_Activity.this,"Email not verified, Please verify your email.");
                    }*/
                }else {
                    //login failed
                    Utility.showToast(Login_Account_Activity.this,task.getException().getLocalizedMessage());


                }

            }
        });


    }



    void changeInProgress(boolean inProgrss){
        if (inProgrss){
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }


    boolean validateData(String email, String password){
        //validate the data that are input by user.
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is invalid");
            return false;
        }
        if (password.length()<6){
            passwordEditText.setError("Password length is invalid");
            return false;
        }

        return true;
    }



}