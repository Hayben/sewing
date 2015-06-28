package com.sidooo.extractor;

import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.sax.BodyContentHandler;

public class XlsxExtractor extends ContentExtractor{
	
	public XlsxExtractor(String path) {
		super(path);
	}
	
	private void parseSheets(String content) {
		
		String[] lines = content.split("\n");
		String sheetName = null;
		String meta = null;
		for(String line : lines) {
			if (line.split("\t").length == 1) {
				sheetName = line;
				meta = null;
				continue;
			}
			
			if (meta == null) {
				meta = line;
				continue;
			}
			
			addItem(meta + line);
		}
	}

	@Override
	public void extract(InputStream stream) {
		clearItems();
		setTitle(FilenameUtils.getBaseName(path));
		try {
			BodyContentHandler handler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			ParseContext pcontext = new ParseContext();
	
			// OOXml parser
			OOXMLParser parser = new OOXMLParser();
			parser.parse(stream, handler, metadata, pcontext);
			
			parseSheets(handler.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			finish();
		}
	}
}
