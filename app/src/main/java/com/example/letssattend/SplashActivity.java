package com.example.letssattend;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG="SplashActivity";
    private FirebaseAuth mAuth;
    FirebaseUser mUser;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        imageView = findViewById(R.id.imageview);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mAuth.getCurrentUser()==null) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, imageView, "attend");
                    startActivity(intent,activityOptions.toBundle());
                    finish();
                }
                else{
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 500);
    }
}
