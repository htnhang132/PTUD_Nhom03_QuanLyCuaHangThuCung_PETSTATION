package com.petstation.database;

import com.petstation.util.DuLieu;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public final class ThaoTacSQL {
    @FunctionalInterface
    public interface CongViec<KieuDuLieu> {
        KieuDuLieu chay(Connection ketNoi) throws Exception;
    }

    private static KetNoiCSDL csdl;
    private static final ThreadLocal<Connection> HIEN_TAI = new ThreadLocal<>();

    public static boolean dangTrongGiaoDich() {
        return HIEN_TAI.get() != null;
    }

    private ThaoTacSQL() {}

    public static void cauHinh(KetNoiCSDL giaTri) {
        csdl = Objects.requireNonNull(giaTri);
    }

    public static <KieuDuLieu> KieuDuLieu thucHienGiaoDich(CongViec<KieuDuLieu> lamViec) {
        Connection hienCo = HIEN_TAI.get();
        if (hienCo != null) {
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
        if (csdl == null) throw new IllegalStateException("Chưa cấu hình kết nối SQL Server.");
        try (Connection ketNoi = csdl.mo()) {
            ketNoi.setAutoCommit(false);
            HIEN_TAI.set(ketNoi);
            try {
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

    private static RuntimeException taoLoi(Exception loi) {
        return loi instanceof RuntimeException loi2
            ? loi2
            : new TruyCapDuLieuException("Không thể truy cập SQL Server: " + loi.getMessage(), loi);
    }

    private static <KieuDuLieu> KieuDuLieu dungKetNoi(CongViec<KieuDuLieu> lamViec) {
        Connection hienTai = HIEN_TAI.get();
        if (hienTai == null) return thucHienGiaoDich(lamViec);
        try {
            return lamViec.chay(hienTai);
        } catch (Exception loi) {
            throw taoLoi(loi);
        }
    }

    private static PreparedStatement chuanBi(Connection ketNoi, String sql, Object... cacDoiSo)
            throws SQLException {
        PreparedStatement cauLenh = ketNoi.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        for (int chiSo = 0; chiSo < cacDoiSo.length; chiSo++) {
            Object giaTri = cacDoiSo[chiSo];
            if (giaTri instanceof LocalDate ngay)
                cauLenh.setDate(chiSo + 1, java.sql.Date.valueOf(ngay));
            else if (giaTri instanceof LocalDateTime thoiGian)
                cauLenh.setTimestamp(chiSo + 1, Timestamp.valueOf(thoiGian));
            else if (giaTri instanceof String chuoi) cauLenh.setNString(chiSo + 1, chuoi);
            else cauLenh.setObject(chiSo + 1, giaTri);
        }
        return cauLenh;
    }

    public static List<Object[]> truyVan(String sql, Object... cacDoiSo) {
        return dungKetNoi(
                ketNoi -> {
                    try (PreparedStatement cauLenh = chuanBi(ketNoi, sql, cacDoiSo);
                            ResultSet tapKetQua = cauLenh.executeQuery()) {
                        List<Object[]> cacDong = new ArrayList<>();
                        int kichThuoc = tapKetQua.getMetaData().getColumnCount();
                        while (tapKetQua.next()) {
                            Object[] dong = new Object[kichThuoc];
                            for (int chiSo = 0; chiSo < kichThuoc; chiSo++)
                                dong[chiSo] = tapKetQua.getObject(chiSo + 1);
                            cacDong.add(dong);
                        }
                        return cacDong;
                    }
                });
    }

    public static int capNhat(String sql, Object... cacDoiSo) {
        return dungKetNoi(
                ketNoi -> {
                    try (PreparedStatement cauLenh = chuanBi(ketNoi, sql, cacDoiSo)) {
                        return cauLenh.executeUpdate();
                    }
                });
    }

    public static long them(String sql, Object... cacDoiSo) {
        return dungKetNoi(
                ketNoi -> {
                    try (PreparedStatement cauLenh = chuanBi(ketNoi, sql, cacDoiSo)) {
                        cauLenh.executeUpdate();
                        try (ResultSet cacKhoa = cauLenh.getGeneratedKeys()) {
                            if (cacKhoa.next()) {
                                return cacKhoa.getLong(1);
                            }
                        }
                        if (cacDoiSo != null && cacDoiSo.length > 0 && cacDoiSo[0] != null) {
                            long id = DuLieu.layIdCauNoi(null, String.valueOf(cacDoiSo[0]));
                            if (id > 0) return id;
                            long hash = Math.abs((long) cacDoiSo[0].hashCode());
                            return hash == 0 ? 1L : hash;
                        }
                        return 1L;
                    }
                });
    }

    public static Object layGiaTriDon(String sql, Object... cacDoiSo) {
        List<Object[]> cacDong = truyVan(sql, cacDoiSo);
        return cacDong.isEmpty() ? null : cacDong.get(0)[0];
    }

    public static long laySo(String sql, Object... cacDoiSo) {
        Object giaTri = layGiaTriDon(sql, cacDoiSo);
        return giaTri == null ? 0 : ((Number) giaTri).longValue();
    }
}
