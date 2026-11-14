package com.petstation.gui.chung;

import com.petstation.dto.PhienDangNhap;
import com.petstation.service.hethong.PhienDangNhapService;

import java.awt.Component;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import javax.swing.SwingWorker;

/** Bộ chạy tác vụ nền thống nhất trên SwingWorker, tách truy vấn CSDL khỏi EDT. */
public final class TacVuNen {
    private TacVuNen() {}

    public static <T> void chay(
            Callable<T> tacVu, Consumer<T> khiXong, Consumer<Throwable> khiLoi) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return tacVu.call();
            }

            @Override
            protected void done() {
                try {
                    T ketQua = get();
                    if (khiXong != null) khiXong.accept(ketQua);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    if (khiLoi != null) khiLoi.accept(ex.getCause());
                } catch (RuntimeException ex) {
                    if (khiLoi != null) khiLoi.accept(ex);
                }
            }
        }.execute();
    }

    public static <T> void chayCoPhien(
            PhienDangNhap phien,
            BooleanSupplier conHopLe,
            Callable<T> tacVu,
            Runnable truocKhiApDung,
            Consumer<T> khiXong,
            Consumer<Throwable> khiLoi) {
        PhienDangNhapService dichVuPhien = PhienDangNhapService.macDinh();
        chay(
                () -> dichVuPhien.trongPhien(phien, tacVu),
                ketQua -> {
                    if (conHopLe != null && !conHopLe.getAsBoolean()) return;
                    if (dichVuPhien.layPhienHienTai() != phien) return;
                    if (truocKhiApDung != null) truocKhiApDung.run();
                    if (khiXong != null) khiXong.accept(ketQua);
                },
                loi -> {
                    if (conHopLe != null && !conHopLe.getAsBoolean()) return;
                    if (dichVuPhien.layPhienHienTai() != phien) return;
                    if (truocKhiApDung != null) truocKhiApDung.run();
                    if (khiLoi != null) khiLoi.accept(loi);
                });
    }

    public static <T> void chayCoPhien(
            Component cha, BooleanSupplier conHopLe, Callable<T> tacVu, Consumer<T> khiXong) {
        chayCoPhien(
                cha,
                conHopLe,
                tacVu,
                khiXong,
                loi -> HopThoai.hienThiCanhBao(cha, thongDiepLoi(loi)));
    }

    public static <T> void chayCoPhien(
            Component cha,
            BooleanSupplier conHopLe,
            Callable<T> tacVu,
            Consumer<T> khiXong,
            Consumer<Throwable> khiLoi) {
        PhienDangNhap phien = PhienDangNhapService.macDinh().layPhienHienTai();
        chayCoPhien(phien, conHopLe, tacVu, null, khiXong, khiLoi);
    }

    public static void chayCoPhien(
            Component cha, BooleanSupplier conHopLe, Runnable tacVu, Runnable khiXong) {
        chayCoPhien(
                cha,
                conHopLe,
                tacVu,
                khiXong,
                loi -> HopThoai.hienThiCanhBao(cha, thongDiepLoi(loi)));
    }

    public static void chayCoPhien(
            Component cha,
            BooleanSupplier conHopLe,
            Runnable tacVu,
            Runnable khiXong,
            Consumer<Throwable> khiLoi) {
        PhienDangNhap phien = PhienDangNhapService.macDinh().layPhienHienTai();
        chayCoPhien(
                phien,
                conHopLe,
                () -> {
                    tacVu.run();
                    return null;
                },
                null,
                ignored -> {
                    if (khiXong != null) khiXong.run();
                },
                khiLoi);
    }

    public static String thongDiepLoi(Throwable loi) {
        if (loi == null) return "Đã xảy ra lỗi không xác định.";
        Throwable goc = loi;
        while (goc.getCause() != null) goc = goc.getCause();
        return goc.getMessage() != null ? goc.getMessage() : goc.toString();
    }

    public static void baoLoi(Component cha, Throwable loi) {
        HopThoai.hienThiLoi(cha, thongDiepLoi(loi));
    }
}
