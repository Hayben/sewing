package com.sidooo.extractor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.FilenameUtils;

public class CsvExtractor extends ContentExtractor{
	
	private final int LINE_LIMIT = 1000;
	
	private String headerLine;
	BufferedReader reader = null;
	
	public CsvExtractor(String path, InputStream stream) {
		super(path, stream);
	}
	
	@Override
	public void extract() {
		clearItems();
		setTitle(FilenameUtils.getBaseName(path));
		try {
			if (reader == null) {
				reader = new BufferedReader(new InputStreamReader(stream));
				headerLine = reader.readLine();
			}
			
			

			int lineCount = 0;
			while (lineCount <= LINE_LIMIT) {
				
				String line = reader.readLine();
				if (line == null) {
					
					finish();
					
					try {
						reader.close();
						reader = null;
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
				
				lineCount ++ ;
				
				addContent(line);
			}
		} catch (Exception e) {
			finish();
		}

	}

}
