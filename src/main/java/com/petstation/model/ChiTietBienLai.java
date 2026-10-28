package com.petstation.model;

/** Thông tin in bổ sung; số dư điểm lịch sử để null nếu không xác định được. */
public record ChiTietBienLai(
        GioHang.BienLai bienLai,
        String tenCuaHang,
        String diaChi,
        String dienThoaiCuaHang,
        String dienThoaiKhach,
        int diemCong,
        Long soDuDiemSau,
        String loaiNguon,
        String maNguon,
        long daCanTru,
        String hoaDonGoc) {
    public int tongSoLuong() {
        return bienLai.cacDong().stream().mapToInt(GioHang.DongHang::soLuong).sum();
    }
}
