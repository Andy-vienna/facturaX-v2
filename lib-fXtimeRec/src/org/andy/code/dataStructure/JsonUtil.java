package org.andy.code.dataStructure;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import org.andy.code.dataStructure.entityJSON.JsonSettings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtil {

	private final static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	// ###################################################################################################################################################
	// json-Files lesen
	// ###################################################################################################################################################

	public static JsonSettings loadSettings(Path p) throws IOException {
		if (Files.notExists(p))
			return new JsonSettings();
		try (Reader r = Files.newBufferedReader(p, UTF_8)) {
			return GSON.fromJson(r, JsonSettings.class);
		}
	}
	
	// ###################################################################################################################################################
	// json-Files schreiben
	// ###################################################################################################################################################

	public static void saveSettings(Path file, JsonSettings s) throws IOException {
		Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
		byte[] data = GSON.toJson(s).getBytes(StandardCharsets.UTF_8);
		try (FileChannel ch = FileChannel.open(tmp, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
				StandardOpenOption.TRUNCATE_EXISTING)) {
			ByteBuffer buf = ByteBuffer.wrap(data);
			while (buf.hasRemaining())
				ch.write(buf);
			ch.force(true); // fsync: Daten + Metadata
		}
		Files.move(tmp, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
	}
	
}
