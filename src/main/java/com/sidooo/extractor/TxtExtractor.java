package com.sidooo.extractor;

import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.txt.TXTParser;
import org.apache.tika.sax.BodyContentHandler;

public class TxtExtractor extends ContentExtractor {

	@Override
	public void extract(InputStream input) {
		clearItems();
		try {
			BodyContentHandler handler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			ParseContext pcontext = new ParseContext();
	
			// parsing the document using PDF parser
			TXTParser parser = new TXTParser();
			parser.parse(input, handler, metadata, pcontext);
	
			setTitle(FilenameUtils.getBaseName(path));
			addItem(handler.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			finish();
		}
		
	}

}
