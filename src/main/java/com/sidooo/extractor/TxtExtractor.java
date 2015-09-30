package com.sidooo.extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.FilenameUtils;

public class TxtExtractor extends ContentExtractor {

//	private void extractSmallTxt(InputStream input) {
//		try {
//			BodyContentHandler handler = new BodyContentHandler(this.MAX_SIZE);
//			Metadata metadata = new Metadata();
//			ParseContext pcontext = new ParseContext();
//	
//			// parsing the document using PDF parser
//			TXTParser parser = new TXTParser();
//			parser.parse(input, handler, metadata, pcontext);
//	
//			setTitle(FilenameUtils.getBaseName(path));
//			addItem(handler.toString());
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			finish();
//		}
//	}
//	

	private BufferedReader reader = null;
	private boolean modeLarge = false;
	private final int BUFFER_SIZE = 16*1024;
	
	@Override
	public void setInput(InputStream input, String charset) throws Exception {
		reader = new BufferedReader(new InputStreamReader(input));
		setTitle(FilenameUtils.getBaseName(path));
		if (input.available() > BUFFER_SIZE) {
			modeLarge = true;
		} 
	}

	@Override
	public String extract() {
		try {
			if (modeLarge) {
				return reader.readLine();
			} else {
				char[] buffer = new char[BUFFER_SIZE];
				int size = reader.read(buffer);
				if (size > 0) {
					return String.valueOf(buffer);
				} else {
					return null;
				}
			}
		} catch(Exception e) {
			return null;
		}
	}

	@Override
	public void close() {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
			}
		}
		
		modeLarge = false;
		
	}

}
