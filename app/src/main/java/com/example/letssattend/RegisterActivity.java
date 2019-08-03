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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.letssattend.model.Student;
import com.example.letssattend.util.Constants;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
private FirebaseAuth mAuth;
public static final String TAG="RegisterActivity";
TextInputEditText editText,editText2,editText3,editText4;
TextView textView;
Button btn;
Spinner spinner;
FirebaseDatabase database;
DatabaseReference dbreference;
String email,password,conf_password,name,courses;
String s_course="---Select Course---";
List<String> courseList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }
    private void init(){
        mAuth = FirebaseAuth.getInstance();
        editText = findViewById(R.id.email);
        editText2 = findViewById(R.id.password);
        editText3 = findViewById(R.id.con_password);
        editText4 = findViewById(R.id.name);
        textView = findViewById(R.id.logintext);
        btn = findViewById(R.id.registerbtn);
        spinner = findViewById(R.id.coursespin);
        database = FirebaseDatabase.getInstance();
        dbreference = database.getReference(Constants.STUDENT);

        courseList.add("---Select Course---");
        final ArrayAdapter<String>adapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_list_item_1, courseList);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0){
                    Log.d(TAG, "Spinner list course selected");
                    courses = courseList.get(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG, "No spinner item selected");
            }
        });
        DatabaseReference courseRef = database.getReference(Constants.COURSES);
        courseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     for(DataSnapshot d : dataSnapshot.getChildren()){
                         Log.d(TAG, "Data in courses "+d.toString());
                         courseList.add((String) d.getValue());
                     }
                     adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "Error in courses "+databaseError.getMessage());
            }
        });


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
                email = editText.getText().toString();
                password = editText2.getText().toString();
                conf_password = editText3.getText().toString();
                name = editText4.getText().toString();
                courses = spinner.getSelectedItem().toString();
                if(courses.equals(s_course)){
                    Toast.makeText(RegisterActivity.this, "Please select course", Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(name)){
                    editText4.setError("Please enter your fullname");
                    editText4.requestFocus();
                    return;
                }
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

                signUp(email,password,name,courses);
            }
        });
    }
    public void signUp(final String email, String password, final String name, final String courses){
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
                               FirebaseUser user = mAuth.getCurrentUser();
                               Student student = new Student(name,email,courses);
                               dbreference.child(user.getUid()).setValue(student);
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
