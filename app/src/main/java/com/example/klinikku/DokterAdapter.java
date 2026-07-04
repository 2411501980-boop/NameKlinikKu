package com.example.klinikku;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DokterAdapter extends RecyclerView.Adapter<DokterAdapter.ViewHolder> {

    private ArrayList<DokterModel> listDokter;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onEdit(DokterModel dokter);
        void onDelete(DokterModel dokter);
    }

    public DokterAdapter(ArrayList<DokterModel> listDokter, OnItemActionListener listener) {
        this.listDokter = listDokter;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dokter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DokterModel dokter = listDokter.get(position);

        holder.txtNama.setText(dokter.getNama_dokter());
        holder.txtSpesialis.setText("Spesialis: " + dokter.getSpesialis());
        holder.txtTelepon.setText("Telepon: " + dokter.getTelepon());
        holder.txtJadwal.setText("Jadwal: " + dokter.getHari() + " (" + dokter.getJam() + ")");

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(dokter);
        });

        holder.btnHapus.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(dokter);
        });
    }

    @Override
    public int getItemCount() {
        return listDokter.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNama, txtSpesialis, txtTelepon, txtJadwal;
        ImageButton btnEdit, btnHapus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNama = itemView.findViewById(R.id.txtNamaDokter);
            txtSpesialis = itemView.findViewById(R.id.txtSpesialis);
            txtTelepon = itemView.findViewById(R.id.txtTeleponDokter);
            txtJadwal = itemView.findViewById(R.id.txtJadwal);
            btnEdit = itemView.findViewById(R.id.btnEditDokter);
            btnHapus = itemView.findViewById(R.id.btnHapusDokter);
        }
    }
}
