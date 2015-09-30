package com.sidooo.extractor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;


public class HtmlExtractor extends ContentExtractor {
	
	private BufferedInputStream stream = null;
	
	private String content = null;
	
	HtmlParser parser = new HtmlParser();
	
	ParseContext context = new ParseContext();
	
	@Override
	public void setInput(InputStream input, String charset) throws Exception {
		stream = new BufferedInputStream(input);
		stream.mark(stream.available()+1);
		BodyContentHandler handler = new BodyContentHandler(MAX_SIZE);
		Metadata metadata = new Metadata();
		
		
		parser.parse(stream, handler, metadata, context);
		
		setTitle(metadata.get("title"));
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
		
		if (content != null) {
			content = null;
		}
		
	}

}
