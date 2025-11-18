package org.andy.code.misc;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileSelect {

	private static final String NOT_SELECTED = "---";
	private static final String[] FILE_EXTENSIONS = {"css", "csv", "html", "jpg", "msg", "pdf", "png", "rar", "xlsm", "xlsx", "xml", "zip"};

	// ###################################################################################################################################################
	// public Teil
	// ###################################################################################################################################################

	public static String choosePath(String path) {
		return selectPath(path);
	}

	public static String chooseFile(String path) {
		return selectFile(path);
	}

	// ###################################################################################################################################################
	// private Teil
	// ###################################################################################################################################################

	private static String selectPath(String path) {
		JFileChooser pathChooser = new JFileChooser();
		pathChooser.setDialogTitle("Verzeichnis für Download auswählen");
		pathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		pathChooser.setCurrentDirectory(new File(path));

		return (pathChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) ?
				pathChooser.getSelectedFile().getAbsolutePath() + File.separator : NOT_SELECTED;
	}

	private static String selectFile(String path) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Datei auswählen");
		fileChooser.setFileFilter(new FileNameExtensionFilter("Dateiauswahl", FILE_EXTENSIONS));
		fileChooser.setCurrentDirectory(new File(path));

		return (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) ?
				fileChooser.getSelectedFile().getAbsolutePath() : NOT_SELECTED;
	}

	// ###################################################################################################################################################
	// Getter und Setter
	// ###################################################################################################################################################

	public static String getNotSelected() {
		return NOT_SELECTED;
	}

}
