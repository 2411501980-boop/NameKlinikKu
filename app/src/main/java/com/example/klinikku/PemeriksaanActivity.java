package com.example.klinikku;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PemeriksaanActivity extends AppCompatActivity implements PemeriksaanAdapter.OnItemActionListener {

    RecyclerView recyclerView;
    FloatingActionButton fabTambah;
    ProgressBar progressBar;
    EditText edtCari;
    ArrayList<PemeriksaanModel> listPemeriksaan;
    ArrayList<PemeriksaanModel> listFilter;
    PemeriksaanAdapter adapter;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pemeriksaan_list);

        recyclerView = findViewById(R.id.rvPemeriksaan);
        fabTambah = findViewById(R.id.fabTambahPemeriksaan);
        edtCari = findViewById(R.id.edtCariPemeriksaan);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listPemeriksaan = new ArrayList<>();
        listFilter = new ArrayList<>();
        adapter = new PemeriksaanAdapter(listFilter, this);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        // Ambil query pencarian dari Dashboard jika ada
        String query = getIntent().getStringExtra("SEARCH_QUERY");
        if (query != null && !query.isEmpty()) {
            edtCari.setText(query);
        }

        fabTambah.setOnClickListener(v -> {
            startActivity(new Intent(PemeriksaanActivity.this, TambahPemeriksaanActivity.class));
        });

        edtCari.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadData();
    }

    private void filterData(String keyword) {
        if (keyword == null) keyword = "";
        listFilter.clear();
        String searchKey = keyword.toLowerCase();
        for (PemeriksaanModel p : listPemeriksaan) {
            String nama = p.getNamaPasien() != null ? p.getNamaPasien().toLowerCase() : "";
            String diagnosa = p.getDiagnosa() != null ? p.getDiagnosa().toLowerCase() : "";
            String keluhan = p.getKeluhan() != null ? p.getKeluhan().toLowerCase() : "";
            
            if (nama.contains(searchKey) ||
                diagnosa.contains(searchKey) ||
                keluhan.contains(searchKey)) {
                listFilter.add(p);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        String url = Server.BASE_URL + "pemeriksaan/get.php";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        listPemeriksaan.clear();
                        String res = (response != null) ? response.trim() : "";
                        JSONArray array = null;
                        
                        if (res.startsWith("[")) {
                            array = new JSONArray(res);
                        } else if (res.startsWith("{")) {
                            JSONObject object = new JSONObject(res);
                            // Some APIs might wrap data in a "data" or "status" object
                            array = object.optJSONArray("data");
                        }

                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                listPemeriksaan.add(new PemeriksaanModel(
                                        obj.optString("id_pemeriksaan", ""),
                                        obj.optString("id_pasien", ""),
                                        obj.optString("nama_pasien", "Pasien " + obj.optString("id_pasien", "?")),
                                        obj.optString("keluhan", ""),
                                        obj.optString("diagnosa", ""),
                                        obj.optString("resep", ""),
                                        obj.optString("tanggal", "")
                                ));
                            }
                        }
                        filterData(edtCari.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Gagal memuat data riwayat", Toast.LENGTH_SHORT).show()
        );
        requestQueue.add(request);
    }

    @Override
    public void onEdit(PemeriksaanModel model) {
        Intent intent = new Intent(this, TambahPemeriksaanActivity.class);
        intent.putExtra("id_pemeriksaan", model.getIdPemeriksaan());
        intent.putExtra("id_pasien", model.getIdPasien());
        intent.putExtra("nama_pasien", model.getNamaPasien());
        intent.putExtra("keluhan", model.getKeluhan());
        intent.putExtra("diagnosa", model.getDiagnosa());
        intent.putExtra("resep", model.getResep());
        startActivity(intent);
    }

    @Override
    public void onDelete(PemeriksaanModel model) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Data")
                .setMessage("Yakin ingin menghapus data pemeriksaan " + model.getNamaPasien() + "?")
                .setPositiveButton("Ya", (dialog, which) -> deleteData(model.getIdPemeriksaan()))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteData(String id) {
        if (id == null || id.isEmpty()) return;
        
        String url = Server.BASE_URL + "pemeriksaan/delete.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.optBoolean("status", false)) {
                            Toast.makeText(this, "Berhasil dihapus", Toast.LENGTH_SHORT).show();
                            loadData();
                        } else {
                            Toast.makeText(this, object.optString("message", "Gagal menghapus"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        loadData();
                    }
                },
                error -> Toast.makeText(this, "Gagal menghapus", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_pemeriksaan", id);
                return params;
            }
        };
        requestQueue.add(request);
    }
}
