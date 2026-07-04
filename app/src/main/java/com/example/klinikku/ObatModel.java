package com.example.klinikku;

public class ObatModel {
    private String id_obat;
    private String nama_obat;
    private String jenis_obat;
    private String harga;
    private String stok;

    public ObatModel() {}

    public ObatModel(String id_obat, String nama_obat, String jenis_obat, String harga, String stok) {
        this.id_obat = id_obat;
        this.nama_obat = nama_obat;
        this.jenis_obat = jenis_obat;
        this.harga = harga;
        this.stok = stok;
    }

    public String getId_obat() { return id_obat; }
    public void setId_obat(String id_obat) { this.id_obat = id_obat; }

    public String getNama_obat() { return nama_obat; }
    public void setNama_obat(String nama_obat) { this.nama_obat = nama_obat; }

    public String getJenis_obat() { return jenis_obat; }
    public void setJenis_obat(String jenis_obat) { this.jenis_obat = jenis_obat; }

    public String getHarga() { return harga; }
    public void setHarga(String harga) { this.harga = harga; }

    public String getStok() { return stok; }
    public void setStok(String stok) { this.stok = stok; }
}
