package com.easy.market;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    ImageView iv;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        sp = getSharedPreferences(ConstantSp.PREF,MODE_PRIVATE);
        iv = findViewById(R.id.splash_iv);
        AlphaAnimation animation = new AlphaAnimation(0,1);
        animation.setDuration(2700);
        iv.startAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sp.getString(ConstantSp.USER_TYPE,"").equals("")){
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
                else if (true){
                    startActivity(new Intent(SplashActivity.this,DashboardNavigationActivity.class));
                    finish();
                }
                else{
                    if (new ConnectionDetector(SplashActivity.this).networkConnected()) {
                    } else {
                        new ConnectionDetector(SplashActivity.this).networkDisconnected();
                    }
                }
            }
        },3000);
    }
}
