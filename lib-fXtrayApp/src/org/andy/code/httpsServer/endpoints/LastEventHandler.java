package org.andy.code.httpsServer.endpoints;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.andy.code.dataStructure.entity.WorkTimeRaw;
import org.andy.code.dataStructure.repository.WorkTimeRawRepository;
import org.andy.code.misc.App;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class LastEventHandler implements HttpHandler {

	private static final Logger logger = LogManager.getLogger(LastEventHandler.class);
	private static App a = new App();
	private String lastEventJson = """
			{"event":"%s", "username":"%s", "ts":"%s"}
			""";

	// ###################################################################################################################################################
	// Server-Endpunkt für Statusabfragen
	// ###################################################################################################################################################

	public void handle(HttpExchange exchange) throws IOException {
		// -------------------------------------------------------------------------
		// Event GET auswerten
		if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
			try {
			    Thread.sleep(500); // 500 ms Wartezeit ...
			} catch (InterruptedException ie) {
			    Thread.currentThread().interrupt(); // Interrupt respektieren
			}
			String query = exchange.getRequestURI().getQuery();
			String username = null;
			// ---------------------------------------------------------------------
			// Usernamen aus URL lesen
			if (query != null && query.startsWith("username=")) {
				username = query.substring("username=".length());
				username = java.net.URLDecoder.decode(username, StandardCharsets.UTF_8.name());
			}
			// ---------------------------------------------------------------------
			// Fehlerbehandlung wenn kein username
			if (username == null || username.trim().isEmpty()) {
				exchange.sendResponseHeaders(400, -1); // 400 Bad Request
				logger.warn("Abfrage für letzten Event ohne Username erhalten.");
				return;
			}
			// ---------------------------------------------------------------------
			// letzten Eintrag für username finden
			WorkTimeRawRepository repo = new WorkTimeRawRepository();
			WorkTimeRaw wtr = repo.findLastEvent(username);
			// ---------------------------------------------------------------------
			// Fehlerbehandlung wenn kein Datensatz
			if (wtr == null) {
				exchange.sendResponseHeaders(404, -1); // 404 Not Found
				if (a.DEBUG) logger.warn("Für den user: " + username + " exisitiert kein Datensatz.");
				return;
			}
			// ---------------------------------------------------------------------
			// Antwort senden
			String answer = String.format(lastEventJson, wtr.getEvent(), username, wtr.getTs().toString());
			byte[] responseBytes = answer.getBytes(StandardCharsets.UTF_8);
			exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
			exchange.sendResponseHeaders(200, responseBytes.length);
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(responseBytes);
			}
		} else {
			exchange.sendResponseHeaders(405, -1); // Method Not Allowed
		}
	}
}
