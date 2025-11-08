package org.andy.code.httpsServer.endpoints;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;

import org.andy.code.dataStructure.entityJSON.JsonHttps;
import org.andy.code.dataStructure.entityJSON.JsonUtil;
import org.andy.code.dataStructure.repository.WorkTimeRawRepository;
import org.andy.gui.main.ClockTrayApp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class GenericHandler implements HttpHandler {

	private static final Logger logger = LogManager.getLogger(GenericHandler.class);

	private static String requestBody = null;
	private static ArrayList<JsonHttps> receive = new ArrayList<>();
	private static boolean isData = false;

	// ###################################################################################################################################################
	// Server-Endpunkt für Stempelungen
	// ###################################################################################################################################################

	public void handle(HttpExchange exchange) throws IOException {
		// -------------------------------------------------------------------------
		// Event POST auswerten
		if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
			try (InputStream requestBodyStream = exchange.getRequestBody()) {
				byte[] requestBodyBytes = requestBodyStream.readAllBytes();
				requestBody = new String(requestBodyBytes, StandardCharsets.UTF_8);
			}
		}
		// -------------------------------------------------------------------------
		// Event beantworten, damit die Gegenstelle weitermacht
		String response = "OK";
		exchange.sendResponseHeaders(200, response.length());
		try (OutputStream responseBodyStream = exchange.getResponseBody()) {
			responseBodyStream.write(response.getBytes());
		}
		// -------------------------------------------------------------------------
		// Daten pro Event aufzeichnen
		JsonHttps recv = parseData(requestBody); // Empfangenen Sting parsen
		if (recv.event.contains("IN") || recv.event.contains("BREAK") || recv.event.contains("OUT")) {
			receive.add(recv); // pro emfangenem JSON einen neuen Eintrag in die ArrayList schreiben
			logger.debug("daten empfangen: " + requestBody);
			isData = false;
		} else {
			logger.debug("status empfangen");
			isData = true;
		}
		if (isData)
			readData(receive);
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------
	// Hilfsmethoden für Klasse

	private JsonHttps parseData(String json) {
		return JsonUtil.parseString(json);
	}

	private void readData(ArrayList<JsonHttps> receive) {
		if (receive.size() == 0)
			return;
		JsonHttps erg = null;
		WorkTimeRawRepository repo = new WorkTimeRawRepository();
		for (int n = 0; n < receive.size(); n++) {
			erg = receive.get(n);
			logger.debug(n + "|" + erg.event + "|" + erg.username + "|" + erg.source + "|" + erg.tz + "|" + erg.ts + "|"
					+ erg.deviceId);
			OffsetDateTime odt = OffsetDateTime.parse(erg.ts);
			ClockTrayApp.wt = repo.record(erg.event, erg.source, erg.tz, odt, erg.username, erg.deviceId);
		}
		receive.clear(); // Empfangene Daten loswerden
		isData = false;
		if (ClockTrayApp.getUser().equals(erg.username))
			ClockTrayApp.actState(); // Icon und Status nachsetzen
	}

}
