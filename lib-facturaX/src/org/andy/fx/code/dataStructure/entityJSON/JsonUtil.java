package org.andy.fx.code.dataStructure.entityJSON;

import com.google.gson.*;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public class JsonUtil {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	// ###################################################################################################################################################
	// json-Files lesen
	// ###################################################################################################################################################

	public static JsonApp loadAPP(Path p) throws IOException {
		if (Files.notExists(p))
			return new JsonApp();
		try (Reader r = Files.newBufferedReader(p, UTF_8)) {
			return GSON.fromJson(r, JsonApp.class);
		}
	}
	
	public static JsonAI loadAI(Path p) throws IOException {
		if (Files.notExists(p))
			return new JsonAI();
		try (Reader r = Files.newBufferedReader(p, UTF_8)) {
			return GSON.fromJson(r, JsonAI.class);
		}
	}

	public static JsonDb loadDB(Path p) throws IOException {
		if (Files.notExists(p))
			return new JsonDb();
		try (Reader r = Files.newBufferedReader(p, UTF_8)) {
			return GSON.fromJson(r, JsonDb.class);
		}
	}

	// ###################################################################################################################################################
	// json-Files schreiben
	// ###################################################################################################################################################

	public static void saveAPP(Path file, JsonApp s) throws IOException {
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

	public static void saveDB(Path file, JsonDb s) throws IOException {
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
	
	public static void saveAI(Path file, JsonAI s) throws IOException {
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
