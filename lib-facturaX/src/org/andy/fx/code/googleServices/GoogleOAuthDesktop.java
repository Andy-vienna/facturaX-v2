package org.andy.fx.code.googleServices;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.JsonFactory;

import javax.swing.*;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.*;
import java.util.Timer;

public final class GoogleOAuthDesktop {
    private static final String AUTH = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String TOKEN = "https://oauth2.googleapis.com/token";
    private static final SecureRandom RNG = new SecureRandom();

    public static final class Result {
        public final String sub;       // Google user id
        public final String email;
        public final boolean emailVerified;
        public Result(String sub, String email, boolean v){ this.sub=sub; this.email=email; this.emailVerified=v; }
    }

    public static Result login(JFrame parent, String clientId, String clientSecret) throws Exception {
        int port = freePort();
        String redirect = "http://127.0.0.1:" + port + "/callback";

        String verifier = b64url(random(64));
        String challenge = b64url(sha256(verifier));
        String state = b64url(random(16));

        URI authUri = new URI(AUTH + "?" + join(Map.of(
            "response_type","code",
            "client_id", clientId,
            "redirect_uri", redirect,
            "scope","openid email profile",
            "code_challenge", challenge,
            "code_challenge_method","S256",
            "state", state,
            "access_type","offline",
            "prompt","select_account"
        )));

        String code = awaitCode(parent, authUri, port, state);
        
        HttpClient http = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        String body = join(new LinkedHashMap<>() {{
            put("grant_type","authorization_code");
            put("code", code);
            put("redirect_uri", redirect);
            put("client_id", clientId);
            put("code_verifier", verifier);
            if (clientSecret != null && !clientSecret.isBlank()) put("client_secret", clientSecret);
        }});

        HttpRequest req = HttpRequest.newBuilder(URI.create(TOKEN))
            .timeout(Duration.ofSeconds(20))
            .header("Content-Type","application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode()!=200) throw new IOException("token http "+resp.statusCode()+": "+resp.body());

        Map<String,Object> tok = parseJson(resp.body());
        String idToken = (String) tok.get("id_token");
        if (idToken==null) throw new IllegalStateException("no id_token");

        JsonFactory jf = GsonFactory.getDefaultInstance();
        GoogleIdTokenVerifier verifierG = new GoogleIdTokenVerifier
            .Builder(new NetHttpTransport(), jf)
            .setAudience(Collections.singletonList(clientId))
            .build();
        GoogleIdToken idTok = verifierG.verify(idToken);
        if (idTok==null) throw new SecurityException("ID token invalid");

        GoogleIdToken.Payload p = idTok.getPayload();
        String sub = p.getSubject();
        String email = (String)p.get("email");
        boolean emailVerified = Boolean.TRUE.equals(p.getEmailVerified());

        return new Result(sub, email, emailVerified);
    }

    // ---------- helpers ----------
    private static String awaitCode(JFrame parent, URI authUri, int port, String expectState) throws Exception {
        var server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        final String[] codeHolder = {null};
        server.createContext("/callback", exchange -> {
            try {
                var q = splitQuery(exchange.getRequestURI().getRawQuery());
                if (!Objects.equals(q.get("state"), expectState)) throw new SecurityException("state mismatch");
                codeHolder[0] = q.get("code");
                byte[] html = "<html><body>Login OK. Dieses Fenster kann geschlossen werden.</body></html>".getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
                exchange.sendResponseHeaders(200, html.length);
                try (OutputStream os = exchange.getResponseBody()) { os.write(html); }
            } finally { exchange.close(); new Timer().schedule(new TimerTask(){@Override public void run(){server.stop(0);} }, 300); }
        });
        server.start();
        Desktop.getDesktop().browse(authUri);
        while (codeHolder[0]==null) Thread.sleep(100);
        return codeHolder[0];
    }

    private static int freePort() throws IOException {
        try (ServerSocket s = new ServerSocket(0, 0, InetAddress.getByName("127.0.0.1"))) { return s.getLocalPort(); }
    }

    private static byte[] random(int n){ byte[] b=new byte[n]; RNG.nextBytes(b); return b; }
    private static byte[] sha256(String s)throws Exception{ return MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.US_ASCII)); }
    private static String b64url(byte[] b){ return Base64.getUrlEncoder().withoutPadding().encodeToString(b); }
    private static String join(Map<String,String> m){
        StringBuilder sb=new StringBuilder();
        for (var e:m.entrySet()){
            if (sb.length()>0) sb.append('&');
            sb.append(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8)).append('=')
              .append(URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8));
        }
        return sb.toString();
    }
    private static Map<String,String> splitQuery(String q) {
        Map<String,String> m=new HashMap<>();
        if (q==null) return m;
        for (String p:q.split("&")) {
            int i=p.indexOf('=');
            String k=i<0?p: p.substring(0,i);
            String v=i<0?"": p.substring(i+1);
            m.put(URLDecoder.decode(k, StandardCharsets.UTF_8), URLDecoder.decode(v, StandardCharsets.UTF_8));
        }
        return m;
    }

    @SuppressWarnings("unchecked")
    private static Map<String,Object> parseJson(String json) {
        return new com.google.gson.Gson().fromJson(json, Map.class);
    }
}

