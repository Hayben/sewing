package com.sidooo.crawl.instructment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jayway.jsonpath.JsonPath;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

abstract public class SelectInstructment extends BaseInstructment {

	private String[] xml(String[] texts, String key, Integer index,
			String attribute) {

		List<String> result = new ArrayList<String>();

		for (String text : texts) {
			Document doc = Jsoup.parse(text, "", Parser.xmlParser());
			Elements elements = doc.select(key);
			if (elements == null || elements.size() <= 0) {
				continue;
			}

			if (index != null) {
				Element element = elements.get(index.intValue());
				if (element == null) {
					continue;
				}

				if (attribute != null) {

					if ("text".equals(attribute)) {
						result.add(element.text());
					} else {
						result.add(element.attr(attribute));
					}
				} else {

					result.add(element.html());
				}
			} else {
				for (Element element : elements) {
					if (attribute != null) {
						result.add(element.attr(attribute));
					} else {
						result.add(element.outerHtml());
					}
				}
			}
		}

		return result.toArray(new String[0]);
	}

	private String[] jquery(String[] texts, String key, Integer index,
			String attribute) {

		List<String> result = new ArrayList<String>();

		for (String text : texts) {
			Document doc = Jsoup.parse(text, "", Parser.htmlParser());

			Elements elements = null;
			if (key == null) {
				elements = doc.children();
			} else {
				elements = doc.select(key);
			}

			if (elements == null || elements.size() <= 0) {
				continue;
			}

			if (index != null) {
				Element element = elements.get(index.intValue());
				if (element == null) {
					continue;
				}

				if (attribute != null) {
					if ("text".equals(attribute)) {
						result.add(element.text());
					} else {
						result.add(element.attr(attribute));
					}
				} else {
					result.add(element.outerHtml());
				}
			} else {
				for (Element element : elements) {
					if (attribute != null) {
						if ("text".equals(attribute)) {
							result.add(element.text());
						} else {
							result.add(element.attr(attribute));
						}

					} else {
						result.add(element.outerHtml());
					}
				}
			}
		}

		return new HashSet<String>(result).toArray(new String[0]);
	}

	private String[] json(String[] texts, String key, Integer index,
			String attribute) {

		List<String> result = new ArrayList<String>();

		for (String text : texts) {
			result = JsonPath.read(text, key);
		}
		return result.toArray(new String[0]);
	}

	private String[] regular(String[] texts, String key, Integer index,
			String attribute) {

		List<String> result = new ArrayList<String>();

		for (String text : texts) {
			Pattern pattern = Pattern.compile(key);
			Matcher matcher = pattern.matcher(text);
			if (matcher.find()) {
				if (index != null) {
					if (index.intValue() > (matcher.groupCount() - 1)) {
						continue;
					}
					result.add(matcher.group(index.intValue() + 1));
				} else {
					int groupCount = matcher.groupCount();
					for (int i = 1; i <= groupCount; i++) {
						result.add(matcher.group(i));
					}
				}
			} else {
				continue;
			}
		}

		return result.toArray(new String[0]);
	}

	private String[] split(String[] texts, String key, Integer index,
			String attribute) {
		List<String> result = new ArrayList<String>();
		for (String text : texts) {
			String[] terms = text.split(",");
			if (index.intValue() >= terms.length) {
				continue;
			}
			
			String term = terms[index.intValue()];
			if (attribute != null) {
				if ("trim".equals(attribute)) {
					term = term.trim();
					if (term.startsWith("'")) {
						term = term.substring(1);
					}
					
					if (term.endsWith("'")) {
						term = term.substring(0, term.length()-1);
					}
					result.add(term);
				} else {
					result.add(term);
				}
			} else {
				result.add(term);
			}
		}
		
		return result.toArray(new String[0]);
	}

	protected String[] select(SelectMethod method, String key, Integer index,
			String attribute, String[] input) {
		String[] matches = null;

		if (method == null) {
			return null;
		} else if (method == SelectMethod.JQUERY) {
			matches = jquery(input, key, index, attribute);
		} else if (method == SelectMethod.JSON) {
			matches = json(input, key, index, attribute);
		} else if (method == SelectMethod.REGULAR) {
			matches = regular(input, key, index, attribute);
		} else if (method == SelectMethod.XML) {
			matches = xml(input, key, index, attribute);
		} else if (method == SelectMethod.SPLIT) {
			matches = split(input, key, index, attribute);
		} else {
			return null;
		}

		return matches;
	}
}
