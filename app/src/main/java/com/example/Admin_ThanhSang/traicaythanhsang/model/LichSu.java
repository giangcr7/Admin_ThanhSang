package com.example.Admin_ThanhSang.traicaythanhsang.model;

import java.io.Serializable;

public class LichSu implements Serializable {
    private String ngaydat;
    private String tensanpham;
    private int giasanpham;
    private int soluong;
    private double tongtien; // dùng double để chính xác

    // Constructor 4 tham số
    public LichSu(String ngaydat, String tensanpham, int giasanpham, int soluong) {
        this.ngaydat = ngaydat;
        this.tensanpham = tensanpham;
        this.giasanpham = giasanpham;
        this.soluong = soluong;
    }

    // Setter/getter
    public String getNgaydat() {
        return ngaydat;
    }

    public void setNgaydat(String ngaydat) {
        this.ngaydat = ngaydat;
    }

    public String getTensanpham() {
        return tensanpham;
    }

    public void setTensanpham(String tensanpham) {
        this.tensanpham = tensanpham;
    }

    public int getGiasanpham() {
        return giasanpham;
    }

    public void setGiasanpham(int giasanpham) {
        this.giasanpham = giasanpham;
    }

    public int getSoluong() {
        return soluong;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }
}
