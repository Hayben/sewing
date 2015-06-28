package com.sidooo.extractor;

import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.OfficeParser;
import org.apache.tika.sax.BodyContentHandler;

public class XlsExtractor extends ContentExtractor{

	public XlsExtractor(String path) {
		super(path);
	}
	
	private void parseSheets(String content) {
		
 	 	String[] lines = content.split("\n");
		String sheetName = null;
		String meta = null;
		for(String line : lines) {
			
			if (line.length() <= 1) {
				continue;
			}
			
			if (line.charAt(0) != '\t') {
				sheetName = line;
				meta = null;
				continue;
			}
			
			if (meta == null) {
				meta = line;
				continue;
			}
			
//			int fieldCount = meta.split("\t").length;
//			int count = line.split("\t").length;
//			if (fieldCount != count) {
			addItem(line);
//			}
			
		}
	}

	@Override
	public void extract(InputStream stream) {
		clearItems();
		setTitle(FilenameUtils.getBaseName(path));
		try {
			BodyContentHandler handler = new BodyContentHandler(stream.available());
			Metadata metadata = new Metadata();
			ParseContext pcontext = new ParseContext();
	
			// parsing the document using PDF parser
			OfficeParser parser = new OfficeParser();
			parser.parse(stream, handler, metadata, pcontext);
			
			parseSheets(handler.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			finish();
		}
	}

}
