package com.petstation.dao.impl;

import static com.petstation.dao.SqlDatabase.*;

import com.petstation.model.CatalogType;
import com.petstation.model.Role;

import org.mindrot.jbcrypt.BCrypt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class JdbcCatalogDao {
    public static List<Object[]> priceHistory() {
        return strings(
                query(
                        "SELECT CONVERT(varchar(16),h.thoi_diem_thay_doi,103)+'"
                            + " '+CONVERT(varchar(5),h.thoi_diem_thay_doi,108),p.ma_san_pham,p.ten,h.gia_cu,h.gia_moi,COALESCE(n.ho_ten,N'Hệ"
                            + " thống') FROM lich_su_gia h JOIN san_pham p ON p.id=h.san_pham_id"
                            + " LEFT JOIN tai_khoan t ON t.id=h.nguoi_thay_doi_id LEFT JOIN"
                            + " nhan_vien n ON n.id=t.nhan_vien_id ORDER BY h.id DESC"));
    }

    public static List<Object[]> load(CatalogType kind) {
        String sql =
                switch (kind) {
                    case CUSTOMERS ->
                            "SELECT ma_khach_hang,ho_ten,so_dien_thoai,email,dia_chi,ghi_chu,CASE"
                                + " hang_thanh_vien WHEN 'REGULAR' THEN N'Đồng' ELSE"
                                + " hang_thanh_vien END,diem_tich_luy,CASE dang_hoat_dong WHEN 1"
                                + " THEN N'Đang hoạt động' ELSE N'Ngừng hoạt động' END FROM"
                                + " khach_hang ORDER BY ma_khach_hang";
                    case PETS ->
                            "SELECT p.ma_thu_cung,p.ten,CASE p.loai WHEN 'DOG' THEN N'Chó' ELSE"
                                + " N'Mèo'"
                                + " END,p.giong,p.gioi_tinh,CONVERT(varchar(10),p.ngay_sinh,103),p.can_nang,p.mau_long,k.ma_khach_hang+'"
                                + " - '+k.ho_ten,p.tiem_chung,p.di_ung_benh_ly,p.tinh_cach,p.anh,CASE"
                                + " p.dang_theo_doi WHEN 1 THEN N'Đang hoạt động' ELSE N'Ngừng hoạt"
                                + " động' END FROM thu_cung p JOIN khach_hang k ON"
                                + " k.id=p.khach_hang_id ORDER BY p.ma_thu_cung";
                    case STAFF ->
                            "SELECT"
                                + " ma_nhan_vien,ho_ten,so_dien_thoai,cccd,chuc_vu,CONVERT(varchar(10),ngay_vao_lam,103),CASE"
                                + " dang_lam_viec WHEN 1 THEN N'Đang làm' ELSE N'Nghỉ việc' END"
                                + " FROM nhan_vien ORDER BY ma_nhan_vien";
                    case ACCOUNTS ->
                            "SELECT t.ten_dang_nhap,n.ma_nhan_vien+' - '+n.ho_ten,t.vai_tro,CASE"
                                + " t.trang_thai WHEN 'ACTIVE' THEN N'Hoạt động' ELSE N'Đã khóa'"
                                + " END,'' FROM tai_khoan t JOIN nhan_vien n ON n.id=t.nhan_vien_id"
                                + " ORDER BY t.ten_dang_nhap";
                    case PRODUCTS ->
                            "SELECT"
                                + " p.ma_san_pham,p.ten,p.nhom_hang,p.don_vi_tinh,p.gia_ban,p.gia_nhap_gan_nhat,p.ton_toi_thieu,CASE"
                                + " p.theo_doi_hsd WHEN 1 THEN N'Có' ELSE N'Không'"
                                + " END,COALESCE((SELECT SUM(l.so_luong_ton) FROM lo_hang l WHERE"
                                + " l.san_pham_id=p.id),0),CASE p.trang_thai WHEN 'SELLING' THEN"
                                + " N'Đang kinh doanh' ELSE N'Ngừng kinh doanh' END FROM san_pham p"
                                + " ORDER BY p.ma_san_pham";
                    case SUPPLIERS ->
                            "SELECT"
                                + " ma_ncc,ten,nguoi_dai_dien,so_dien_thoai,dia_chi,ma_so_thue,dieu_khoan,CASE"
                                + " trang_thai WHEN 'COOPERATING' THEN N'Đang hợp tác' ELSE N'Ngừng"
                                + " hợp tác' END FROM nha_cung_cap ORDER BY ma_ncc";
                    case PROMOTIONS ->
                            "SELECT ma_chuong_trinh,ten,CASE loai_giam WHEN 'PERCENT' THEN N'Giảm"
                                + " %' ELSE N'Giảm tiền' END,CASE nguon WHEN 'PRODUCT' THEN N'Sản"
                                + " phẩm' WHEN 'SERVICE' THEN N'Dịch vụ' ELSE N'Cả đơn'"
                                + " END,gia_tri,don_toi_thieu,CONVERT(varchar(10),bat_dau,103),CONVERT(varchar(10),DATEADD(second,-1,ket_thuc),103),CASE"
                                + " trang_thai WHEN 'INACTIVE' THEN N'Ngừng áp dụng' ELSE N'Đang áp"
                                + " dụng' END FROM chuong_trinh_khuyen_mai ORDER BY"
                                + " ma_chuong_trinh";
                    case VOUCHERS ->
                            "SELECT"
                                + " ma_voucher,menh_gia,CONVERT(varchar(10),DATEADD(second,-1,hieu_luc_den),103),so_luong,da_dung,don_toi_thieu,CASE"
                                + " trang_thai WHEN 'ACTIVE' THEN N'Còn hiệu lực' ELSE N'Ngừng' END"
                                + " FROM voucher ORDER BY ma_voucher";
                    case SPA ->
                            "SELECT ma_goi,ten_goi,mo_ta,thoi_gian_du_kien,'',CASE trang_thai WHEN"
                                    + " 'ACTIVE' THEN N'Đang hoạt động' ELSE N'Ngừng hoạt động' END"
                                    + " FROM goi_dich_vu_spa ORDER BY ma_goi";
                    case ROOMS ->
                            "SELECT ma_phong,loai_phong,suc_chua,gia_dem,ghi_chu,CASE trang_thai"
                                + " WHEN 'AVAILABLE' THEN N'Trống' WHEN 'OCCUPIED' THEN N'Có thú'"
                                + " WHEN 'CLEANING' THEN N'Đang dọn' WHEN 'MAINTENANCE' THEN N'Bảo"
                                + " trì' ELSE N'Ngừng hoạt động' END FROM phong_hotel ORDER BY"
                                + " ma_phong";
                };
        List<Object[]> rows = strings(query(sql));
        if (kind == CatalogType.ACCOUNTS)
            for (Object[] r : rows) r[2] = Role.valueOf(text(r[2])).displayName();
        if (kind == CatalogType.SPA) {
            for (Object[] r : rows) {
                List<String> prices = new ArrayList<>();
                for (Object[] p :
                        query(
                                "SELECT CASE b.loai_thu_cung WHEN 'DOG' THEN N'Chó' ELSE N'Mèo'"
                                    + " END,b.khoang_can,b.don_gia FROM bang_gia_spa b JOIN"
                                    + " goi_dich_vu_spa g ON g.id=b.goi_id WHERE g.ma_goi=? ORDER"
                                    + " BY b.id",
                                r[0]))
                    prices.add(p[0] + "|" + p[1] + "|" + ((BigDecimal) p[2]).toPlainString());
                r[4] = String.join(";", prices);
            }
        }
        return rows;
    }

    private static List<Object[]> strings(List<Object[]> rows) {
        for (Object[] r : rows)
            for (int i = 0; i < r.length; i++)
                r[i] =
                        r[i] instanceof BigDecimal b
                                ? b.stripTrailingZeros().toPlainString()
                                : text(r[i]);
        return rows;
    }

    private static LocalDate date(Object value) {
        return text(value).isBlank()
                ? null
                : LocalDate.parse(text(value), DateTimeFormatter.ofPattern("dd/MM/uuuu"));
    }

    private static BigDecimal decimal(Object value) {
        return text(value).isBlank() ? null : new BigDecimal(text(value).replace(',', '.'));
    }

    private static String role(Object label) {
        return Arrays.stream(Role.values())
                .filter(r -> r.displayName().equals(label))
                .findFirst()
                .orElseThrow()
                .name();
    }

    public static String codeOf(Object option) {
        return text(option).split(" - ", 2)[0];
    }

    private static void upsert(
            String table, String key, String original, String[] columns, Object... values) {
        // Identifiers are private constants from the switch above; values are always bound
        // parameters.
        if (scalar(
                        "SELECT id FROM " + table + " WITH(UPDLOCK,HOLDLOCK) WHERE " + key + "=?",
                        original)
                == null) {
            update(
                    "INSERT INTO "
                            + table
                            + "("
                            + String.join(",", columns)
                            + ") VALUES("
                            + String.join(",", Collections.nCopies(values.length, "?"))
                            + ")",
                    values);
        } else {
            List<Object> args = new ArrayList<>(Arrays.asList(values));
            args.add(original);
            update(
                    "UPDATE "
                            + table
                            + " SET "
                            + String.join(",", Arrays.stream(columns).map(c -> c + "=?").toList())
                            + " WHERE "
                            + key
                            + "=?",
                    args.toArray());
        }
    }

    public static long id(String table, String key, String code) {
        if (!Set.of(
                        "khach_hang",
                        "thu_cung",
                        "nhan_vien",
                        "tai_khoan",
                        "san_pham",
                        "nha_cung_cap",
                        "goi_dich_vu_spa",
                        "phong_hotel",
                        "lo_hang",
                        "hoa_don",
                        "phieu_tiep_nhan_spa",
                        "hop_dong_luu_tru")
                .contains(table)) throw new IllegalArgumentException("Unknown table");
        Object value = scalar("SELECT id FROM " + table + " WHERE " + key + "=?", code);
        if (value == null) throw new IllegalArgumentException("Không tìm thấy mã " + code + ".");
        return ((Number) value).longValue();
    }

    public static int lockAccountsOf(String staffCode) {
        return update(
                "UPDATE tai_khoan SET trang_thai='INACTIVE' WHERE nhan_vien_id=(SELECT id FROM"
                        + " nhan_vien WHERE ma_nhan_vien=?) AND trang_thai='ACTIVE'",
                staffCode);
    }

    public static void resetPassword(String username, String password) {
        if (password.length() < 8)
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 8 ký tự.");
        update(
                "UPDATE tai_khoan SET"
                    + " mat_khau_hash=?,bat_buoc_doi_mat_khau=1,so_lan_dang_nhap_sai=0,khoa_den=NULL"
                    + " WHERE ten_dang_nhap=?",
                BCrypt.hashpw(password, BCrypt.gensalt()),
                username);
    }

    public static void save(
            CatalogType kind, Object[] before, Object[] value, long actorId, String actorRole) {
        String key = before == null ? text(value[0]) : text(before[0]);
        Object[] v = value;
        switch (kind) {
            case CUSTOMERS ->
                    upsert(
                            "khach_hang",
                            "ma_khach_hang",
                            key,
                            new String[] {
                                "ma_khach_hang",
                                "ho_ten",
                                "so_dien_thoai",
                                "email",
                                "dia_chi",
                                "ghi_chu",
                                "hang_thanh_vien",
                                "diem_tich_luy",
                                "dang_hoat_dong"
                            },
                            v[0],
                            v[1],
                            v[2],
                            v[3],
                            v[4],
                            v[5],
                            v[6],
                            Integer.parseInt(text(v[7])),
                            "Đang hoạt động".equals(v[8]));
            case PETS ->
                    upsert(
                            "thu_cung",
                            "ma_thu_cung",
                            key,
                            new String[] {
                                "ma_thu_cung",
                                "ten",
                                "loai",
                                "giong",
                                "gioi_tinh",
                                "ngay_sinh",
                                "can_nang",
                                "mau_long",
                                "khach_hang_id",
                                "tiem_chung",
                                "di_ung_benh_ly",
                                "tinh_cach",
                                "anh",
                                "dang_theo_doi"
                            },
                            v[0],
                            v[1],
                            "Chó".equals(v[2]) ? "DOG" : "CAT",
                            v[3],
                            v[4],
                            date(v[5]),
                            decimal(v[6]),
                            v[7],
                            id("khach_hang", "ma_khach_hang", codeOf(v[8])),
                            v[9],
                            v[10],
                            v[11],
                            v[12],
                            "Đang hoạt động".equals(v[13]));
            case STAFF -> {
                upsert(
                        "nhan_vien",
                        "ma_nhan_vien",
                        key,
                        new String[] {
                            "ma_nhan_vien",
                            "ho_ten",
                            "so_dien_thoai",
                            "cccd",
                            "chuc_vu",
                            "ngay_vao_lam",
                            "dang_lam_viec"
                        },
                        v[0],
                        v[1],
                        v[2],
                        v[3],
                        v[4],
                        date(v[5]),
                        !"Nghỉ việc".equals(v[6]));
                if ("Nghỉ việc".equals(v[6])) lockAccountsOf(key);
            }
            case ACCOUNTS -> {
                long employee = id("nhan_vien", "ma_nhan_vien", codeOf(v[1]));
                if (before == null && (v.length < 5 || text(v[4]).length() < 8))
                    throw new IllegalArgumentException(
                            "Tài khoản mới cần mật khẩu ít nhất 8 ký tự.");
                if (before != null
                        && number("SELECT id FROM tai_khoan WHERE ten_dang_nhap=?", key) == actorId
                        && (!"Hoạt động".equals(v[3]) || !actorRole.equals(role(v[2]))))
                    throw new IllegalArgumentException(
                            "Không thể tự khóa hoặc đổi vai trò tài khoản đang đăng" + " nhập.");
                if (before == null)
                    insert(
                            "INSERT INTO"
                                + " tai_khoan(ten_dang_nhap,nhan_vien_id,vai_tro,trang_thai,mat_khau_hash)"
                                + " VALUES(?,?,?,?,?)",
                            v[0],
                            employee,
                            role(v[2]),
                            "Hoạt động".equals(v[3]) ? "ACTIVE" : "INACTIVE",
                            BCrypt.hashpw(text(v[4]), BCrypt.gensalt()));
                else {
                    String existingHash =
                            text(
                                    scalar(
                                            "SELECT mat_khau_hash FROM tai_khoan WHERE"
                                                    + " ten_dang_nhap=?",
                                            key));
                    if ("Hoạt động".equals(v[3])
                            && !existingHash.startsWith("$2")
                            && (v.length < 5 || text(v[4]).isBlank()))
                        throw new IllegalArgumentException(
                                "Hãy đặt mật khẩu riêng trước khi mở khóa tài khoản" + " mẫu.");
                    update(
                            "UPDATE tai_khoan SET"
                                    + " ten_dang_nhap=?,nhan_vien_id=?,vai_tro=?,trang_thai=?"
                                    + " WHERE ten_dang_nhap=?",
                            v[0],
                            employee,
                            role(v[2]),
                            "Hoạt động".equals(v[3]) ? "ACTIVE" : "INACTIVE",
                            key);
                    if (v.length > 4 && !text(v[4]).isBlank())
                        resetPassword(text(v[0]), text(v[4]));
                }
                if (v.length > 4) v[4] = "";
            }
            case PRODUCTS -> {
                Object old =
                        scalar(
                                "SELECT gia_ban FROM san_pham WITH(UPDLOCK,HOLDLOCK)"
                                        + " WHERE ma_san_pham=?",
                                key);
                upsert(
                        "san_pham",
                        "ma_san_pham",
                        key,
                        new String[] {
                            "ma_san_pham",
                            "ten",
                            "nhom_hang",
                            "don_vi_tinh",
                            "gia_ban",
                            "gia_nhap_gan_nhat",
                            "ton_toi_thieu",
                            "theo_doi_hsd",
                            "trang_thai"
                        },
                        v[0],
                        v[1],
                        v[2],
                        v[3],
                        decimal(v[4]),
                        decimal(v[5]),
                        Integer.parseInt(text(v[6])),
                        "Có".equals(v[7]),
                        "Đang kinh doanh".equals(v[9]) ? "SELLING" : "STOPPED");
                if (old == null || ((BigDecimal) old).compareTo(decimal(v[4])) != 0)
                    update(
                            "INSERT INTO"
                                    + " lich_su_gia(san_pham_id,gia_cu,gia_moi,nguoi_thay_doi_id)"
                                    + " VALUES(?,?,?,?)",
                            id("san_pham", "ma_san_pham", text(v[0])),
                            old == null ? BigDecimal.ZERO : old,
                            decimal(v[4]),
                            actorId);
            }
            case SUPPLIERS ->
                    upsert(
                            "nha_cung_cap",
                            "ma_ncc",
                            key,
                            new String[] {
                                "ma_ncc",
                                "ten",
                                "nguoi_dai_dien",
                                "so_dien_thoai",
                                "dia_chi",
                                "ma_so_thue",
                                "dieu_khoan",
                                "trang_thai"
                            },
                            v[0],
                            v[1],
                            v[2],
                            v[3],
                            v[4],
                            v[5],
                            v[6],
                            "Đang hợp tác".equals(v[7]) ? "COOPERATING" : "INACTIVE");
            case PROMOTIONS ->
                    upsert(
                            "chuong_trinh_khuyen_mai",
                            "ma_chuong_trinh",
                            key,
                            new String[] {
                                "ma_chuong_trinh",
                                "ten",
                                "loai_giam",
                                "nguon",
                                "gia_tri",
                                "don_toi_thieu",
                                "bat_dau",
                                "ket_thuc",
                                "trang_thai"
                            },
                            v[0],
                            v[1],
                            "Giảm %".equals(v[2]) ? "PERCENT" : "AMOUNT",
                            "Sản phẩm".equals(v[3])
                                    ? "PRODUCT"
                                    : "Dịch vụ".equals(v[3]) ? "SERVICE" : "ALL",
                            decimal(v[4]),
                            decimal(v[5]) == null ? BigDecimal.ZERO : decimal(v[5]),
                            date(v[6]).atStartOfDay(),
                            date(v[7]).plusDays(1).atStartOfDay(),
                            "Ngừng áp dụng".equals(v[8]) ? "INACTIVE" : "ACTIVE");
            case VOUCHERS ->
                    upsert(
                            "voucher",
                            "ma_voucher",
                            key,
                            new String[] {
                                "ma_voucher",
                                "menh_gia",
                                "hieu_luc_den",
                                "so_luong",
                                "don_toi_thieu",
                                "trang_thai",
                                "chi_ap_dung_spa"
                            },
                            v[0],
                            decimal(v[1]),
                            date(v[2]).plusDays(1).atStartOfDay(),
                            Integer.parseInt(text(v[3])),
                            decimal(v[5]),
                            "Ngừng".equals(v[6]) ? "INACTIVE" : "ACTIVE",
                            false);
            case SPA -> {
                upsert(
                        "goi_dich_vu_spa",
                        "ma_goi",
                        key,
                        new String[] {
                            "ma_goi", "ten_goi", "mo_ta", "thoi_gian_du_kien", "trang_thai"
                        },
                        v[0],
                        v[1],
                        v[2],
                        Integer.parseInt(text(v[3])),
                        "Đang hoạt động".equals(v[5]) ? "ACTIVE" : "INACTIVE");
                long packageId = id("goi_dich_vu_spa", "ma_goi", text(v[0]));
                update("DELETE FROM bang_gia_spa WHERE goi_id=?", packageId);
                for (String line : text(v[4]).split(";")) {
                    String[] p = line.split("\\|");
                    if (p.length != 3) throw new IllegalArgumentException("Bảng giá không hợp lệ.");
                    List<BigDecimal> weights = new ArrayList<>();
                    java.util.regex.Matcher matcher =
                            java.util.regex.Pattern.compile("\\d+(?:[.,]\\d+)?").matcher(p[1]);
                    while (matcher.find())
                        weights.add(new BigDecimal(matcher.group().replace(',', '.')));
                    BigDecimal low =
                            weights.size() > 1
                                    ? weights.get(0)
                                    : p[1].contains(">") && !weights.isEmpty()
                                            ? weights.get(0)
                                            : BigDecimal.ZERO;
                    BigDecimal high =
                            weights.size() > 1
                                    ? weights.get(1)
                                    : p[1].contains("<") && !weights.isEmpty()
                                            ? weights.get(0)
                                            : null;
                    update(
                            "INSERT INTO"
                                + " bang_gia_spa(goi_id,loai_thu_cung,khoang_can,can_nang_tu,can_nang_den,don_gia)"
                                + " VALUES(?,?,?,?,?,?)",
                            packageId,
                            "Chó".equals(p[0]) ? "DOG" : "CAT",
                            p[1],
                            low,
                            high,
                            new BigDecimal(p[2]));
                }
            }
            case ROOMS -> {
                String status =
                        switch (text(v[5])) {
                            case "Trống" -> "AVAILABLE";
                            case "Có thú" -> "OCCUPIED";
                            case "Đang dọn" -> "CLEANING";
                            case "Bảo trì" -> "MAINTENANCE";
                            default -> "INACTIVE";
                        };
                upsert(
                        "phong_hotel",
                        "ma_phong",
                        key,
                        new String[] {
                            "ma_phong", "loai_phong", "suc_chua", "gia_dem", "ghi_chu", "trang_thai"
                        },
                        v[0],
                        v[1],
                        Integer.parseInt(text(v[2])),
                        decimal(v[3]),
                        v[4],
                        status);
            }
        }
    }
}
