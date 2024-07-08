package com.example.testfx;

import java.security.Timestamp;

public class Borrowing {
    private int borrow_Id;
    private int user_Id;
    private int item_Id;
    private String borrow_Date;
    private String return_Date;
    private String username;
    private String namaAlat;

    public Borrowing(int borrow_Id, int user_Id, int item_Id, String borrow_Date, String return_Date, String username, String namaAlat) {
        this.borrow_Id = borrow_Id;
        this.user_Id = user_Id;
        this.item_Id = item_Id;
        this.borrow_Date = borrow_Date;
        this.return_Date = return_Date;
        this.username = username;
        this.namaAlat = namaAlat;
    }

    public int getBorrow_Id() {
        return borrow_Id;
    }

    public void setBorrow_Id(int borrow_Id) {
        this.borrow_Id = borrow_Id;
    }

    public int getUser_Id() {
        return user_Id;
    }

    public void setUser_Id(int user_Id) {
        this.user_Id = user_Id;
    }

    public int getItem_Id() {
        return item_Id;
    }

    public void setItem_Id(int item_Id) {
        this.item_Id = item_Id;
    }

    public String getBorrow_Date() {
        return borrow_Date;
    }

    public void setBorrow_Date(String borrow_Date) {
        this.borrow_Date = borrow_Date;
    }

    public String getReturn_Date() {
        return return_Date;
    }

    public void setReturn_Date(String return_Date) {
        this.return_Date = return_Date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNamaAlat() {
        return namaAlat;
    }

    public void setNamaAlat(String namaAlat) {
        this.namaAlat = namaAlat;
    }
}