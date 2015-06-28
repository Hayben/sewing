package com.sidooo.extractor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.detect.TypeDetector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.NullOutputStream;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class ContentDetector {

	private Tika tika = new Tika();
	
	public ContentType detect(InputStream input) {
		
//		Metadata meta = new Metadata();
//		MimeTypes mimeTypes = TikaConfig.getDefaultConfig().getMimeRepository();
//		MediaType type;
//		try {
//			type = mimeTypes.detect(input, meta);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}
//		System.out.println(type);
//		System.out.println(meta);
//		return type.getType();
//		
//		TikaInputStream tikaStream = TikaInputStream.get(input);
//		Metadata meta = new Metadata();
//		MediaType mime;
//		try {
//			mime = tika.getDetector().detect(tikaStream, meta);
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}
//		System.out.println(mime.toString());
//		System.out.println(meta);
//		return mime.getType();
		
//		Metadata meta = new Metadata();
//		TypeDetector typeDetector = new TypeDetector();
//		MediaType mime = typeDetector.detect(input, meta);
//		if (MediaType.OCTET_STREAM == mime) {
//			Detector defaultDetector = new DefaultDetector();
//			mime = defaultDetector.detect(input, meta);
//		}
//        for (String name : meta.names()) {
//            System.out.println(name + ":\t" + meta.get(name));
//        }
//		return mime.toString();
		
//		BodyContentHandler handler = new BodyContentHandler();
//		Metadata metadata = new Metadata();
//		AutoDetectParser parser = new AutoDetectParser();
//		ParseContext context = new ParseContext();
//		parser.parse(input, handler, metadata, context);
//		System.out.println(metadata);
//		System.out.println(context);
//		return null;

         BodyContentHandler handler = new BodyContentHandler(new NullOutputStream());
         Metadata meta = new Metadata();
         Parser parser = new AutoDetectParser();
         try {
			parser.parse(input, handler, meta, new ParseContext());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
         
         
         for (String name : meta.names()) {
               System.out.println(name + ":\t" + meta.get(name));
         }
         
         String attr = meta.get("Content-Type");
         if (attr != null) {
        	 ContentType result = new ContentType();
        	 MediaType type = MediaType.parse(attr);
        	 result.charset = type.getParameters().get("charset");
        	 result.mime = type.getBaseType().toString();
        	 return result;
         } else {
        	 return null;
         }
	}
	
	public ContentType detect(byte[] input) {

		ByteArrayInputStream stream = new ByteArrayInputStream(input);
		return detect(stream);
	}
	

}
