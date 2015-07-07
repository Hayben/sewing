package com.sidooo.extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.FilenameUtils;

public class CsvExtractor extends ContentExtractor{
	
	BufferedReader reader = null;
	
	@Override
	public void setInput(InputStream stream, String charset) throws Exception {
		setTitle(FilenameUtils.getBaseName(path));
		reader = new BufferedReader(new InputStreamReader(stream));
		//headerLine = reader.readLine();
	}
	
	@Override
	public String extract() {
		try {
			return reader.readLine();
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public void close() {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
