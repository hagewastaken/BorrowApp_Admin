package com.example.testfx;

public class Item {
    private int id;
    private String NomorInventaris;
    private String NamaAlat;
    private String NomorSeri;
    private int TahunBeli;
    private String Kondisi;
    private String Spesifikasi;
    private String Status;
    private String Deskripsi;
    private String Category;

    public Item(int id, String NomorInventaris, String NamaAlat, String NomorSeri, int TahunBeli, String Kondisi, String Spesifikasi, String Status, String Deskripsi, String Category) {
        this.id = id;
        this.NomorInventaris = NomorInventaris;
        this.NamaAlat = NamaAlat;
        this.NomorSeri = NomorSeri;
        this.TahunBeli = TahunBeli;
        this.Kondisi = Kondisi;
        this.Spesifikasi = Spesifikasi;
        this.Status = Status;
        this.Deskripsi = Deskripsi;
        this.Category = Category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomorInventaris() {
        return NomorInventaris;
    }

    public void setNomorInventaris(String NomorInventaris) {
        this.NomorInventaris = NomorInventaris;
    }

    public String getNamaAlat() {
        return NamaAlat;
    }

    public void setNamaAlat(String NamaAlat) {
        this.NamaAlat = NamaAlat;
    }

    public String getNomorSeri() {
        return NomorSeri;
    }

    public void setNomorSeri(String NomorSeri) {
        this.NomorSeri = NomorSeri;
    }

    public int getTahunBeli() {
        return TahunBeli;
    }

    public void setTahunBeli(int TahunBeli) {
        this.TahunBeli = TahunBeli;
    }

    public String getKondisi() {
        return Kondisi;
    }

    public void setKondisi(String Kondisi) {
        this.Kondisi = Kondisi;
    }

    public String getSpesifikasi() {
        return Spesifikasi;
    }

    public void setSpesifikasi(String Spesifikasi) {
        this.Spesifikasi = Spesifikasi;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public String getDeskripsi() {
        return Deskripsi;
    }

    public void setDeskripsi(String Deskripsi) {
        this.Deskripsi = Deskripsi;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String Category) {
        this.Category = Category;
    }
}