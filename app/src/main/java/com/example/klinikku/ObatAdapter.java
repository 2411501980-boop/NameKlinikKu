package com.example.klinikku;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ObatAdapter extends RecyclerView.Adapter<ObatAdapter.ViewHolder> {

    private ArrayList<ObatModel> listObat;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onEdit(ObatModel obat);
        void onDelete(ObatModel obat);
    }

    public ObatAdapter(ArrayList<ObatModel> listObat, OnItemActionListener listener) {
        this.listObat = listObat;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_obat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ObatModel obat = listObat.get(position);

        holder.txtNama.setText(obat.getNama_obat());
        holder.txtJenis.setText("Jenis: " + obat.getJenis_obat());
        holder.txtHarga.setText("Harga: Rp " + obat.getHarga());
        holder.txtStok.setText("Stok: " + obat.getStok());

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(obat);
        });

        holder.btnHapus.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(obat);
        });
    }

    @Override
    public int getItemCount() {
        return listObat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNama, txtJenis, txtHarga, txtStok;
        ImageButton btnEdit, btnHapus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNama = itemView.findViewById(R.id.txtNamaObat);
            txtJenis = itemView.findViewById(R.id.txtJenisObat);
            txtHarga = itemView.findViewById(R.id.txtHarga);
            txtStok = itemView.findViewById(R.id.txtStok);
            btnEdit = itemView.findViewById(R.id.btnEditObat);
            btnHapus = itemView.findViewById(R.id.btnHapusObat);
        }
    }
}
