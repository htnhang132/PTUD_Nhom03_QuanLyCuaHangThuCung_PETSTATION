package com.petstation.service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public final class BarcodeUsbServer implements AutoCloseable {
    public static final int DEFAULT_PORT = 8765;
    private static final int MAX_BODY_BYTES = 64;
    private static final String TOKEN_HEADER = "X-PetStation-Token";

    private final HttpServer server;
    private final ExecutorService executor;
    private final Consumer<String> onBarcode;
    private final String pairingCode;
    private final int port;
    private boolean started;

    public BarcodeUsbServer(Consumer<String> onBarcode) throws IOException {
        this(onBarcode, DEFAULT_PORT);
    }

    public BarcodeUsbServer(Consumer<String> onBarcode, int port) throws IOException {
        this.onBarcode = Objects.requireNonNull(onBarcode, "onBarcode");
        this.port = port;
        this.pairingCode = taoMaGhepNoi();

        this.server = HttpServer.create(
                new InetSocketAddress(InetAddress.getByName("127.0.0.1"), port), 0);
        this.executor = Executors.newFixedThreadPool(2, task -> {
            Thread thread = new Thread(task, "petstation-usb-scanner");
            thread.setDaemon(true);
            return thread;
        });

        server.setExecutor(executor);
        server.createContext("/health", this::handleHealth);
        server.createContext("/api/scan", this::handleScan);
    }

    public synchronized void start() {
        if (started) return;
        server.start();
        started = true;
    }

    public String getPairingCode() {
        return pairingCode;
    }

    public int getPort() {
        return port;
    }

    private void handleHealth(HttpExchange exchange) throws IOException {
        try (exchange) {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                send(exchange, 405, "{\"ok\":false,\"message\":\"Method not allowed\"}");
                return;
            }
            if (!authorized(exchange)) {
                send(exchange, 401, "{\"ok\":false,\"message\":\"Invalid pairing code\"}");
                return;
            }
            send(exchange, 200, "{\"ok\":true,\"message\":\"PetStation is ready\"}");
        }
    }

    private void handleScan(HttpExchange exchange) throws IOException {
        try (exchange) {
            if (!"/api/scan".equals(exchange.getRequestURI().getPath())) {
                send(exchange, 404, "{\"ok\":false,\"message\":\"Not found\"}");
                return;
            }
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                send(exchange, 405, "{\"ok\":false,\"message\":\"Method not allowed\"}");
                return;
            }
            if (!authorized(exchange)) {
                send(exchange, 401, "{\"ok\":false,\"message\":\"Invalid pairing code\"}");
                return;
            }

            byte[] body = exchange.getRequestBody().readNBytes(MAX_BODY_BYTES + 1);
            if (body.length == 0 || body.length > MAX_BODY_BYTES) {
                send(exchange, 400, "{\"ok\":false,\"message\":\"Invalid request body\"}");
                return;
            }

            String barcode = new String(body, StandardCharsets.UTF_8).trim();
            if (!isValidEan13(barcode)) {
                send(exchange, 400, "{\"ok\":false,\"message\":\"Barcode must be a valid EAN-13\"}");
                return;
            }

            onBarcode.accept(barcode);
            send(exchange, 202, "{\"ok\":true,\"barcode\":\"" + barcode + "\"}");
        } catch (RuntimeException exception) {
            try {
                send(exchange, 500, "{\"ok\":false,\"message\":\"Desktop handler error\"}");
            } catch (IOException ignored) {
            }
        }
    }

    private boolean authorized(HttpExchange exchange) {
        String supplied = exchange.getRequestHeaders().getFirst(TOKEN_HEADER);
        if (supplied == null) return false;
        return MessageDigest.isEqual(
                pairingCode.getBytes(StandardCharsets.UTF_8),
                supplied.trim().getBytes(StandardCharsets.UTF_8));
    }

    private static boolean isValidEan13(String value) {
        if (value == null || !value.matches("\\d{13}")) return false;
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = value.charAt(i) - '0';
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int checkDigit = (10 - sum % 10) % 10;
        return checkDigit == value.charAt(12) - '0';
    }

    private static String taoMaGhepNoi() {
        byte[] bytes = new byte[8];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes).toUpperCase();
    }

    private static void send(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        exchange.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
        exchange.getResponseHeaders().set("Connection", "close");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
    }

    @Override
    public synchronized void close() {
        if (started) {
            server.stop(0);
            started = false;
        }
        executor.shutdownNow();
    }
}
