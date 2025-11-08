package org.andy.code.dataStructure.entityJSON;

import com.google.gson.*;

import java.nio.file.*;
import java.io.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public class JsonUtil {

	private final static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	// ###################################################################################################################################################
	// json-Files lesen
	// ###################################################################################################################################################

	public static JsonDb loadDB(Path p) throws IOException {
		if (Files.notExists(p))
			return new JsonDb();
		try (Reader r = Files.newBufferedReader(p, UTF_8)) {
			return GSON.fromJson(r, JsonDb.class);
		}
	}
	
	public static JsonHttps parseString(String https) {
	    if (https == null || https.isBlank()) return new JsonHttps();
	    return GSON.fromJson(https, JsonHttps.class);
	}

}
