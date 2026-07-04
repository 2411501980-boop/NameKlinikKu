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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class DokterActivity extends AppCompatActivity implements DokterAdapter.OnItemActionListener {

    RecyclerView recyclerView;
    EditText edtCari;
    SwipeRefreshLayout swipeRefresh;
    ProgressBar progressBar;
    FloatingActionButton fabTambah;

    ArrayList<DokterModel> listDokter;
    ArrayList<DokterModel> listFilter;

    DokterAdapter adapter;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dokter);

        recyclerView = findViewById(R.id.rvDokter);
        edtCari = findViewById(R.id.edtCariDokter);
        swipeRefresh = findViewById(R.id.swipeRefreshDokter);
        progressBar = findViewById(R.id.progressDokter);
        fabTambah = findViewById(R.id.fabTambahDokter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listDokter = new ArrayList<>();
        listFilter = new ArrayList<>();

        adapter = new DokterAdapter(listFilter, this);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        swipeRefresh.setOnRefreshListener(this::loadData);

        String query = getIntent().getStringExtra("SEARCH_QUERY");
        if (query != null && !query.isEmpty()) {
            edtCari.setText(query);
        }
        
        loadData();

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

        fabTambah.setOnClickListener(v -> {
            Intent intent = new Intent(DokterActivity.this, TambahDokterActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void filterData(String keyword) {
        if (keyword == null) keyword = "";
        listFilter.clear();
        for (DokterModel d : listDokter) {
            if (d.getNama_dokter().toLowerCase().contains(keyword.toLowerCase()) ||
                d.getSpesialis().toLowerCase().contains(keyword.toLowerCase()) ||
                d.getHari().toLowerCase().contains(keyword.toLowerCase())) {
                listFilter.add(d);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void loadData() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        String url = Server.BASE_URL + "dokter/get.php";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);
                    listDokter.clear();
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
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                listDokter.add(new DokterModel(
                                        obj.optString("id_dokter"),
                                        obj.optString("nama_dokter"),
                                        obj.optString("spesialis"),
                                        obj.optString("telepon"),
                                        obj.optString("hari", "-"),
                                        obj.optString("jam", "-")
                                ));
                            }
                        }
                        filterData(edtCari.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Gagal memproses data dokter", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);
                    Toast.makeText(this, "Gagal koneksi server dokter", Toast.LENGTH_SHORT).show();
                }
        );
        requestQueue.add(request);
    }

    @Override
    public void onEdit(DokterModel dokter) {
        Intent intent = new Intent(this, TambahDokterActivity.class);
        intent.putExtra("id_dokter", dokter.getId_dokter());
        intent.putExtra("nama_dokter", dokter.getNama_dokter());
        intent.putExtra("spesialis", dokter.getSpesialis());
        intent.putExtra("hari", dokter.getHari());
        intent.putExtra("jam", dokter.getJam());
        intent.putExtra("telepon", dokter.getTelepon());
        startActivity(intent);
    }

    @Override
    public void onDelete(DokterModel dokter) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Dokter")
                .setMessage("Yakin ingin menghapus dokter " + dokter.getNama_dokter() + "?")
                .setPositiveButton("Ya", (dialog, which) -> deleteData(dokter.getId_dokter()))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteData(String id) {
        String url = Server.BASE_URL + "dokter/delete.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Dokter berhasil dihapus", Toast.LENGTH_SHORT).show();
                    loadData();
                },
                error -> Toast.makeText(this, "Gagal menghapus dokter", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_dokter", id);
                return params;
            }
        };
        requestQueue.add(request);
    }
}
