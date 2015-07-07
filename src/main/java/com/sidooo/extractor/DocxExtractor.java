package com.sidooo.extractor;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.sax.BodyContentHandler;

public class DocxExtractor extends ContentExtractor{

	private InputStream stream = null;
	private String content = null;
	
	@Override
	public void setInput(InputStream input, String charset) throws Exception {
		this.stream = input;
		
		BodyContentHandler handler = new BodyContentHandler(MAX_SIZE);
		Metadata metadata = new Metadata();
		ParseContext pcontext = new ParseContext();

		// parsing the document using PDF parser
		OOXMLParser parser = new OOXMLParser();
		parser.parse(stream, handler, metadata, pcontext);

		setTitle(FilenameUtils.getBaseName(path));
		content = handler.toString();
	}

	@Override
	public String extract() {
		try {
			return content;
		} finally {
			content = null;
		}
	}

	@Override
	public void close() {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
		
	}
}
