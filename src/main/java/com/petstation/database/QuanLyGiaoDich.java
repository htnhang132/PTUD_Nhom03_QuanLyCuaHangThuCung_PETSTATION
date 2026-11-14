package com.petstation.database;

import java.sql.Connection;
import java.sql.SQLException;

public final class QuanLyGiaoDich {
    @FunctionalInterface
    public interface CongViecGiaoDich<KieuDuLieu> {
        KieuDuLieu thucThi(Connection ketNoi) throws Exception;
    }

    private static final QuanLyGiaoDich MAC_DINH = new QuanLyGiaoDich(null);

    private final KetNoiCSDL csdl;

    public QuanLyGiaoDich(KetNoiCSDL csdl) {
        this.csdl = csdl;
    }

    public static QuanLyGiaoDich macDinh() {
        return MAC_DINH;
    }

    public static boolean dangTrongGiaoDich() {
        return ThaoTacSQL.dangTrongGiaoDich();
    }

    public <KieuDuLieu> KieuDuLieu thucThi(CongViecGiaoDich<KieuDuLieu> lamViec) {
        if (csdl == null || ThaoTacSQL.layKetNoiHienTai() != null) {
            return ThaoTacSQL.thucHienGiaoDich(lamViec::thucThi);
        }
        try (Connection ketNoi = csdl.mo()) {
            ketNoi.setAutoCommit(false);
            ThaoTacSQL.ganKetNoiHienTai(ketNoi);
            try {
                KieuDuLieu ketQua = lamViec.thucThi(ketNoi);
                ketNoi.commit();
                return ketQua;
            } catch (RuntimeException ngoaiLe) {
                ketNoi.rollback();
                throw ngoaiLe;
            } catch (Exception ngoaiLe) {
                ketNoi.rollback();
                throw new IllegalStateException("Giao dịch đã bị hoàn tác.", ngoaiLe);
            } finally {
                ThaoTacSQL.ganKetNoiHienTai(null);
                ketNoi.setAutoCommit(true);
            }
        } catch (SQLException ngoaiLe) {
            throw new IllegalStateException("Không thể kết nối SQL Server.", ngoaiLe);
        }
    }
}
