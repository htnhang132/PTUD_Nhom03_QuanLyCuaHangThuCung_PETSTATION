package com.petstation.service.chung;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Bộ phát sự kiện an toàn đa luồng dùng chung cho các service nghiệp vụ. */
public final class PhatSuKien<T> {
    private static final Logger LOG = LoggerFactory.getLogger(PhatSuKien.class);
    private final List<BoLangNgheSuKien<T>> cacBoLangNghe = new CopyOnWriteArrayList<>();

    public void them(BoLangNgheSuKien<T> boLangNghe) {
        if (boLangNghe != null && !cacBoLangNghe.contains(boLangNghe)) {
            cacBoLangNghe.add(boLangNghe);
        }
    }

    public void xoa(BoLangNgheSuKien<T> boLangNghe) {
        cacBoLangNghe.remove(boLangNghe);
    }

    public void xoaTatCa() {
        cacBoLangNghe.clear();
    }

    public void phat(T suKien) {
        for (BoLangNgheSuKien<T> boLangNghe : cacBoLangNghe) {
            try {
                boLangNghe.khiThayDoi(suKien);
            } catch (RuntimeException ex) {
                LOG.warn("Lỗi bộ lắng nghe; tiếp tục thông báo các bộ lắng nghe khác.", ex);
            }
        }
    }
}
