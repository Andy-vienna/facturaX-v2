package org.andy.fx.code.googleServices;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class InterfaceBuilder {

	// ###################################################################################################################################################
	// Record bilden
	// ###################################################################################################################################################

	public record DocAiConfig(String projectId, String location, String processorId) {
	}

	public record InvoiceExtractionResult(Map<String, String> header, // invoice_id, dates, totals…
			List<Map<String, String>> lineItems, // description, quantity, unit_price, amount
			String rawText) {
	}

	// ###################################################################################################################################################
	// Interface bilden
	// ###################################################################################################################################################

	public interface InvoiceExtractor {
		InvoiceExtractionResult extract(Path pdf) throws Exception;
	}

}
