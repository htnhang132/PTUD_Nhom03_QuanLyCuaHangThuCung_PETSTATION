package com.petstation.gui.components.common;

import com.petstation.gui.components.MauUngDung;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import javax.swing.*;

public class BoChonNgayGio extends JPanel {
    private final BoChonNgay boChonNgay;
    private final JSpinner spGio;

    public BoChonNgayGio() {
        this(LocalDateTime.now().plusMinutes(30).withSecond(0).withNano(0));
    }

    public BoChonNgayGio(LocalDateTime banDau) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        setOpaque(false);

        LocalDateTime thoiGian = banDau != null
                ? banDau
                : LocalDateTime.now().plusMinutes(30).withSecond(0).withNano(0);

        boChonNgay = new BoChonNgay(thoiGian.toLocalDate());
        boChonNgay.setPreferredSize(new Dimension(160, 32));

        Date timeDate = Date.from(thoiGian.atZone(ZoneId.systemDefault()).toInstant());
        SpinnerDateModel model = new SpinnerDateModel(timeDate, null, null, Calendar.MINUTE);
        spGio = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spGio, "HH:mm");
        spGio.setEditor(editor);
        spGio.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        spGio.setPreferredSize(new Dimension(85, 32));

        JLabel lblGio = new JLabel("Giờ:");
        lblGio.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        lblGio.setForeground(MauUngDung.VAN_BAN_TOI);

        add(boChonNgay);
        add(lblGio);
        add(spGio);
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

    public BoChonNgay layBoChonNgay() {
        return boChonNgay;
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
