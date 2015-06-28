package com.sidooo.crawl;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;

public class Filter {
	
	private static String allowFileType = "doc,docx,html,htm,xls,xlsx,pdf,csv";

	private boolean acceptByExt(String fileType) {
		String[] types = allowFileType.split(",");
		for(String type: types) {
			if (fileType.equalsIgnoreCase(type)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean accept(String url) {
		String ext = FilenameUtils.getExtension(url.toString());
		if (ext == null || ext.length() <= 0) {
			return true;
		}
		return acceptByExt(ext);
	}

}
