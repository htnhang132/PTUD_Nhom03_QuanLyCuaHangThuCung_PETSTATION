package com.petstation.model;

import java.time.LocalDate;

public record Employee(
    long id,
    String employeeCode,
    String fullName,
    LocalDate dateOfBirth,
    String phone,
    String address,
    String position,
    LocalDate startDate,
    boolean active
) {}
