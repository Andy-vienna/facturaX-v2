package org.andy.code.workTime;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.andy.code.main.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class WorkTimeLoader {
	
	private static final Logger logger = LogManager.getLogger(WorkTimeLoader.class);
	
	private List<String> eintragList = new ArrayList<>();

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public List<String> syncEvents(String username) {

		// fetch data from server
	    String jsonString = fetchFromServer(username);
	    
	    if (jsonString != null) {
	        List<Integer> receivedIds = new ArrayList<>();
	        
	        try {
		        JSONObject fullResponse = new JSONObject(jsonString);
		        
		        // only if entry has "data"
		        if (fullResponse.has("data")) {
		            JSONArray dataArray = fullResponse.getJSONArray("data");
		            
		            for (int i = 0; i < dataArray.length(); i++) {
		                // get single event-object
		                JSONObject event = dataArray.getJSONObject(i);
		                
		                // get the whole event and put it into the list
		                eintragList.add(event.toString());
		                
		                // remember id from online-database to delete entry
		                receivedIds.add(event.getInt("id"));
		            }
		        }
		    } catch (Exception e) {
		        logger.error("Fehler beim Parsen: " + e.getMessage());
		    }
	        
	        // do delete the entrys with the saved id's
            if (!receivedIds.isEmpty()) {
                confirmDeletion(receivedIds);
	        }
	    }
		return eintragList;
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

	private String fetchFromServer(String username) {
		String responseText = null;
	    try {
	        // URL mit Parameter aufrufen
	        String urlString = Settings.getSettings().urlGetData + "getevents.php?username=" + username;
	        URL url = URI.create(urlString).toURL();
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

	        // Header setzen (WICHTIG: Dein API-Key aus dem PHP-Script)
	        conn.setRequestMethod("GET");
	        conn.setRequestProperty("X-API-KEY", Settings.getSettings().urlXKey);
	        conn.setRequestProperty("Accept", "application/json");

	        int responseCode = conn.getResponseCode();
	        if (responseCode == 200) { // OK
	            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	            StringBuilder response = new StringBuilder();
	            String inputLine;

	            while ((inputLine = in.readLine()) != null) {
	                response.append(inputLine);
	            }
	            in.close();

	            // Ergebnis verarbeiten
	            responseText = response.toString();

	        } else {
	            logger.error("Fehler: " + responseCode);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return responseText;
	}
	
	private void confirmDeletion(List<Integer> idsToDelete) {
	    try {
	    	String urlString = Settings.getSettings().urlGetData + "deleteevents.php";
	        URL url = URI.create(urlString).toURL();
	        
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("POST");
	        conn.setRequestProperty("X-API-KEY", Settings.getSettings().urlXKey);
	        conn.setRequestProperty("Content-Type", "application/json");
	        conn.setDoOutput(true);

	        // JSON-Body erstellen: {"ids": [1, 2, 3]}
	        JSONObject jsonBody = new JSONObject();
	        jsonBody.put("ids", new JSONArray(idsToDelete));

	        try (OutputStream os = conn.getOutputStream()) {
	            os.write(jsonBody.toString().getBytes("utf-8"));
	        }

	        if (conn.getResponseCode() == 200) {
	        	// this is for successful deleted events on server
	        }
	    } catch (Exception e) {
	    	logger.error("Fehler: " + e.getMessage());
	    }
	}
	
}
