package com.sidooo.extractor;

import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.OfficeParser;
import org.apache.tika.sax.BodyContentHandler;

public class DocExtractor extends ContentExtractor{

	@Override
	public void extract(InputStream stream) {
		clearItems();
		try {
			BodyContentHandler handler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			ParseContext pcontext = new ParseContext();
	
			// parsing the document using PDF parser
			OfficeParser parser = new OfficeParser();
			parser.parse(stream, handler, metadata, pcontext);
	
			setTitle(FilenameUtils.getBaseName(path));
			addItem(handler.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			finish();
		}
		
	}
	
}
