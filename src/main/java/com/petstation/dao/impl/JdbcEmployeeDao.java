package com.petstation.dao.impl;

import com.petstation.dao.EmployeeDao;
import com.petstation.model.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public final class JdbcEmployeeDao implements EmployeeDao {
    @Override
    public Optional<Employee> findById(Connection connection, long employeeId) throws Exception {
        String sql =
                """
                SELECT id, ma_nhan_vien, ho_ten, ngay_sinh, so_dien_thoai, dia_chi,
                       chuc_vu, ngay_vao_lam, dang_lam_viec
                FROM nhan_vien
                WHERE id = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, employeeId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return Optional.empty();
                }
                return Optional.of(
                        new Employee(
                                result.getLong("id"),
                                result.getString("ma_nhan_vien"),
                                result.getString("ho_ten"),
                                result.getDate("ngay_sinh") == null
                                        ? null
                                        : result.getDate("ngay_sinh").toLocalDate(),
                                result.getString("so_dien_thoai"),
                                result.getString("dia_chi"),
                                result.getString("chuc_vu"),
                                result.getDate("ngay_vao_lam").toLocalDate(),
                                result.getBoolean("dang_lam_viec")));
            }
        }
    }
}
