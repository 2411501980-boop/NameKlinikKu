package com.example.klinikku;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class PemeriksaanAdapter extends RecyclerView.Adapter<PemeriksaanAdapter.ViewHolder> {

    private ArrayList<PemeriksaanModel> listPemeriksaan;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onEdit(PemeriksaanModel model);
        void onDelete(PemeriksaanModel model);
    }

    public PemeriksaanAdapter(ArrayList<PemeriksaanModel> listPemeriksaan, OnItemActionListener listener) {
        this.listPemeriksaan = listPemeriksaan;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pemeriksaan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PemeriksaanModel model = listPemeriksaan.get(position);
        holder.txtNama.setText(model.getNamaPasien());
        holder.txtDiagnosa.setText("Diagnosa: " + model.getDiagnosa());
        holder.txtTanggal.setText(model.getTanggal());
        holder.txtKeluhan.setText("Keluhan: " + model.getKeluhan());

        holder.itemView.setOnClickListener(v -> listener.onEdit(model));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(model));
    }

    @Override
    public int getItemCount() {
        return listPemeriksaan.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNama, txtDiagnosa, txtTanggal, txtKeluhan;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNama = itemView.findViewById(R.id.txtNamaPasienItem);
            txtDiagnosa = itemView.findViewById(R.id.txtDiagnosaItem);
            txtTanggal = itemView.findViewById(R.id.txtTanggalItem);
            txtKeluhan = itemView.findViewById(R.id.txtKeluhanItem);
            // Pastikan ImageButton ada di layout item_pemeriksaan.xml
            btnDelete = itemView.findViewById(R.id.btnDeletePemeriksaan);
        }
    }
}
