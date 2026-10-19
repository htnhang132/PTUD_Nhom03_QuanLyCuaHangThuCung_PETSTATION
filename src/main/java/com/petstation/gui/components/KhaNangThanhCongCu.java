package com.petstation.gui.components;

/**
 * Cung cấp giao diện và xử lý tương tác cho thanh công cụ khả năng.
 *
 * @param coTheTao có thể tạo
 * @param coTheLuu có thể lưu
 * @param coTheTim có thể tìm
 * @param coTheIn có thể in
 * @param coTheHoanTac có thể hoàn tác
 * @param coTheTroGiup có thể trợ giúp
 */
public record KhaNangThanhCongCu(
        boolean coTheTao,
        boolean coTheLuu,
        boolean coTheTim,
        boolean coTheIn,
        boolean coTheHoanTac,
        boolean coTheTroGiup) {
    /**
     * Tạo rỗng trong KhaNangThanhCongCu.
     *
     * @return kết quả tạo rỗng
     */
    public static KhaNangThanhCongCu taoRong() {
        return new KhaNangThanhCongCu(false, false, false, false, false, false);
    }

    /**
     * Lấy trợ giúp chỉ trong KhaNangThanhCongCu.
     *
     * @return kết quả lấy trợ giúp chỉ
     */
    public static KhaNangThanhCongCu layTroGiupChi() {
        return new KhaNangThanhCongCu(false, false, false, false, false, true);
    }
}
