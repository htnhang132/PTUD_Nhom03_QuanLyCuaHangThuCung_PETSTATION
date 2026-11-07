package com.petstation.gui.components;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;

import javax.swing.*;

public final class BieuTuongNet implements Icon {
	public enum Kieu {
		TRANG_CHINH,
		MOI,
		LUU,
		TIM_KIEM,
		IN,
		HOAN_TAC,
		TRO_GIUP,
		GIO_HANG,
		CAC_NGUOI_DUNG,
		DAU_CHAN,
		GIUONG,
		HOP,
		XE_TAI,
		BIEU_DO,
		LICH,
		KIEM_TRA,
		KHOA,
		TIEN_MAT,
		NHAN,
		DOI_TRA,
		TEP,
		GIOT_NUOC,
		BANH_RANG,
		DONG_HO,
		QUAY_LAI,
		CHUONG,
		KHIEN,
		QUA_TANG,
		THUNG_RAC,
		BO_NHO_TAM,
		KHOA2
	}

	private final Kieu kieu;

	private final int kichThuoc;

	private final Color mau;

	public BieuTuongNet(Kieu kieu, int kichThuoc, Color mau) {
		this.kieu = kieu;
		this.kichThuoc = kichThuoc;
		this.mau = mau;
	}

	@Override
	public int getIconWidth() {
		return kichThuoc;
	}

	@Override
	public int getIconHeight() {
		return kichThuoc;
	}

	@Override
	public void paintIcon(Component thanhPhan, Graphics doHoa, int toaDoNgang, int toaDoDoc) {
		Graphics2D doHoa2 = (Graphics2D) doHoa.create();
		doHoa2.translate(toaDoNgang, toaDoDoc);
		doHoa2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		doHoa2.setColor(thanhPhan.isEnabled() ? mau : MauUngDung.VO_HIEU_HOA);
		doHoa2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		int moHinhDangXet = 3;

		int suKien = kichThuoc - 4;
		if (kieu.ordinal() > Kieu.TRO_GIUP.ordinal()) {
			double tyLe = kichThuoc / 24.0;
			doHoa2.scale(tyLe, tyLe);
			doHoa2.setStroke(
					new BasicStroke(
							(float) (1.8 / tyLe), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			veKyHieu(doHoa2);
			doHoa2.dispose();
			return;
		}
		switch (kieu) {
		case TRANG_CHINH -> {
			double s = kichThuoc;

			Path2D ngoiNha = new Path2D.Double();

			ngoiNha.moveTo(s * 0.12, s * 0.46);
			ngoiNha.lineTo(s * 0.50, s * 0.14);
			ngoiNha.lineTo(s * 0.88, s * 0.46);

			ngoiNha.moveTo(s * 0.23, s * 0.43);
			ngoiNha.lineTo(s * 0.23, s * 0.86);
			ngoiNha.lineTo(s * 0.77, s * 0.86);
			ngoiNha.lineTo(s * 0.77, s * 0.43);

			doHoa2.draw(ngoiNha);

			Path2D cuaChinh = new Path2D.Double();
			cuaChinh.moveTo(s * 0.42, s * 0.86);
			cuaChinh.lineTo(s * 0.42, s * 0.65);
			cuaChinh.quadTo(s * 0.42, s * 0.62, s * 0.45, s * 0.62);
			cuaChinh.lineTo(s * 0.55, s * 0.62);
			cuaChinh.quadTo(s * 0.58, s * 0.62, s * 0.58, s * 0.65);
			cuaChinh.lineTo(s * 0.58, s * 0.86);

			doHoa2.draw(cuaChinh);
		}
		case MOI -> {
			doHoa2.drawLine(kichThuoc / 2, moHinhDangXet, kichThuoc / 2, suKien);
			doHoa2.drawLine(moHinhDangXet, kichThuoc / 2, suKien, kichThuoc / 2);
		}
		case LUU -> {
			doHoa2.drawRect(moHinhDangXet, moHinhDangXet, kichThuoc - 7, kichThuoc - 7);
			doHoa2.drawRect(6, moHinhDangXet, kichThuoc - 12, 5);
			doHoa2.drawRect(6, kichThuoc - 9, kichThuoc - 12, 6);
		}
		case TIM_KIEM -> {
			doHoa2.drawOval(moHinhDangXet, moHinhDangXet, kichThuoc - 10, kichThuoc - 10);
			doHoa2.drawLine(kichThuoc - 8, kichThuoc - 8, suKien, suKien);
		}
		case IN -> {
			doHoa2.drawRect(5, moHinhDangXet, kichThuoc - 10, 6);
			doHoa2.drawRect(moHinhDangXet, 8, kichThuoc - 6, 8);
			doHoa2.drawRect(6, 13, kichThuoc - 12, kichThuoc - 17);
		}
		case HOAN_TAC -> {
			doHoa2.draw(
					new Arc2D.Double(4, 4, kichThuoc - 8, kichThuoc - 8, 30, 250, Arc2D.OPEN));
			doHoa2.drawLine(4, 8, 4, 3);
			doHoa2.drawLine(4, 8, 9, 8);
		}
		case TRO_GIUP -> {
			double s = kichThuoc;

			int le = (int) Math.round(s * 0.14);
			int duongKinh = (int) Math.round(s * 0.72);

			doHoa2.drawOval(le, le, duongKinh, duongKinh);

			Path2D dauHoi = new Path2D.Double();

			dauHoi.moveTo(s * 0.38, s * 0.39);
			dauHoi.curveTo(
					s * 0.38, s * 0.27,
					s * 0.46, s * 0.22,
					s * 0.54, s * 0.22
					);
			dauHoi.curveTo(
					s * 0.65, s * 0.22,
					s * 0.69, s * 0.31,
					s * 0.66, s * 0.39
					);
			dauHoi.curveTo(
					s * 0.64, s * 0.46,
					s * 0.57, s * 0.49,
					s * 0.52, s * 0.54
					);
			dauHoi.curveTo(
					s * 0.50, s * 0.57,
					s * 0.50, s * 0.60,
					s * 0.50, s * 0.64
					);

			doHoa2.draw(dauHoi);

			int kichThuocCham = Math.max(2, (int) Math.round(s * 0.075));
			int toaDoCham = (int) Math.round(s * 0.50 - kichThuocCham / 2.0);

			doHoa2.fillOval(
					toaDoCham,
					(int) Math.round(s * 0.73),
					kichThuocCham,
					kichThuocCham
					);
		}
		}
		doHoa2.dispose();
	}

	private static Path2D layDuongDan(double... vungDangXet) {
		Path2D duongDan = new Path2D.Double();
		duongDan.moveTo(vungDangXet[0], vungDangXet[1]);
		for (int chiSo = 2; chiSo < vungDangXet.length; chiSo += 2)
			duongDan.lineTo(vungDangXet[chiSo], vungDangXet[chiSo + 1]);
		return duongDan;
	}

	private static void layDong(
			Graphics2D doHoa,
			double toaDoNgang1,
			double toaDoDoc1,
			double toaDoNgang2,
			double toaDoDoc2) {
		doHoa.draw(new java.awt.geom.Line2D.Double(toaDoNgang1, toaDoDoc1, toaDoNgang2, toaDoDoc2));
	}

	private static void layElip(
			Graphics2D doHoa, double tamNgang, double tamDoc, double dongDuLieu) {
		doHoa.draw(
				new java.awt.geom.Ellipse2D.Double(
						tamNgang - dongDuLieu,
						tamDoc - dongDuLieu,
						2 * dongDuLieu,
						2 * dongDuLieu));
	}

	private void veKyHieu(Graphics2D doHoa) {
		switch (kieu) {
		case GIO_HANG -> {
			doHoa.draw(layDuongDan(3, 4, 6, 4, 8.5, 15, 19, 15, 21, 7, 7, 7));
			layElip(doHoa, 10, 19, 1.6);
			layElip(doHoa, 17, 19, 1.6);
		}
		case CAC_NGUOI_DUNG -> {
			layElip(doHoa, 12, 8, 4);

			Path2D noiDung = new Path2D.Double();
			noiDung.moveTo(4, 21);
			noiDung.quadTo(4, 13.5, 12, 13.5);
			noiDung.quadTo(20, 13.5, 20, 21);
			doHoa.draw(noiDung);
		}
		case DAU_CHAN -> {
			layElip(doHoa, 5.5, 11, 2);
			layElip(doHoa, 9.5, 6.5, 2);
			layElip(doHoa, 14.5, 6.5, 2);
			layElip(doHoa, 18.5, 11, 2);
			doHoa.draw(new java.awt.geom.Ellipse2D.Double(7, 13, 10, 8));
		}
		case GIUONG -> {
			layDong(doHoa, 3, 19, 3, 7);
			doHoa.draw(layDuongDan(3, 15, 21, 15, 21, 19));
			doHoa.draw(layDuongDan(21, 15, 21, 11, 10, 11, 10, 15));
			layElip(doHoa, 6.5, 12, 1.8);
		}
		case HOP -> {
			doHoa.draw(layDuongDan(12, 3, 20, 7, 20, 17, 12, 21, 4, 17, 4, 7, 12, 3));
			doHoa.draw(layDuongDan(4, 7, 12, 11, 20, 7));
			layDong(doHoa, 12, 11, 12, 21);
		}
		case XE_TAI -> {
			doHoa.drawRect(2, 6, 12, 10);
			doHoa.draw(layDuongDan(14, 10, 18, 10, 21, 13, 21, 16, 14, 16));
			layElip(doHoa, 7, 18, 2);
			layElip(doHoa, 17, 18, 2);
		}
		case BIEU_DO -> {
			layDong(doHoa, 4, 20, 20, 20);
			doHoa.drawRect(6, 12, 3, 8);
			doHoa.drawRect(11, 7, 3, 13);
			doHoa.drawRect(16, 3, 3, 17);
		}
		case LICH -> {
			doHoa.draw(new java.awt.geom.RoundRectangle2D.Double(4, 5, 16, 15, 3, 3));
			layDong(doHoa, 4, 10, 20, 10);
			layDong(doHoa, 8, 3, 8, 7);
			layDong(doHoa, 16, 3, 16, 7);
		}
		case KIEM_TRA -> {
			layElip(doHoa, 12, 12, 9);
			doHoa.draw(layDuongDan(8, 12, 11, 15, 16, 9));
		}
		case KHOA -> {
			doHoa.draw(new java.awt.geom.RoundRectangle2D.Double(5, 11, 14, 10, 2, 2));

			Path2D quaiKhoa = new Path2D.Double();
			quaiKhoa.moveTo(8, 11);
			quaiKhoa.lineTo(8, 8);
			quaiKhoa.append(new Arc2D.Double(8, 4, 8, 8, 180, -180, Arc2D.OPEN), true);
			quaiKhoa.lineTo(16, 11);
			doHoa.draw(quaiKhoa);
		}
		case TIEN_MAT -> {
			doHoa.draw(new java.awt.geom.RoundRectangle2D.Double(2, 6, 20, 12, 2, 2));
			layElip(doHoa, 12, 12, 2.8);
			layDong(doHoa, 5.5, 12, 6, 12);
			layDong(doHoa, 18, 12, 18.5, 12);
		}
		case NHAN -> {
			doHoa.draw(layDuongDan(3, 12, 12, 3, 20, 3, 20, 11, 11, 20, 3, 12));
			layElip(doHoa, 15.5, 7.5, 1.2);
		}
		case DOI_TRA -> {
			doHoa.draw(new Arc2D.Double(5, 6, 14, 14, 90, 250, Arc2D.OPEN));
			doHoa.draw(layDuongDan(5, 3, 5, 8, 10, 8));
		}
		case TEP -> {
			doHoa.draw(layDuongDan(6, 3, 14, 3, 19, 8, 19, 21, 6, 21, 6, 3));
			doHoa.draw(layDuongDan(14, 3, 14, 8, 19, 8));
			layDong(doHoa, 9, 13, 16, 13);
			layDong(doHoa, 9, 17, 16, 17);
		}
		case GIOT_NUOC -> {
			Path2D tha = new Path2D.Double();
			tha.moveTo(12, 3);
			tha.quadTo(19, 11, 19, 15);
			tha.append(new Arc2D.Double(5, 8, 14, 14, 0, -180, Arc2D.OPEN), true);
			tha.quadTo(5, 11, 12, 3);
			doHoa.draw(tha);
		}
		case BANH_RANG -> {
			layElip(doHoa, 12, 12, 3);
			layElip(doHoa, 12, 12, 7);
			for (int chiSo = 0; chiSo < 8; chiSo++) {
				double giaTriDau = Math.toRadians(chiSo * 45);
				layDong(
						doHoa,
						12 + 7 * Math.cos(giaTriDau),
						12 + 7 * Math.sin(giaTriDau),
						12 + 9.5 * Math.cos(giaTriDau),
						12 + 9.5 * Math.sin(giaTriDau));
			}
		}
		case DONG_HO -> {
			layElip(doHoa, 12, 12, 9);
			doHoa.draw(layDuongDan(12, 7, 12, 12, 16, 14));
		}
		case QUAY_LAI -> doHoa.draw(layDuongDan(15, 5, 8, 12, 15, 19));
		case CHUONG -> {
			Path2D chuong = new Path2D.Double();
			chuong.moveTo(6, 17);
			chuong.lineTo(6, 11);
			chuong.append(new Arc2D.Double(6, 5, 12, 12, 180, -180, Arc2D.OPEN), true);
			chuong.lineTo(18, 17);
			chuong.lineTo(20, 19);
			chuong.lineTo(4, 19);
			chuong.closePath();
			doHoa.draw(chuong);
			layDong(doHoa, 10, 21.5, 14, 21.5);
		}
		case KHIEN -> {
			Path2D khien = new Path2D.Double();
			khien.moveTo(12, 3);
			khien.lineTo(20, 6);
			khien.lineTo(20, 12);
			khien.quadTo(20, 18, 12, 21);
			khien.quadTo(4, 18, 4, 12);
			khien.lineTo(4, 6);
			khien.closePath();
			doHoa.draw(khien);
		}
		case QUA_TANG -> {
			doHoa.drawRect(4, 10, 16, 10);
			doHoa.drawRect(3, 7, 18, 3);
			layDong(doHoa, 12, 7, 12, 20);
			doHoa.draw(new java.awt.geom.Ellipse2D.Double(7.5, 3, 4.5, 4));
			doHoa.draw(new java.awt.geom.Ellipse2D.Double(12, 3, 4.5, 4));
		}
		case THUNG_RAC -> {
			layDong(doHoa, 4, 7, 20, 7);
			doHoa.draw(layDuongDan(9, 7, 9, 4, 15, 4, 15, 7));
			doHoa.draw(layDuongDan(6, 7, 7.5, 21, 16.5, 21, 18, 7));
			layDong(doHoa, 10, 11, 10, 17);
			layDong(doHoa, 14, 11, 14, 17);
		}
		case BO_NHO_TAM -> {
			doHoa.draw(new java.awt.geom.RoundRectangle2D.Double(5, 4, 14, 17, 2, 2));
			doHoa.drawRect(9, 2, 6, 4);
			doHoa.draw(layDuongDan(8.5, 12, 10.5, 14, 15, 10));
			layDong(doHoa, 8.5, 18, 15.5, 18);
		}
		case KHOA2 -> {
			layElip(doHoa, 8, 15, 4);
			layDong(doHoa, 11, 12, 20, 3);
			layDong(doHoa, 17, 6, 20, 9);
		}
		default -> {}
		}
	}
}
