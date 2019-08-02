package com.example.letssattend;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
        //hash();
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
    private void hash()
    {
        try
        {
            PackageInfo info = getPackageManager().getPackageInfo( "com.example.letssattend", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                //System.out.println(""+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e) { Log.d("errorrr",e.getMessage()); }
        catch (NoSuchAlgorithmException e) { Log.d("errorrr",e.getMessage()); }
    }
}
