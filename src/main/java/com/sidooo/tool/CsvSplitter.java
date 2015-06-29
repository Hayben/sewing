package com.sidooo.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;

import org.apache.commons.io.FilenameUtils;

public class CsvSplitter {

	private final int SPLIT_COUNT = 40000;
	
	private long lineCount = 0;
	
	public long getLineCount() {
		return lineCount;
	}

	private int generateFile(BufferedReader reader, File newFile)
			throws IOException {

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile)));
		int count = 0;
		String line = null;
		while ( count < SPLIT_COUNT && (line = reader.readLine()) != null) {
			count++;
			lineCount ++;
			writer.write(line + "\n");
		}
		writer.close();
		
		return count;
	}

	public long split(File file, File output) throws Exception {

		if (file.isDirectory()) {
			File[] children = file.listFiles();
			for(File child : children) {
				File outPath = new File(output.toString() + "/" + child.getName().replace(".", "_"));
				outPath.mkdir();
				split(child, outPath);
			}
		} else {
			BufferedReader reader = new BufferedReader(
										new InputStreamReader(
											new FileInputStream(file), "utf-8"));
			for(int i=0; i<50000; i++) {
				String fileName = file.getName();
				String baseName = FilenameUtils.getBaseName(fileName);
				String ext = FilenameUtils.getExtension(fileName);
				
				String newFileName = null;
				if (ext == null) {
					newFileName = baseName + "_" + i;
				} else {
					newFileName = baseName + "_" + i + "." + ext;
				}
				
				File newFile = new File(output.getPath() + "/" + newFileName);
				int readCount = generateFile(reader, newFile);
				System.out.println("Generate " + newFileName + ", Count: " + readCount);
				if (readCount < SPLIT_COUNT) {
					break;
				}
			}

			reader.close();
		}
		
		return 0;
	}

	public static void main(String[] args) throws Exception {

		if (args.length != 1) {
			System.out.println("CsvSplitter <file>");
			return;
		}

		File file = new File(args[0]);
		if (!file.exists()) {
			System.out.println(file.getPath() + " not exist.");
			return;
		}
		
		File outFile = null;
		if (file.isDirectory()) {
			outFile = new File(file.getPath() + "_splitted");
		} else {
			outFile = new File(file.getPath() + "_splitted"); 
		}
		outFile.mkdir();
		
		CsvSplitter splitter = new CsvSplitter();
		try {
			splitter.split(file, outFile);
			System.out.println("Split Finished. Total Write Line Count : " + splitter.getLineCount());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
