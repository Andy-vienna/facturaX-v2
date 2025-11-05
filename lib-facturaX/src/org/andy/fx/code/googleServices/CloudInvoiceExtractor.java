package org.andy.fx.code.googleServices;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.documentai.v1.*;
import com.google.protobuf.ByteString;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.andy.fx.code.googleServices.InterfaceBuilder.DocAiConfig;
import org.andy.fx.code.googleServices.InterfaceBuilder.InvoiceExtractionResult;
import org.andy.fx.code.googleServices.InterfaceBuilder.InvoiceExtractor;

public class CloudInvoiceExtractor implements InvoiceExtractor {
	
	private final DocAiConfig cfg;

	// ###################################################################################################################################################
	// public Teil
	// ###################################################################################################################################################

	public CloudInvoiceExtractor(DocAiConfig cfg) {
		this.cfg = cfg;
	}

	@Override
	public InvoiceExtractionResult extract(Path pdf) throws Exception {
		String endpoint = cfg.location() + "-documentai.googleapis.com:443";
		DocumentProcessorServiceSettings s = DocumentProcessorServiceSettings.newBuilder().setEndpoint(endpoint)
				.build();
		try (DocumentProcessorServiceClient c = DocumentProcessorServiceClient.create(s)) {
			String name = "projects/%s/locations/%s/processors/%s".formatted(cfg.projectId(), cfg.location(),
					cfg.processorId());
			byte[] content = Files.readAllBytes(pdf);
			ProcessRequest req = ProcessRequest.newBuilder().setName(name).setRawDocument(RawDocument.newBuilder()
					.setContent(ByteString.copyFrom(content)).setMimeType("application/pdf").build()).build();
			ProcessResponse resp = c.processDocument(req);
			Document d = resp.getDocument();

			var header = new LinkedHashMap<String, String>();
			var row = new java.util.LinkedHashMap<String, String>();
			var items = new ArrayList<java.util.Map<String, String>>();
			
			for (Document.Entity e : d.getEntitiesList()) {
				switch (e.getType()) {
				case "line_item" -> {
					for (Document.Entity p : e.getPropertiesList()) {
						row.put(p.getType(), val(p));
					}
					items.add(row);
				}
				default -> header.put(e.getType(), norm(e));
				}
			}

			return new InvoiceExtractionResult(header, items, d.getText());
		} catch (ApiException ex) {
			throw ex; // Orchestrator fängt ab und fällt zurück
		}
	}

	// ###################################################################################################################################################
	// private Teil
	// ###################################################################################################################################################

	private static String val(Document.Entity e) {
		String t = e.getTextAnchor().getContent();
		return (t == null || t.isBlank()) ? e.getMentionText() : t;
	}

	private static String norm(Document.Entity e) {
		return e.hasNormalizedValue() && !e.getNormalizedValue().getText().isBlank() ? e.getNormalizedValue().getText()
				: val(e);
	}
}
