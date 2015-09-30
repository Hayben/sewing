package com.sidooo.extractor;

import org.apache.commons.io.FilenameUtils;

public class ExtractorManager {
	
	private DocExtractor docExtractor = new DocExtractor();
	private DocxExtractor docxExtractor = new DocxExtractor();
	private XlsExtractor xlsExtractor = new XlsExtractor();
	private XlsxExtractor xlsxExtractor = new XlsxExtractor();
	private CsvExtractor csvExtractor = new CsvExtractor();
	private HtmlExtractor htmlExtractor = new HtmlExtractor();
	private PdfExtractor pdfExtractor = new PdfExtractor();
	private TxtExtractor txtExtractor = new TxtExtractor();
	
	private ContentDetector detector = new ContentDetector();
	

	public ContentExtractor getInstanceByUrl(String path) {
		
		String format = FilenameUtils.getExtension(path);
		if (format == null) {
			return null;
		}
		
		if ("html".equalsIgnoreCase(format) || 
			"htm".equalsIgnoreCase(format)) {
			return this.htmlExtractor;
		} else if ("csv".equalsIgnoreCase(format)) {
			return this.csvExtractor;
		} else if ("xls".equalsIgnoreCase(format)){
			return this.xlsExtractor;
		} else if ("xlsx".equalsIgnoreCase(format)) {
			return this.xlsxExtractor;
		} else if ("doc".equalsIgnoreCase(format)) {
			return this.docExtractor;
		} else if ("docx".equalsIgnoreCase(format)) {
			return this.docxExtractor;
		} else if ("pdf".equalsIgnoreCase(format)) {
			return this.pdfExtractor;
		} else if ("txt".equalsIgnoreCase(format)) {
			return this.txtExtractor;
		} else {
			return null;
		}
	}
	
	public ContentExtractor getInstanceByMime(String mime) {
		
		if ("text/html".equalsIgnoreCase(mime)) {
			return this.htmlExtractor;
		} else if ("text/csv".equalsIgnoreCase(mime)) {
			return this.csvExtractor;
		} else if ("text/plain".equalsIgnoreCase(mime)) {
			return this.txtExtractor;
		} else if ("application/pdf".equalsIgnoreCase(mime)) {
			return this.pdfExtractor;
		} else if ("application/msword".equalsIgnoreCase(mime)) {
			return this.docExtractor;
		} else if ("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equalsIgnoreCase(mime)) {
			return this.docxExtractor;
		} else if ("application/vnd.ms-excel".equalsIgnoreCase(mime)) {
			return this.xlsExtractor;
		} else if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equalsIgnoreCase(mime)) {
			return this.xlsxExtractor;
		} else {
			return null;
		}
	}
	
	public ContentExtractor getInstanceByContent(byte[] content) {
		ContentType ct = detector.detect(content);
		if (ct == null) {
			return null;
		}
		
		if (ct.mime == null || ct.mime.length() <= 0) {
			return null;
		}
		
		return getInstanceByMime(ct.mime);
	}

}
