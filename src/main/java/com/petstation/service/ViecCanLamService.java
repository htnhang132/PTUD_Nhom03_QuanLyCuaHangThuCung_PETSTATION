package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.ViecCanLamDAO;
import com.petstation.dao.impl.ViecCanLamImpl;
import com.petstation.model.MaManHinh;
import com.petstation.model.Quyen;

import java.time.*;
import java.util.*;

/** Danh sách việc được lọc theo quyền trước khi truy vấn dữ liệu. */
public final class ViecCanLamService {
    private static final ViecCanLamDAO viecCanLamDAO = new ViecCanLamImpl();

    private ViecCanLamService() {}

    public record Viec(
            String khoa,
            String noiDung,
            LocalDateTime han,
            MaManHinh dich,
            long id,
            String ma,
            boolean nhac) {}

    public static List<Viec> lay() {
        var session = layPhien();
        List<Viec> viec = new ArrayList<>();
        if (session.kiemTraCo(Quyen.SPA_MANAGE)) {
            for (Object[] d : viecCanLamDAO.layLichSapDen()) {
                LocalDateTime start = ((java.sql.Timestamp) d[3]).toLocalDateTime();
                viec.add(
                        new Viec(
                                "HEN-" + d[0] + "-" + start,
                                "Spa: " + d[1] + " · " + d[2],
                                start,
                                MaManHinh.SPA_SCHEDULE,
                                num(d[0]),
                                d[1].toString(),
                                !start.minusHours(2).isAfter(LocalDateTime.now())));
            }
            for (Object[] d : viecCanLamDAO.laySpaChoXuLy())
                viec.add(
                        new Viec(
                                "SPA-" + d[0],
                                "Spa " + d[1] + " · " + d[2],
                                time(d[3]),
                                MaManHinh.SPA_INTAKE,
                                num(d[0]),
                                d[1].toString(),
                                false));
        }
        if (session.kiemTraCo(Quyen.HOTEL_MANAGE)) {
            for (Object[] d : viecCanLamDAO.layHotelSapTra()) {
                LocalDateTime end = time(d[3]);
                viec.add(
                        new Viec(
                                "HOTEL-" + d[0] + "-" + end,
                                (end.isBefore(LocalDateTime.now())
                                                ? "Quá giờ trả: "
                                                : "Sắp trả phòng: ")
                                        + d[1]
                                        + " · "
                                        + d[2],
                                end,
                                MaManHinh.CHECKOUT,
                                num(d[0]),
                                d[1].toString(),
                                true));
            }
            for (Object[] d : viecCanLamDAO.layPhongChoDon())
                viec.add(
                        new Viec(
                                "DON-" + d[0],
                                "Phòng chờ dọn: " + d[1],
                                null,
                                MaManHinh.HOTEL_BOOKING,
                                num(d[0]),
                                d[1].toString(),
                                false));
        }
        if (session.kiemTraCo(Quyen.PRODUCT_MANAGE)) {
            for (Object[] d : viecCanLamDAO.layTonThap())
                viec.add(
                        new Viec(
                                "TON-" + d[0],
                                "Tồn thấp: " + d[1] + " · " + d[2],
                                null,
                                MaManHinh.INVENTORY,
                                num(d[0]),
                                d[1].toString(),
                                false));
            for (Object[] d : viecCanLamDAO.layLoSapHetHan())
                viec.add(
                        new Viec(
                                "HSD-" + d[0],
                                "Hạn dùng: " + d[1] + " · lô " + d[2],
                                ((java.sql.Date) d[3]).toLocalDate().atStartOfDay(),
                                MaManHinh.BATCH_STOCK,
                                num(d[0]),
                                d[1].toString(),
                                false));
        }
        if (session.kiemTraCo(Quyen.PET_MANAGE))
            for (Object[] d : viecCanLamDAO.layVaccineDenHan()) {
                LocalDateTime end = ((java.sql.Date) d[4]).toLocalDate().atStartOfDay();
                viec.add(
                        new Viec(
                                "VAC-" + d[5] + "-" + end,
                                "Vaccine đến hạn: " + d[1] + " · " + d[2] + " · " + d[3],
                                end,
                                MaManHinh.PETS,
                                num(d[0]),
                                d[1].toString(),
                                true));
            }
        Map<String, Object[]> daXem = new HashMap<>();
        for (Object[] r : viecCanLamDAO.layTrangThaiNhac(session.taiKhoan().id()))
            daXem.put(r[0].toString(), r);
        viec.removeIf(
                v -> {
                    Object[] r = daXem.get(v.khoa());
                    return r != null
                            && (Boolean.TRUE.equals(r[1])
                                    || r[2] != null && time(r[2]).isAfter(LocalDateTime.now()));
                });
        viec.sort(Comparator.comparing(Viec::han, Comparator.nullsLast(Comparator.naturalOrder())));
        return List.copyOf(viec);
    }

    public static void danhDau(String khoa, boolean daXem, LocalDateTime hoan) {
        if (khoa == null || khoa.length() > 160)
            throw new IllegalArgumentException("Khóa nhắc việc không hợp lệ.");
        long user = layIdTaiKhoan();
        thucHienGiaoDich(
                c -> {
                    viecCanLamDAO.luuTrangThaiNhac(user, khoa, daXem, hoan);
                    return null;
                });
    }

    private static long num(Object o) {
        return ((Number) o).longValue();
    }

    private static LocalDateTime time(Object o) {
        return ((java.sql.Timestamp) o).toLocalDateTime();
    }
}
