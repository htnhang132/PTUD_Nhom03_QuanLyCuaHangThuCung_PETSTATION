package com.petstation.model;

import java.util.List;

/** Dữ liệu chứng từ và cột dùng để đối chiếu nhóm trên biểu đồ với bảng chi tiết. */
public record ChiTietBaoCao(String[] cot, List<Object[]> dong, int cotNhom) {}
