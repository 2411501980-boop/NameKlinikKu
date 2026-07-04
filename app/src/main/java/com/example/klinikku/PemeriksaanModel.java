package com.example.klinikku;

public class PemeriksaanModel {
    private String idPemeriksaan;
    private String idPasien;
    private String namaPasien;
    private String keluhan;
    private String diagnosa;
    private String resep;
    private String tanggal;

    public PemeriksaanModel(String idPemeriksaan, String idPasien, String namaPasien, String keluhan, String diagnosa, String resep, String tanggal) {
        this.idPemeriksaan = idPemeriksaan;
        this.idPasien = idPasien;
        this.namaPasien = namaPasien;
        this.keluhan = keluhan;
        this.diagnosa = diagnosa;
        this.resep = resep;
        this.tanggal = tanggal;
    }

    public String getIdPemeriksaan() { return idPemeriksaan; }
    public String getIdPasien() { return idPasien; }
    public String getNamaPasien() { return namaPasien; }
    public String getKeluhan() { return keluhan; }
    public String getDiagnosa() { return diagnosa; }
    public String getResep() { return resep; }
    public String getTanggal() { return tanggal; }
}
