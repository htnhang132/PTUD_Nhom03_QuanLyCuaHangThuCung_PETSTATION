package com.petstation.gui.navigation;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/** Trạng thái ca làm việc trong phiên (dữ liệu tạm, chưa lưu database). */
public final class ShiftState {
    public enum Method { CASH, TRANSFER, CARD }

    private final List<Runnable> listeners = new ArrayList<>();
    private boolean open;
    private String shiftName = "";
    private LocalTime openedAt;
    private long openingCash;
    private long cashSales;
    private long transferSales;
    private long cardSales;
    private int invoiceCount;
    private String lastHandover = "";
    private String pendingTitle = "";

    /** Tên chức năng người dùng đang muốn vào khi bị chặn vì chưa mở ca (để hiện gợi ý và tự quay lại). */
    public String pendingTitle() { return pendingTitle; }
    public void setPendingTitle(String title) { this.pendingTitle = title == null ? "" : title; }

    public boolean isOpen() { return open; }
    public String shiftName() { return shiftName; }
    public LocalTime openedAt() { return openedAt; }
    public long openingCash() { return openingCash; }
    public long cashSales() { return cashSales; }
    public long transferSales() { return transferSales; }
    public long cardSales() { return cardSales; }
    public int invoiceCount() { return invoiceCount; }
    public long expectedCash() { return openingCash + cashSales; }
    public String lastHandover() { return lastHandover; }

    public String label() {
        return open ? "Đang mở – "+ shiftName : "Chưa mở ca";
    }

    public void open(String shiftName, long openingCash) {
        this.open = true;
        this.shiftName = shiftName;
        this.openingCash = openingCash;
        this.openedAt = LocalTime.now().withSecond(0).withNano(0);
        this.cashSales = 0;
        this.transferSales = 0;
        this.cardSales = 0;
        this.invoiceCount = 0;
        fire();
    }

    public void recordSale(Method method, long amount) {
        if (!open) return;
        switch (method) {
            case CASH -> cashSales += amount;
            case TRANSFER -> transferSales += amount;
            case CARD -> cardSales += amount;
        }
        invoiceCount++;
        fire();
    }

    public void close(long countedCash, String note) {
        long diff = countedCash - expectedCash();
        lastHandover = shiftName + ": thực đếm "+ countedCash + "/ dự kiến "+ expectedCash()
            + "(chênh lệch "+ diff + ")"+ (note == null || note.isBlank() ? "": "– "+ note.trim());
        open = false;
        fire();
    }

    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    private void fire() {
        for (Runnable listener : List.copyOf(listeners)) listener.run();
    }
}
