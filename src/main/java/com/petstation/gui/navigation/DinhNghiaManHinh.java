package com.petstation.gui.navigation;

import com.petstation.model.Quyen;

/**
 * Mô tả tiêu đề, gợi ý và quyền cần có của một màn hình.
 *
 * @param id id
 * @param tieuDe tiêu đề
 * @param goiY gợi ý
 * @param quyen quyền
 * @param daTrienKhai đã triển khai
 */
public record DinhNghiaManHinh(
        MaManHinh id, String tieuDe, String goiY, Quyen quyen, boolean daTrienKhai) {}
