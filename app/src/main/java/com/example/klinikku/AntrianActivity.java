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

public class AntrianActivity extends AppCompatActivity
        implements AntrianAdapter.OnItemClickListener {

    RecyclerView rvAntrian;
    EditText edtCari;
    FloatingActionButton fabTambah;

    ArrayList<AntrianModel> listAntrian;
    ArrayList<AntrianModel> listFilter;

    AntrianAdapter adapter;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antrian);

        rvAntrian = findViewById(R.id.rvAntrian);
        edtCari = findViewById(R.id.edtCari);
        fabTambah = findViewById(R.id.fabTambah);

        rvAntrian.setLayoutManager(new LinearLayoutManager(this));

        listAntrian = new ArrayList<>();
        listFilter = new ArrayList<>();

        adapter = new AntrianAdapter(listFilter, this);

        rvAntrian.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        // Ambil query pencarian dari Dashboard jika ada
        String query = getIntent().getStringExtra("SEARCH_QUERY");
        if (query != null && !query.isEmpty()) {
            edtCari.setText(query);
        }

        loadData();

        fabTambah.setOnClickListener(v -> {
            Intent i = new Intent(AntrianActivity.this, TambahAntrianActivity.class);
            startActivity(i);
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

    private void filterData(String key){
        if (key == null) key = "";
        listFilter.clear();
        String searchKey = key.toLowerCase();
        
        for(AntrianModel a : listAntrian){
            // Safe null checks untuk mencegah crash "broken channel"
            String pasien = a.getPasien() != null ? a.getPasien().toLowerCase() : "";
            String nomor = a.getNomorAntrian() != null ? a.getNomorAntrian().toLowerCase() : "";
            String dokter = a.getDokter() != null ? a.getDokter().toLowerCase() : "";
            String status = a.getStatus() != null ? a.getStatus().toLowerCase() : "";

            if(pasien.contains(searchKey) || nomor.contains(searchKey) || 
               dokter.contains(searchKey) || status.contains(searchKey)){
                listFilter.add(a);
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
    public void onEdit(AntrianModel model) {
        Intent i = new Intent(AntrianActivity.this, EditAntrianActivity.class);
        i.putExtra("id", model.getId());
        i.putExtra("nomor", model.getNomorAntrian());
        i.putExtra("pasien", model.getPasien());
        i.putExtra("dokter", model.getDokter());
        i.putExtra("tanggal", model.getTanggal());
        i.putExtra("jam", model.getJam());
        i.putExtra("status", model.getStatus());
        startActivity(i);
    }

    @Override
    public void onDelete(AntrianModel model) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Data")
                .setMessage("Yakin ingin menghapus antrian untuk " + model.getPasien() + "?")
                .setPositiveButton("Ya", (dialog, which) -> deleteData(model.getId()))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void loadData() {
        String url = Server.BASE_URL + "antrian/get.php";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        listAntrian.clear();
                        String res = (response != null) ? response.trim() : "";
                        JSONArray array = null;
                        
                        if (res.startsWith("[")) {
                            array = new JSONArray(res);
                        } else if (res.startsWith("{")) {
                            JSONObject object = new JSONObject(res);
                            if (object.optBoolean("status", false)) {
                                array = object.optJSONArray("data");
                            }
                        }

                        if (array != null) {
                            for(int i=0; i<array.length(); i++){
                                JSONObject obj = array.getJSONObject(i);
                                // Menggunakan optString untuk mencegah JSONException / NullPointerException
                                listAntrian.add(new AntrianModel(
                                        obj.optString("id_antrian", ""),
                                        obj.optString("nomor_antrian", ""),
                                        obj.optString("pasien", ""),
                                        obj.optString("dokter", ""),
                                        obj.optString("tanggal", ""),
                                        obj.optString("jam", ""),
                                        obj.optString("status", "")
                                ));
                            }
                        }
                        filterData(edtCari.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Kesalahan memproses data server", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal koneksi server", Toast.LENGTH_SHORT).show()
        );
        requestQueue.add(request);
    }

    private void deleteData(String id){
        if (id == null || id.isEmpty()) return;
        
        String url = Server.BASE_URL + "antrian/delete.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.optBoolean("status", false)){
                            Toast.makeText(this, "Data dihapus", Toast.LENGTH_SHORT).show();
                            loadData();
                        } else {
                            Toast.makeText(this, object.optString("message", "Gagal menghapus"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        loadData(); // Refresh anyway
                    }
                },
                error -> Toast.makeText(this, "Error server", Toast.LENGTH_SHORT).show()
        ){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("id_antrian", id);
                return params;
            }
        };
        requestQueue.add(request);
    }
}
