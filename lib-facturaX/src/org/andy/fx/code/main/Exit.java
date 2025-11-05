package org.andy.fx.code.main;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class Exit {
	
	private static final AtomicInteger CODE = new AtomicInteger(0); // Default: 0
	private static volatile Map<Integer, String> DESC = Map.of();
	
	// ###################################################################################################################################################
	// public Teil
	// ###################################################################################################################################################

	public static void init() {
		DESC = loadDescriptions();
	}

	public static void mark(int status) {
		CODE.set(status);
	}

	public static void exit(int status) {
		CODE.set(status);
		System.exit(status);
	}

	public static int plannedExitCode() {
		return CODE.get();
	}

	public static String desc(int code) {
		return DESC.getOrDefault(code, "unbekannt");
	}
	
	// ###################################################################################################################################################
	// private Teil
	// ###################################################################################################################################################

	private static Map<Integer, String> loadDescriptions() {
		try (InputStream raw = openResource("exitCodes.json")) {
			if (raw == null)
				throw new FileNotFoundException("exitCodes.json fehlt");

			ObjectMapper om = JsonMapper.builder().enable(JsonReadFeature.ALLOW_JAVA_COMMENTS.mappedFeature())
					.enable(JsonReadFeature.ALLOW_YAML_COMMENTS.mappedFeature())
					.enable(JsonReadFeature.ALLOW_TRAILING_COMMA.mappedFeature())
					.enable(JsonReadFeature.ALLOW_SINGLE_QUOTES.mappedFeature()).build();

			Map<String, String> m = om.readValue(raw, new TypeReference<Map<String, String>>() {
			});
			return m.entrySet().stream().collect(java.util.stream.Collectors
					.toUnmodifiableMap(e -> Integer.parseInt(stripBom(e.getKey()).trim()), Map.Entry::getValue));
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return Map.of();
		}
	}

	private static String stripBom(String s) {
		return (s != null && !s.isEmpty() && s.charAt(0) == '\uFEFF') ? s.substring(1) : s;
	}

	private static InputStream openResource(String name) throws FileNotFoundException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InputStream in = (cl != null) ? cl.getResourceAsStream(name) : null;
		if (in == null)
			in = Exit.class.getResourceAsStream("/" + name);
		if (in == null)
			throw new FileNotFoundException("Ressource nicht gefunden: " + name);
		return in;
	}
}
