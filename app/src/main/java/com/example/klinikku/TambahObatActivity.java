package com.example.klinikku;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class TambahObatActivity extends AppCompatActivity {
    EditText edtNama, edtJenis, edtHarga, edtStok;
    Button btnSimpan;
    TextView txtJudul;
    String id_obat = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_obat);

        txtJudul = findViewById(R.id.txtJudulTambahObat);
        edtNama = findViewById(R.id.edtNamaObat);
        edtJenis = findViewById(R.id.edtJenisObat);
        edtHarga = findViewById(R.id.edtHarga);
        edtStok = findViewById(R.id.edtStok);
        btnSimpan = findViewById(R.id.btnSimpanObat);

        if (getIntent().hasExtra("id_obat")) {
            id_obat = getIntent().getStringExtra("id_obat");
            txtJudul.setText("Edit Data Obat");
            edtNama.setText(getIntent().getStringExtra("nama_obat"));
            edtJenis.setText(getIntent().getStringExtra("jenis_obat"));
            edtHarga.setText(getIntent().getStringExtra("harga"));
            edtStok.setText(getIntent().getStringExtra("stok"));
            btnSimpan.setText("Update Data Obat");
        }

        btnSimpan.setOnClickListener(v -> simpanData());
    }

    private void simpanData() {
        String url = Server.BASE_URL + (id_obat.isEmpty() ? "obat/insert.php" : "obat/update.php");
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Berhasil menyimpan data obat", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> Toast.makeText(this, "Gagal simpan: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                if (!id_obat.isEmpty()) params.put("id_obat", id_obat);
                params.put("nama_obat", edtNama.getText().toString());
                params.put("jenis_obat", edtJenis.getText().toString());
                params.put("harga", edtHarga.getText().toString());
                params.put("stok", edtStok.getText().toString());
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}
