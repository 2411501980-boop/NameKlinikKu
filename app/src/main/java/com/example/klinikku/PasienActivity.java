package com.example.klinikku;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
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

public class PasienActivity extends AppCompatActivity
        implements PasienAdapter.OnItemActionListener {

    RecyclerView recyclerView;
    FloatingActionButton fabTambah;
    EditText edtCari;

    ArrayList<PasienModel> listPasien;
    ArrayList<PasienModel> listFilter;

    PasienAdapter adapter;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasien);

        recyclerView = findViewById(R.id.rvPasien);
        fabTambah = findViewById(R.id.fabTambah);
        edtCari = findViewById(R.id.edtCari);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listPasien = new ArrayList<>();
        listFilter = new ArrayList<>();

        adapter = new PasienAdapter(listFilter, this);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        // Ambil query pencarian dari Dashboard jika ada
        String searchQuery = getIntent().getStringExtra("SEARCH_QUERY");
        if (searchQuery != null) {
            edtCari.setText(searchQuery);
        }

        loadData();

        fabTambah.setOnClickListener(v -> {
            Intent intent = new Intent(PasienActivity.this, TambahPasienActivity.class);
            startActivity(intent);
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
    }

    private void filterData(String keyword) {
        listFilter.clear();
        for (PasienModel p : listPasien) {
            if (p.getNama().toLowerCase().contains(keyword.toLowerCase()) || 
                p.getNik().contains(keyword)) {
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

    @Override
    public void onEdit(PasienModel pasien) {
        Intent intent = new Intent(PasienActivity.this, EditPasienActivity.class);
        intent.putExtra("id", pasien.getId_pasien());
        intent.putExtra("nik", pasien.getNik());
        intent.putExtra("nama", pasien.getNama());
        intent.putExtra("jk", pasien.getJenis_kelamin());
        intent.putExtra("tgl", pasien.getTanggal_lahir());
        intent.putExtra("alamat", pasien.getAlamat());
        intent.putExtra("telepon", pasien.getTelepon());
        startActivity(intent);
    }

    @Override
    public void onDelete(PasienModel pasien) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Data")
                .setMessage("Yakin ingin menghapus pasien " + pasien.getNama() + "?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    deleteData(pasien.getId_pasien());
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void loadData() {
        String url = Server.BASE_URL + "pasien/get.php";

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    listPasien.clear();
                    try {
                        String res = response.trim();
                        JSONArray array = null;
                        if (res.startsWith("[")) {
                            array = new JSONArray(res);
                        } else if (res.startsWith("{")) {
                            JSONObject obj = new JSONObject(res);
                            array = obj.optJSONArray("data");
                        }

                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                PasienModel pasien = new PasienModel(
                                        obj.getString("id_pasien"),
                                        obj.getString("nik"),
                                        obj.getString("nama"),
                                        obj.getString("jenis_kelamin"),
                                        obj.getString("tanggal_lahir"),
                                        obj.getString("alamat"),
                                        obj.getString("telepon")
                                );
                                listPasien.add(pasien);
                            }
                        }
                        filterData(edtCari.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(PasienActivity.this, "Gagal memuat data: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );
        requestQueue.add(request);
    }

    private void deleteData(String id) {
        String url = Server.BASE_URL + "pasien/delete.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Toast.makeText(PasienActivity.this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                    loadData();
                },
                error -> Toast.makeText(PasienActivity.this, "Gagal menghapus: " + error.getMessage(), Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_pasien", id);
                return params;
            }
        };
        requestQueue.add(request);
    }
}
