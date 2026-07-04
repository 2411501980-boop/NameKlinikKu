package com.example.klinikku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText edtUsername, edtPassword;
    Button btnLogin;
    ProgressBar progressBar;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionManager(this);

        if (session.isLogin()) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        }

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressLogin);

        btnLogin.setOnClickListener(v -> validateAndLogin());
    }

    private void validateAndLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty()) {
            edtUsername.setError("Username wajib diisi");
            return;
        }
        if (password.isEmpty()) {
            edtPassword.setError("Password wajib diisi");
            return;
        }

        login(username, password);
    }

    private void login(String username, String password) {
        btnLogin.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(Request.Method.POST, Server.BASE_URL + "login.php",
                response -> {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setVisibility(View.VISIBLE);
                    try {
                        JSONObject obj = new JSONObject(response);
                        // Menggunakan optBoolean lebih aman daripada getBoolean
                        if (obj.optBoolean("status", false)) {
                            String id = obj.optString("id_user", "0");
                            String user = obj.optString("username", username);
                            String role = obj.optString("role", "pasien");
                            
                            session.createSession(id, user, role);
                            
                            Toast.makeText(this, "Selamat Datang, " + user, Toast.LENGTH_SHORT).show();
                            
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, obj.optString("message", "Login Gagal"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Respon server tidak valid", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
