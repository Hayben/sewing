package com.sidooo.extractor;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;


public class PdfExtractor extends ContentExtractor {
	
	
	private InputStream stream = null;
	private String content = null;
	
	@Override
	public void setInput(InputStream input, String charset) throws Exception {
		stream = input;
		setTitle(FilenameUtils.getBaseName(path));
		BodyContentHandler handler = new BodyContentHandler(MAX_SIZE);
		Metadata metadata = new Metadata();
		ParseContext pcontext = new ParseContext();

		// parsing the document using PDF parser
		PDFParser pdfparser = new PDFParser();
		pdfparser.parse(stream, handler, metadata, pcontext);

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
