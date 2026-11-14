package com.petstation.gui.chung.thanhphan;

import com.petstation.gui.chung.MauUngDung;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

public class BoChonNgayGio extends JPanel {
    private final BoChonNgay boChonNgay;
    private final JSpinner spGio;

    public BoChonNgayGio() {
        this(LocalDateTime.now().plusMinutes(30).withSecond(0).withNano(0));
    }

    public BoChonNgayGio(LocalDateTime banDau) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 2));
        setOpaque(false);
        LocalDateTime thoiGian =
                banDau != null
                        ? banDau
                        : LocalDateTime.now().plusMinutes(30).withSecond(0).withNano(0);
        boChonNgay = new BoChonNgay(thoiGian.toLocalDate());
        boChonNgay.setPreferredSize(new Dimension(175, 34));
        Date timeDate = Date.from(thoiGian.atZone(ZoneId.systemDefault()).toInstant());
        SpinnerDateModel model = new SpinnerDateModel(timeDate, null, null, Calendar.MINUTE);
        spGio = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spGio, "HH:mm");
        spGio.setEditor(editor);
        spGio.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        spGio.setPreferredSize(new Dimension(95, 34));
        JLabel nhanGio = new JLabel("Giờ:");
        nhanGio.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        nhanGio.setForeground(MauUngDung.VAN_BAN_TOI);
        add(boChonNgay);
        add(nhanGio);
        add(spGio);
        setPreferredSize(new Dimension(340, 38));
    }

    public LocalDateTime layGiaTri() {
        LocalDate ngay = boChonNgay.layNgayDuocChon();
        if (ngay == null) {
            ngay = LocalDate.now();
        }
        Date dateVal = (Date) spGio.getValue();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateVal);
        int gio = cal.get(Calendar.HOUR_OF_DAY);
        int phut = cal.get(Calendar.MINUTE);
        return ngay.atTime(LocalTime.of(gio, phut));
    }

    public void datGiaTri(LocalDateTime ldt) {
        if (ldt == null) return;
        boChonNgay.datNgayDuocChon(ldt.toLocalDate());
        Date dateVal = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        spGio.setValue(dateVal);
    }

    public void addChangeListener(Runnable listener) {
        if (listener == null) return;
        boChonNgay.addPropertyChangeListener("date", e -> listener.run());
        spGio.addChangeListener(e -> listener.run());
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        boChonNgay.setEnabled(enabled);
        spGio.setEnabled(enabled);
    }
}
