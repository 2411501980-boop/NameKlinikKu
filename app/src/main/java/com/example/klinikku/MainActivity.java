package com.example.klinikku;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText username, password;
    private TextView status, roleResult;
    private RadioGroup rgRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.txtUsername);
        password = findViewById(R.id.txtPassword);
        status = findViewById(R.id.status);
        roleResult = findViewById(R.id.role);
        
        // Asumsi kita menambahkan RadioGroup di XML untuk memilih role
        // Jika belum ada di XML, defaultnya kita kirim 'pasien'
        rgRole = findViewById(R.id.rgRole); 
    }

    private String getSelectedRole() {
        if (rgRole != null) {
            int selectedId = rgRole.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton rb = findViewById(selectedId);
                return rb.getText().toString().toLowerCase();
            }
        }
        return "pasien"; // Default role
    }

    // Fungsi SIMPAN (Create)
    public void Simpan(View view) {
        String user = username.getText().toString();
        String pass = password.getText().toString();
        String roleStr = getSelectedRole();
        
        // Memanggil flag 2 dengan 3 parameter (user, pass, role)
        new SigninActivity(this, status, roleResult, 2).execute(user, pass, roleStr);
    }

    // Fungsi TAMPIL DATA (Read)
    public void Tampil(View view) {
        new SigninActivity(this, status, roleResult, 3).execute("", "");
    }

    // Fungsi HAPUS DATA (Delete)
    public void Hapus(View view) {
        String user = username.getText().toString();
        new SigninActivity(this, status, roleResult, 4).execute(user, "");
    }

    // Fungsi UBAH DATA (Update)
    public void Ubah(View view) {
        String user = username.getText().toString();
        String pass = password.getText().toString();
        String roleStr = getSelectedRole();
        
        // Memanggil flag 5 dengan 3 parameter (user, pass, role)
        new SigninActivity(this, status, roleResult, 5).execute(user, pass, roleStr);
    }

    public void loginGet(View view) {
        String user = username.getText().toString();
        String pass = password.getText().toString();
        new SigninActivity(this, status, roleResult, 0).execute(user, pass);
    }
}
