package com.example.klinikku;

public class DokterModel {
    private String id_dokter;
    private String nama_dokter;
    private String spesialis;
    private String telepon;
    private String hari;
    private String jam;

    public DokterModel(String id_dokter, String nama_dokter, String spesialis, String telepon, String hari, String jam) {
        this.id_dokter = id_dokter;
        this.nama_dokter = nama_dokter;
        this.spesialis = spesialis;
        this.telepon = telepon;
        this.hari = hari;
        this.jam = jam;
    }

    public String getId_dokter() {
        return id_dokter;
    }

    public String getNama_dokter() {
        return nama_dokter;
    }

    public String getSpesialis() {
        return spesialis;
    }

    public String getTelepon() {
        return telepon;
    }

    public String getHari() {
        return hari;
    }

    public String getJam() {
        return jam;
    }
}
