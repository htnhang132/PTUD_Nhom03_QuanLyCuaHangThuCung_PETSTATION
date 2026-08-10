package com.petstore.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	static {
        // Mở khóa cho phép Java kết nối qua giao thức TLSv1 cũ của SQL Server
        java.security.Security.setProperty(
            "jdk.tls.disabledAlgorithms", 
            "SSLv3, RC4, DES, MD5withRSA, DH keySize < 1024, EC keySize < 224, 3DES_EDE_CBC"
        );
    }
    // Nếu dùng SQL Server Express thì chuỗi DB_URL sẽ có dạng: 
    // "jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=PetStoreDB;encrypt=false;trustServerCertificate=true;"
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=PetStoreDB;encrypt=false;trustServerCertificate=true;";
    private static final String USER = "sa";       // Tài khoản sa của bạn
    private static final String PASS = "sapassword";   // Mật khẩu sa của bạn

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println(">>> KẾT NỐI MS SQL SERVER THÀNH CÔNG! <<<");
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy Driver MS SQL!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối CSDL! Hãy kiểm tra Username, Password hoặc Cổng 1433.");
            e.printStackTrace();
        }
        return conn;
    }

    public static void main(String[] args) {
        getConnection();
    }
}