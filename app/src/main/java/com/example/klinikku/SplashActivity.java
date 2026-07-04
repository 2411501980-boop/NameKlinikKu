package com.example.klinikku;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sessionManager = new SessionManager(this);

        new Handler().postDelayed(() -> {
            if (sessionManager.isLogin()) {
                // Jika sudah login, langsung ke Dashboard
                startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
            } else {
                // Jika belum login, ke halaman Login
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, 2500);
    }
}