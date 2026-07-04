package com.example.klinikku;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AntrianAdapter extends RecyclerView.Adapter<AntrianAdapter.ViewHolder> {

    private ArrayList<AntrianModel> listAntrian;
    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onEdit(AntrianModel model);
        void onDelete(AntrianModel model);
    }

    public AntrianAdapter(ArrayList<AntrianModel> listAntrian,
                          OnItemClickListener listener){

        this.listAntrian = listAntrian;
        this.listener = listener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_antrian,parent,false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        AntrianModel model = listAntrian.get(position);

        holder.txtNo.setText(model.getNomorAntrian());

        holder.txtPasien.setText(model.getPasien());

        holder.txtDokter.setText(model.getDokter());

        holder.txtTanggal.setText(model.getTanggal());

        holder.txtJam.setText(model.getJam());

        holder.txtStatus.setText(model.getStatus());

        holder.btnEdit.setOnClickListener(v->{

            listener.onEdit(model);

        });

        holder.btnDelete.setOnClickListener(v->{

            listener.onDelete(model);

        });

    }

    @Override
    public int getItemCount() {

        return listAntrian.size();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtNo,
                txtPasien,
                txtDokter,
                txtTanggal,
                txtJam,
                txtStatus;

        ImageButton btnEdit,
                btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNo=itemView.findViewById(R.id.txtNoAntrian);

            txtPasien=itemView.findViewById(R.id.txtNamaPasien);

            txtDokter=itemView.findViewById(R.id.txtNamaDokter);

            txtTanggal=itemView.findViewById(R.id.txtTanggal);

            txtJam=itemView.findViewById(R.id.txtJam);

            txtStatus=itemView.findViewById(R.id.txtStatus);

            btnEdit=itemView.findViewById(R.id.btnEdit);

            btnDelete=itemView.findViewById(R.id.btnDelete);

        }

    }

}