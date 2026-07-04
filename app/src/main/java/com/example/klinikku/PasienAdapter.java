package com.example.klinikku;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PasienAdapter extends RecyclerView.Adapter<PasienAdapter.ViewHolder> {

    private ArrayList<PasienModel> listPasien;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onEdit(PasienModel pasien);
        void onDelete(PasienModel pasien);
    }

    public PasienAdapter(ArrayList<PasienModel> listPasien,
                         OnItemActionListener listener) {
        this.listPasien = listPasien;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pasien, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        PasienModel pasien = listPasien.get(position);

        holder.txtNama.setText(pasien.getNama());
        holder.txtNik.setText("NIK : " + pasien.getNik());
        holder.txtJK.setText("Jenis Kelamin : " + pasien.getJenis_kelamin());
        holder.txtAlamat.setText("Alamat : " + pasien.getAlamat());
        holder.txtTelepon.setText("Telepon : " + pasien.getTelepon());

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(pasien);
            }
        });

        holder.btnHapus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(pasien);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listPasien.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtNama, txtNik, txtJK, txtAlamat, txtTelepon;
        Button btnEdit, btnHapus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNama = itemView.findViewById(R.id.txtNama);
            txtNik = itemView.findViewById(R.id.txtNik);
            txtJK = itemView.findViewById(R.id.txtJK);
            txtAlamat = itemView.findViewById(R.id.txtAlamat);
            txtTelepon = itemView.findViewById(R.id.txtTelepon);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnHapus = itemView.findViewById(R.id.btnHapus);
        }
    }

}