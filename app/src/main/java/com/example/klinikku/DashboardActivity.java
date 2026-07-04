package com.example.klinikku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class DashboardActivity extends AppCompatActivity {

    CardView cardPasien, cardDokter, cardObat, cardPemeriksaan, cardAntrian, cardUser;
    TextView txtPasien, txtDokter, txtWelcome;
    EditText edtCari;
    ImageButton btnLogout, btnSearchGlobal;
    RequestQueue requestQueue;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        txtWelcome = findViewById(R.id.txtWelcome);
        txtPasien = findViewById(R.id.txtPasien);
        txtDokter = findViewById(R.id.txtDokter);
        edtCari = findViewById(R.id.edtCari);
        btnLogout = findViewById(R.id.btnLogout);
        btnSearchGlobal = findViewById(R.id.btnSearchGlobal);

        cardPasien = findViewById(R.id.cardPasien);
        cardDokter = findViewById(R.id.cardDokter);
        cardObat = findViewById(R.id.cardObat);
        cardPemeriksaan = findViewById(R.id.cardPemeriksaan);
        cardAntrian = findViewById(R.id.cardAntrian);
        cardUser = findViewById(R.id.cardUser);

        requestQueue = Volley.newRequestQueue(this);
        sessionManager = new SessionManager(this);

        setupRoleUI();

        btnSearchGlobal.setOnClickListener(v -> performGlobalSearch());

        cardPasien.setOnClickListener(v -> startActivity(new Intent(this, PasienActivity.class)));
        cardDokter.setOnClickListener(v -> startActivity(new Intent(this, DokterActivity.class)));
        cardObat.setOnClickListener(v -> startActivity(new Intent(this, ObatActivity.class)));
        cardPemeriksaan.setOnClickListener(v -> startActivity(new Intent(this, PemeriksaanActivity.class)));
        cardAntrian.setOnClickListener(v -> startActivity(new Intent(this, AntrianActivity.class)));
        cardUser.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            finish();
        });

        loadDashboardStats();
    }

    private void setupRoleUI() {
        String role = sessionManager.getRole();
        if (role == null) role = "pasien";
        txtWelcome.setText("Halo, " + role.toUpperCase());
        
        if (role.equalsIgnoreCase("pasien")) {
            if (cardPasien != null) cardPasien.setVisibility(View.GONE);
            if (cardUser != null) cardUser.setVisibility(View.GONE);
            if (cardObat != null) cardObat.setVisibility(View.GONE);
        }
    }

    private void performGlobalSearch() {
        String query = edtCari.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(this, "Masukkan kata kunci...", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent;
        String lowerQuery = query.toLowerCase();

        if (lowerQuery.startsWith("a") && lowerQuery.length() > 1) {
            intent = new Intent(this, AntrianActivity.class);
        } else if (lowerQuery.contains("dr") || lowerQuery.contains("spesialis")) {
            intent = new Intent(this, DokterActivity.class);
        } else if (lowerQuery.contains("obat") || lowerQuery.contains("paracet") || lowerQuery.contains("antibiotik")) {
            intent = new Intent(this, ObatActivity.class);
        } else {
            intent = new Intent(this, PasienActivity.class);
        }

        intent.putExtra("SEARCH_QUERY", query);
        startActivity(intent);
    }

    private void loadDashboardStats() {
        fetchCount(Server.BASE_URL + "pasien/get.php", txtPasien);
        fetchCount(Server.BASE_URL + "dokter/get.php", txtDokter);
    }

    private void fetchCount(String url, TextView targetView) {
        if (targetView == null) return;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        String res = (response != null) ? response.trim() : "";
                        JSONArray array = null;
                        if (res.startsWith("[")) {
                            array = new JSONArray(res);
                        } else if (res.startsWith("{")) {
                            JSONObject obj = new JSONObject(res);
                            array = obj.optJSONArray("data");
                        }
                        
                        if (array != null) {
                            targetView.setText(String.valueOf(array.length()));
                        } else {
                            targetView.setText("0");
                        }
                    } catch (Exception e) { 
                        targetView.setText("0"); 
                    }
                },
                error -> {
                    if (targetView != null) targetView.setText("0");
                }
        );
        requestQueue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardStats();
    }
}
