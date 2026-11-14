package com.petstation.gui.khachhang;

import static com.petstation.gui.chung.TruongDanhMuc.docNgay;

import com.petstation.gui.chung.QuanLyDanhMucPanel;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.model.chung.Quyen;
import com.petstation.service.chung.DieuKienTaiDuLieu;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.khachhang.TichDiemService;
import com.petstation.service.khachhang.VoucherService;
import com.petstation.util.DinhDang;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

// Màn hình con (Tab 2) trong Danh mục > Điểm thưởng và voucher
public final class DanhMucVoucherPanel extends QuanLyDanhMucPanel {
    private final TichDiemService tichDiemService = new TichDiemService();
    private final Map<String, Boolean> quyenSuaCache = new ConcurrentHashMap<>();
    private final Set<String> dangTaiQuyenSua = ConcurrentHashMap.newKeySet();
    private final AtomicReference<BoChonNhieuKhachHang> boChonKhachRef;

    // 1. KIỂM TRA PHÂN QUYỀN & RÀNG BUỘC CHỈNH SỬA BẢN GHI VOUCHER

    @Override
    protected boolean coTheChinhSua() {
        return PhienDangNhapService.macDinh().layPhien().kiemTraCo(Quyen.QUAN_LY_KHUYEN_MAI);
    }

    @Override
    protected boolean coTheSuaBanGhi(Object[] dong) {
        if (dong == null || dong[0] == null) return false;
        if (DinhDang.doc(String.valueOf(dong[4])) > 0) return false;
        String ma = String.valueOf(dong[0]);
        Boolean cached = quyenSuaCache.get(ma);
        if (cached != null) return cached;
        if (!SwingUtilities.isEventDispatchThread() || !DieuKienTaiDuLieu.coTheTachLuong()) {
            boolean choPhep = tichDiemService.coTheSuaDanhMuc(ma);
            quyenSuaCache.put(ma, choPhep);
            return choPhep;
        }
        if (dangTaiQuyenSua.add(ma)) {
            TacVuNen.chayCoPhien(
                    this,
                    this::isDisplayable,
                    () -> tichDiemService.coTheSuaDanhMuc(ma),
                    choPhep -> {
                        dangTaiQuyenSua.remove(ma);
                        quyenSuaCache.put(ma, Boolean.TRUE.equals(choPhep));
                        capNhatTrangThaiNut();
                    },
                    ex -> dangTaiQuyenSua.remove(ma));
        }
        return false;
    }

    @Override
    protected String layLyDoKhongSuaBanGhi(Object[] dong) {
        if (dong == null || dong[0] == null) return null;
        if (DinhDang.doc(String.valueOf(dong[4])) > 0) {
            return "Voucher đã có lượt sử dụng ("
                    + dong[4]
                    + "), chỉ được tra cứu chứ không sửa hoặc phát hành lại.";
        }
        if (Boolean.FALSE.equals(quyenSuaCache.get(String.valueOf(dong[0])))) {
            return "Voucher đổi điểm giữ nguyên mệnh giá, hạn dùng, chủ sở hữu và 1 lượt; không"
                    + " sửa qua danh mục.";
        }
        return null;
    }

    @Override
    protected void khiLamMoiDuLieu() {
        quyenSuaCache.clear();
        BoChonNhieuKhachHang boChon = boChonKhachRef.get();
        if (boChon != null) boChon.lamMoiNguon();
    }

    // 2. KHỞI TẠO MÀN HÌNH & CẤU HÌNH BỘ LỌC / CHUẨN HÓA TRẠNG THÁI VOUCHER

    public DanhMucVoucherPanel() {
        this(new AtomicReference<>());
    }

    private DanhMucVoucherPanel(AtomicReference<BoChonNhieuKhachHang> boChonKhachRef) {
        super(
                taoCauHinh(),
                taoCacTruong(boChonKhachRef),
                VoucherService.macDinh()
                        .chuanBi(
                                !SwingUtilities.isEventDispatchThread()
                                        || !DieuKienTaiDuLieu.coTheTachLuong()));
        this.boChonKhachRef = boChonKhachRef;
    }

    private static CauHinh taoCauHinh() {
        return CauHinh.tao("Danh mục voucher", "voucher")
                .datTienTo("VOUCHER")
                .themBoLoc("Chủ sở hữu", d -> String.valueOf(d[7]))
                .themBoLoc("Phạm vi", d -> String.valueOf(d[8]))
                .timKiem("Tìm theo mã voucher, khách hàng...")
                .datTrangThai(6, "Còn hiệu lực", "Ngừng")
                .datBoChuanHoa(
                        dongDuLieu -> {
                            LocalDate hanDung = docNgay(dongDuLieu[2]);
                            String trangThai = String.valueOf(dongDuLieu[6]);
                            long soLuong = DinhDang.doc(String.valueOf(dongDuLieu[3]));
                            long daDung = DinhDang.doc(String.valueOf(dongDuLieu[4]));
                            if ("Ngừng".equals(trangThai)) return;
                            if (hanDung != null && hanDung.isBefore(LocalDate.now())) {
                                dongDuLieu[6] = "Hết hạn";
                            } else if (soLuong >= 0 && daDung >= soLuong) {
                                dongDuLieu[6] = "Hết lượt";
                            } else if (!"Hết hạn".equals(trangThai)
                                    && !"Hết lượt".equals(trangThai)) {
                                dongDuLieu[6] = "Còn hiệu lực";
                            }
                        })
                .datBoKiemTra(DanhMucVoucherPanel::kiemTraVoucher);
    }

    private static String kiemTraVoucher(Object[] dongDuLieu) {
        long menhGia = DinhDang.doc(String.valueOf(dongDuLieu[1]));
        if (menhGia <= 0) {
            return "Giá trị voucher phải lớn hơn 0₫.";
        }
        long donToiThieu = DinhDang.doc(String.valueOf(dongDuLieu[5]));
        if (donToiThieu < 0) {
            return "Đơn tối thiểu không được âm.";
        }
        LocalDate hanDung = docNgay(dongDuLieu[2]);
        String tt = String.valueOf(dongDuLieu[6]);
        if (hanDung != null && hanDung.isBefore(LocalDate.now())) {
            if ("Còn hiệu lực".equals(tt)) {
                return "Hạn dùng đã qua, không thể lưu ở trạng thái 'Còn hiệu lực'.";
            }
        } else if ("Hết hạn".equals(tt)) {
            return "Hạn dùng chưa qua; hãy chọn 'Còn hiệu lực' hoặc 'Ngừng'.";
        }
        long soLuong = DinhDang.doc(String.valueOf(dongDuLieu[3]));
        long daDung = DinhDang.doc(String.valueOf(dongDuLieu[4]));
        if ("Hết lượt".equals(tt) && daDung < soLuong) {
            return "Voucher chưa dùng hết lượt; hãy chọn 'Còn hiệu lực' hoặc 'Ngừng'.";
        }
        if (String.valueOf(dongDuLieu[7]).isBlank()) {
            return "Vui lòng chọn ít nhất một khách hàng sở hữu voucher.";
        }
        return VoucherService.kiemTraVoucherSoLuong(soLuong, daDung);
    }

    // 3. ĐỊNH NGHĨA CÁC CỘT BẢNG & TRƯỜNG NHẬP LIỆU VOUCHER

    private static List<TruongDuLieu> taoCacTruong(
            AtomicReference<BoChonNhieuKhachHang> boChonKhachRef) {
        return List.of(
                TruongDuLieu.tao("Mã voucher", Kieu.VAN_BAN).batBuoc().kiemTraDuyNhat(),
                TruongDuLieu.tao("Giá trị (₫)", Kieu.TIEN_TE).batBuoc(),
                TruongDuLieu.tao("Hạn dùng", Kieu.NGAY).batBuoc(),
                TruongDuLieu.tao("Số lượt (voucher cá nhân: 1)", Kieu.SO_NGUYEN)
                        .chiDoc()
                        .datMacDinh("1"),
                TruongDuLieu.tao("Đã dùng", Kieu.SO_NGUYEN).chiDoc().datMacDinh("0"),
                TruongDuLieu.tao("Đơn tối thiểu", Kieu.TIEN_TE).datMacDinh("0"),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON)
                        .datLuaChon("Còn hiệu lực", "Hết hạn", "Hết lượt", "Ngừng"),
                TruongDuLieu.tao("Khách hàng sở hữu", Kieu.TUY_CHINH)
                        .batBuoc()
                        .datBoSoanThao(
                                () -> {
                                    BoChonNhieuKhachHang bo = new BoChonNhieuKhachHang();
                                    boChonKhachRef.set(bo);
                                    return bo;
                                }),
                TruongDuLieu.tao("Phạm vi", Kieu.HOP_CHON).datLuaChon("Chỉ Spa", "Mọi hóa đơn"));
    }
}
