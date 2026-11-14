package com.petstation.service.pheduyet;

import com.petstation.dao.pheduyet.DeXuatGiaDAO;
import com.petstation.dao.pheduyet.impl.DeXuatGiaDAOImpl;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.model.chung.Quyen;
import com.petstation.model.pheduyet.DeXuatGia;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;

import java.util.Objects;
import java.util.Optional;

public final class DeXuatGiaService {
    private final DeXuatGiaDAO deXuatGiaDAO;
    private final PheDuyetService pheDuyetService;
    private final NhatKyHeThongService nhatKyService;
    private final PhienDangNhapService phienService;

    public DeXuatGiaService() {
        this(
                new DeXuatGiaDAOImpl(),
                new PheDuyetService(),
                NhatKyHeThongService.macDinh(),
                PhienDangNhapService.macDinh());
    }

    public DeXuatGiaService(DeXuatGiaDAO deXuatGiaDAO) {
        this(
                deXuatGiaDAO,
                new PheDuyetService(),
                NhatKyHeThongService.macDinh(),
                PhienDangNhapService.macDinh());
    }

    public DeXuatGiaService(
            DeXuatGiaDAO deXuatGiaDAO,
            PheDuyetService pheDuyetService,
            NhatKyHeThongService nhatKyService,
            PhienDangNhapService phienService) {
        this.deXuatGiaDAO = Objects.requireNonNull(deXuatGiaDAO);
        this.pheDuyetService = Objects.requireNonNull(pheDuyetService);
        this.nhatKyService = Objects.requireNonNull(nhatKyService);
        this.phienService = Objects.requireNonNull(phienService);
    }

    public PheDuyetService.YeuCau gui(String ma, long gia, String lyDo) {
        phienService.yeuCau(Quyen.DE_XUAT_GIA);
        if (gia <= 0 || lyDo == null || lyDo.isBlank())
            throw new IllegalArgumentException("Giá đề xuất phải dương và cần lý do.");
        return QuanLyGiaoDich.macDinh()
                .thucThi(
                        c -> {
                            DeXuatGiaDAO.ThongTinSanPham sanPham =
                                    deXuatGiaDAO
                                            .timSanPham(ma)
                                            .orElseThrow(
                                                    () ->
                                                            new IllegalArgumentException(
                                                                    "Không tìm thấy sản phẩm."));
                            long cu = sanPham.giaBan();
                            if (cu == gia)
                                throw new IllegalArgumentException(
                                        "Giá đề xuất trùng giá hiện tại.");
                            String maSanPham =
                                    deXuatGiaDAO.them(
                                            sanPham.maSanPham(),
                                            phienService.layMaNhanVien(),
                                            cu,
                                            gia,
                                            lyDo);
                            return pheDuyetService.guiThaoTac(
                                    PheDuyetService.Loai.GIA_BAN,
                                    lyDo,
                                    ma + ": " + cu,
                                    ma + ": " + gia,
                                    "DOI_GIA",
                                    maSanPham,
                                    "");
                        });
    }

    public void apDung(String maDoiTuongHoacYeuCau) {
        phienService.yeuCau(Quyen.DUYET_GIA);
        DeXuatGiaDAO.ThongTinDeXuat d =
                deXuatGiaDAO
                        .timChoDuyet(maDoiTuongHoacYeuCau)
                        .orElseThrow(() -> new IllegalStateException("Đề xuất giá đã xử lý."));
        if (d.giaCu() != d.giaHienTai())
            throw new IllegalArgumentException(
                    "Giá hiện tại đã thay đổi. Hãy từ chối và gửi đề xuất mới.");
        deXuatGiaDAO.apDung(
                maDoiTuongHoacYeuCau, d.maSanPham(), d.giaMoi(), phienService.layMaNhanVien());
        nhatKyService.ghiNhatKy(
                "Áp dụng giá đề xuất", d.maSanPham() + " / " + d.giaCu() + " → " + d.giaMoi());
    }

    public void tuChoi(String maDoiTuongHoacYeuCau) {
        phienService.yeuCau(Quyen.DUYET_GIA);
        deXuatGiaDAO.tuChoi(maDoiTuongHoacYeuCau);
    }

    public Optional<DeXuatGia> timDeXuat(String maYeuCau) {
        phienService.yeuCau(Quyen.DUYET_GIA);
        return deXuatGiaDAO.timDeXuatTheoMa(maYeuCau);
    }
}
