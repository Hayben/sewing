package com.sidooo.extractor;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.OfficeParser;
import org.apache.tika.sax.BodyContentHandler;

public class XlsExtractor extends ContentExtractor{

	
//	private void parseSheets(String content) {
//		
// 	 	String[] lines = content.split("\n");
//		String sheetName = null;
//		String meta = null;
//		for(String line : lines) {
//			
//			if (line.length() <= 1) {
//				continue;
//			}
//			
//			if (line.charAt(0) != '\t') {
//				sheetName = line;
//				meta = null;
//				continue;
//			}
//			
//			if (meta == null) {
//				meta = line;
//				continue;
//			}
//			
////			int fieldCount = meta.split("\t").length;
////			int count = line.split("\t").length;
////			if (fieldCount != count) {
//			addItem(line);
////			}
//			
//		}
//	}
	
	private InputStream stream = null;
	private String[] lines = null;
	private int offset = 0;
	
	OfficeParser parser = new OfficeParser();

	@Override
	public void setInput(InputStream input, String charset) throws Exception {
		stream = input;
		setTitle(FilenameUtils.getBaseName(path));
		
		BodyContentHandler handler = new BodyContentHandler(MAX_SIZE);
		Metadata metadata = new Metadata();
		ParseContext pcontext = new ParseContext();

		// parsing the document using PDF parser

		parser.parse(stream, handler, metadata, pcontext);
		
		lines = handler.toString().split("\n");
	}

	@Override
	public String extract() {
		if (lines == null || offset >= lines.length || offset < 0) {
			return null;
		}
		
		String line = lines[offset];
		offset ++;
		return line;
	}

	@Override
	public void close() {
		
		if (lines != null) {
			lines = null;
		}
		
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
		
		offset = 0;

	}

}
