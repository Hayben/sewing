package com.sidooo.extractor;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.Link;
import org.apache.tika.sax.LinkContentHandler;

public class HtmlExtractor extends ContentExtractor {
	
	public HtmlExtractor(String path, InputStream stream) {
		super(path, new BufferedInputStream(stream));
	}

	@Override
	public void extract() {
		clearItems();
		try {
			stream.mark(stream.available()+1);
			BodyContentHandler handler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			HtmlParser parser = new HtmlParser();
			ParseContext context = new ParseContext();
			parser.parse(stream, handler, metadata, context);
			
			setTitle(metadata.get("title"));
			addContent(handler.toString());
			
			
			//InputStream stream = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));
			stream.reset();
			metadata = new Metadata();
			LinkContentHandler linkHandler = new LinkContentHandler();
			
			HtmlParser htmlParser = new HtmlParser();
			htmlParser.parse(stream, linkHandler, metadata);
			
			List<Link> links = linkHandler.getLinks();
			for(Link link : links) {
				addLink(link.getUri());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			finish();
		}
	}
	
	
}
