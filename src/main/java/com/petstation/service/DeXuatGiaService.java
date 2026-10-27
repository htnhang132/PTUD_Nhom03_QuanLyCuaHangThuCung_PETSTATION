package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.DeXuatGiaDAO;
import com.petstation.dao.impl.DeXuatGiaImpl;
import com.petstation.model.Quyen;
import com.petstation.model.VaiTro;

/** Gửi và áp dụng đề xuất giá trong cùng transaction với phê duyệt. */
public final class DeXuatGiaService {
    private static final DeXuatGiaDAO deXuatGiaDAO = new DeXuatGiaImpl();

    private DeXuatGiaService() {}

    public static PheDuyetService.YeuCau gui(String ma, long gia, String lyDo) {
        yeuCau(Quyen.PRICE_PROPOSE);
        if (gia <= 0 || lyDo == null || lyDo.isBlank())
            throw new IllegalArgumentException("Giá đề xuất phải dương và cần lý do.");
        return thucHienGiaoDich(
                c -> {
                    var dong = deXuatGiaDAO.timSanPham(ma);
                    if (dong.isEmpty())
                        throw new IllegalArgumentException("Không tìm thấy sản phẩm.");
                    long cu = ((Number) dong.get(0)[1]).longValue();
                    if (cu == gia)
                        throw new IllegalArgumentException("Giá đề xuất trùng giá hiện tại.");
                    long id =
                            deXuatGiaDAO.them(
                                    ((Number) dong.get(0)[0]).longValue(),
                                    layIdTaiKhoan(),
                                    cu,
                                    gia,
                                    lyDo);
                    return PheDuyetService.guiThaoTac(
                            PheDuyetService.Loai.PRICE,
                            lyDo,
                            ma + ": " + cu,
                            ma + ": " + gia,
                            "PRICE_CHANGE",
                            id,
                            "");
                });
    }

    static void apDung(long id) {
        yeuCau(Quyen.PRICE_APPROVE);
        if (layPhien().docVaiTro() != VaiTro.STORE_MANAGER)
            throw new PhanQuyenException("Chỉ cửa hàng trưởng được áp dụng giá bán.");
        var dong = deXuatGiaDAO.timChoDuyet(id);
        if (dong.isEmpty()) throw new IllegalArgumentException("Đề xuất giá đã xử lý.");
        Object[] d = dong.get(0);
        if (((Number) d[1]).longValue() != ((Number) d[3]).longValue())
            throw new IllegalArgumentException(
                    "Giá hiện tại đã thay đổi. Hãy từ chối và gửi đề xuất mới.");
        deXuatGiaDAO.apDung(
                id, ((Number) d[0]).longValue(), ((Number) d[2]).longValue(), layIdTaiKhoan());
        NhatKyHeThongService.ghiNhatKy("Áp dụng giá đề xuất", id + " / " + d[1] + " → " + d[2]);
    }

    static void tuChoi(long id) {
        yeuCau(Quyen.PRICE_APPROVE);
        deXuatGiaDAO.tuChoi(id);
    }
}
