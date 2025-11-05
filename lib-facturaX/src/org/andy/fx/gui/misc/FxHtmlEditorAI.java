package org.andy.fx.gui.misc;

import java.awt.BorderLayout;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.JPanel;

import org.andy.fx.code.googleServices.CheckEnvAI;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;

public class FxHtmlEditorAI extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JFXPanel fxPanel = new JFXPanel();
    private WebEngine engine;
    private volatile boolean ready = false;
    private String pendingHtml;

    // Busy-Overlay
    private StackPane root;
    private BorderPane mainPane;
    private StackPane busyOverlay;
    private final Label busyLabel = new Label("KI arbeitet …");
    private Button aiBtn; // wird in buildToolbar gesetzt

    //###################################################################################################################################################
    // public Teil
    //###################################################################################################################################################

    public FxHtmlEditorAI() {
        super(new BorderLayout());
        add(fxPanel, BorderLayout.CENTER);

        Platform.runLater(() -> {
            WebView webView = new WebView();
            engine = webView.getEngine();
            noBrowser(engine);
            engine.loadContent(HTML_TEMPLATE);

            ToolBar tb = buildToolbar();

            mainPane = new BorderPane(webView);
            mainPane.setTop(tb);

            busyOverlay = createBusyOverlay();
            root = new StackPane(mainPane, busyOverlay);
            fxPanel.setScene(new Scene(root));

            engine.getLoadWorker().stateProperty().addListener((_, _, n) -> {
                if (n == Worker.State.SUCCEEDED) {
                    ready = true;
                    if (pendingHtml != null) setHtml(pendingHtml);
                }
            });
        });
    }

    //###################################################################################################################################################

    public void setHtml(String html) {
        Platform.runLater(() -> {
            if (engine == null || !ready) { pendingHtml = html; return; }
            engine.executeScript("window.editor.setHtml(`" + escapeTemplate(html) + "`);");
        });
    }

    public String getHtml() {
        if (engine == null || !ready) return "";
        CompletableFuture<String> cf = new CompletableFuture<>();
        Platform.runLater(() -> cf.complete(String.valueOf(
                engine.executeScript("window.editor.getHtml();"))));
        try { return cf.get(2, TimeUnit.SECONDS); }
        catch (TimeoutException te) { return ""; }
        catch (Exception e) { return ""; }
    }

    //###################################################################################################################################################
    // private Teil
    //###################################################################################################################################################

    private void noBrowser(WebEngine engine) {
        engine.setCreatePopupHandler(_ -> null);
        engine.getLoadWorker().stateProperty().addListener((_,_,n) -> {
            if (n == javafx.concurrent.Worker.State.SUCCEEDED) {
                engine.executeScript("""
                    (function(){
                      document.addEventListener('click', function(e){
                        const a = e.target.closest('a');
                        if (!a) return;
                        e.preventDefault();
                      }, true);
                      document.addEventListener('auxclick', function(e){
                        if (e.button === 1 && e.target.closest('a')) e.preventDefault();
                      }, true);
                      document.addEventListener('contextmenu', function(e){
                        if (e.target.closest('a')) e.preventDefault();
                      });
                    })();
                """);
            }
        });
    }

    private ToolBar buildToolbar() {
    	
    	int sz = 16;
    	Image img = new Image(
    	    getClass().getResource("/org/resources/icons/buttons/gemini.png").toExternalForm(),
    	    sz, sz, true, true
    	);
    	ImageView iv = new ImageView(img);
    	
    	Tooltip tip = new Tooltip("Textpassage markieren und dann KI-Vorschlag anfordern");
    	tip.setShowDelay(Duration.millis(250));
    	tip.setShowDuration(Duration.seconds(12));
    	tip.setHideDelay(Duration.millis(200));

        Button undo = new Button("↶");     undo.setOnAction(_ -> exec("undo"));
        Button redo = new Button("↷");     redo.setOnAction(_ -> exec("redo"));

        ToggleButton bold = new ToggleButton("B"); bold.setStyle("-fx-font-weight:bold;"); bold.setOnAction(_ -> exec("bold"));
        ToggleButton italic = new ToggleButton("I"); italic.setStyle("-fx-font-style:italic;"); italic.setOnAction(_ -> exec("italic"));
        ToggleButton underline = new ToggleButton("U"); underline.setStyle("-fx-underline:true;"); underline.setOnAction(_ -> exec("underline"));
        Button clr = new Button("Format löschen");  clr.setOnAction(_ -> exec("removeFormat"));

        Button ul = new Button("• Liste"); ul.setOnAction(_ -> exec("insertUnorderedList"));
        Button ol = new Button("1. Liste"); ol.setOnAction(_ -> exec("insertOrderedList"));
        Button outdent = new Button("⇤"); outdent.setOnAction(_ -> exec("outdent"));
        Button indent  = new Button("⇥"); indent.setOnAction(_ -> exec("indent"));

        ComboBox<String> block = new ComboBox<>();
        block.getItems().addAll("Absatz", "H1", "H2", "Zitat", "Code");
        block.getSelectionModel().select(0);
        block.setOnAction(_ -> {
            switch (block.getValue()) {
                case "H1"   -> exec("formatBlock", "h1");
                case "H2"   -> exec("formatBlock", "h2");
                case "Zitat"-> exec("formatBlock", "blockquote");
                case "Code" -> exec("formatBlock", "pre");
                default     -> exec("formatBlock", "p");
            }
        });

        Button link = new Button("Link");
        link.setOnAction(_ -> {
            TextInputDialog d = new TextInputDialog("https://");
            d.setHeaderText(null); d.setContentText("URL:"); d.setTitle("Link einfügen");
            d.showAndWait().ifPresent(url -> { if (!url.isBlank()) exec("createLink", url); });
        });
        Button mailto = new Button("Mail");
        mailto.setOnAction(_ -> {
            TextInputDialog m = new TextInputDialog("mailto:");
            m.setHeaderText(null); m.setContentText("URL:"); m.setTitle("Mail-Link einfügen");
            m.showAndWait().ifPresent(url -> { if (!url.isBlank()) exec("createLink", url); });
        });
        Button unlink = new Button("Link entfernen"); unlink.setOnAction(_ -> exec("unlink"));

        Button htmlGet = new Button("HTML anzeigen");
        htmlGet.setOnAction(_ -> {
            String html = String.valueOf(engine.executeScript("window.editor.getHtml();"));
            TextArea ta = new TextArea(html); ta.setEditable(false); ta.setPrefRowCount(30); ta.setPrefColumnCount(100);
            Dialog<Void> dlg = new Dialog<>(); dlg.setTitle("HTML");
            dlg.getDialogPane().setContent(ta); dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE); dlg.show();
        });

        Button aiBtn = new Button("Gemini Textvorschlag", iv);
        aiBtn.setTooltip(tip);
        aiBtn.setStyle("-fx-background-color:#fff2cc; -fx-text-fill:green; -fx-font-weight:bold;");
        aiBtn.setDisable(!CheckEnvAI.getSettingsAI().isGeminiAPI);
        aiBtn.setOnAction(_ -> askGeminiInteractive());

        HBox spacer = new HBox(); spacer.setMinWidth(10);
        return new ToolBar(
                undo, redo, new Separator(),
                bold, italic, underline, clr, new Separator(),
                ul, ol, outdent, indent, new Separator(),
                block, new Separator(),
                link, mailto, unlink, new Separator(),
                htmlGet, new Separator(), aiBtn
        );
    }

    private StackPane createBusyOverlay() {
        ProgressIndicator pi = new ProgressIndicator();
        VBox box = new VBox(10, pi, busyLabel);
        box.setAlignment(Pos.CENTER);
        StackPane overlay = new StackPane(box);
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.25);");
        overlay.setVisible(false);
        overlay.setMouseTransparent(false); // blockiert Eingaben
        return overlay;
    }

    private void showBusy(String msg) {
        Platform.runLater(() -> {
            busyLabel.setText(msg);
            busyOverlay.setVisible(true);
            if (aiBtn != null) aiBtn.setDisable(true);
        });
    }

    private void hideBusy() {
        Platform.runLater(() -> {
            busyOverlay.setVisible(false);
            if (aiBtn != null) aiBtn.setDisable(false);
        });
    }

    private void exec(String command) {
        engine.executeScript("window.editor.cmd('" + escapeJs(command) + "');");
    }
    private void exec(String command, String value) {
        engine.executeScript("window.editor.cmd('" + escapeJs(command) + "', '" + escapeJs(value) + "');");
    }

    private static String escapeJs(String s) { return s.replace("\\","\\\\").replace("'","\\'"); }
    private static String escapeTemplate(String s) { return s.replace("\\","\\\\").replace("`","\\`").replace("${","\\${"); }

    private static final String HTML_TEMPLATE = """
        <!doctype html>
        <html lang="de">
        <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1">
          <title>Editor</title>
          <style>
            html, body { height:100%; margin:0; padding:0; }
            body { font-family: Arial, sans-serif; font-size:10pt; line-height:1.2; }
            #editable { box-sizing: border-box; min-height:100vh; padding:16px; outline:none; line-height:1.5; }
            #editable:empty:before { content: attr(data-placeholder); color:#888; }
            #editable p { margin: 0; }
            #editable p + p { margin-top: 0.01em; }
            blockquote { border-left:4px solid #ccc; margin:8px 0; padding:8px 12px; color:#ba55d3; font-weight:700; }
            pre { background:#f6f8fa; padding:12px; border-radius:6px; overflow:auto; }
            h1,h2 { margin:1.2em 0 0.5em; }
          </style>
        </head>
        <body>
          <div id="editable" contenteditable="true" spellcheck="false" data-placeholder="Hier schreiben...">
            <p><b>Willkommen.</b> Dies ist ein einfacher HTML-Editor.</p>
          </div>

          <script>
            (function () {
              const ed = document.getElementById('editable');
              try { document.execCommand('defaultParagraphSeparator', false, 'div'); } catch (e) {}

              function cmd(name, value = null) {
                ed.focus();
                document.execCommand(name, false, value);
                ed.dispatchEvent(new Event('input', { bubbles: true }));
              }
              function getHtml() { return ed.innerHTML; }

              function isEmptyPara(el) {
                if (!el || el.tagName !== 'P') return false;
                const h = el.innerHTML
                  .replace(/<br\\s*\\/?>/gi, '')
                  .replace(/&nbsp;/gi, ' ')
                  .trim();
                return h === '';
              }
              function setHtml(html) {
                const tmp = document.createElement('div');
                tmp.innerHTML = (html || '').trim();
                while (isEmptyPara(tmp.firstElementChild)) tmp.removeChild(tmp.firstElementChild);
                ed.innerHTML = tmp.innerHTML;
                while (isEmptyPara(ed.firstElementChild)) ed.removeChild(ed.firstElementChild);
                placeCaretEnd(ed);
              }
              function placeCaretEnd(el) {
                el.focus();
                const r = document.createRange();
                r.selectNodeContents(el);
                r.collapse(false);
                const s = window.getSelection();
                s.removeAllRanges();
                s.addRange(r);
              }
              function insertText(t){ ed.focus(); document.execCommand('insertText', false, t||''); }

              ed.addEventListener('paste', function (e) {
                e.preventDefault();
                const text = (e.clipboardData || window.clipboardData).getData('text/plain');
                document.execCommand('insertText', false, text);
              });
              ed.addEventListener('keydown', function (e) {
                if (e.ctrlKey && !e.shiftKey && !e.altKey) {
                  if (e.key === 'b' || e.key === 'B') { e.preventDefault(); cmd('bold'); }
                  if (e.key === 'i' || e.key === 'I') { e.preventDefault(); cmd('italic'); }
                  if (e.key === 'u' || e.key === 'U') { e.preventDefault(); cmd('underline'); }
                }
              });

              window.editor = { cmd, getHtml, setHtml, insertText };
            })();
          </script>
        </body>
        </html>
        """;

    //###################################################################################################################################################
    // KI Funktionalität
    
    private String getSelection() {
        return String.valueOf(engine.executeScript("window.getSelection().toString()"));
    }

    @SuppressWarnings("unused")
	private void askGemini(String instruction) {
        if (engine == null || !ready) return;
        showBusy("KI formuliert …");
        String selection = String.valueOf(engine.executeScript("window.getSelection().toString()"));

        CompletableFuture
            .supplyAsync(() -> callGeminiREST(CheckEnvAI.getGem(), "gemini-2.5-flash", instruction, selection))
            .whenComplete((text, ex) -> Platform.runLater(() -> {
                hideBusy();
                if (ex != null) {
                    // optional: Meldung
                    return;
                }
                if (text == null || text.isBlank()) return;
                engine.executeScript("window.editor.insertText(" + toJsonString(text) + ");");
            }));
    }
    
    private void askGeminiInteractive() {
        Platform.runLater(() -> {
            String sel = getSelection();

            TextField tfInstr = new TextField("Formuliere eine präzise, formelle Angebotsbeschreibung in Deutsch.");
            TextArea taCtx = new TextArea(sel);
            taCtx.setPrefRowCount(8);

            CheckBox cbUseSel = new CheckBox("Auswahl als Kontext verwenden");
            cbUseSel.setSelected(!sel.isBlank());
            cbUseSel.selectedProperty().addListener((_,_,nv) -> { if (nv) taCtx.setText(getSelection()); });

            Dialog<ButtonType> dlg = new Dialog<>();
            dlg.setTitle("Gemini Textvorschlag");
            dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dlg.getDialogPane().setContent(new javafx.scene.layout.VBox(8,
                new javafx.scene.control.Label("Anweisung:"), tfInstr,
                cbUseSel,
                new javafx.scene.control.Label("Kontext (mehrzeilig):"), taCtx
            ));
            dlg.showAndWait().filter(bt -> bt == ButtonType.OK).ifPresent(_ -> {
                String instruction = tfInstr.getText().trim();
                String context = taCtx.getText();
                showBusy("KI formuliert …");
                CompletableFuture
                    .supplyAsync(() -> callGeminiREST(CheckEnvAI.getGem(), "gemini-2.5-flash", instruction, context))
                    .whenComplete((text, ex) -> Platform.runLater(() -> {
                        hideBusy();
                        if (ex == null && text != null && !text.isBlank()) {
                            engine.executeScript("window.editor.insertText(" + toJsonString(text) + ");");
                        }
                    }));
            });
        });
    }

    private static String callGeminiREST(String apiKey, String model, String instruction, String context) {
        if (apiKey == null) return "[Fehler: GEMINI_API_KEY fehlt]";
        String prompt = instruction + "\n\nKontext:\n" + (context == null ? "" : context);

        String body = """
        {
          "systemInstruction": { "role": "system", "parts": [ { "text": "Du schreibst kurze, präzise Angebotsbeschreibungen." } ] },
          "contents": [ { "role": "user", "parts": [ { "text": %s } ] } ],
          "generationConfig": { "temperature": 0.2, "maxOutputTokens": 4096, "responseMimeType": "text/plain" }
        }
        """.formatted(toJsonString(prompt));

        try {
            HttpRequest req = HttpRequest.newBuilder(
                    URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent"))
            		.timeout(java.time.Duration.ofSeconds(60))
                .header("x-goog-api-key", apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
            HttpResponse<String> res = HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() / 100 != 2) return "[Gemini-HTTP-" + res.statusCode() + "] " + res.body();
            return extractGeminiText(res.body());
        } catch (Exception e) {
            return "[Gemini-Fehler: " + e.getClass().getSimpleName() + "]";
        }
    }

    private static String extractGeminiText(String json) {
        int ci = json.indexOf("\"candidates\"");
        if (ci < 0) return "";
        int pi = json.indexOf("\"parts\"", ci);
        if (pi < 0) return "";
        int ti = json.indexOf("\"text\"", pi);
        if (ti < 0) return "";
        int q1 = json.indexOf('"', json.indexOf(':', ti) + 1);
        StringBuilder sb = new StringBuilder();
        for (int i = q1 + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '"' && json.charAt(i - 1) != '\\') break;
            sb.append(c);
        }
        return sb.toString().replace("\\n","\n").replace("\\t","\t").replace("\\\"","\"").trim();
    }

    private static String toJsonString(String s) {
        if (s == null) s = "";
        return "\"" + s.replace("\\", "\\\\")
                       .replace("\"", "\\\"")
                       .replace("\n", "\\n")
                       .replace("\r", "") + "\"";
    }
}
