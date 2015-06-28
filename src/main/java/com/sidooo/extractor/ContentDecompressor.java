package com.sidooo.extractor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

public class ContentDecompressor {

	private final int BUFFER_SIZE = 16 * 1024;
	
	private void dearchiveFile(File destFile, TarArchiveInputStream input) throws Exception {

		BufferedOutputStream output = null;
		try {
			output = new BufferedOutputStream(
					new FileOutputStream(destFile));
			byte[] buffer = new byte[BUFFER_SIZE];
			int size = 0;
			while((size = input.read(buffer)) != -1) {
				output.write(buffer, 0, size);
			}
		} finally {
			if (output != null) {
				output.close();
			}
		}
	}

	public void decompressGzipArchive(String gzFileName) throws Exception {

		File gzFile = new File(gzFileName.toString());

		String tarFileName = FilenameUtils.getBaseName(gzFileName);
		File tarFile = new File(gzFile.getParent() + '/' + tarFileName);

		GZIPInputStream gzInput = null;
		FileOutputStream tarOutput = null;
		try {
			gzInput = new GZIPInputStream(new FileInputStream(gzFile));

			tarOutput = new FileOutputStream(tarFile);

			byte[] buf = new byte[BUFFER_SIZE];
			int len = 0;
			while ((len = gzInput.read(buf)) > 0) {
				tarOutput.write(buf, 0, len);
			}

		} finally {
			if (gzInput != null) {
				try {
					gzInput.close();
				} catch (IOException e) {
				}
			}
			if (tarOutput != null) {
				try {
					tarOutput.close();
				} catch (IOException e) {
				}
			}
		}

		if (!tarFile.exists()) {
			throw new IOException("tar file not found.");
		}

		TarArchiveInputStream tarInput = null;
		try {
			tarInput = new TarArchiveInputStream(new FileInputStream(tarFile));
			TarArchiveEntry entry = null;
			while ((entry = tarInput.getNextTarEntry()) != null) {
				// String fileName = gzFile.getName().substring(0,
				// gzFile.getName().lastIndexOf('.'));
				// fileName = fileName.substring(0, fileName.lastIndexOf('.'));
				File outputFile = new File(gzFile.getParent() + "/"
						+ entry.getName());
//				if (!outputDir.getParentFile().exists()) {
//					outputDir.getParentFile().mkdirs();
//				}
				// if the entry in the tar is a directory, it needs to be
				// created, only files can be extracted
				if (entry.isDirectory()) {
					outputFile.mkdirs();
				} else {
					dearchiveFile(outputFile, tarInput);
				}
			}
		} finally {
			if (tarInput != null) {
				try {
					tarInput.close();
				} catch (IOException e) {
				}
			}

			if (tarFile.exists()) {
				tarFile.delete();
			}
		}
	}
}
