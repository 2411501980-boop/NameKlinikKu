package com.example.klinikku;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TambahPasienActivity extends AppCompatActivity {

    EditText edtNik, edtNama, edtTglLahir, edtAlamat, edtTelepon;
    RadioGroup rgJk;
    RadioButton rbLaki, rbPerempuan;
    Button btnSimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_pasien);

        edtNik = findViewById(R.id.edtNik);
        edtNama = findViewById(R.id.edtNama);
        edtTglLahir = findViewById(R.id.edtTglLahir);
        edtAlamat = findViewById(R.id.edtAlamat);
        edtTelepon = findViewById(R.id.edtTelepon);
        rgJk = findViewById(R.id.rgJk);
        rbLaki = findViewById(R.id.rbLaki);
        rbPerempuan = findViewById(R.id.rbPerempuan);
        btnSimpan = findViewById(R.id.btnSimpan);

        // Input Tanggal Lahir menggunakan DatePicker
        edtTglLahir.setFocusable(false);
        edtTglLahir.setClickable(true);
        edtTglLahir.setOnClickListener(v -> showDatePicker());

        btnSimpan.setOnClickListener(v -> simpanData());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    edtTglLahir.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void simpanData() {
        String nik = edtNik.getText().toString().trim();
        String nama = edtNama.getText().toString().trim();
        String tgl = edtTglLahir.getText().toString().trim();
        String alamat = edtAlamat.getText().toString().trim();
        String telp = edtTelepon.getText().toString().trim();
        
        int selectedJkId = rgJk.getCheckedRadioButtonId();
        RadioButton selectedJk = findViewById(selectedJkId);
        String jk = (selectedJk != null) ? selectedJk.getText().toString() : "";

        if (nik.isEmpty() || nama.isEmpty() || tgl.isEmpty() || alamat.isEmpty() || telp.isEmpty() || jk.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = Server.BASE_URL + "pasien/insert.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Toast.makeText(TambahPasienActivity.this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> Toast.makeText(TambahPasienActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nik", nik);
                params.put("nama", nama);
                params.put("jenis_kelamin", jk);
                params.put("tanggal_lahir", tgl);
                params.put("alamat", alamat);
                params.put("telepon", telp);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
