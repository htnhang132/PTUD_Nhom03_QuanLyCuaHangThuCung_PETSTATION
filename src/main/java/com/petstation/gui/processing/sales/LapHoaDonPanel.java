package com.petstation.gui.processing.sales;

import com.petstation.gui.components.BoGocPanel;
import com.petstation.gui.components.CoSoPanel;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.KhaNangThanhCongCu;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.gui.components.common.BoChonKhachHang;
import com.petstation.model.GioHang;
import com.petstation.model.MaManHinh;
import com.petstation.model.PhienDangNhap;
import com.petstation.service.BanHangService;
import com.petstation.service.BarcodeUsbServer;
import com.petstation.service.PhienDangNhapService;
import com.petstation.util.TienTe;

import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LapHoaDonPanel extends CoSoPanel {
	private record SanPham(
			String ma,
			String ten,
			String nhomHang,
			long gia,
			int tonKho,
			String anh,
			String barcode) {
	}

	private static List<SanPham> laySanPham() {
		List<SanPham> ra = new ArrayList<>();
		for (Object[] dongDuLieu : BanHangService.layCoTheBanCacSanPham()) {
			String barcode = dongDuLieu.length > 6 && dongDuLieu[6] != null
					? String.valueOf(dongDuLieu[6]).trim()
					: "";
			ra.add(
					new SanPham(
							String.valueOf(dongDuLieu[0]),
							String.valueOf(dongDuLieu[1]),
							String.valueOf(dongDuLieu[2]),
							((Number) dongDuLieu[3]).longValue(),
							((Number) dongDuLieu[4]).intValue(),
							dongDuLieu[5] == null ? "" : String.valueOf(dongDuLieu[5]),
							barcode));
		}
		return ra;
	}

	private static final String[] CAC_NHOM_HANG = {
			"Tất cả", "Thức ăn", "Phụ kiện", "Chuồng - Nệm", "Vệ sinh", "Grooming", "Thuốc & Sức khỏe"
	};

	private static final int SO_THE_MOI_TRANG = 24;

	private final GioHang banHang;
	private final Consumer<MaManHinh> boDieuHuong;
	private final JPanel pnlLuoi = new JPanel(new GridLayout(0, 3, 12, 12));
	private final JPanel pnlVungSanPham = new JPanel(new CardLayout());
	private final javax.swing.table.DefaultTableModel moHinhBangNhanh = new javax.swing.table.DefaultTableModel(
			new String[] { "Mã SP", "Tên sản phẩm", "Nhóm", "Giá bán", "Tồn" }, 0) {
		@Override
		public boolean isCellEditable(int r, int c) {
			return false;
		}
	};
	private JTable tblBangNhanh;
	private final List<SanPham> danhSachDaLoc = new ArrayList<>();
	private final JLabel lblThongTinTrang = new JLabel();
	private final NutDinhDang btnTrangTruoc = new NutDinhDang("< Trước", NutDinhDang.KieuNut.TRONG_SUOT);
	private final NutDinhDang btnTrangSau = new NutDinhDang("Sau >", NutDinhDang.KieuNut.TRONG_SUOT);
	private final JComboBox<String> cboCheDoHienThi = new JComboBox<>(
			new String[] { "Thẻ hình ảnh", "Bảng nhanh (POS)" });
	private int trangHienTai = 0;
	private boolean cheDoBangNhanh = false;
	private final Timer bohenGioTimKiem;
	private final JPanel pnlGioHangCacDong = new JPanel();
	private final JLabel lblNhanTamTinh = new JLabel();
	private final JLabel lblNhanGiamGia = new JLabel();
	private final JLabel lblNhanTong = new JLabel();
	private final JLabel lblTrongGioHang = new JLabel("Chưa có sản phẩm. Bấm vào thẻ sản phẩm hoặc quét mã.");
	private final JTextField txtTimKiem;
	private final BoChonKhachHang pnlBoChon;
	private final JLabel lblNhanKhuyenMai = new JLabel("—");
	private final NutDinhDang btnNutThanhToan = new NutDinhDang("Thanh toán (F9)", NutDinhDang.KieuNut.CHINH);
	private final NutDinhDang btnNutTreo = new NutDinhDang("Lưu đơn nháp (F7)", NutDinhDang.KieuNut.PHU);
	private final NutDinhDang btnNutLay = new NutDinhDang("Mở đơn nháp (F8)", NutDinhDang.KieuNut.TRONG_SUOT);
	private final List<GioHang.DongHang> gioHang = new ArrayList<>();
	private final Deque<String> lichSuThem = new ArrayDeque<>();

	private List<SanPham> danhSachSanPham = new ArrayList<>();
	private final JLabel lblTrangThaiQuet = new JLabel("Sẵn sàng nhận mã vạch từ điện thoại.");
	private final JLabel lblKetNoiUsb = new JLabel("USB: chưa khởi tạo.");
	private BarcodeUsbServer barcodeUsbServer;
	private String nhomHang = "Tất cả";

	public LapHoaDonPanel(GioHang banHang, Consumer<MaManHinh> boDieuHuong) {
		this.banHang = banHang;
		this.boDieuHuong = boDieuHuong;
		this.pnlBoChon = new BoChonKhachHang(banHang, this::capNhatCacTong);
		this.bohenGioTimKiem = new Timer(
				120,
				e -> {
					trangHienTai = 0;
					dungLaiLuoi();
				});
		this.bohenGioTimKiem.setRepeats(false);
		this.banHang.themBoLangNghe(() -> {
			if (!java.util.Objects.equals(this.gioHang, banHang.getCacDong())) {
				Runnable dongBo = () -> {
					this.gioHang.clear();
					this.gioHang.addAll(banHang.getCacDong());
					this.pnlBoChon.dongBo();
					this.lamMoiGioHang();
				};
				if (javax.swing.SwingUtilities.isEventDispatchThread()) {
					dongBo.run();
				} else {
					javax.swing.SwingUtilities.invokeLater(dongBo);
				}
			}
		});

		setBorder(new EmptyBorder(14, 16, 14, 16));
		setLayout(new BorderLayout(16, 0));
		pnlLuoi.addComponentListener(
				new java.awt.event.ComponentAdapter() {
					@Override
					public void componentResized(java.awt.event.ComponentEvent e) {
						int cot = Math.max(1, Math.min(4, pnlLuoi.getWidth() / 200));
						GridLayout luoi = (GridLayout) pnlLuoi.getLayout();
						if (luoi.getColumns() != cot) {
							luoi.setColumns(cot);
							pnlLuoi.revalidate();
						}
					}
				});

		txtTimKiem = taoOTimKiem("Quét mã vạch / nhập barcode / mã / tên sản phẩm rồi Enter");
		txtTimKiem.setPreferredSize(new Dimension(0, 42));
		txtTimKiem.setFont(MauUngDung.taoPhongChu(Font.BOLD, 15));
		txtTimKiem.setToolTipText("Có thể nhập mã sản phẩm, barcode hoặc tên sản phẩm.");
		txtTimKiem
				.getDocument()
				.addDocumentListener(
						new javax.swing.event.DocumentListener() {
							private void kichHoatLoc() {
								trangHienTai = 0;
								if (SwingUtilities.isEventDispatchThread() && isShowing()) {
									bohenGioTimKiem.restart();
								} else {
									dungLaiLuoi();
								}
							}

							@Override
							public void insertUpdate(javax.swing.event.DocumentEvent suKien) {
								kichHoatLoc();
							}

							@Override
							public void removeUpdate(javax.swing.event.DocumentEvent suKien) {
								kichHoatLoc();
							}

							@Override
							public void changedUpdate(javax.swing.event.DocumentEvent suKien) {
								kichHoatLoc();
							}
						});
		txtTimKiem.addActionListener(suKien -> themTheoQuet());

		JPanel pnlTrai = taoVungTrongSuot(new BorderLayout(0, 10));
		JPanel pnlTren = taoVungTrongSuot(new BorderLayout(0, 8));
		pnlTren.add(taoKhuVucQuet(), BorderLayout.NORTH);

		com.petstation.gui.components.ThanhDanhSach thanh = new com.petstation.gui.components.ThanhDanhSach();
		themCacNutNhomHang(thanh);
		cboCheDoHienThi.addActionListener(
				e -> {
					cheDoBangNhanh = cboCheDoHienThi.getSelectedIndex() == 1;
					((CardLayout) pnlVungSanPham.getLayout())
							.show(pnlVungSanPham, cheDoBangNhanh ? "BANG" : "THE");
					dungLaiLuoi();
				});
		thanh.themBoLoc("Hiển thị:", cboCheDoHienThi);
		pnlTren.add(thanh, BorderLayout.SOUTH);
		pnlTrai.add(pnlTren, BorderLayout.NORTH);

		pnlLuoi.setOpaque(false);
		JPanel pnlLuoiVungChua = taoVungTrongSuot(new BorderLayout());
		pnlLuoiVungChua.add(pnlLuoi, BorderLayout.NORTH);
		JScrollPane scrLuoiCuon = new JScrollPane(pnlLuoiVungChua);
		scrLuoiCuon.setBorder(null);
		scrLuoiCuon.setOpaque(false);
		scrLuoiCuon.getViewport().setOpaque(false);
		scrLuoiCuon.getVerticalScrollBar().setUnitIncrement(18);

		JPanel pnlPhanTrang = taoVungTrongSuot(new BorderLayout(8, 0));
		pnlPhanTrang.setBorder(new EmptyBorder(6, 2, 0, 2));
		lblThongTinTrang.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
		lblThongTinTrang.setForeground(MauUngDung.VAN_BAN_XAM);
		JPanel pnlNutTrang = taoVungTrongSuot(new FlowLayout(FlowLayout.RIGHT, 6, 0));
		btnTrangTruoc.addActionListener(
				e -> {
					if (trangHienTai > 0) {
						trangHienTai--;
						dungLaiLuoi();
					}
				});
		btnTrangSau.addActionListener(
				e -> {
					trangHienTai++;
					dungLaiLuoi();
				});
		pnlNutTrang.add(btnTrangTruoc);
		pnlNutTrang.add(btnTrangSau);
		pnlPhanTrang.add(lblThongTinTrang, BorderLayout.WEST);
		pnlPhanTrang.add(pnlNutTrang, BorderLayout.EAST);

		JPanel pnlCheDoThe = taoVungTrongSuot(new BorderLayout(0, 4));
		pnlCheDoThe.add(scrLuoiCuon, BorderLayout.CENTER);
		pnlCheDoThe.add(pnlPhanTrang, BorderLayout.SOUTH);

		JScrollPane scrBangNhanh = taoBangDinhDang(moHinhBangNhanh);
		tblBangNhanh = layBang(scrBangNhanh);
		tblBangNhanh.addMouseListener(
				new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if (e.getClickCount() == 2 && tblBangNhanh.getSelectedRow() >= 0) {
							int idx = tblBangNhanh.convertRowIndexToModel(
									tblBangNhanh.getSelectedRow());
							if (idx >= 0 && idx < danhSachDaLoc.size()) {
								SanPham sp = danhSachDaLoc.get(idx);
								if (sp.tonKho() > 0)
									themDenGioHang(sp);
							}
						}
					}
				});

		pnlVungSanPham.setOpaque(false);
		pnlVungSanPham.add(pnlCheDoThe, "THE");
		pnlVungSanPham.add(scrBangNhanh, "BANG");
		pnlTrai.add(pnlVungSanPham, BorderLayout.CENTER);

		BoGocPanel pnlGioHang = taoGioHang();

		pnlTrai.setMinimumSize(new Dimension(340, 260));
		pnlGioHang.setMinimumSize(new Dimension(340, 260));
		pnlGioHang.setPreferredSize(new Dimension(480, 0));

		add(pnlTrai, BorderLayout.CENTER);
		add(pnlGioHang, BorderLayout.EAST);

		danhSachSanPham = laySanPham();
		dungLaiLuoi();
		lamMoiGioHang();

		dangKyPhimTat("F9", "pay", suKien -> quyetToan());
		dangKyPhimTat("F7", "hold", suKien -> treoGioHangHienTai());
		dangKyPhimTat("F8", "retrieve", suKien -> hienThiTreoCacGioHang());
		dangKyPhimTat("Ctrl+K", "search", suKien -> txtTimKiem.requestFocusInWindow());
		dangKyPhimTat("F1", "search_focus", suKien -> txtTimKiem.requestFocusInWindow());

		addHierarchyListener(
				suKien -> {
					if ((suKien.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0
							&& !isDisplayable()) {
						dongBarcodeUsbServer();
					}
				});
	}

	private JPanel taoKhuVucQuet() {
		JPanel panel = taoVungTrongSuot(new BorderLayout(0, 7));
		panel.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(MauUngDung.DO_THUONG_HIEU, 2, true),
						new EmptyBorder(9, 11, 9, 11)));

		JLabel tieuDe = new JLabel("QUÉT MÃ VẠCH / TÌM SẢN PHẨM");
		tieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
		tieuDe.setForeground(MauUngDung.NAU);

		JPanel dongNhap = taoVungTrongSuot(new BorderLayout(8, 0));
		dongNhap.add(txtTimKiem, BorderLayout.CENTER);
		JButton nutThem = new NutDinhDang("Thêm mã", NutDinhDang.KieuNut.CHINH);
		nutThem.addActionListener(suKien -> themTheoQuet());
		dongNhap.add(nutThem, BorderLayout.EAST);

		JPanel trangThai = taoVungTrongSuot(new GridLayout(2, 1, 0, 3));
		lblTrangThaiQuet.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
		lblTrangThaiQuet.setForeground(MauUngDung.VAN_BAN_XAM);
		lblKetNoiUsb.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
		lblKetNoiUsb.setForeground(MauUngDung.NAU);
		trangThai.add(lblTrangThaiQuet);
		trangThai.add(lblKetNoiUsb);

		panel.add(tieuDe, BorderLayout.NORTH);
		panel.add(dongNhap, BorderLayout.CENTER);
		panel.add(trangThai, BorderLayout.SOUTH);
		return panel;
	}

	private void dangKyPhimTat(String khoa, String ten, java.awt.event.ActionListener thaoTac) {
		KeyStroke phim = KeyStroke.getKeyStroke(khoa);
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(phim, ten);
		getActionMap()
				.put(
						ten,
						new AbstractAction() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent suKien) {
								thaoTac.actionPerformed(suKien);
							}
						});
	}

	private void themCacNutNhomHang(com.petstation.gui.components.ThanhDanhSach thanh) {
		ButtonGroup nhom = new ButtonGroup();
		for (String ten : CAC_NHOM_HANG) {
			JToggleButton btnNhan = new JToggleButton(ten);
			btnNhan.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
			btnNhan.setFocusPainted(false);
			btnNhan.setBackground(Color.WHITE);
			btnNhan.setSelected(ten.equals(nhomHang));
			btnNhan.addActionListener(
					suKien -> {
						nhomHang = ten;
						trangHienTai = 0;
						dungLaiLuoi();
					});
			nhom.add(btnNhan);
			thanh.themBoLoc(btnNhan);
		}
	}

	private void dungLaiLuoi() {
		String tuKhoa = txtTimKiem.getText().trim().toLowerCase(Locale.ROOT);
		danhSachDaLoc.clear();
		for (SanPham sanPham : danhSachSanPham) {
			boolean vaoNhomHang = nhomHang.equals("Tất cả") || sanPham.nhomHang().equals(nhomHang);
			boolean khop = tuKhoa.isEmpty()
					|| sanPham.ten().toLowerCase(Locale.ROOT).contains(tuKhoa)
					|| sanPham.ma().toLowerCase(Locale.ROOT).contains(tuKhoa)
					|| sanPham.barcode().toLowerCase(Locale.ROOT).contains(tuKhoa);
			if (vaoNhomHang && khop) {
				danhSachDaLoc.add(sanPham);
			}
		}

		if (cheDoBangNhanh) {
			moHinhBangNhanh.setRowCount(0);
			for (SanPham sp : danhSachDaLoc) {
				moHinhBangNhanh.addRow(
						new Object[] {
								sp.ma(),
								sp.ten(),
								sp.nhomHang(),
								TienTe.dinhDang(sp.gia()),
								sp.tonKho() <= 0 ? "Hết hàng" : sp.tonKho()
						});
			}
			com.petstation.gui.components.common.DinhDangBang.canChinhDoRongCot(tblBangNhanh);
			return;
		}

		pnlLuoi.removeAll();
		int tongSo = danhSachDaLoc.size();
		int tongTrang = Math.max(1, (tongSo + SO_THE_MOI_TRANG - 1) / SO_THE_MOI_TRANG);
		if (trangHienTai >= tongTrang)
			trangHienTai = tongTrang - 1;
		if (trangHienTai < 0)
			trangHienTai = 0;

		int batDau = trangHienTai * SO_THE_MOI_TRANG;
		int ketThuc = Math.min(tongSo, batDau + SO_THE_MOI_TRANG);
		for (int i = batDau; i < ketThuc; i++) {
			pnlLuoi.add(laySanPhamThe(danhSachDaLoc.get(i)));
		}
		if (pnlLuoi.getComponentCount() == 0) {
			JLabel lblKhongCo = new JLabel("Không tìm thấy sản phẩm phù hợp.");
			lblKhongCo.setForeground(MauUngDung.VAN_BAN_XAM);
			pnlLuoi.add(lblKhongCo);
		}
		int soCot = Math.max(1, ((GridLayout) pnlLuoi.getLayout()).getColumns());
		while (pnlLuoi.getComponentCount() % soCot != 0 && pnlLuoi.getComponentCount() > 0) {
			pnlLuoi.add(Box.createGlue());
		}
		lblThongTinTrang.setText(
				tongSo == 0
						? "0 sản phẩm"
						: "Hiển thị "
								+ (batDau + 1)
								+ "–"
								+ ketThuc
								+ " / "
								+ tongSo
								+ " SP · Trang "
								+ (trangHienTai + 1)
								+ "/"
								+ tongTrang);
		btnTrangTruoc.setEnabled(trangHienTai > 0);
		btnTrangSau.setEnabled(trangHienTai + 1 < tongTrang);
		pnlLuoi.revalidate();
		pnlLuoi.repaint();
	}

	private JPanel laySanPhamThe(SanPham sanPham) {
		boolean hetHang = sanPham.tonKho() <= 0;
		BoGocPanel pnlThe = new BoGocPanel(10, MauUngDung.THE_NEN, true);
		pnlThe.setLayout(new BorderLayout(0, 4));
		pnlThe.setBorder(new EmptyBorder(12, 14, 12, 14));
		pnlThe.setPreferredSize(new Dimension(180, 310));
		pnlThe.setCursor(
				hetHang
						? Cursor.getDefaultCursor()
						: Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		pnlThe.getAccessibleContext().setAccessibleName(sanPham.ten());

		JLabel lblMa = new JLabel(sanPham.ma() + " · " + sanPham.nhomHang());
		lblMa.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
		lblMa.setForeground(MauUngDung.VAN_BAN_XAM);
		JLabel lblTen = new JLabel("<html>" + sanPham.ten() + "</html>");
		lblTen.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
		lblTen.setForeground(hetHang ? MauUngDung.VO_HIEU_HOA : MauUngDung.VAN_BAN_TOI);

		JPanel pnlDuoi = taoVungTrongSuot(new BorderLayout());
		JLabel lblGia = new JLabel(TienTe.dinhDang(sanPham.gia()));
		lblGia.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
		lblGia.setForeground(hetHang ? MauUngDung.VO_HIEU_HOA : MauUngDung.DO_THUONG_HIEU);
		JLabel lblTonKho = new JLabel(
				hetHang
						? "Hết hàng"
						: sanPham.tonKho() <= 5
								? "Còn " + sanPham.tonKho()
								: "Tồn " + sanPham.tonKho());
		lblTonKho.setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));
		lblTonKho.setForeground(
				hetHang
						? MauUngDung.VAN_BAN_NGUY_HIEM
						: sanPham.tonKho() <= 5
								? MauUngDung.CAM
								: MauUngDung.VAN_BAN_XAM);
		pnlDuoi.add(lblGia, BorderLayout.WEST);
		pnlDuoi.add(lblTonKho, BorderLayout.EAST);

		var pnlAnh = new com.petstation.gui.components.common.AnhSanPhamPanel(140);
		pnlAnh.datAnh(sanPham.anh());
		JPanel pnlThongTin = taoVungTrongSuot(new BorderLayout(0, 4));
		pnlThongTin.add(lblMa, BorderLayout.NORTH);
		pnlThongTin.add(lblTen, BorderLayout.CENTER);
		pnlThe.add(pnlAnh, BorderLayout.NORTH);
		pnlThe.add(pnlThongTin, BorderLayout.CENTER);
		pnlThe.add(pnlDuoi, BorderLayout.SOUTH);

		if (!hetHang) {
			ganThaoTacThe(
					pnlThe,
					new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent suKien) {
							themDenGioHang(sanPham);
						}
					});
		}
		return pnlThe;
	}

	private void ganThaoTacThe(Component thanhPhan, MouseAdapter thaoTac) {
		thanhPhan.addMouseListener(thaoTac);
		thanhPhan.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		if (thanhPhan instanceof Container vung) {
			for (Component con : vung.getComponents())
				ganThaoTacThe(con, thaoTac);
		}
	}

	private void themTheoQuet() {
		String tuKhoa = txtTimKiem.getText().trim();
		if (tuKhoa.isEmpty())
			return;

		try {
			danhSachSanPham = laySanPham();
		} catch (RuntimeException loi) {
			capNhatTrangThaiQuet(
					"Không tải được sản phẩm: " + loi.getMessage(),
					MauUngDung.VAN_BAN_NGUY_HIEM);
			return;
		}

		List<SanPham> timThay = danhSachSanPham.stream()
				.filter(
						sp -> sp.barcode().equalsIgnoreCase(tuKhoa)
								|| sp.ma().equalsIgnoreCase(tuKhoa))
				.toList();

		if (timThay.isEmpty() && tuKhoa.matches("\\d{6}")) {
			timThay = danhSachSanPham.stream()
					.filter(
							sp -> (!sp.barcode().isBlank()
									&& sp.barcode().endsWith(tuKhoa))
									|| sp.ma().endsWith(tuKhoa))
					.toList();
		}

		if (timThay.isEmpty()) {
			String tuKhoaTen = tuKhoa.toLowerCase(Locale.ROOT);
			timThay = danhSachSanPham.stream()
					.filter(sp -> sp.ten().toLowerCase(Locale.ROOT).contains(tuKhoaTen))
					.toList();
		}

		if (timThay.isEmpty()) {
			capNhatTrangThaiQuet(
					"Không tìm thấy sản phẩm: " + tuKhoa,
					MauUngDung.VAN_BAN_NGUY_HIEM);
			HopThoai.hienThiThongBaoHopThoai(
					this,
					"Không có sản phẩm mã, barcode hoặc tên \"" + tuKhoa + "\".",
					"Không tìm thấy",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (timThay.size() > 1) {
			capNhatTrangThaiQuet(
					"Có nhiều sản phẩm phù hợp. Hãy nhập barcode hoặc mã chính xác.",
					MauUngDung.VAN_BAN_NGUY_HIEM);
			return;
		}

		SanPham sanPham = timThay.get(0);
		if (sanPham.tonKho() <= 0) {
			capNhatTrangThaiQuet(sanPham.ten() + " đã hết hàng.", MauUngDung.VAN_BAN_NGUY_HIEM);
			HopThoai.hienThiThongBaoHopThoai(
					this,
					sanPham.ten() + " đã hết hàng.",
					"Hết hàng",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		if (themDenGioHang(sanPham)) {
			capNhatTrangThaiQuet(
					"Đã thêm " + sanPham.ten() + " vào giỏ hàng.", MauUngDung.XANH_LA);
			txtTimKiem.setText("");
		}
	}

	private BoGocPanel taoGioHang() {
		BoGocPanel pnlGioHangVungHienThi = layThe(new BorderLayout(0, 10));
		pnlGioHangVungHienThi.setPreferredSize(new Dimension(480, 0));

		JPanel pnlTieuDe = new JPanel(new BorderLayout(8, 0));
		pnlTieuDe.setOpaque(false);
		pnlTieuDe.add(taoTieuDeMuc("Giỏ hàng"), BorderLayout.WEST);

		JPanel pnlTieuDeCacThaoTac = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
		pnlTieuDeCacThaoTac.setOpaque(false);
		btnNutTreo.setPreferredSize(new Dimension(150, 32));
		btnNutTreo.setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));
		btnNutLay.setPreferredSize(new Dimension(130, 32));
		btnNutLay.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
		pnlTieuDeCacThaoTac.add(btnNutTreo);
		pnlTieuDeCacThaoTac.add(btnNutLay);
		pnlTieuDe.add(pnlTieuDeCacThaoTac, BorderLayout.EAST);
		pnlGioHangVungHienThi.add(pnlTieuDe, BorderLayout.NORTH);

		pnlGioHangCacDong.setLayout(new BoxLayout(pnlGioHangCacDong, BoxLayout.Y_AXIS));
		pnlGioHangCacDong.setOpaque(false);
		lblTrongGioHang.setForeground(MauUngDung.VAN_BAN_XAM);
		lblTrongGioHang.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));

		JPanel pnlCacDongVungChua = new TheoChieuRongPanel();
		pnlCacDongVungChua.setOpaque(false);
		pnlCacDongVungChua.add(pnlGioHangCacDong, BorderLayout.NORTH);
		JScrollPane scrCuon = new JScrollPane(pnlCacDongVungChua);
		scrCuon.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
		scrCuon.getViewport().setBackground(Color.WHITE);
		scrCuon.getVerticalScrollBar().setUnitIncrement(16);
		scrCuon.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pnlGioHangVungHienThi.add(scrCuon, BorderLayout.CENTER);

		JPanel pnlDuoi = new JPanel();
		pnlDuoi.setOpaque(false);
		pnlDuoi.setLayout(new BoxLayout(pnlDuoi, BoxLayout.Y_AXIS));
		pnlDuoi.setBorder(new EmptyBorder(8, 0, 0, 0));

		pnlDuoi.add(pnlBoChon);
		pnlDuoi.add(Box.createVerticalStrut(8));
		pnlDuoi.add(tinhTongDong("Tạm tính", lblNhanTamTinh, false));
		pnlDuoi.add(tinhTongDong("Khuyến mãi tự áp", lblNhanKhuyenMai, false));
		pnlDuoi.add(tinhTongDong("Giảm giá (KM + điểm)", lblNhanGiamGia, false));
		pnlDuoi.add(tinhTongDong("Tổng cộng", lblNhanTong, true));
		pnlDuoi.add(Box.createVerticalStrut(10));

		NutDinhDang btnXoaNut = new NutDinhDang("Xóa giỏ", NutDinhDang.KieuNut.NGUY_HIEM);
		btnXoaNut.addActionListener(suKien -> xoaGioHang(true));
		btnNutThanhToan.addActionListener(suKien -> quyetToan());

		JPanel pnlCacNut = new JPanel(new GridLayout(1, 2, 8, 0));
		pnlCacNut.setOpaque(false);
		pnlCacNut.add(btnXoaNut);
		pnlCacNut.add(btnNutThanhToan);
		pnlCacNut.setAlignmentX(Component.LEFT_ALIGNMENT);
		pnlCacNut.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
		btnNutThanhToan.setPreferredSize(new Dimension(150, 44));
		pnlDuoi.add(pnlCacNut);
		pnlGioHangVungHienThi.add(pnlDuoi, BorderLayout.SOUTH);

		btnNutTreo.addActionListener(suKien -> treoGioHangHienTai());
		btnNutLay.addActionListener(suKien -> hienThiTreoCacGioHang());
		return pnlGioHangVungHienThi;
	}

	private JLabel layNhanNho(String vanBan) {
		JLabel lblDongDangXet = new JLabel(vanBan);
		lblDongDangXet.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
		lblDongDangXet.setForeground(MauUngDung.VAN_BAN_XAM);
		lblDongDangXet.setAlignmentX(Component.LEFT_ALIGNMENT);
		return lblDongDangXet;
	}

	private JPanel tinhTongDong(String nhan, JLabel lblGiaTri, boolean manh) {
		JPanel pnlDong = taoVungTrongSuot(new BorderLayout());
		JLabel lblDongDangXet = new JLabel(nhan);
		lblDongDangXet.setFont(
				MauUngDung.taoPhongChu(manh ? Font.BOLD : Font.PLAIN, manh ? 15 : 13));
		lblGiaTri.setFont(MauUngDung.taoPhongChu(Font.BOLD, manh ? 22 : 13));
		lblGiaTri.setForeground(
				manh ? MauUngDung.DO_THUONG_HIEU : MauUngDung.VAN_BAN_TOI);
		pnlDong.add(lblDongDangXet, BorderLayout.WEST);
		pnlDong.add(lblGiaTri, BorderLayout.EAST);
		pnlDong.setAlignmentX(Component.LEFT_ALIGNMENT);
		pnlDong.setMaximumSize(new Dimension(Integer.MAX_VALUE, manh ? 36 : 24));
		return pnlDong;
	}

	private boolean themDenGioHang(SanPham sanPham) {
		for (int chiSo = 0; chiSo < gioHang.size(); chiSo++) {
			GioHang.DongHang dongDangXet = gioHang.get(chiSo);
			if (!dongDangXet.getMa().equals(sanPham.ma()))
				continue;

			if (dongDangXet.getSoLuong() >= sanPham.tonKho()) {
				HopThoai.hienThiThongBaoHopThoai(
						this,
						"Chỉ còn " + sanPham.tonKho() + " " + sanPham.ten() + " trong kho.",
						"Vượt tồn kho",
						JOptionPane.WARNING_MESSAGE);
				return false;
			}

			gioHang.set(
					chiSo,
					new GioHang.DongHang(
							dongDangXet.getMa(),
							dongDangXet.getTen(),
							dongDangXet.getDonGia(),
							dongDangXet.getSoLuong() + 1));
			lichSuThem.push(sanPham.ma());
			lamMoiGioHang();
			return true;
		}

		gioHang.add(
				new GioHang.DongHang(sanPham.ma(), sanPham.ten(), sanPham.gia(), 1));
		lichSuThem.push(sanPham.ma());
		lamMoiGioHang();
		return true;
	}

	private void layThayDoiSoLuong(String ma, int chenhLech) {
		for (int chiSo = 0; chiSo < gioHang.size(); chiSo++) {
			GioHang.DongHang dongDangXet = gioHang.get(chiSo);
			if (!dongDangXet.getMa().equals(ma))
				continue;

			int tonKho = laySanPham().stream()
					.filter(sanPham -> sanPham.ma().equals(ma))
					.mapToInt(SanPham::tonKho)
					.findFirst()
					.orElse(Integer.MAX_VALUE);
			int soLuong = dongDangXet.getSoLuong() + chenhLech;
			if (soLuong > tonKho)
				return;
			if (soLuong <= 0) {
				gioHang.remove(chiSo);
			} else {
				gioHang.set(
						chiSo,
						new GioHang.DongHang(
								dongDangXet.getMa(),
								dongDangXet.getTen(),
								dongDangXet.getDonGia(),
								soLuong));
			}
			break;
		}
		lamMoiGioHang();
	}

	private void datSoLuong(String ma, int soLuongMoi) {
		for (int chiSo = 0; chiSo < gioHang.size(); chiSo++) {
			GioHang.DongHang dongDangXet = gioHang.get(chiSo);
			if (!dongDangXet.getMa().equals(ma))
				continue;

			int tonKho = laySanPham().stream()
					.filter(sanPham -> sanPham.ma().equals(ma))
					.mapToInt(SanPham::tonKho)
					.findFirst()
					.orElse(Integer.MAX_VALUE);
			if (soLuongMoi > tonKho) {
				HopThoai.hienThiThongBaoHopThoai(
						this,
						"Chỉ còn " + tonKho + " " + dongDangXet.getTen() + " trong kho.",
						"Vượt tồn kho",
						JOptionPane.WARNING_MESSAGE);
				soLuongMoi = tonKho;
			}
			if (soLuongMoi <= 0) {
				gioHang.remove(chiSo);
			} else {
				gioHang.set(
						chiSo,
						new GioHang.DongHang(
								dongDangXet.getMa(),
								dongDangXet.getTen(),
								dongDangXet.getDonGia(),
								soLuongMoi));
			}
			break;
		}
		lamMoiGioHang();
	}

	private void lamMoiGioHang() {
		pnlGioHangCacDong.removeAll();
		if (gioHang.isEmpty()) {
			JPanel pnlVungChua = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
			pnlVungChua.setOpaque(false);
			pnlVungChua.add(lblTrongGioHang);
			pnlGioHangCacDong.add(pnlVungChua);
		}
		for (GioHang.DongHang dong : gioHang) {
			pnlGioHangCacDong.add(layGioHangDong(dong));
		}
		pnlGioHangCacDong.revalidate();
		pnlGioHangCacDong.repaint();
		banHang.setCacDong(gioHang);
		capNhatCacTong();
		if (txtTimKiem != null && isShowing()) {
			SwingUtilities.invokeLater(txtTimKiem::requestFocusInWindow);
		}
	}

	private JPanel layGioHangDong(GioHang.DongHang dong) {
		JPanel pnlDong = new JPanel(new BorderLayout(8, 0));
		pnlDong.setBackground(Color.WHITE);
		pnlDong.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(0, 0, 1, 0, MauUngDung.VIEN),
						new EmptyBorder(8, 10, 8, 10)));
		pnlDong.setMaximumSize(new Dimension(Integer.MAX_VALUE, 118));

		JPanel pnlThongTin = taoVungTrongSuot(new BorderLayout());
		JLabel lblTen = new JLabel(
				"<html><body style='width:175px'>"
						+ dong.getTen().replace("&", "&amp;").replace("<", "&lt;")
						+ "</body></html>");
		lblTen.setToolTipText(dong.getTen());
		lblTen.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
		JLabel lblDonVi = new JLabel(TienTe.dinhDang(dong.getDonGia()));
		lblDonVi.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
		lblDonVi.setForeground(MauUngDung.VAN_BAN_XAM);
		pnlThongTin.add(lblTen, BorderLayout.NORTH);
		pnlThongTin.add(lblDonVi, BorderLayout.SOUTH);

		JPanel pnlSoLuong = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
		pnlSoLuong.setOpaque(false);
		JButton btnTru = layNutSoLuong("−", "Giảm số lượng " + dong.getTen());
		JButton btnCong = layNutSoLuong("+", "Tăng số lượng " + dong.getTen());
		btnTru.addActionListener(suKien -> layThayDoiSoLuong(dong.getMa(), -1));
		btnCong.addActionListener(suKien -> layThayDoiSoLuong(dong.getMa(), +1));
		JTextField txtSoLuong = new JTextField(String.valueOf(dong.getSoLuong()));
		txtSoLuong.setHorizontalAlignment(SwingConstants.CENTER);
		txtSoLuong.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
		txtSoLuong.setPreferredSize(new Dimension(38, 26));
		txtSoLuong.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(MauUngDung.VIEN),
						new EmptyBorder(1, 2, 1, 2)));
		Runnable capNhat = () -> {
			try {
				int sl = Integer.parseInt(txtSoLuong.getText().trim());
				if (sl != dong.getSoLuong()) {
					datSoLuong(dong.getMa(), sl);
				}
			} catch (NumberFormatException ex) {
				txtSoLuong.setText(String.valueOf(dong.getSoLuong()));
			}
		};
		txtSoLuong.addActionListener(e -> capNhat.run());
		txtSoLuong.addFocusListener(new java.awt.event.FocusAdapter() {
			@Override
			public void focusLost(java.awt.event.FocusEvent e) {
				capNhat.run();
			}
		});
		pnlSoLuong.add(btnTru);
		pnlSoLuong.add(txtSoLuong);
		pnlSoLuong.add(btnCong);
		JButton btnXoa = layNutSoLuong("×", "Xóa sản phẩm " + dong.getTen());
		btnXoa.setForeground(MauUngDung.NGUY_HIEM);
		btnXoa.setToolTipText("Xóa món này khỏi giỏ hàng");
		btnXoa.addActionListener(suKien -> layThayDoiSoLuong(dong.getMa(), -dong.getSoLuong()));
		pnlSoLuong.add(btnXoa);

		JLabel lblSoTien = new JLabel(TienTe.dinhDang(dong.docSoTien()), SwingConstants.RIGHT);
		lblSoTien.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
		lblSoTien.setPreferredSize(new Dimension(84, 20));

		pnlDong.add(pnlThongTin, BorderLayout.CENTER);
		JPanel pnlPhai = taoVungTrongSuot(new BorderLayout(6, 0));
		pnlPhai.add(pnlSoLuong, BorderLayout.WEST);
		pnlPhai.add(lblSoTien, BorderLayout.EAST);
		pnlDong.add(pnlPhai, BorderLayout.EAST);

		return pnlDong;
	}

	private JButton layNutSoLuong(String vanBan, String tenTroNang) {
		JButton btnGiaTriSau = new JButton(vanBan);
		btnGiaTriSau.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
		btnGiaTriSau.setMargin(new Insets(0, 0, 0, 0));
		btnGiaTriSau.setPreferredSize(new Dimension(26, 26));
		btnGiaTriSau.setFocusPainted(false);
		btnGiaTriSau.setBackground(MauUngDung.BE_MAT_XEN_KE);
		btnGiaTriSau.getAccessibleContext().setAccessibleName(tenTroNang);
		return btnGiaTriSau;
	}

	private void capNhatCacTong() {
		lblNhanTamTinh.setText(TienTe.dinhDang(banHang.tinhTamTinh()));
		lblNhanGiamGia.setText(
				banHang.tinhGiamGia() == 0
						? "0₫"
						: "−" + TienTe.dinhDang(banHang.tinhGiamGia()));
		lblNhanTong.setText(TienTe.dinhDang(banHang.tinhTong()));
		GioHang.KhuyenMai khuyenMai = banHang.getKhuyenMaiTotNhat();
		lblNhanKhuyenMai.setText(
				khuyenMai == null
						? "—"
						: khuyenMai.getTen() + ": −" + TienTe.dinhDang(khuyenMai.getSoTien()));
		lblNhanKhuyenMai.setToolTipText(
				khuyenMai == null
						? "Chưa có khuyến mãi phù hợp"
						: khuyenMai.getMa() + " - " + khuyenMai.getTen());
		btnNutThanhToan.setEnabled(!gioHang.isEmpty());
	}

	private void xoaGioHang(boolean xacNhan) {
		if (gioHang.isEmpty())
			return;
		if (xacNhan
				&& HopThoai.hienThiXacNhanHopThoai(
						this,
						"Xóa toàn bộ " + gioHang.size() + " dòng trong giỏ hàng?",
						"Xóa giỏ hàng",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
			return;
		}
		gioHang.clear();
		lichSuThem.clear();
		banHang.xoa();
		pnlBoChon.xoa();
		lamMoiGioHang();
	}

	private void quyetToan() {
		capNhatCacTong();
		boDieuHuong.accept(MaManHinh.THANH_TOAN);
	}

	private void treoGioHangHienTai() {
		if (gioHang.isEmpty()) {
			HopThoai.hienThiCanhBao(this, "Giỏ hàng trống.");
			return;
		}
		try {
			banHang.setCacDong(gioHang);
			BanHangService.treo(banHang);
			xoaGioHang(false);
			HopThoai.hienThiThanhCong(this, "Đã lưu đơn treo vào SQL Server.");
		} catch (RuntimeException ngoaiLe) {
			HopThoai.hienThiCanhBao(this, ngoaiLe.getMessage());
		}
	}

	private void hienThiTreoCacGioHang() {
		boDieuHuong.accept(MaManHinh.HOA_DON_CHO);
	}

	@Override
	public KhaNangThanhCongCu layKhaNangThanhCongCu() {
		return new KhaNangThanhCongCu(true, false, true, false, true, true);
	}

	@Override
	public void xuLyTaoMoi() {
		xoaGioHang(true);
	}

	@Override
	public void xuLyHoanTac() {
		if (lichSuThem.isEmpty())
			return;
		String ma = lichSuThem.pop();
		for (int chiSo = 0; chiSo < gioHang.size(); chiSo++) {
			GioHang.DongHang dongDangXet = gioHang.get(chiSo);
			if (!dongDangXet.getMa().equals(ma))
				continue;
			if (dongDangXet.getSoLuong() <= 1) {
				gioHang.remove(chiSo);
			} else {
				gioHang.set(
						chiSo,
						new GioHang.DongHang(
								dongDangXet.getMa(),
								dongDangXet.getTen(),
								dongDangXet.getDonGia(),
								dongDangXet.getSoLuong() - 1));
			}
			break;
		}
		lamMoiGioHang();
	}

	@Override
	public void xuLyHienThi() {
		gioHang.clear();
		gioHang.addAll(banHang.getCacDong());
		danhSachSanPham = laySanPham();
		dungLaiLuoi();
		pnlBoChon.dongBo();
		lamMoiGioHang();

		khoiDongBarcodeUsb();

		if (banHang.kiemTraTrong() && !gioHang.isEmpty()) {
			gioHang.clear();
			lichSuThem.clear();
			pnlBoChon.dongBo();
			lamMoiGioHang();
		}
		txtTimKiem.requestFocusInWindow();
	}

	private void khoiDongBarcodeUsb() {
		if (barcodeUsbServer != null) {
			lblKetNoiUsb.setText(
					"USB: chờ điện thoại | Mã ghép nối: "
							+ barcodeUsbServer.getPairingCode()
							+ " | Cổng: " + barcodeUsbServer.getPort());
			return;
		}

		BarcodeUsbServer serverTam = null;
		try {
			serverTam = new BarcodeUsbServer(
					barcode -> SwingUtilities.invokeLater(
							() -> xuLyBarcodeTuDienThoai(barcode)));
			serverTam.start();
			barcodeUsbServer = serverTam;

			lblKetNoiUsb.setForeground(MauUngDung.NAU);
			lblKetNoiUsb.setText(
					"USB: chờ điện thoại | Mã ghép nối: "
							+ serverTam.getPairingCode()
							+ " | Cổng: " + serverTam.getPort());
			lblKetNoiUsb.setToolTipText(
					"Nhập mã ghép nối hiển thị tại đây vào ứng dụng PetStation Scanner.");
		} catch (Exception loi) {
			if (serverTam != null)
				serverTam.close();
			barcodeUsbServer = null;
			lblKetNoiUsb.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
			lblKetNoiUsb.setText("USB Scanner chưa khởi động: " + loi.getMessage());
		}
	}

	private void dongBarcodeUsbServer() {
		if (barcodeUsbServer == null)
			return;
		barcodeUsbServer.close();
		barcodeUsbServer = null;
	}

	private void xuLyBarcodeTuDienThoai(String barcode) {
		if (barcode == null || !barcode.matches("\\d{13}")) {
			capNhatTrangThaiQuet(
					"Mã nhận được không đúng định dạng EAN-13.",
					MauUngDung.VAN_BAN_NGUY_HIEM);
			return;
		}

		if (!isShowing()) {
			capNhatTrangThaiQuet(
					"Đã nhận mã " + barcode
							+ " nhưng màn hình Lập hóa đơn chưa mở. Không tự thêm hàng.",
					MauUngDung.VAN_BAN_NGUY_HIEM);
			return;
		}

		PhienDangNhap phienXuLy = PhienDangNhapService.layPhienHienTai();
		if (phienXuLy == null) {
			capNhatTrangThaiQuet(
					"Phiên đăng nhập đã kết thúc. Hãy đăng nhập lại.",
					MauUngDung.VAN_BAN_NGUY_HIEM);
			return;
		}

		capNhatTrangThaiQuet(
				"Đã nhận mã " + barcode + ". Đang kiểm tra sản phẩm...",
				MauUngDung.NAU);

		new SwingWorker<List<SanPham>, Void>() {
			@Override
			protected List<SanPham> doInBackground() throws Exception {
				return PhienDangNhapService.trongPhien(
						phienXuLy, LapHoaDonPanel::laySanPham);
			}

			@Override
			protected void done() {
				try {
					List<SanPham> sanPhamMoi = get();

					if (!isShowing()
							|| PhienDangNhapService.layPhienHienTai() != phienXuLy) {
						return;
					}

					danhSachSanPham = sanPhamMoi;
					List<SanPham> ketQua = danhSachSanPham.stream()
							.filter(sp -> sp.barcode().equals(barcode))
							.toList();

					if (ketQua.isEmpty()) {
						capNhatTrangThaiQuet(
								"Không tìm thấy sản phẩm có barcode " + barcode,
								MauUngDung.VAN_BAN_NGUY_HIEM);
						return;
					}
					if (ketQua.size() > 1) {
						capNhatTrangThaiQuet(
								"Barcode bị trùng trong dữ liệu. Không tự thêm để tránh bán nhầm.",
								MauUngDung.VAN_BAN_NGUY_HIEM);
						return;
					}

					SanPham sanPham = ketQua.get(0);
					if (sanPham.tonKho() <= 0) {
						capNhatTrangThaiQuet(
								sanPham.ten() + " đã hết hàng.",
								MauUngDung.VAN_BAN_NGUY_HIEM);
						return;
					}

					if (themDenGioHang(sanPham)) {
						capNhatTrangThaiQuet(
								"Đã thêm " + sanPham.ten() + " vào giỏ hàng.",
								MauUngDung.XANH_LA);
						lblKetNoiUsb.setText(
								"USB: đã nhận mã từ điện thoại | " + barcode);
						if (barcode.equals(txtTimKiem.getText().trim())) {
							txtTimKiem.setText("");
						}
					}
				} catch (Exception loi) {
					Throwable nguyenNhan = loi.getCause() == null ? loi : loi.getCause();
					capNhatTrangThaiQuet(
							"Không xử lý được barcode: " + nguyenNhan.getMessage(),
							MauUngDung.VAN_BAN_NGUY_HIEM);
				}
			}
		}.execute();
	}

	private void capNhatTrangThaiQuet(String noiDung, Color mau) {
		lblTrangThaiQuet.setText(noiDung);
		lblTrangThaiQuet.setForeground(mau);
	}

	@Override
	protected String layNoiDungTroGiup() {
		return "Quét mã vạch bằng camera điện thoại hoặc gõ mã/tên rồi Enter để thêm vào giỏ.\n"
				+ "Ứng dụng điện thoại cần nhập mã ghép nối hiển thị ở khu vực USB.\n"
				+ "Bấm thẻ sản phẩm để thêm; dùng − / + để đổi số lượng.\n"
				+ "Nhập SĐT khách hàng -> Enter để lookup (tự hiện tên, hạng, điểm).\n"
				+ "Giảm giá: chọn % hoặc số tiền (VD: 50000đ).\n"
				+ "F7: Treo đơn; F8: Mở đơn treo; F9: Thanh toán; Ctrl+Z: Bỏ SP vừa thêm;"
				+ " Ctrl+K: Tìm SP.";
	}

	@Override
	protected String layTieuDeTroGiup() {
		return "Lập đơn hàng";
	}

	private static final class TheoChieuRongPanel extends JPanel implements Scrollable {
		TheoChieuRongPanel() {
			super(new BorderLayout());
		}

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}

		@Override
		public int getScrollableUnitIncrement(
				Rectangle dongDuLieu, int doiTuong, int ngayDangXet) {
			return 16;
		}

		@Override
		public int getScrollableBlockIncrement(
				Rectangle dongDuLieu, int doiTuong, int ngayDangXet) {
			return 64;
		}

		@Override
		public boolean getScrollableTracksViewportWidth() {
			return true;
		}

		@Override
		public boolean getScrollableTracksViewportHeight() {
			return false;
		}
	}
}
