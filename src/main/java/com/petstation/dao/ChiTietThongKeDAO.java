package com.petstation.dao;

import com.petstation.model.*;

import java.util.*;

/** Thao tác dữ liệu cho ChiTietThongKe; service gọi qua hợp đồng này. */
public interface ChiTietThongKeDAO {
    ChiTietBaoCao lay(String baoCao, String goc, KhoangNgay ky, Long taiKhoan, Long nhanVien);
}
