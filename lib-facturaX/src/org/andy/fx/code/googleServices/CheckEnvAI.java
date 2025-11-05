package org.andy.fx.code.googleServices;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.andy.fx.code.dataStructure.entityJSON.JsonAI;
import org.andy.fx.code.dataStructure.entityJSON.JsonUtil;
import org.andy.fx.code.googleServices.ClientSecret.ClientSecrets;
import org.andy.fx.code.googleServices.InterfaceBuilder.DocAiConfig;
import org.andy.fx.code.main.StartUp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.cloud.documentai.v1.DocumentProcessorServiceClient;
import com.google.cloud.documentai.v1.DocumentProcessorServiceSettings;
import com.google.cloud.documentai.v1.GetProcessorRequest;

public class CheckEnvAI {
	
	private static final Logger logger = LogManager.getLogger(CheckEnvAI.class);
	private static final String secretsDir = System.getProperty("user.dir") + "\\secrets\\";
	private static final String envGeminiApi = "GEMINI_API_KEY";
	private static final String envGoogleApplication = "GOOGLE_APPLICATION_CREDENTIALS";
	
	private static JsonAI settingsAI = new JsonAI();
	private static ClientSecrets cs = null;
	private static String gem = null;

	// ###################################################################################################################################################
	// public teil
	// ###################################################################################################################################################

	public static void checkAI(Path fileAI) {
		try {
			CheckEnvAI.getEnv(fileAI);
		} catch (Exception e) {
			logger.error("error checking AI Environment " + e.getMessage());
			StartUp.gracefulQuit(93);
		}
	}
	
	// ###################################################################################################################################################
	// private teil
	// ###################################################################################################################################################

	private static void getEnv(Path fileAI) throws Exception {
		
		if (fileAI == null) return; // Settings-Datei für AI nicht vorhanden, Exit
		
		gem = System.getenv(envGeminiApi); // Environment-Key lesen
		String goo = System.getenv(envGoogleApplication);
		
		settingsAI = JsonUtil.loadAI(fileAI); // Einstellungen laden
		
		//-----------------------------------------------------------------------------------------------------------------------
		// Prüfung ob OAuth2 Login möglich ist
		if (settingsAI.isOAuth2Login) {
			Path secretFile = null;
			if (settingsAI.oauth2file != null || !settingsAI.oauth2file.isEmpty()) {
				boolean ok = isJsonFile(secretsDir + settingsAI.oauth2file) ? true : false; // ist die Datei überhaupt vorhanden ?
				if (ok)	secretFile = findJsonFile(settingsAI.oauth2file);
				cs = ClientSecret.loadClientSecrets(secretFile);
			} else {
				settingsAI.isOAuth2Login = false; // ab hier ist klar, dass kein OAuth2-Login zur Verfügung steht
			}
		}
		
		//-----------------------------------------------------------------------------------------------------------------------
		// Prüfung ob Gemini-API freigeschaltet ist oder Gemini-API-Key vorhanden ist
		if (settingsAI.isGeminiAPI) {
			if (settingsAI.geminiApiKey == null || settingsAI.geminiApiKey.isEmpty()) settingsAI.isGeminiAPI = false;
			if (!gem.equals(settingsAI.geminiApiKey)) settingsAI.isGeminiAPI = false;
		}
		
		//-----------------------------------------------------------------------------------------------------------------------
		// Prüfung ob DocumentAI zur Verfügung steht
		if (settingsAI.isDocumentAI) {
			if (goo == null || goo.isEmpty()) settingsAI.isDocumentAI = false;
			if (settingsAI.documentAIprojectID == null || settingsAI.documentAIprojectID.isEmpty()
					|| settingsAI.documentAIlocation == null || settingsAI.documentAIlocation.isEmpty()
					|| settingsAI.documentAIprocessorId == null || settingsAI.documentAIprocessorId.isEmpty()) {
				settingsAI.isDocumentAI = false;
			}
			if (!isAvailable()) settingsAI.isDocumentAI = false;
		}
		
		JsonUtil.saveAI(fileAI, settingsAI); // json-File wieder sichern
	}
	
	// ###################################################################################################################################################
	// Hilfsmethoden
	// ###################################################################################################################################################

	private static Path findJsonFile(String name) throws IOException {
		Path secrets = Paths.get(secretsDir);
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(secrets, name)) {
			for (Path p : ds) {
				if (Files.isRegularFile(p))
					return p;
			}
		}
		return null;
	}

	private static boolean isJsonFile(String name) {
		File f = new File(name);
		return f.isFile() ? true : false;
	}
	
	private static boolean isAvailable() {
		DocAiConfig cfg = new DocAiConfig(settingsAI.documentAIprojectID, settingsAI.documentAIlocation, settingsAI.documentAIprocessorId);
		String endpoint = cfg.location() + "-documentai.googleapis.com:443";
		try {
			DocumentProcessorServiceSettings s = DocumentProcessorServiceSettings.newBuilder().setEndpoint(endpoint).build();
			try (DocumentProcessorServiceClient c = DocumentProcessorServiceClient.create(s)) {
				String name = "projects/%s/locations/%s/processors/%s".formatted(cfg.projectId(), cfg.location(), cfg.processorId());
				c.getProcessor(GetProcessorRequest.newBuilder().setName(name).build());
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}

	// ###################################################################################################################################################
	// Getter und Setter
	// ###################################################################################################################################################

	public static JsonAI getSettingsAI() {
		return settingsAI;
	}

	public static void setSettingsAI(JsonAI settingsAI) {
		CheckEnvAI.settingsAI = settingsAI;
	}

	public static ClientSecrets getCs() {
		return cs;
	}

	public static String getGem() {
		return gem;
	}
	
}
