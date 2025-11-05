package org.andy.fx.code.googleServices;

import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.google.gson.*;

public class ClientSecret {
	
	// ###################################################################################################################################################
	// Record bilden
	// ###################################################################################################################################################

	public record ClientSecrets(String clientId, String projectId, String clientSecret, List<String> redirectUris) {}

	// ###################################################################################################################################################
	// json-File lesen
	// ###################################################################################################################################################

	public static ClientSecrets loadClientSecrets(Path jsonPath) throws Exception {
	    String json = Files.readString(jsonPath, StandardCharsets.UTF_8);
	    JsonObject root = JsonParser.parseString(json).getAsJsonObject();
	    JsonObject o = root.has("installed") ? root.getAsJsonObject("installed")
	                : root.getAsJsonObject("web"); // Fallback, falls „web“-Client
	    String clientId = o.get("client_id").getAsString();
	    String projectId = o.get("project_id").getAsString();
	    String clientSecret = o.has("client_secret") ? o.get("client_secret").getAsString() : null;
	    List<String> redirects = new ArrayList<>();
	    if (o.has("redirect_uris")) {
	        for (var e : o.getAsJsonArray("redirect_uris")) redirects.add(e.getAsString());
	    }
	    return new ClientSecrets(clientId, projectId, clientSecret, redirects);
	}

}

