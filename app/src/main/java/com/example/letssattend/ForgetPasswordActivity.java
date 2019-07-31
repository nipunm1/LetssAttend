package com.example.letssattend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {
    private static final String TAG="ForgetPasswordActivity";
    TextInputEditText text;
    Button btn;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        text = findViewById(R.id.verifiedemailtext);
        btn = findViewById(R.id.verifiedbtn);
        mAuth = FirebaseAuth.getInstance();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "verification btn");
                String email = text.getText().toString();
                if(email.isEmpty()){
                    text.setError("Please enter your authenticated email address");
                    text.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    text.setError("Please enter valid email");
                    text.requestFocus();
                    return;
                }
                sendEmail(email);
            }
        });
    }
    private void sendEmail(String email){
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(ForgetPasswordActivity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "Email has sent");
                    Toast.makeText(ForgetPasswordActivity.this, "Password Reset Email has sent to your address.Check it", Toast.LENGTH_LONG).show();
                }
                else{
                    Log.d(TAG, task.getException().toString());
                    Toast.makeText(ForgetPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
