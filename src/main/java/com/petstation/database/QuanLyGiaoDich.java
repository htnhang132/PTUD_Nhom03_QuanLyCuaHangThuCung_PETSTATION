package com.petstation.database;

import java.sql.Connection;
import java.sql.SQLException;

public final class QuanLyGiaoDich {
    @FunctionalInterface
    public interface CongViecGiaoDich<KieuDuLieu> {
        KieuDuLieu thucThi(Connection ketNoi) throws Exception;
    }

    private final KetNoiCSDL csdl;

    public QuanLyGiaoDich(KetNoiCSDL csdl) {
        this.csdl = csdl;
    }

    public <KieuDuLieu> KieuDuLieu thucThi(CongViecGiaoDich<KieuDuLieu> lamViec) {
        Connection dangCo = ThaoTacSQL.layKetNoiHienTai();
        if (dangCo != null) {
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
                throw new TruyCapDuLieuException("Giao dịch đã bị hoàn tác.", ngoaiLe);
            } finally {
                ThaoTacSQL.ganKetNoiHienTai(null);
                ketNoi.setAutoCommit(true);
            }
        } catch (SQLException ngoaiLe) {
            throw new TruyCapDuLieuException("Không thể kết nối SQL Server.", ngoaiLe);
        }
    }
}
