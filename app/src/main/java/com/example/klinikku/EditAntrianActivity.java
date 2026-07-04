package com.example.klinikku;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditAntrianActivity extends AppCompatActivity {

    EditText edtNomor, edtTanggal, edtJam;
    Spinner spPasien, spDokter, spStatus;
    Button btnUpdate;

    String idAntrian, intentPasien, intentDokter, intentStatus;
    RequestQueue requestQueue;

    ArrayList<SpinnerModel> pasienList;
    ArrayList<SpinnerModel> dokterList;
    ArrayAdapter<SpinnerModel> pasienAdapter;
    ArrayAdapter<SpinnerModel> dokterAdapter;

    String[] statusList = {"Menunggu", "Dipanggil", "Selesai"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_antrian);

        edtNomor = findViewById(R.id.edtNomor);
        edtTanggal = findViewById(R.id.edtTanggal);
        edtJam = findViewById(R.id.edtJam);
        spPasien = findViewById(R.id.spPasien);
        spDokter = findViewById(R.id.spDokter);
        spStatus = findViewById(R.id.spStatus);
        btnUpdate = findViewById(R.id.btnUpdate);

        requestQueue = Volley.newRequestQueue(this);

        idAntrian = getIntent().getStringExtra("id");
        edtNomor.setText(getIntent().getStringExtra("nomor"));
        edtTanggal.setText(getIntent().getStringExtra("tanggal"));
        edtJam.setText(getIntent().getStringExtra("jam"));
        intentPasien = getIntent().getStringExtra("pasien");
        intentDokter = getIntent().getStringExtra("dokter");
        intentStatus = getIntent().getStringExtra("status");

        pasienList = new ArrayList<>();
        dokterList = new ArrayList<>();

        pasienAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, pasienList);
        dokterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dokterList);

        spPasien.setAdapter(pasienAdapter);
        spDokter.setAdapter(dokterAdapter);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statusList);
        spStatus.setAdapter(statusAdapter);

        for (int i = 0; i < statusList.length; i++) {
            if (statusList[i].equalsIgnoreCase(intentStatus)) {
                spStatus.setSelection(i);
                break;
            }
        }

        loadPasien();
        loadDokter();

        edtTanggal.setOnClickListener(v -> showDate());
        edtJam.setOnClickListener(v -> showTime());
        btnUpdate.setOnClickListener(v -> updateData());
    }

    private void loadPasien() {
        String url = Server.BASE_URL + "pasien/get.php";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        String res = response != null ? response.trim() : "";
                        JSONArray array = null;
                        if (res.startsWith("[")) {
                            array = new JSONArray(res);
                        } else if (res.startsWith("{")) {
                            JSONObject obj = new JSONObject(res);
                            array = obj.optJSONArray("data");
                        }

                        if (array != null) {
                            pasienList.clear();
                            int selection = 0;
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                String id = obj.optString("id_pasien");
                                String nama = obj.optString("nama");
                                pasienList.add(new SpinnerModel(id, nama));
                                if (nama.equalsIgnoreCase(intentPasien)) {
                                    selection = i;
                                }
                            }
                            pasienAdapter.notifyDataSetChanged();
                            spPasien.setSelection(selection);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Gagal muat pasien", Toast.LENGTH_SHORT).show()
        );
        requestQueue.add(request);
    }

    private void loadDokter() {
        String url = Server.BASE_URL + "dokter/get.php";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        String res = response != null ? response.trim() : "";
                        JSONArray array = null;
                        if (res.startsWith("[")) {
                            array = new JSONArray(res);
                        } else if (res.startsWith("{")) {
                            JSONObject obj = new JSONObject(res);
                            array = obj.optJSONArray("data");
                        }

                        if (array != null) {
                            dokterList.clear();
                            int selection = 0;
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                String id = obj.optString("id_dokter");
                                String nama = obj.optString("nama_dokter");
                                dokterList.add(new SpinnerModel(id, nama));
                                if (nama.equalsIgnoreCase(intentDokter)) {
                                    selection = i;
                                }
                            }
                            dokterAdapter.notifyDataSetChanged();
                            spDokter.setSelection(selection);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Gagal muat dokter", Toast.LENGTH_SHORT).show()
        );
        requestQueue.add(request);
    }

    private void showDate() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, day) -> edtTanggal.setText(String.format(Locale.getDefault(), "%d-%d-%d", year, month + 1, day)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void showTime() {
        Calendar c = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(this,
                (view, hour, minute) -> edtJam.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute)),
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
        dialog.show();
    }

    private void updateData() {
        if (edtNomor.getText().toString().trim().isEmpty() || 
            edtTanggal.getText().toString().trim().isEmpty() || 
            edtJam.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        SpinnerModel pasien = (SpinnerModel) spPasien.getSelectedItem();
        SpinnerModel dokter = (SpinnerModel) spDokter.getSelectedItem();
        
        if (pasien == null || dokter == null) {
            Toast.makeText(this, "Pilih pasien dan dokter terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        String status = spStatus.getSelectedItem().toString();

        String url = Server.BASE_URL + "antrian/update.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.optBoolean("status", false)) {
                            Toast.makeText(this, "Data berhasil diupdate", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, object.optString("message", "Gagal update"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Berhasil diupdate", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                },
                error -> Toast.makeText(this, "Error server", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_antrian", idAntrian);
                params.put("nomor_antrian", edtNomor.getText().toString().trim());
                params.put("id_pasien", pasien.getId());
                params.put("id_dokter", dokter.getId());
                params.put("tanggal", edtTanggal.getText().toString().trim());
                params.put("jam", edtJam.getText().toString().trim());
                params.put("status", status);
                return params;
            }
        };
        requestQueue.add(request);
    }
}
