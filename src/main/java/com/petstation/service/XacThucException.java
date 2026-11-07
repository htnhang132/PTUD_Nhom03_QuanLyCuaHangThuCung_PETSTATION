package com.petstation.service;

public class XacThucException extends RuntimeException {
    public XacThucException(String thongBao) {
        super(thongBao);
    }
}
