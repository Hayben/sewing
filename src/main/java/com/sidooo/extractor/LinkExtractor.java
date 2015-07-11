package com.sidooo.extractor;

import java.io.InputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class LinkExtractor extends ContentExtractor {

	private Document doc = null;
	private Elements links = null;
	
	private int offset = 0;
	
	@Override
	public void setInput(InputStream stream, String charset) throws Exception{
		doc = Jsoup.parse(stream, charset, this.path);
		links = doc.select("a[href]");
	}

	@Override
	public String extract() {
		if ( links == null || offset >= links.size() || offset < 0) {
			return null;
		}
		String link = links.get(offset).attr("abs:href");
		
		offset ++;
		int index = link.indexOf("#");
		if (index >= 0) {
			link = link.substring(0, index);
		}
		return link;
	}

	@Override
	public void close() {
		if (doc != null) {
			doc = null;
		}
		
		if (links != null) {
			links = null;
		}
	}
	
//	public void extractLink(InputStream stream) {
//		
//		try {
//			Metadata metadata = new Metadata();
//			LinkContentHandler linkHandler = new LinkContentHandler();
//			
//			HtmlParser htmlParser = new HtmlParser();
//			htmlParser.parse(stream, linkHandler, metadata);
//			
//			List<Link> links = linkHandler.getLinks();
//			for(Link link : links) {
//				addLink(link.getUri());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

}
