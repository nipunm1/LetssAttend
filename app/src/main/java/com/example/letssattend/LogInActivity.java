package com.example.letssattend;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {
private static final String TAG="LogInActivity";
private FirebaseAuth mAuth;
TextInputEditText text,text2;
Button btn;
TextView textView,textView2;
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
                            if(mAuth.getCurrentUser().isEmailVerified()) {
                                Log.d(TAG, "Successfully LogedIn");
                                Intent intent = new Intent(LogInActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(LogInActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
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
}
