package com.example.klinikku;

public class AntrianModel {

    private String id;
    private String nomorAntrian;
    private String pasien;
    private String dokter;
    private String tanggal;
    private String jam;
    private String status;

    public AntrianModel() {

    }

    public AntrianModel(String id,
                        String nomorAntrian,
                        String pasien,
                        String dokter,
                        String tanggal,
                        String jam,
                        String status) {

        this.id = id;
        this.nomorAntrian = nomorAntrian;
        this.pasien = pasien;
        this.dokter = dokter;
        this.tanggal = tanggal;
        this.jam = jam;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomorAntrian() {
        return nomorAntrian;
    }

    public void setNomorAntrian(String nomorAntrian) {
        this.nomorAntrian = nomorAntrian;
    }

    public String getPasien() {
        return pasien;
    }

    public void setPasien(String pasien) {
        this.pasien = pasien;
    }

    public String getDokter() {
        return dokter;
    }

    public void setDokter(String dokter) {
        this.dokter = dokter;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}