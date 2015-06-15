package com.sidooo.wheart;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;

public class Filter {
	
	@Value("${allow}")
	private static String allowFileType;

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
		return acceptByExt(ext);
	}
}
