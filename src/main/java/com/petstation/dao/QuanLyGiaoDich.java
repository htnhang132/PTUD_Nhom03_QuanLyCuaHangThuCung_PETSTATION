package com.petstation.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Thực thi công việc JDBC trong một transaction, commit khi thành công và rollback khi thất bại.
 */
public final class QuanLyGiaoDich {
    /** Khai báo thao tác truy cập dữ liệu của giao dịch làm việc. */
    @FunctionalInterface
    public interface CongViecGiaoDich<KieuDuLieu> {
        /**
         * Thực thi trong CongViecGiaoDich.
         *
         * @param ketNoi kết nối
         * @return kết quả thực thi
         * @throws Exception nếu thao tác không thể hoàn tất
         */
        KieuDuLieu thucThi(Connection ketNoi) throws Exception;
    }

    /** CSDL. */
    private final KetNoiCSDL csdl;

    /**
     * Khởi tạo QuanLyGiaoDich với các giá trị được cung cấp.
     *
     * @param csdl CSDL
     */
    public QuanLyGiaoDich(KetNoiCSDL csdl) {
        this.csdl = csdl;
    }

    /**
     * Thực thi trong QuanLyGiaoDich.
     *
     * @param <KieuDuLieu> kiểu dữ liệu được xử lý
     * @param lamViec làm việc
     * @return kết quả thực thi
     */
    public <KieuDuLieu> KieuDuLieu thucThi(CongViecGiaoDich<KieuDuLieu> lamViec) {
        try (Connection ketNoi = csdl.mo()) {
            ketNoi.setAutoCommit(false);
            try {
                // Kết quả.
                KieuDuLieu ketQua = lamViec.thucThi(ketNoi);
                ketNoi.commit();
                return ketQua;
            } catch (RuntimeException ngoaiLe) {
                ketNoi.rollback();
                throw ngoaiLe;
            } catch (Exception ngoaiLe) {
                ketNoi.rollback();
                throw new TruyCapDuLieuException("Giao dịch đã bị hoàn tác.", ngoaiLe);
            } finally {
                ketNoi.setAutoCommit(true);
            }
        } catch (SQLException ngoaiLe) {
            throw new TruyCapDuLieuException("Không thể kết nối SQL Server.", ngoaiLe);
        }
    }
}
