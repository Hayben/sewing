package com.sidooo.extractor;

import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.sax.BodyContentHandler;

public class DocxExtractor extends ContentExtractor{


	public DocxExtractor(String path, InputStream stream) {
		super(path, stream);
	}

	@Override
	public void extract() {
		clearItems();
		try {
			BodyContentHandler handler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			ParseContext pcontext = new ParseContext();
	
			// parsing the document using PDF parser
			OOXMLParser parser = new OOXMLParser();
			parser.parse(stream, handler, metadata, pcontext);
	
			setTitle(FilenameUtils.getBaseName(path));
			addContent(handler.toString());
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			finish();
		}
	}
}
