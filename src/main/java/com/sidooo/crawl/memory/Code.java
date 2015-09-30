package com.sidooo.crawl.memory;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sidooo.crawl.instructment.BaseInstructment;
import com.sidooo.crawl.instructment.Captcha;
import com.sidooo.crawl.instructment.Connect;
import com.sidooo.crawl.instructment.Inc;
import com.sidooo.crawl.instructment.JumpWhenSuccess;
import com.sidooo.crawl.instructment.Refer;
import com.sidooo.crawl.instructment.Save;
import com.sidooo.crawl.instructment.Select;

import com.sidooo.crawl.instructment.Submit;
import com.sidooo.crawl.instructment.Var;

public class Code {
	
	
	
	private List<BaseInstructment> instructments = new ArrayList<BaseInstructment>();


	public void compile(String script) throws Exception {
		
		Element conf = Jsoup.parse(script).select("province").first();
		
		String name = conf.attr("name");
		Var varName = new Var("name", name);
		instructments.add(varName);
		
		String host = conf.attr("host");
		Var varHost = new Var("host", host);
		instructments.add(varHost);
		
		Elements nodes = conf.children();
		for(Element node : nodes) {
			if ("connect".equals(node.nodeName())) {
				String path = node.attr("path");
				Connect connect = new Connect(path);
				instructments.add(connect);
			} else if ("var".equals(node.nodeName())) {
				Var var = Var.parse(node);
				instructments.add(var);
			} else if ("submit".equals(node.nodeName())) {
				Submit submit = Submit.parse(node);
				instructments.add(submit);
			} else if ("save".equals(node.nodeName())) {
				Save save = Save.parse(node);
				instructments.add(save);
			} else if ("refer".equals(node.nodeName())) {
				Refer refer = Refer.parse(node);
				instructments.add(refer);
			} else if ("select".equals(node.nodeName())) {
				Select select = Select.parse(node);
				instructments.add(select);
			} else if ("captcha".equals(node.nodeName())) {
				Captcha captcha = Captcha.parse(node);
				instructments.add(captcha);
			} else if ("for".equals(node.nodeName())) {
				
				String itName = node.attr("name").toString();
				String start = node.attr("start").toString();
				Var var = new Var(itName, start);
				instructments.add(var);
				int index = instructments.size() - 1;
				
				Elements children = node.children();
				for(Element child : children) {
					
					if ("submit".equals(child.nodeName())) {
						Submit submit = Submit.parse(child);
						instructments.add(submit);
					} else if("save".equals(child.nodeName())) {
						Save save = Save.parse(child);
						instructments.add(save);
					} else {
						
					}
				}
				
				Inc inc = new Inc(itName);
				instructments.add(inc);
				
				JumpWhenSuccess jump = new JumpWhenSuccess(index+1);
				instructments.add(jump);
				
			}
		}
		
	}
	
	public BaseInstructment getInstuctment(int address) throws Exception {
		
		if (address < 0 || address >= instructments.size()) {
			throw new Exception("Invalid Instructment Address.");
		}
		
		return instructments.get(address);
	}
}
