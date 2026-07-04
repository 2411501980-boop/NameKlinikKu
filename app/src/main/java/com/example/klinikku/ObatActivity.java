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

public class ObatActivity extends AppCompatActivity 
        implements ObatAdapter.OnItemActionListener {

    RecyclerView recyclerView;
    FloatingActionButton fabTambah;
    EditText edtCari;

    ArrayList<ObatModel> listObat;
    ArrayList<ObatModel> listFilter;

    ObatAdapter adapter;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obat);

        recyclerView = findViewById(R.id.rvObat);
        fabTambah = findViewById(R.id.fabTambahObat);
        edtCari = findViewById(R.id.edtCariObat);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listObat = new ArrayList<>();
        listFilter = new ArrayList<>();

        adapter = new ObatAdapter(listFilter, this);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        // Ambil query pencarian dari Dashboard jika ada
        String query = getIntent().getStringExtra("SEARCH_QUERY");
        if (query != null && !query.isEmpty()) {
            edtCari.setText(query);
        }

        loadData();

        fabTambah.setOnClickListener(v -> {
            Intent intent = new Intent(ObatActivity.this, TambahObatActivity.class);
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
        for (ObatModel o : listObat) {
            if (o.getNama_obat().toLowerCase().contains(keyword.toLowerCase())) {
                listFilter.add(o);
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
        String url = Server.BASE_URL + "obat/get.php";

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    listObat.clear();
                    try {
                        if (response.trim().startsWith("[")) {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                listObat.add(new ObatModel(
                                        obj.getString("id_obat"),
                                        obj.getString("nama_obat"),
                                        obj.getString("jenis_obat"),
                                        obj.getString("harga"),
                                        obj.getString("stok")
                                ));
                            }
                        } else if (response.trim().startsWith("{")) {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray array = jsonObject.optJSONArray("data");
                            if (array != null) {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    listObat.add(new ObatModel(
                                            obj.getString("id_obat"),
                                            obj.getString("nama_obat"),
                                            obj.getString("jenis_obat"),
                                            obj.getString("harga"),
                                            obj.getString("stok")
                                    ));
                                }
                            }
                        }
                        filterData(edtCari.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Gagal koneksi server", Toast.LENGTH_SHORT).show()
        );
        requestQueue.add(request);
    }

    @Override
    public void onEdit(ObatModel obat) {
        Intent intent = new Intent(ObatActivity.this, TambahObatActivity.class);
        intent.putExtra("id_obat", obat.getId_obat());
        intent.putExtra("nama_obat", obat.getNama_obat());
        intent.putExtra("jenis_obat", obat.getJenis_obat());
        intent.putExtra("harga", obat.getHarga());
        intent.putExtra("stok", obat.getStok());
        startActivity(intent);
    }

    @Override
    public void onDelete(ObatModel obat) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Obat")
                .setMessage("Yakin ingin menghapus " + obat.getNama_obat() + "?")
                .setPositiveButton("Ya", (dialog, which) -> deleteData(obat.getId_obat()))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteData(String id) {
        String url = Server.BASE_URL + "obat/delete.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Obat dihapus", Toast.LENGTH_SHORT).show();
                    loadData();
                },
                error -> Toast.makeText(this, "Gagal menghapus", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_obat", id);
                return params;
            }
        };
        requestQueue.add(request);
    }
}
