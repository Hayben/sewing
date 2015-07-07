package com.sidooo.extractor;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.sax.BodyContentHandler;

public class XlsxExtractor extends ContentExtractor{
//	
//	private void parseSheets(String content) {
//		
//		String[] lines = content.split("\n");
//		String sheetName = null;
//		String meta = null;
//		for(String line : lines) {
//			if (line.split("\t").length == 1) {
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
//			addItem(meta + line);
//		}
//	}

	private InputStream stream = null;
	private String[] lines = null;
	private int offset = 0;

	@Override
	public void setInput(InputStream stream, String charset) throws Exception {
		setTitle(FilenameUtils.getBaseName(path));
		
		BodyContentHandler handler = new BodyContentHandler(MAX_SIZE);
		Metadata metadata = new Metadata();
		ParseContext pcontext = new ParseContext();

		// OOXml parser
		OOXMLParser parser = new OOXMLParser();
		parser.parse(stream, handler, metadata, pcontext);
		
		lines = handler.toString().split("\n");
	}

	@Override
	public String extract() {
		if (lines == null || offset < 0 || offset >= lines.length) {
			return null;
		}
		
		String line =  lines[offset];
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
		
	}
}
