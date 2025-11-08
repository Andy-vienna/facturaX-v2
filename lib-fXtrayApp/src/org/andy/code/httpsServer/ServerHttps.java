package org.andy.code.httpsServer;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.andy.code.httpsServer.endpoints.GenericHandler;
import org.andy.code.httpsServer.endpoints.LastEventHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.net.httpserver.HttpsConfigurator; // Wichtig
import com.sun.net.httpserver.HttpsServer; // Wichtig: HttpsServer importieren

public class ServerHttps {

	private static final Logger logger = LogManager.getLogger(ServerHttps.class);

	// ###################################################################################################################################################
	// Server starten
	// ###################################################################################################################################################

	public static void startServer() throws Exception { // throws Exception statt IOException
		// 1. SSLContext initialisieren
		SSLContext sslContext = SSLContext.getInstance("TLS");

		// 2. Keystore laden
		char[] password = "password".toCharArray(); // Das Passwort, das du im keytool verwendet hast
		KeyStore ks = KeyStore.getInstance("JKS");
		// Lade die keystore.jks Datei
		try (FileInputStream fis = new FileInputStream("keystore.jks")) {
			ks.load(fis, password);
		}

		// 3. KeyManagerFactory einrichten
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(ks, password);

		// 4. TrustManagerFactory einrichten (optional, aber gute Praxis)
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		tmf.init(ks);

		// 5. SSLContext mit den Keys konfigurieren
		sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

		// 6. HttpsServer erstellen
		HttpsServer server = HttpsServer.create(new InetSocketAddress(8112), 0);
		// HttpsConfigurator wird direkt mit dem SSLContext erstellt.
		server.setHttpsConfigurator(new HttpsConfigurator(sslContext));

		// 7. Context und Handler bleiben gleich
		server.createContext("/api/time-events", new GenericHandler()); // Server-Endpunkt für Stempelungen
		server.createContext("/api/last-event", new LastEventHandler()); // Server-Endpunkt für Status-Übergabe
		server.setExecutor(null);
		server.start();

		// Konsolenausgabe anpassen!
		logger.debug("Server läuft auf https://localhost:8112/");
	}

	// ###################################################################################################################################################
	// Server-Endpunkt für Statusabfragen
	// ###################################################################################################################################################

}
