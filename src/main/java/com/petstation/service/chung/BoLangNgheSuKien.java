package com.petstation.service.chung;

/** Interface listener dùng chung để thông báo thay đổi có kiểu dữ liệu. */
@FunctionalInterface
public interface BoLangNgheSuKien<T> {
    void khiThayDoi(T suKien);
}
