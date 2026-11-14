package com.petstation.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

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

    static Connection layKetNoiHienTai() {
        return HIEN_TAI.get();
    }

    static void ganKetNoiHienTai(Connection ketNoi) {
        if (ketNoi == null) HIEN_TAI.remove();
        else HIEN_TAI.set(ketNoi);
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
        if (csdl == null) throw new IllegalArgumentException("Chưa cấu hình kết nối SQL Server.");
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
                : new IllegalStateException(
                        "Không thể truy cập SQL Server: " + loi.getMessage(), loi);
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

    public static int demSoThamSo(String sql) {
        if (sql == null) return 0;
        int dem = 0;
        boolean trongChuoi = false;
        boolean trongCommentDon = false;
        boolean trongCommentKhoi = false;
        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);
            if (trongChuoi) {
                if (c == '\'') {
                    if (i + 1 < sql.length() && sql.charAt(i + 1) == '\'') {
                        i++;
                    } else {
                        trongChuoi = false;
                    }
                }
            } else if (trongCommentDon) {
                if (c == '\n' || c == '\r') {
                    trongCommentDon = false;
                }
            } else if (trongCommentKhoi) {
                if (c == '*' && i + 1 < sql.length() && sql.charAt(i + 1) == '/') {
                    trongCommentKhoi = false;
                    i++;
                }
            } else {
                if (c == '\'') {
                    trongChuoi = true;
                } else if (c == '-' && i + 1 < sql.length() && sql.charAt(i + 1) == '-') {
                    trongCommentDon = true;
                    i++;
                } else if (c == '/' && i + 1 < sql.length() && sql.charAt(i + 1) == '*') {
                    trongCommentKhoi = true;
                    i++;
                } else if (c == '?') {
                    dem++;
                }
            }
        }
        return dem;
    }

    private static Object[] chuanHoaCacDoiSo(Object... cacDoiSo) {
        if (cacDoiSo == null) return new Object[0];
        if (cacDoiSo.length == 1 && cacDoiSo[0] != null) {
            if (cacDoiSo[0] instanceof Object[] mang) {
                return mang;
            }
            if (cacDoiSo[0] instanceof Collection<?> boSuuTap) {
                return boSuuTap.toArray();
            }
        }
        return cacDoiSo;
    }

    private static void ganThamSo(PreparedStatement cauLenh, int viTri, Object giaTri)
            throws SQLException {
        if (giaTri == null) {
            cauLenh.setObject(viTri, null);
        } else if (giaTri instanceof LocalDate ngay) {
            cauLenh.setDate(viTri, Date.valueOf(ngay));
        } else if (giaTri instanceof LocalDateTime thoiGian) {
            cauLenh.setTimestamp(viTri, Timestamp.valueOf(thoiGian));
        } else if (giaTri instanceof String chuoi) {
            cauLenh.setNString(viTri, chuoi);
        } else {
            cauLenh.setObject(viTri, giaTri);
        }
    }

    private static PreparedStatement chuanBi(Connection ketNoi, String sql, Object... cacDoiSo)
            throws SQLException {
        Object[] doiSoChuanHoa = chuanHoaCacDoiSo(cacDoiSo);
        int soThamSoYeuCau = demSoThamSo(sql);
        PreparedStatement cauLenh = ketNoi.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        int soLuong =
                soThamSoYeuCau > 0
                        ? Math.min(doiSoChuanHoa.length, soThamSoYeuCau)
                        : doiSoChuanHoa.length;
        for (int chiSo = 0; chiSo < soLuong; chiSo++) {
            ganThamSo(cauLenh, chiSo + 1, doiSoChuanHoa[chiSo]);
        }
        if (soThamSoYeuCau > doiSoChuanHoa.length) {
            for (int chiSo = doiSoChuanHoa.length; chiSo < soThamSoYeuCau; chiSo++) {
                cauLenh.setObject(chiSo + 1, null);
            }
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

    public static <Kieu> List<Kieu> truyVanDanhSach(
            String sql, Function<Object[], Kieu> anhXa, Object... cacDoiSo) {
        List<Object[]> ketQua = truyVan(sql, cacDoiSo);
        List<Kieu> ds = new ArrayList<>(ketQua.size());
        for (Object[] dong : ketQua) {
            ds.add(anhXa.apply(dong));
        }
        return ds;
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
