package org.andy.fx.code.qr;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class ZxingQR {

	public static void makeQR(String sSchema, String sKI, String sIBAN, String sBIC, String sWert, String sRef) throws WriterException, IOException {
		//QR Schema mit Realdaten befüllen
		String qrCodeText = sSchema.replace("{BIC}", sBIC);
		qrCodeText =qrCodeText.replace("{KI}", sKI);
		qrCodeText =qrCodeText.replace("{IBAN}", sIBAN);
		qrCodeText =qrCodeText.replace("{SUM}", sWert);
		qrCodeText =qrCodeText.replace("{RENR}", sRef);
		qrCodeText =qrCodeText.replace("/", "\r\n");

		int size = 150;
		String fileType = "png";
		File qrFile = new File(System.getProperty("user.dir") + "\\qr.png");

		createQRImage(qrFile, qrCodeText, size, fileType);
	}

	public static void makeLinkQR(String sLink) throws WriterException, IOException {
		String qrCodeText = sLink;

		int size = 150;
		String fileType = "png";
		File qrFile = new File(System.getProperty("user.dir") + "\\link.png");

		createQRImage(qrFile, qrCodeText, size, fileType);
	}

	static void createQRImage(File qrFile, String qrCodeText, int size, String fileType) throws WriterException, IOException {
		// Create the HashMap for the QR-Code that encodes the given String
		Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);

		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);

		// Make the BufferedImage to hold the QRCode
		int matrixWidth = byteMatrix.getWidth();
		BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
		image.createGraphics();

		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, matrixWidth, matrixWidth);

		// Paint and save the image using the ByteMatrix
		graphics.setColor(Color.BLACK);
		for (int i = 0; i < matrixWidth; i++) {
			for (int j = 0; j < matrixWidth; j++) {
				if (byteMatrix.get(i, j)) {
					graphics.fillRect(i, j, 1, 1);
				}
			}
		}

		ImageIO.write(image, fileType, qrFile);
	}

}
