package com.sidooo.crawl.instructment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.io.ClassPathResource;

public class ConfigurationManager {

	private Map<String, String> provinces = new HashMap<String, String>();

	public String getConf(String province) {
		return provinces.get(province);
	}

	public void addConf(String name, String conf) {
		this.provinces.put(name, conf);
	}

	public static String InputStreamToString(InputStream is, String encoding)
			throws IOException {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[4096];
		int count = -1;
		while ((count = is.read(data, 0, 2048)) != -1)
			outStream.write(data, 0, count);

		data = null;
		return new String(outStream.toByteArray(), encoding);
	}

	public static ConfigurationManager newInstance(String xmlFile)
			throws Exception {

		ClassPathResource cpr = new ClassPathResource(xmlFile);
		Document document = Jsoup.parse(cpr.getFile(), "utf-8");
		Elements children = document.select("province");

		ConfigurationManager confmgr = new ConfigurationManager();
		for (Element child : children) {
			String name = child.attr("name");
			confmgr.addConf(name, child.toString());
		}

		return confmgr;
	}

}
