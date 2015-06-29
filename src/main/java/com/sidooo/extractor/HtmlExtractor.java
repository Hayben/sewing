package com.sidooo.extractor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.Link;
import org.apache.tika.sax.LinkContentHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

public class HtmlExtractor extends ContentExtractor {
	

	@Override
	public void extract(InputStream stream) {
		InputStream input = new BufferedInputStream(stream);
		clearItems();
		try {
			input.mark(input.available()+1);
			BodyContentHandler handler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			HtmlParser parser = new HtmlParser();
			ParseContext context = new ParseContext();
			parser.parse(input, handler, metadata, context);
			
			setTitle(metadata.get("title"));
			addItem(handler.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			finish();
		}
	}
	
	public void extractLink(InputStream stream) {
		
		try {
			Metadata metadata = new Metadata();
			LinkContentHandler linkHandler = new LinkContentHandler();
			
			HtmlParser htmlParser = new HtmlParser();
			htmlParser.parse(stream, linkHandler, metadata);
			
			List<Link> links = linkHandler.getLinks();
			for(Link link : links) {
				addLink(link.getUri());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void extractLink(InputStream stream, String charset) {
		
		Document doc;
		try {
			doc = Jsoup.parse(stream, charset, this.path);
		} catch (IOException e) {
			return;
		}
		Elements links = doc.select("a[href]");
		for(Element link : links) {
			String url = link.attr("abs:href");
			addLink(url);
		}
	}
	
	
}
