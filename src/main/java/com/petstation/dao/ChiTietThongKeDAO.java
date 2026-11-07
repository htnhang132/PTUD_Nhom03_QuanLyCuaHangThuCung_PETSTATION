package com.petstation.dao;

import com.petstation.model.*;

import java.util.*;

public interface ChiTietThongKeDAO {
    ChiTietBaoCao lay(String baoCao, String goc, KhoangNgay ky, Long taiKhoan, Long nhanVien);
}
