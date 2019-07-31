package com.example.letssattend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
private FirebaseAuth mAuth;
public static final String TAG="RegisterActivity";
TextInputEditText editText,editText2,editText3;
TextView textView;
Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        editText = findViewById(R.id.email);
        editText2 = findViewById(R.id.password);
        editText3 = findViewById(R.id.con_password);
        textView = findViewById(R.id.logintext);
        btn = findViewById(R.id.registerbtn);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,LogInActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email,password,conf_password;
                email = editText.getText().toString();
                password = editText2.getText().toString();
                conf_password = editText3.getText().toString();
                if(TextUtils.isEmpty(email)){
                    editText.setError("Please enter Email");
                    editText.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    editText2.setError("Please enter Password");
                    editText2.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(conf_password)){
                    editText3.setError("Please Confirm Password");
                    editText3.requestFocus();
                    return;
                }
                if(!password.equals(conf_password)){
                    editText3.setError("Password Mismatch");
                    editText3.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    editText.setError("Please enter valid email");
                    editText.requestFocus();
                    return;
                }
                if(password.length()<6){
                    editText2.setError("Please enter password of minimum 6 digits");
                    editText2.requestFocus();
                    return;
                }
                    signUp(email,password);
            }
        });
    }
    public void signUp(String email,String password){
        Log.d(TAG, "SignUp");
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
               if(task.isSuccessful()){
                   mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful()) {
                               Log.d(TAG, "create user : Success");
                               Intent intent = new Intent(RegisterActivity.this, LogInActivity.class);
                               startActivity(intent);
                               Toast.makeText(RegisterActivity.this, "You have successfully registered.Please check your email for verification to login Let's Attend.", Toast.LENGTH_SHORT).show();
                               finish();
                           }
                           else{
                               Log.d(TAG, task.getException().toString());
                               Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                           }
                      }
                   });
               }
               else{
                   if(task.getException() instanceof FirebaseAuthUserCollisionException){
                       Log.d(TAG, "User already registered");
                       Toast.makeText(RegisterActivity.this, "You are already Registered", Toast.LENGTH_LONG).show();
                   }
                   else {
                       Log.d(TAG, "Auth failed" + task.getException());
                       Toast.makeText(RegisterActivity.this, "Authentication failed "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                   }
               }
            }
        });
    }
}
