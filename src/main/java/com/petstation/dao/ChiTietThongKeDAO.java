package com.petstation.dao;

import com.petstation.model.*;

import java.util.*;

// Giao diện DAO truy vấn danh sách chứng từ gốc (Drill-down) cho các báo cáo thống kê.
public interface ChiTietThongKeDAO {
    // Lấy danh sách chứng từ chi tiết theo tên báo cáo, góc nhìn (chế độ xem) và kỳ báo cáo
    ChiTietBaoCao lay(String baoCao, String goc, KhoangNgay ky, Long taiKhoan, Long nhanVien);
}
