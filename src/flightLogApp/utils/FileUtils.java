package flightLogApp.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import flightLogApp.controller.Log;

public class FileUtils {

	static public void extractFolder(String zipFile) throws ZipException, IOException {
		System.out.println(zipFile);
		int BUFFER = 2048;
		File file = new File(zipFile);

		ZipFile zip = new ZipFile(file);
		String newPath = ".";//= zipFile.substring(0, zipFile.length() - 4);

		//new File(newPath).mkdir();
		Enumeration<?> zipFileEntries = zip.entries();

		// Process each entry
		while (zipFileEntries.hasMoreElements()) {
			// grab a zip file entry
			ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
			String currentEntry = entry.getName();
			File destFile = new File(newPath, currentEntry);
			// destFile = new File(newPath, destFile.getName());
			File destinationParent = destFile.getParentFile();

			// create the parent directory structure if needed
			destinationParent.mkdirs();

			if (!entry.isDirectory()) {
				BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
				int currentByte;
				// establish buffer for writing file
				byte data[] = new byte[BUFFER];

				// write the current file to disk
				FileOutputStream fos = new FileOutputStream(destFile);
				BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

				// read and write until last byte is encountered
				while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, currentByte);
				}
				dest.flush();
				dest.close();
				is.close();
			}

			if (currentEntry.endsWith(".zip")) {
				// found a zip file, try to open
				extractFolder(destFile.getAbsolutePath());
			}
		}
		zip.close();
	}
	
	static public boolean loadFile(String file, String url, Log log) {
		try {
			// String fileName = "file.txt"; // The file that will be saved on
			// your
			// computer
			URL link = new URL(url); // The file that you want
										// to download

			// Code to download
			InputStream in = new BufferedInputStream(link.openStream());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int n = 0;
			while (-1 != (n = in.read(buf))) {
				out.write(buf, 0, n);
			}
			out.close();
			in.close();
			byte[] response = out.toByteArray();

			FileOutputStream fos = new FileOutputStream(file);
			fos.write(response);
			fos.close();
			// End download code

			System.out.println("ECHO:file loaded");
			return true;
		} catch (Exception e) {
			log.error("could not download file!", e);
		}
		return false;
	}
}
