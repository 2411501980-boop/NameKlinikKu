package com.example.klinikku;

public class SpinnerModel {

    private String id;
    private String nama;

    public SpinnerModel() {
    }

    public SpinnerModel(String id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    @Override
    public String toString() {
        // Yang akan tampil di Spinner
        return nama;
    }
}
