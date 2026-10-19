package com.petstation.service;

import static com.petstation.dao.SqlDatabase.*;
import static com.petstation.service.SessionService.*;
import static com.petstation.service.WorkflowSupport.*;

import com.petstation.dao.impl.JdbcSpaDao;
import com.petstation.model.Permission;

import java.time.*;
import java.util.*;

public final class SpaService {
    public static long appointment(
            String petCode, String packageCode, String staffCode, LocalDateTime start) {
        require(Permission.SPA_MANAGE);
        return transaction(
                c -> {
                    Object[] p = pet(petCode);
                    long pkg = id("goi_dich_vu_spa", "ma_goi", packageCode),
                            staff = id("nhan_vien", "ma_nhan_vien", staffCode);
                    positive(JdbcSpaDao.countAppointment(pkg));
                    positive(JdbcSpaDao.countAppointment2(staff));
                    LocalDateTime end = start.plusMinutes(JdbcSpaDao.countAppointment3(pkg));
                    if (JdbcSpaDao.countAppointment4(staff, p[0], end, start) > 0)
                        throw new IllegalArgumentException(
                                "Nhân viên hoặc thú cưng đã có lịch trùng giờ.");
                    long result =
                            JdbcSpaDao.insertAppointment(
                                    code("LH"), p[1], p[0], start, end, staff, pkg);
                    AuditService.log("Tạo lịch hẹn", petCode);
                    return result;
                });
    }

    public static long intake(String petCode, String appointmentCode, double weight, String note) {
        require(Permission.SPA_MANAGE);
        if (!Double.isFinite(weight) || weight <= 0)
            throw new IllegalArgumentException("Cân nặng phải lớn hơn 0.");
        return transaction(
                c -> {
                    Object[] p = pet(petCode);
                    Long appointment = null;
                    long staff = employeeId();
                    if (appointmentCode != null && !appointmentCode.isBlank()) {
                        List<Object[]> rows = JdbcSpaDao.findIntake(appointmentCode, p[0]);
                        if (rows.isEmpty())
                            throw new IllegalArgumentException(
                                    "Lịch hẹn không phù hợp hoặc đã tiếp nhận.");
                        appointment = value(rows.get(0)[0]);
                        if (rows.get(0)[1] != null) staff = value(rows.get(0)[1]);
                        JdbcSpaDao.updateIntake(appointment);
                    }
                    long result =
                            JdbcSpaDao.insertIntake(
                                    code("SPA"), appointment, p[0], weight, staff, note);
                    JdbcSpaDao.updateIntake2(weight, note, p[0]);
                    if (appointment != null) {
                        Object pkg = JdbcSpaDao.getIntake(appointment);
                        if (pkg != null) addSpaService(result, text(pkg));
                    }
                    AuditService.log("Tiếp nhận Spa", petCode);
                    return result;
                });
    }

    public static void addSpaService(long ticket, String packageCode) {
        require(Permission.SPA_MANAGE);
        transaction(
                c -> {
                    List<Object[]> tickets = JdbcSpaDao.findAddSpaService(ticket);
                    if (tickets.isEmpty()
                            || !Set.of("RECEIVED", "IN_PROGRESS").contains(text(tickets.get(0)[2])))
                        throw new IllegalArgumentException(
                                "Phiếu không còn cho phép thêm dịch vụ.");
                    Object[] t = tickets.get(0);
                    List<Object[]> prices =
                            JdbcSpaDao.findAddSpaService2(packageCode, t[1], t[0], t[0]);
                    if (prices.isEmpty())
                        throw new IllegalArgumentException(
                                "Chưa có bảng giá phù hợp với loài và cân nặng.");
                    Object[] price = prices.get(0);
                    JdbcSpaDao.updateAddSpaService(ticket, price[0], price[2], price[1]);
                    JdbcSpaDao.updateAddSpaService2(ticket, ticket);
                    AuditService.log("Thêm dịch vụ Spa", packageCode + " / " + ticket);
                    return null;
                });
    }

    public static void spaStatus(long ticket, String next) {
        require(Permission.SPA_MANAGE);
        String expected =
                switch (next) {
                    case "IN_PROGRESS" -> "RECEIVED";
                    case "COMPLETED" -> "IN_PROGRESS";
                    default -> throw new IllegalArgumentException("Trạng thái không hợp lệ.");
                };
        transaction(
                c -> {
                    if (JdbcSpaDao.countSpaStatus(ticket) == 0)
                        throw new IllegalArgumentException("Cần chọn dịch vụ cho phiếu.");
                    if (JdbcSpaDao.updateSpaStatus(next, ticket, expected) != 1)
                        throw new IllegalArgumentException(
                                "Phiếu đã chuyển trạng thái. Hãy tải lại.");
                    if (next.equals("COMPLETED")) JdbcSpaDao.updateSpaStatus2(ticket);
                    AuditService.log("Cập nhật Spa", ticket + " / " + next);
                    return null;
                });
    }

    public static void cancelAppointment(long id, String code) {
        require(Permission.SPA_MANAGE);
        transaction(
                c -> {
                    if (JdbcSpaDao.cancelAppointmentRecord(id) != 1)
                        throw new IllegalArgumentException("Chỉ có thể hủy lịch chưa tiếp nhận.");
                    AuditService.log("Hủy lịch hẹn", code);
                    return null;
                });
    }

    public static Object appointmentPetCode(long appointment) {
        SessionService.require(com.petstation.model.Permission.SPA_MANAGE);
        return JdbcSpaDao.appointmentPetCode(appointment);
    }

    public static List<Object[]> ticketDetails(long ticket) {
        SessionService.require(com.petstation.model.Permission.SPA_MANAGE);
        return JdbcSpaDao.ticketDetails(ticket);
    }
}
