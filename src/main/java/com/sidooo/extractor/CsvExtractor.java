package com.sidooo.extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.FilenameUtils;

public class CsvExtractor extends ContentExtractor{
	
	private final int LINE_LIMIT = 1000;
	
	private String headerLine;
	
	@Override
	public void extract(InputStream stream) {
		clearItems();
		setTitle(FilenameUtils.getBaseName(path));
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(stream));
			//headerLine = reader.readLine();

			String line = null;
			while ((line = reader.readLine()) != null) {
				addItem(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
