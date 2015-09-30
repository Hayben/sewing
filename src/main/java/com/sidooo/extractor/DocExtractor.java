package com.sidooo.extractor;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.OfficeParser;
import org.apache.tika.sax.BodyContentHandler;

public class DocExtractor extends ContentExtractor{

	private InputStream stream  = null;
	private String content = null;
	
	OfficeParser parser = new OfficeParser();
	ParseContext pcontext = new ParseContext();
	
	@Override
	public void setInput(InputStream stream, String charset) throws Exception {
		this.stream = stream;
		setTitle(FilenameUtils.getBaseName(path));
		
		BodyContentHandler handler = new BodyContentHandler(MAX_SIZE);
		Metadata metadata = new Metadata();
		

		// parsing the document using PDF parser

		parser.parse(stream, handler, metadata, pcontext);

		content =  handler.toString();
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
		
		if (content != null) {
			content = null;
		}
		
	}
	
}
