package org.andy.code.httpServer.endpoints;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class DownloadHandler implements HttpHandler {
    private final Path baseDir;

    public DownloadHandler(Path baseDir) { this.baseDir = baseDir; }

	// ###################################################################################################################################################
	// Server-Endpunkt für Zertifikat-Download
	// ###################################################################################################################################################
    
    @Override
    public void handle(HttpExchange ex) throws IOException {
        try {
            if (!ex.getRequestMethod().equalsIgnoreCase("GET")
                    && !ex.getRequestMethod().equalsIgnoreCase("HEAD")) {
                sendText(ex, 405, "Only GET/HEAD");
                return;
            }

            String query = ex.getRequestURI().getRawQuery();
            Map<String, String> params = parseQuery(query);
            String name = params.get("name");
            if (name == null || name.isBlank()) {
                sendText(ex, 400, "Parameter 'name' fehlt");
                return;
            }

            Path requested = baseDir.resolve(URLDecoder.decode(name, "UTF-8")).normalize();
            if (!requested.startsWith(baseDir) || !Files.isRegularFile(requested)) {
                sendText(ex, 404, "Datei nicht gefunden");
                return;
            }

            String mime = Optional.ofNullable(Files.probeContentType(requested))
                                  .orElse("application/octet-stream");
            long size = Files.size(requested);

            ex.getResponseHeaders().add("Content-Type", mime);
            String fileName = requested.getFileName().toString();
            ex.getResponseHeaders().add("Content-Disposition", contentDispositionAttachment(fileName));
            ex.getResponseHeaders().add("Accept-Ranges", "none");

            boolean head = ex.getRequestMethod().equalsIgnoreCase("HEAD");
            if (!head) {
                ex.sendResponseHeaders(200, size); // bekannt – keine Chunked-Übertragung
                try (OutputStream os = ex.getResponseBody();
                     InputStream in = Files.newInputStream(requested)) {
                    in.transferTo(os);
                }
            } else {
                ex.sendResponseHeaders(200, -1);
                ex.close();
            }
        } catch (Exception e) {
            safeFail(ex);
        }
    }
    
	// ------------------------------------------------------------------------------------------------------------------------------------------------
	// Hilfsmethoden für Klasse

    private static Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> map = new LinkedHashMap<>();
        if (rawQuery == null || rawQuery.isEmpty()) return map;
        for (String pair : rawQuery.split("&")) {
            int idx = pair.indexOf('=');
            if (idx >= 0) map.put(pair.substring(0, idx), pair.substring(idx + 1));
            else map.put(pair, "");
        }
        return map;
    }

    private static String contentDispositionAttachment(String filename) {
        String quoted = filename.replace("\\", "\\\\").replace("\"", "\\\"");
        String utf8 = URLEncoder.encode(filename, java.nio.charset.StandardCharsets.UTF_8).replace("+", "%20");
        return "attachment; filename=\"" + quoted + "\"; filename*=UTF-8''" + utf8;
    }

    private static void sendText(HttpExchange ex, int code, String msg) throws IOException {
        byte[] body = msg.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        ex.sendResponseHeaders(code, body.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(body); }
    }

    private static void safeFail(HttpExchange ex) {
        try {
            if (ex.getResponseHeaders().isEmpty()) sendText(ex, 500, "Interner Fehler");
            else ex.close();
        } catch (IOException ignore) { }
    }
}
