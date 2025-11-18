package org.andy.code.httpServer.endpoints;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class StartPageHandler implements HttpHandler {
	
	private static final String DOWNLOAD_NAME = "cert.crt";
	
	// ###################################################################################################################################################
	// Server-Endpunkt für Startseite
	// ###################################################################################################################################################
	
    @Override
    public void handle(HttpExchange ex) throws IOException {
        String downloadUrl = "/download?name=" + URLEncoder.encode(DOWNLOAD_NAME, "UTF-8");
        String html = """
                <!DOCTYPE html>
                <html lang="de">
                <head>
                    <meta charset="UTF-8">
                    <title>Dateidownload</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f5f5f5;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            height: 100vh;
                            margin: 0;
                        }
                        .card {
                            background: white;
                            padding: 24px 32px;
                            border-radius: 12px;
                            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
                            text-align: center;
                            min-width: 320px;
                        }
                        h1 {
                            margin-top: 0;
                            margin-bottom: 12px;
                            font-size: 22px;
                        }
                        p {
                            margin-top: 0;
                            margin-bottom: 20px;
                            color: #555;
                        }
                        .btn {
                            display: inline-block;
                            padding: 10px 20px;
                            border-radius: 6px;
                            border: none;
                            background-color: #1976d2;
                            color: #fff;
                            font-size: 14px;
                            font-weight: 600;
                            cursor: pointer;
                            text-decoration: none;
                        }
                        .btn:hover {
                            background-color: #1259a3;
                        }
                        .filename {
                            font-family: monospace;
                            font-size: 13px;
                            color: #333;
                        }
                    </style>
                </head>
                <body>
                    <div class="card">
                        <h1>Download bereit</h1>
                        <p>Die folgende Datei steht zum Herunterladen zur Verfügung:</p>
                        <p class="filename">%s</p>
                        <a class="btn" href="%s">Download starten</a>
                    </div>
                </body>
                </html>
                """.formatted(DOWNLOAD_NAME, downloadUrl);

        byte[] body = html.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
        ex.sendResponseHeaders(200, body.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(body);
        }
    }
}