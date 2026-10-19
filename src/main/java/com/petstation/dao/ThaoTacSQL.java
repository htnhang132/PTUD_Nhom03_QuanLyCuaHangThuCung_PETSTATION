package com.petstation.dao;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/** Cung cấp truy vấn JDBC có tham số và phối hợp transaction lồng nhau bằng savepoint. */
public final class ThaoTacSQL {
    /** Khai báo thao tác truy cập dữ liệu của làm việc. */
    @FunctionalInterface
    public interface CongViec<KieuDuLieu> {
        /**
         * Chạy trong CongViec.
         *
         * @param ketNoi kết nối
         * @return kết quả chạy
         * @throws Exception nếu thao tác không thể hoàn tất
         */
        KieuDuLieu chay(Connection ketNoi) throws Exception;
    }

    /** CSDL. */
    private static KetNoiCSDL csdl;

    /** Hiện tại. */
    private static final ThreadLocal<Connection> HIEN_TAI = new ThreadLocal<>();

    /** Khởi tạo ThaoTacSQL với các giá trị được cung cấp. */
    private ThaoTacSQL() {}

    /**
     * Cấu hình trong ThaoTacSQL.
     *
     * @param giaTri giá trị
     */
    public static void cauHinh(KetNoiCSDL giaTri) {
        csdl = Objects.requireNonNull(giaTri);
    }

    /**
     * Thực hiện giao dịch trong ThaoTacSQL.
     *
     * @param <KieuDuLieu> kiểu dữ liệu được xử lý
     * @param lamViec làm việc
     * @return kết quả thực hiện giao dịch
     */
    public static <KieuDuLieu> KieuDuLieu thucHienGiaoDich(CongViec<KieuDuLieu> lamViec) {
        // Hiện có.
        Connection hienCo = HIEN_TAI.get();
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (hienCo != null) {
            // Điểm lưu.
            Savepoint diemLuu;
            try {
                diemLuu = hienCo.setSavepoint();
            } catch (SQLException loi) {
                throw taoLoi(loi);
            }
            try {
                return lamViec.chay(hienCo);
            } catch (Exception loi) {
                try {
                    hienCo.rollback(diemLuu);
                } catch (SQLException hoanTacGiaoDich) {
                    loi.addSuppressed(hoanTacGiaoDich);
                }
                throw taoLoi(loi);
            }
        }
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (csdl == null) throw new IllegalStateException("Chưa cấu hình kết nối SQL Server.");
        try (Connection ketNoi = csdl.mo()) {
            ketNoi.setAutoCommit(false);
            HIEN_TAI.set(ketNoi);
            try {
                // Kết quả.
                KieuDuLieu ketQua = lamViec.chay(ketNoi);
                ketNoi.commit();
                return ketQua;
            } catch (Exception loi) {
                ketNoi.rollback();
                throw taoLoi(loi);
            } finally {
                HIEN_TAI.remove();
            }
        } catch (SQLException loi) {
            throw taoLoi(loi);
        }
    }

    /**
     * Tạo lỗi trong ThaoTacSQL.
     *
     * @param loi ngoại lệ cần xử lý
     * @return kết quả tạo lỗi
     */
    private static RuntimeException taoLoi(Exception loi) {
        return loi instanceof RuntimeException loi2
                ? loi2
                : new TruyCapDuLieuException(
                        "Không thể truy cập SQL Server: " + loi.getMessage(), loi);
    }

    /**
     * Dùng kết nối trong ThaoTacSQL.
     *
     * @param <KieuDuLieu> kiểu dữ liệu được xử lý
     * @param lamViec làm việc
     * @return kết quả dùng kết nối
     */
    private static <KieuDuLieu> KieuDuLieu dungKetNoi(CongViec<KieuDuLieu> lamViec) {
        // Hiện tại.
        Connection hienTai = HIEN_TAI.get();
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (hienTai == null) return thucHienGiaoDich(lamViec);
        try {
            return lamViec.chay(hienTai);
        } catch (Exception loi) {
            throw taoLoi(loi);
        }
    }

    /**
     * Chuẩn bị trong ThaoTacSQL.
     *
     * @param ketNoi kết nối JDBC đang sử dụng
     * @param sql SQL
     * @param cacDoiSo các đối số
     * @return kết quả chuẩn bị
     * @throws SQLException nếu thao tác không thể hoàn tất
     */
    private static PreparedStatement chuanBi(Connection ketNoi, String sql, Object... cacDoiSo)
            throws SQLException {
        // Câu lệnh.
        PreparedStatement cauLenh = ketNoi.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        for (int chiSo = 0; chiSo < cacDoiSo.length; chiSo++) {
            // Giá trị.
            Object giaTri = cacDoiSo[chiSo];
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (giaTri instanceof LocalDate ngay)
                cauLenh.setDate(chiSo + 1, java.sql.Date.valueOf(ngay));
            else // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (giaTri instanceof LocalDateTime thoiGian)
                cauLenh.setTimestamp(chiSo + 1, Timestamp.valueOf(thoiGian));
            else cauLenh.setObject(chiSo + 1, giaTri);
        }
        return cauLenh;
    }

    /**
     * Truy vấn trong ThaoTacSQL.
     *
     * @param sql SQL
     * @param cacDoiSo các đối số
     * @return kết quả truy vấn
     */
    public static List<Object[]> truyVan(String sql, Object... cacDoiSo) {
        return dungKetNoi(
                ketNoi -> {
                    try (PreparedStatement cauLenh = chuanBi(ketNoi, sql, cacDoiSo);
                            ResultSet tapKetQua = cauLenh.executeQuery()) {
                        // Các dòng.
                        List<Object[]> cacDong = new ArrayList<>();
                        // Kích thước.
                        int kichThuoc = tapKetQua.getMetaData().getColumnCount();
                        while (tapKetQua.next()) {
                            // Dòng.
                            Object[] dong = new Object[kichThuoc];
                            for (int chiSo = 0; chiSo < kichThuoc; chiSo++)
                                dong[chiSo] = tapKetQua.getObject(chiSo + 1);
                            cacDong.add(dong);
                        }
                        return cacDong;
                    }
                });
    }

    /**
     * Cập nhật trong ThaoTacSQL.
     *
     * @param sql SQL
     * @param cacDoiSo các đối số
     * @return kết quả cập nhật
     */
    public static int capNhat(String sql, Object... cacDoiSo) {
        return dungKetNoi(
                ketNoi -> {
                    try (PreparedStatement cauLenh = chuanBi(ketNoi, sql, cacDoiSo)) {
                        return cauLenh.executeUpdate();
                    }
                });
    }

    /**
     * Thêm trong ThaoTacSQL.
     *
     * @param sql SQL
     * @param cacDoiSo các đối số
     * @return kết quả thêm
     */
    public static long them(String sql, Object... cacDoiSo) {
        return dungKetNoi(
                ketNoi -> {
                    try (PreparedStatement cauLenh = chuanBi(ketNoi, sql, cacDoiSo)) {
                        cauLenh.executeUpdate();
                        try (ResultSet cacKhoa = cauLenh.getGeneratedKeys()) {
                            // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                            if (!cacKhoa.next())
                                throw new SQLException("Không nhận được mã bản ghi vừa tạo.");
                            return cacKhoa.getLong(1);
                        }
                    }
                });
    }

    /**
     * Lấy giá trị đơn trong ThaoTacSQL.
     *
     * @param sql SQL
     * @param cacDoiSo các đối số
     * @return kết quả lấy giá trị đơn
     */
    public static Object layGiaTriDon(String sql, Object... cacDoiSo) {
        // Các dòng.
        List<Object[]> cacDong = truyVan(sql, cacDoiSo);
        return cacDong.isEmpty() ? null : cacDong.get(0)[0];
    }

    /**
     * Lấy số trong ThaoTacSQL.
     *
     * @param sql SQL
     * @param cacDoiSo các đối số
     * @return kết quả lấy số
     */
    public static long laySo(String sql, Object... cacDoiSo) {
        // Giá trị.
        Object giaTri = layGiaTriDon(sql, cacDoiSo);
        return giaTri == null ? 0 : ((Number) giaTri).longValue();
    }

    /**
     * Tạo mã trong ThaoTacSQL.
     *
     * @param tienTo tiền tố
     * @return kết quả tạo mã
     */
    public static String taoMa(String tienTo) {
        return tienTo + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    }

    /**
     * Chuyển thành chuỗi trong ThaoTacSQL.
     *
     * @param giaTri giá trị
     * @return kết quả chuyển thành chuỗi
     */
    public static String chuyenThanhChuoi(Object giaTri) {
        return giaTri == null ? "" : giaTri.toString();
    }
}
