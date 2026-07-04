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
import java.util.Map;

public class TambahAntrianActivity extends AppCompatActivity {

    EditText edtNomor,
            edtTanggal,
            edtJam;

    Spinner spPasien,
            spDokter,
            spStatus;

    Button btnSimpan;

    RequestQueue requestQueue;

    ArrayList<SpinnerModel> pasienList;
    ArrayList<SpinnerModel> dokterList;

    ArrayAdapter<SpinnerModel> pasienAdapter;
    ArrayAdapter<SpinnerModel> dokterAdapter;

    String[] statusList = {
            "Menunggu",
            "Dipanggil",
            "Selesai"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_antrian);

        edtNomor = findViewById(R.id.edtNomor);
        edtTanggal = findViewById(R.id.edtTanggal);
        edtJam = findViewById(R.id.edtJam);

        spPasien = findViewById(R.id.spPasien);
        spDokter = findViewById(R.id.spDokter);
        spStatus = findViewById(R.id.spStatus);

        btnSimpan = findViewById(R.id.btnSimpan);

        requestQueue = Volley.newRequestQueue(this);

        pasienList = new ArrayList<>();
        dokterList = new ArrayList<>();

        pasienAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                pasienList
        );

        dokterAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                dokterList
        );

        spPasien.setAdapter(pasienAdapter);
        spDokter.setAdapter(dokterAdapter);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                statusList
        );

        spStatus.setAdapter(statusAdapter);

        loadPasien();
        loadDokter();

        edtTanggal.setOnClickListener(v -> showDate());
        edtJam.setOnClickListener(v -> showTime());

        btnSimpan.setOnClickListener(v -> simpanData());

    } // Correctly closed onCreate method

    private void loadPasien() {

        String url = Server.BASE_URL + "pasien/get.php";

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,

                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONArray array = object.getJSONArray("data");

                        pasienList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            pasienList.add(
                                    new SpinnerModel(
                                            obj.getString("id_pasien"),
                                            obj.getString("nama")
                                    )
                            );
                        }
                        pasienAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
        );

        requestQueue.add(request);
    }

    private void loadDokter() {

        String url = Server.BASE_URL + "dokter/get.php";

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,

                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONArray array = object.getJSONArray("data");

                        dokterList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            dokterList.add(
                                    new SpinnerModel(
                                            obj.getString("id_dokter"),
                                            obj.getString("nama")
                                    )
                            );
                        }
                        dokterAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
        );

        requestQueue.add(request);
    }

    private void showDate() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> edtTanggal.setText(year + "-" + (month + 1) + "-" + day),
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void showTime() {
        Calendar c = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hour, minute) -> edtJam.setText(String.format("%02d:%02d", hour, minute)),
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                true
        );
        dialog.show();
    }

    private void simpanData() {
        if (edtNomor.getText().toString().trim().isEmpty()) {
            edtNomor.setError("Nomor antrian wajib diisi");
            edtNomor.requestFocus();
            return;
        }

        if (edtTanggal.getText().toString().trim().isEmpty()) {
            edtTanggal.setError("Tanggal wajib diisi");
            edtTanggal.requestFocus();
            return;
        }

        if (edtJam.getText().toString().trim().isEmpty()) {
            edtJam.setError("Jam wajib diisi");
            edtJam.requestFocus();
            return;
        }

        SpinnerModel pasien = (SpinnerModel) spPasien.getSelectedItem();
        SpinnerModel dokter = (SpinnerModel) spDokter.getSelectedItem();
        String status = spStatus.getSelectedItem().toString();

        if (pasien == null || dokter == null) {
            Toast.makeText(this, "Silahkan pilih pasien dan dokter", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = Server.BASE_URL + "antrian/insert.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        boolean success = object.getBoolean("status");
                        String message = object.getString("message");

                        Toast.makeText(TambahAntrianActivity.this, message, Toast.LENGTH_LONG).show();
                        if (success) {
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(TambahAntrianActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(TambahAntrianActivity.this, error.toString(), Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
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
