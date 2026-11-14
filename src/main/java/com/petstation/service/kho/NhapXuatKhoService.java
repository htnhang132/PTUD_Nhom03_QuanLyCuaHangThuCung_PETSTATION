package com.petstation.service.kho;

import static com.petstation.util.DuLieu.chuyenThanhChuoi;
import static com.petstation.util.DuLieu.kiemTraDuong;
import static com.petstation.util.DuLieu.taoMa;

import com.petstation.dao.kho.NhapXuatKhoDAO;
import com.petstation.dao.kho.impl.NhapXuatKhoDAOImpl;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.model.chung.LoaiPhieuXuat;
import com.petstation.model.chung.Quyen;
import com.petstation.model.kho.ChiTietPhieuXuat;
import com.petstation.model.kho.LoHang;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.hethong.CaiDatService;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.pheduyet.PheDuyetService;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

// Tầng xử lý nghiệp vụ Nhập kho & Xuất hủy kho
public final class NhapXuatKhoService {
    private final NhapXuatKhoDAO nhapXuatKhoDAO;

    public NhapXuatKhoService() {
        this(new NhapXuatKhoDAOImpl());
    }

    public NhapXuatKhoService(NhapXuatKhoDAO nhapXuatKhoDAO) {
        this.nhapXuatKhoDAO = Objects.requireNonNull(nhapXuatKhoDAO);
    }

    // 1. NGHIỆP VỤ NHẬP KHO & CÔNG NỢ NHÀ CUNG CẤP

    public String nhapKho(
            String nhaCungCap,
            String sanPham,
            String loHang,
            LocalDate hanSuDung,
            int soLuong,
            long giaNhap,
            long daTra) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.NHAP_KHO);
        kiemTraDuong(soLuong);
        kiemTraDuong(giaNhap);
        if (loHang == null
                || loHang.isBlank()
                || daTra < 0
                || daTra > Math.multiplyExact((long) soLuong, giaNhap))
            throw new IllegalArgumentException("Số lô hoặc tiền đã trả không hợp lệ.");
        return QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            DanhMucService.macDinh()
                                    .xacNhanTonTai("san_pham", "ma_san_pham", sanPham);
                            DanhMucService.macDinh()
                                    .xacNhanTonTai("nha_cung_cap", "ma_nha_cung_cap", nhaCungCap);
                            if (nhapXuatKhoDAO.demNhapKho(sanPham) > 0
                                    && (hanSuDung == null || hanSuDung.isBefore(LocalDate.now())))
                                throw new IllegalArgumentException(
                                        "Sản phẩm theo dõi HSD cần ngày hết hạn còn hiệu lực.");
                            if (nhapXuatKhoDAO.demNhaCungCapDangHopTac(nhaCungCap) == 0)
                                throw new IllegalArgumentException(
                                        "Nhà cung cấp đã ngừng hợp tác.");
                            String maPhieu =
                                    nhapXuatKhoDAO.themNhapKho(
                                            taoMa("PNK"),
                                            nhaCungCap,
                                            PhienDangNhapService.macDinh().layMaNhanVien(),
                                            soLuong * giaNhap,
                                            daTra);
                            Optional<LoHang> loHienCo = nhapXuatKhoDAO.timNhapKho(sanPham, loHang);
                            String maLo;
                            if (loHienCo.isEmpty()) {
                                nhapXuatKhoDAO.themLoHang(sanPham, loHang, hanSuDung, soLuong);
                                Optional<LoHang> vuaTao =
                                        nhapXuatKhoDAO.timNhapKho(sanPham, loHang);
                                maLo = vuaTao.map(LoHang::getMaLo).orElse(loHang);
                            } else {
                                LoHang loCu = loHienCo.get();
                                maLo = loCu.getMaLo();
                                LocalDate cu = loCu.getHanSuDung();
                                if (!Objects.equals(hanSuDung, cu))
                                    throw new IllegalArgumentException(
                                            "Lô đã tồn tại với hạn sử dụng khác.");
                                nhapXuatKhoDAO.capNhatNhapKho(soLuong, maLo);
                            }
                            nhapXuatKhoDAO.themChiTietNhapKho(
                                    maPhieu, sanPham, maLo, hanSuDung, soLuong, giaNhap);
                            nhapXuatKhoDAO.capNhatGiaNhapGanNhat(giaNhap, sanPham);
                            NhatKyHeThongService.macDinh()
                                    .ghiNhatKy(
                                            "Nhập kho", sanPham + " / " + loHang + " x " + soLuong);
                            return maPhieu;
                        });
    }

    public List<Object[]> layNhapKhoChiTiet(String maPhieuNhap) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.NHAP_KHO);
        return nhapXuatKhoDAO.layNhapKhoChiTiet(maPhieuNhap).stream()
                .map(
                        ct ->
                                new Object[] {
                                    ct.getSanPham() != null
                                            ? ct.getSanPham().getTenSanPham()
                                            : null,
                                    ct.getLoHang() != null ? ct.getLoHang().getMaLo() : null,
                                    ct.getLoHang() != null ? ct.getLoHang().getHanSuDung() : null,
                                    ct.getSoLuong(),
                                    ct.getGiaNhap()
                                })
                .toList();
    }

    public long layCongNoConLai(String maPhieuNhap) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.NHAP_KHO);
        return nhapXuatKhoDAO.layCongNoConLai(maPhieuNhap);
    }

    public void thanhToanNhaCungCap(String maPhieuNhap, long daTra) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.NHAP_KHO);
        if (nhapXuatKhoDAO.layCongNoConLai(maPhieuNhap) <= 0)
            throw new IllegalArgumentException("Không tồn tại công nợ");
        if (daTra <= 0) throw new IllegalArgumentException("Số tiền phải lớn hơn 0.");
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            if (nhapXuatKhoDAO.themThanhToanNhaCungCap(maPhieuNhap, daTra) != 1)
                                throw new IllegalArgumentException("Số tiền vượt công nợ còn lại.");
                            NhatKyHeThongService.macDinh()
                                    .ghiNhatKy("Trả công nợ", maPhieuNhap + " / " + daTra);
                            return null;
                        });
    }

    // 2. NGHIỆP VỤ XUẤT HỦY HÀNG HÓA

    public String ghiHuy(String maLo, int soLuong, String lyDo) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.XUAT_HUY_KHO);
        kiemTraDuong(soLuong);
        if (lyDo == null || lyDo.isBlank())
            throw new IllegalArgumentException("Cần lý do xuất kho.");
        return QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            Optional<LoHang> loTim = nhapXuatKhoDAO.timXuatHuy(maLo);
                            if (loTim.isEmpty() || loTim.get().getSoLuongTon() < soLuong)
                                throw new IllegalStateException("Không đủ tồn lô.");
                            LoHang lo = loTim.get();
                            long giaNhap = lo.getGiaNhap() * soLuong;
                            String maPhieuXuat =
                                    nhapXuatKhoDAO.themXuatHuy(
                                            taoMa("PXK"),
                                            PhienDangNhapService.macDinh().layMaNhanVien(),
                                            lyDo,
                                            giaNhap);
                            nhapXuatKhoDAO.capNhatXuatHuy(maPhieuXuat, maLo, soLuong, giaNhap);
                            if (giaNhap >= CaiDatService.macDinh().ghiGioiHanHuyPheDuyet())
                                new PheDuyetService()
                                        .guiThaoTac(
                                                PheDuyetService.Loai.XUAT_HUY,
                                                lyDo,
                                                "Tồn " + lo.getSoLuongTon(),
                                                "Xuất " + soLuong,
                                                LoaiPhieuXuat.XUAT_HUY.maDB(),
                                                maPhieuXuat,
                                                "");
                            else apDungXuatHuy(maPhieuXuat);
                            return maPhieuXuat;
                        });
    }

    public void apDungXuatHuy(String maPhieuXuat) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.XU_LY_PHE_DUYET);
        if (nhapXuatKhoDAO.demApDungXuatHuy(maPhieuXuat) == 0)
            throw new IllegalStateException("Phiếu xuất đã xử lý.");
        for (ChiTietPhieuXuat dongDuLieu : nhapXuatKhoDAO.timApDungXuatHuy(maPhieuXuat))
            if (nhapXuatKhoDAO.truTonXuatHuy(dongDuLieu.getSoLuong(), dongDuLieu.getMaLo()) != 1)
                throw new IllegalArgumentException("Lô không còn đủ tồn, không thể duyệt xuất.");
        nhapXuatKhoDAO.capNhatApDungXuatHuy(maPhieuXuat);
        NhatKyHeThongService.macDinh().ghiNhatKy("Xuất kho", chuyenThanhChuoi(maPhieuXuat));
    }
}
