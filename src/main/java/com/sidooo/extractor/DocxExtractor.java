package com.sidooo.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ContentHandlerDecorator;




//class ChunkHandler extends ContentHandlerDecorator {
//	final int MAXIMUM_TEXT_CHUNK_SIZE = 32 * 1024 * 1024;
//	private StringBuilder builder = new StringBuilder();
//	
//	   @Override
//	   public void characters(char[] ch, int start, int length) {
//	      builder.append(ch, 0, length);
//	      builder.append("\n");
//	   }
//	   
//	   @Override
//	   public String toString() {
//		   return builder.toString();
//	   }
//}


public class DocxExtractor extends ContentExtractor{

	private InputStream stream = null;
	private String content = null;
	
	
	OOXMLParser parser = new OOXMLParser();
	
	ParseContext pcontext = new ParseContext();
	
	Metadata metadata = new Metadata();
	
	@Override
	public void setInput(InputStream input, String charset) throws Exception {
		this.stream = input;
		
		
		BodyContentHandler handler = new BodyContentHandler(MAX_SIZE);
		//ChunkHandler handler = new ChunkHandler();
		// parsing the document using PDF parser
		
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
		
		if (content != null) {
			content = null;
		}
	}
}
