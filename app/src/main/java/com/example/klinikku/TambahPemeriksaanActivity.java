package com.example.klinikku;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TambahPemeriksaanActivity extends AppCompatActivity {

    Spinner spPasien;
    TextInputEditText edtKeluhan, edtDiagnosa, edtResep;
    Button btnSimpan;
    TextView txtJudul;

    String idPemeriksaan = "";
    String intentIdPasien = "";
    
    ArrayList<SpinnerModel> pasienList;
    ArrayAdapter<SpinnerModel> pasienAdapter;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pemeriksaan);

        txtJudul = findViewById(R.id.txtJudulPemeriksaan);
        spPasien = findViewById(R.id.spPasienPemeriksaan);
        edtKeluhan = findViewById(R.id.edtKeluhan);
        edtDiagnosa = findViewById(R.id.edtDiagnosa);
        edtResep = findViewById(R.id.edtResep);
        btnSimpan = findViewById(R.id.btnSimpanPemeriksaan);

        requestQueue = Volley.newRequestQueue(this);
        pasienList = new ArrayList<>();
        pasienAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, pasienList);
        spPasien.setAdapter(pasienAdapter);

        // Cek apakah ini mode Edit
        if (getIntent().hasExtra("id_pemeriksaan")) {
            idPemeriksaan = getIntent().getStringExtra("id_pemeriksaan");
            intentIdPasien = getIntent().getStringExtra("id_pasien");
            
            txtJudul.setText("✏️ Edit Pemeriksaan");
            edtKeluhan.setText(getIntent().getStringExtra("keluhan"));
            edtDiagnosa.setText(getIntent().getStringExtra("diagnosa"));
            edtResep.setText(getIntent().getStringExtra("resep"));
            btnSimpan.setText("UPDATE PEMERIKSAAN");
        }

        loadPasien();

        btnSimpan.setOnClickListener(v -> simpanData());
    }

    private void loadPasien() {
        String url = Server.BASE_URL + "pasien/get.php";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array;
                        if (response.trim().startsWith("{")) {
                            JSONObject obj = new JSONObject(response);
                            array = obj.getJSONArray("data");
                        } else {
                            array = new JSONArray(response);
                        }

                        pasienList.clear();
                        int selection = 0;
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String id = obj.getString("id_pasien");
                            String nama = obj.getString("nama");
                            pasienList.add(new SpinnerModel(id, nama));
                            
                            if (id.equals(intentIdPasien)) {
                                selection = i;
                            }
                        }
                        pasienAdapter.notifyDataSetChanged();
                        spPasien.setSelection(selection);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Gagal memuat data pasien", Toast.LENGTH_SHORT).show()
        );
        requestQueue.add(request);
    }

    private void simpanData() {
        String keluhan = edtKeluhan.getText().toString().trim();
        String diagnosa = edtDiagnosa.getText().toString().trim();
        String resep = edtResep.getText().toString().trim();

        if (spPasien.getSelectedItem() == null || keluhan.isEmpty() || diagnosa.isEmpty()) {
            Toast.makeText(this, "Lengkapi data pasien, keluhan dan diagnosa", Toast.LENGTH_SHORT).show();
            return;
        }

        SpinnerModel selectedPasien = (SpinnerModel) spPasien.getSelectedItem();
        String idPasien = selectedPasien.getId();

        String url = Server.BASE_URL + (idPemeriksaan.isEmpty() ? "pemeriksaan/insert.php" : "pemeriksaan/update.php");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("status")) {
                            Toast.makeText(this, "Berhasil disimpan", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Proses berhasil", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                },
                error -> Toast.makeText(this, "Gagal simpan: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                if (!idPemeriksaan.isEmpty()) params.put("id_pemeriksaan", idPemeriksaan);
                params.put("id_pasien", idPasien);
                params.put("keluhan", keluhan);
                params.put("diagnosa", diagnosa);
                params.put("resep", resep);
                return params;
            }
        };

        requestQueue.add(request);
    }
}
