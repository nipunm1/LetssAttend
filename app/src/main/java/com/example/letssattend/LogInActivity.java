package com.example.letssattend;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.letssattend.util.Constants;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogInActivity extends AppCompatActivity {
private static final String TAG="LogInActivity";
private FirebaseAuth mAuth;
TextInputEditText text,text2;
Button btn;
TextView textView,textView2;
CallbackManager callbackManager;
FirebaseDatabase database;
DatabaseReference dbreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();
        text = findViewById(R.id.email);
        text2 = findViewById(R.id.password);
        btn = findViewById(R.id.loginbtn);
        textView = findViewById(R.id.registertext);
        textView2 = findViewById(R.id.forgetpasstext);
        database = FirebaseDatabase.getInstance();
        dbreference = database.getReference(Constants.STUDENT);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email,password;
                email = text.getText().toString();
                password = text2.getText().toString();
                if(TextUtils.isEmpty(email)){
                    text.setError("Please enter email");
                    text.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    text2.setError("Please enter password");
                    text2.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    text.setError("Please enter valid email");
                    text.requestFocus();
                    return;
                }
                login(email,password);
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Register textview");
                Intent intent = new Intent(LogInActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Forget password textview");
                Intent intent = new Intent(LogInActivity.this,ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
    private void login(String email,String password){
        Log.d(TAG, "Login Btn");
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user.isEmailVerified()) {
                                Log.d(TAG, "Successfully LogedIn");
                                dbreference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Intent intent = new Intent(LogInActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                        Toast.makeText(LogInActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.d(TAG, "Student details error"+databaseError.getMessage());
                                    }
                                });
                            }
                            else{
                                Log.d(TAG, "Email not Verified");
                                Toast.makeText(LogInActivity.this, "Please verify email before login by clicking on link that has sent to your address.", Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            Log.d(TAG, "Error in task failure login "+task.getException().toString());
                            Toast.makeText(LogInActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void fbLogin(View view){
        Log.d(TAG, "fblogin btn");
        callbackManager = CallbackManager.Factory.create();
        final LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email","public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "FACEBOOK:"+loginResult);
                fbAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Fb Login : cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "Fb login : error");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void fbAccessToken(AccessToken accessToken){
        Log.d(TAG, "fbAccessToken: "+accessToken);
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "Success sign in with credential");
                    Intent intent = new Intent(LogInActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(LogInActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.d(TAG, "Some error:"+task.getException().getMessage());
                    Toast.makeText(LogInActivity.this,"Error : "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
