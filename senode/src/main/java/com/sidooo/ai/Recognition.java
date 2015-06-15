package com.sidooo.ai;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;

public class Recognition {
	
	
	private List<Attribute> attrs = new ArrayList<Attribute>();
	
	public Recognition() {
		this.load();
	}
	
	public void load() {
		File customFile = new File("src/main/resources/custom.xml");
		
		DocumentBuilderFactory dbf = null;
		DocumentBuilder db = null;
		try {
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			
			Document doc = db.parse(customFile);
			Element root = doc.getDocumentElement();
			
			NodeList children = root.getChildNodes();
			for(int i=0; i<children.getLength(); i++) {
				Node node = children.item(i);
				if ("attribute".equals(node.getNodeName())) {
					
					Attribute attr = new Attribute();
					NodeList properties = node.getChildNodes();
					for(int j=0; j<properties.getLength(); j++) {
						Node property = properties.item(j);
						if ("name".equals(property.getNodeName())) {
							attr.setName(property.getTextContent());
						} else if ("id".equals(property.getNodeName())) {
							attr.setId(property.getTextContent());
						} else if ("rule".equals(property.getNodeName())) {
							attr.setRule(property.getTextContent());
						} else if ("enable".equals(property.getNodeName())) {
							if ("true".equals(property.getTextContent())) {
								attr.enable(true);
							} else {
								attr.enable(false);
							}
						} else {
							
						}
					}
					
					attrs.add(attr);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public Attribute[] getAttributes(){
		return attrs.toArray(new Attribute[attrs.size()]);
	}
	
//	private boolean recoginizeDictionary(String sample, String word) {
//		String[] items = sample.split("\n");
//		for(String item : items) {
//			if (word.equals(item)) {
//				return true;
//			}
//		}
//		
//		return false;
//	}
	
	private boolean recoginizeRegex(String rule, String word) {
		Pattern pat = Pattern.compile(rule);
		Matcher mat= pat.matcher(word);
		return mat.matches();
	}
 	
//	//识别人名
//	private boolean recoginizeName(String word) {
//        Segment segment = HanLP.newSegment().enableNameRecognize(true);
// 
//        List<Term> termList = segment.seg(word);
//        if (termList.size() != 1) {
//        	return false;
//        } else {
//        	if (termList.get(0).nature == Nature.nr) {
//        		return true;
//        	} else {
//        		return false;
//        	}
//        }
//	}
	
	//识别机构名称
	private boolean recoginizeOrganization(String word) {
        Segment segment = HanLP.newSegment().enableOrganizationRecognize(true);
        List<Term> termList = segment.seg(word);
        if (termList.size() != 1) {
        	return false;
        } else {
        	if (termList.get(0).nature == Nature.nt) {
        		return true;
        	} else {
        		return false;
        	}
        }
	}
	
	
	public String match(String value) {
		
		//机构名称识别
		if (recoginizeOrganization(value)) {
			return "org";
		}
		
		//人名识别
//		if (recoginizeName(value)) {
//			return "name";
//		}
		
		for(Attribute attr : attrs) {
			if (attr.enabled()) {
				if (recoginizeRegex(attr.getRule(), value)) {
					return attr.getId();
				}	
			}
		}
		
		return null;
	}
	
	private String detectToken(String line) {
		
		int fieldCount = line.split(",").length;
		if (fieldCount > 1) {
			return ",";
		}
		
		fieldCount = line.split("\t").length;
		if (fieldCount > 1) {
			return "\t";
		}
		
		return null;
	}
	
	public IDKeyword[] search(String content) {
		
		Set<IDKeyword> keywords = new HashSet<IDKeyword>();
		
		String[] lines = content.split("\n");
		if (lines.length == 2) {
			
			String token = detectToken(lines[0]);
			if (token != null) {
				String[] titles = lines[0].split(token);
				String[] values = lines[1].split(token);
				if (titles.length == values.length) {
					// csv segment
					for(int i=0; i<titles.length; i++) {
						String attrId = match(values[i]);
						if (attrId != null) {
							IDKeyword keyword = new IDKeyword();
							keyword.attr = attrId;
							keyword.word = values[i];
							keywords.add(keyword);
						}
					}
					return keywords.toArray(new IDKeyword[keywords.size()]);
				}
			}

		} 
		
		//使用分词引擎进行识别
		List<Term> termList = NLPTokenizer.segment(content);
		System.out.println(termList.toString());
		for(Term term : termList) {
			if (term.nature == Nature.nt && 
				term.word.length() > 8 && 
				!"登记机关".equals(term.word) &&
				!"有限责任公司".equals(term.word)) {
				
				if ("公司".equals(term.word.substring(term.word.length() - 2))) {
					IDKeyword keyword = new IDKeyword();
					keyword.attr = "org";
					keyword.word = term.word;
					keywords.add(keyword);
				}

// 			} else if (term.nature == Nature.nr && nlpNameId != null) {
//				IDKeyword keyword = new IDKeyword();
//				keyword.attr = nlpNameId;
//				keyword.word = term.word;
//				keywords.add(keyword);
			} else {
				
			}
		}
		
		//再使用自定义正则表达式进行匹配
		for(Attribute attr : attrs) {
			Pattern pat = Pattern.compile(attr.getRule());
			Matcher mat= pat.matcher(content);
			while(mat.find()) {
				IDKeyword keyword = new IDKeyword();
				keyword.attr = attr.getId();
				keyword.word = mat.group();
				keywords.add(keyword);
			}	
		}
		
		return keywords.toArray(new IDKeyword[keywords.size()]);
	}
}
