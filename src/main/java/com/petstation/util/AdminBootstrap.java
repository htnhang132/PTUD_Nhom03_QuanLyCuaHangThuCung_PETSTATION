package com.petstation.util;

import com.petstation.dao.DatabaseConfig;
import com.petstation.dao.DatabaseConnection;

import org.mindrot.jbcrypt.BCrypt;

import java.io.Console;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Tạo Cửa hàng trưởng đầu tiên sau khi chạy các script database. Mật khẩu không
 * được ghi vào source
 * hoặc SQL seed.
 */
public final class AdminBootstrap {
    private AdminBootstrap() {
    }

    public static void main(String[] args) throws Exception {
        Console console = System.console();
        if (console == null) {
            System.err.println("Hãy chạy lệnh trong terminal tương tác.");
            System.exit(1);
        }

        String username = console.readLine("Tên đăng nhập Cửa hàng trưởng: ").trim();
        String fullName = console.readLine("Họ tên: ").trim();
        String phone = console.readLine("Số điện thoại: ").replaceAll("\\D", "");
        char[] password = console.readPassword("Mật khẩu (tối thiểu 8 ký tự): ");
        char[] confirmation = console.readPassword("Nhập lại mật khẩu: ");

        if (username.isBlank() || fullName.isBlank() || phone.isBlank()) {
            throw new IllegalArgumentException("Tên đăng nhập, họ tên và SĐT không được trống.");
        }
        if (password.length < 8 || !java.util.Arrays.equals(password, confirmation)) {
            throw new IllegalArgumentException("Mật khẩu không hợp lệ hoặc nhập lại không khớp.");
        }

        String hash = BCrypt.hashpw(new String(password), BCrypt.gensalt(12));
        java.util.Arrays.fill(password, '\0');
        java.util.Arrays.fill(confirmation, '\0');

        DatabaseConnection database = new DatabaseConnection(DatabaseConfig.load());
        try (Connection connection = database.open()) {
            connection.setAutoCommit(false);
            try {
                if (managerExists(connection)) {
                    throw new IllegalStateException("Hệ thống đã có tài khoản Cửa hàng trưởng.");
                }
                long employeeId = insertEmployee(connection, fullName, phone);
                insertAccount(connection, employeeId, username, hash);
                connection.commit();
                System.out.println(
                        "Đã tạo Cửa hàng trưởng. Hãy đăng nhập và đổi mật khẩu nếu cần.");
            } catch (Exception exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    private static boolean managerExists(Connection connection) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT TOP 1 1 FROM tai_khoan WHERE vai_tro = 'STORE_MANAGER' AND"
                        + " trang_thai='ACTIVE'");
                ResultSet result = statement.executeQuery()) {
            return result.next();
        }
    }

    private static long insertEmployee(Connection connection, String fullName, String phone)
            throws Exception {
        String code = "NV" + System.currentTimeMillis();
        String sql = """
                INSERT INTO nhan_vien
                    (ma_nhan_vien, ho_ten, so_dien_thoai, chuc_vu, ngay_vao_lam)
                OUTPUT INSERTED.id
                VALUES (?, ?, ?, N'Cửa hàng trưởng', CAST(GETDATE() AS DATE))
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, code);
            statement.setString(2, fullName);
            statement.setString(3, phone);
            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getLong(1);
            }
        }
    }

    private static void insertAccount(
            Connection connection, long employeeId, String username, String hash) throws Exception {
        String sql = """
                INSERT INTO tai_khoan
                    (nhan_vien_id, ten_dang_nhap, mat_khau_hash, vai_tro, bat_buoc_doi_mat_khau)
                VALUES (?, ?, ?, 'STORE_MANAGER', 0)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, employeeId);
            statement.setString(2, username);
            statement.setString(3, hash);
            statement.executeUpdate();
        }
    }
}
