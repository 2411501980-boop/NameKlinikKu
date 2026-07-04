package com.example.klinikku;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SigninActivity extends AsyncTask<String, Void, String> {

    private TextView statusField, roleField;
    private int byGetOrPost = 0;

    // flag 0 = login get
    // flag 1 = login post
    // flag 2 = insert user
    // flag 3 = show users
    // flag 4 = delete user
    // flag 5 = update user
    // flag 6 = get clinical info (terpusat)

    public SigninActivity(Context context, TextView statusField, TextView roleField, int flag) {
        this.statusField = statusField;
        this.roleField = roleField;
        this.byGetOrPost = flag;
    }

    @Override
    protected String doInBackground(String... arg0) {
        try {
            String username = (arg0.length > 0) ? arg0[0] : "";
            String password = (arg0.length > 1) ? arg0[1] : "";
            String roleStr = (arg0.length > 2) ? arg0[2] : "pasien"; // Role tambahan (default: pasien)

            String link = "";
            String data = "";

            if (byGetOrPost == 0) { // Login GET
                link = Server.BASE_URL + "login_get.php?username=" + URLEncoder.encode(username, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");
            } else if (byGetOrPost == 1) { // Login POST
                link = Server.BASE_URL + "login_post.php";
                data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
            } else if (byGetOrPost == 2) { // Insert (SIMPAN USER)
                link = Server.BASE_URL + "insert.php";
                data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                data += "&" + URLEncoder.encode("role", "UTF-8") + "=" + URLEncoder.encode(roleStr, "UTF-8");
            } else if (byGetOrPost == 3) { // Show (TAMPIL)
                link = Server.BASE_URL + "show.php";
            } else if (byGetOrPost == 4) { // Delete (HAPUS)
                link = Server.BASE_URL + "delete.php";
                data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
            } else if (byGetOrPost == 5) { // Update (UBAH USER)
                link = Server.BASE_URL + "update.php";
                data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                data += "&" + URLEncoder.encode("role", "UTF-8") + "=" + URLEncoder.encode(roleStr, "UTF-8");
            } else if (byGetOrPost == 6) { // Get Clinical Info (Terpusat)
                link = Server.BASE_URL + "get_info_klinik.php";
                data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
            }

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            if (!data.isEmpty()) {
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();

        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (this.statusField != null) {
            this.statusField.setText("Proses Selesai");
        }
        if (this.roleField != null) {
            this.roleField.setText(result);
        }
    }
}
