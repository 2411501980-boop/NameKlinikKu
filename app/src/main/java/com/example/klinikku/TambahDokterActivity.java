package com.example.klinikku;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TambahDokterActivity extends AppCompatActivity {
    EditText edtNama, edtSpesialis, edtHari, edtJam, edtTelepon;
    Button btnSimpan;
    TextView txtJudul;
    String id_dokter = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_dokter);

        txtJudul = findViewById(R.id.txtJudulTambahDokter);
        edtNama = findViewById(R.id.edtNamaDokter);
        edtSpesialis = findViewById(R.id.edtSpesialis);
        edtHari = findViewById(R.id.edtHari);
        edtJam = findViewById(R.id.edtJam);
        edtTelepon = findViewById(R.id.edtTelepon);
        btnSimpan = findViewById(R.id.btnSimpanDokter);

        if (getIntent().hasExtra("id_dokter")) {
            id_dokter = getIntent().getStringExtra("id_dokter");
            txtJudul.setText("Edit Data Dokter");
            edtNama.setText(getIntent().getStringExtra("nama_dokter"));
            edtSpesialis.setText(getIntent().getStringExtra("spesialis"));
            edtHari.setText(getIntent().getStringExtra("hari"));
            edtJam.setText(getIntent().getStringExtra("jam"));
            edtTelepon.setText(getIntent().getStringExtra("telepon"));
            btnSimpan.setText("Update Data Dokter");
        }

        // Gunakan TimePickerDialog untuk input jam agar format konsisten
        edtJam.setFocusable(false);
        edtJam.setClickable(true);
        edtJam.setOnClickListener(v -> showTimePicker());

        btnSimpan.setOnClickListener(v -> simpanData());
    }

    private void showTimePicker() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) -> 
                    edtJam.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour)),
                hour, minute, true);
        timePickerDialog.show();
    }

    private void simpanData() {
        String nama = edtNama.getText().toString().trim();
        String spesialis = edtSpesialis.getText().toString().trim();
        String hari = edtHari.getText().toString().trim();
        String jam = edtJam.getText().toString().trim();
        String telp = edtTelepon.getText().toString().trim();

        if (nama.isEmpty() || spesialis.isEmpty() || hari.isEmpty() || jam.isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi data dokter", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = Server.BASE_URL + (id_dokter.isEmpty() ? "dokter/insert.php" : "dokter/update.php");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Berhasil menyimpan data dokter", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> Toast.makeText(this, "Gagal simpan: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                if (!id_dokter.isEmpty()) params.put("id_dokter", id_dokter);
                params.put("nama_dokter", nama);
                params.put("spesialis", spesialis);
                params.put("telepon", telp);
                params.put("hari", hari);
                params.put("jam", jam);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
