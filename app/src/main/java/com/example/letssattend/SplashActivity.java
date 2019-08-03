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

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG="SplashActivity";
    private FirebaseAuth mAuth;
    FirebaseUser mUser;
    ImageView imageView;
    /*AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            checkUpdateTrackerWithToken(currentAccessToken);
        }
    };*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
       // checkUpdateTrackerWithToken(AccessToken.getCurrentAccessToken());
        imageView = findViewById(R.id.imageview);
        //hash();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mAuth.getCurrentUser()==null) {
                    Log.d(TAG, "currentuser=null");
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, imageView, "attend");
                    startActivity(intent,activityOptions.toBundle());
                    finish();
                }
                else if(!mAuth.getCurrentUser().isEmailVerified()){
                    Log.d(TAG, "not email verified");
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, imageView, "attend");
                    startActivity(intent,activityOptions.toBundle());
                    finish();
                }
                else{
                    Log.d(TAG, "user is not null");
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 500);
    }
    /*private void checkUpdateTrackerWithToken(AccessToken accessToken){
        if(accessToken!=null){
            Log.d(TAG, "checkUpdateTrackerWithToken != null");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "FB user is not null");
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 500);
        }
        else{
            Log.d(TAG,"checkUpdateTrackerWithToken==NULL");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "FB user = null");
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, imageView, "attend");
                    startActivity(intent,activityOptions.toBundle());
                    finish();
                }
            }, 500);
        }
    }*/
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
