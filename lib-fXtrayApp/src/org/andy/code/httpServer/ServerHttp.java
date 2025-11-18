package org.andy.code.httpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.andy.code.httpServer.endpoints.DownloadHandler;
import org.andy.code.httpServer.endpoints.StartPageHandler;
import org.andy.code.main.StartUp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.net.httpserver.HttpServer;

public class ServerHttp {
	
	private static final Logger logger = LogManager.getLogger(ServerHttp.class);
	
	// ###################################################################################################################################################
	// Server starten
	// ###################################################################################################################################################

    public static void startServer() {
    	Path baseDir = Paths.get("downloads").toAbsolutePath().normalize();
    	HttpServer server = null;
    	
        try {
			Files.createDirectories(baseDir);
			server = HttpServer.create(new InetSocketAddress(8113), 0);
		} catch (IOException e) {
			logger.error("HTTP Server kann nicht gestartet werden: " + e.getMessage());
			StartUp.gracefulQuit(87);
		}

        server.createContext("/download", new DownloadHandler(baseDir));
        server.createContext("/", new StartPageHandler());
        server.setExecutor(null);
        server.start();
        logger.debug("Server läuft auf http://localhost:8113/");
    }
}
