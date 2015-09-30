package com.sidooo.crawl.instructment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sidooo.ai.Keyword;
import com.sidooo.ai.Recognition;
import com.sidooo.crawl.interrupt.Interrupt;
import com.sidooo.crawl.store.CorprationRepository;
import com.sidooo.crawl.store.Publicity;
import com.sidooo.division.Division;
import com.sidooo.point2.Edge;
import com.sidooo.point2.Graph;
import com.sidooo.point2.KeywordNode;
import com.sidooo.saic.SaicImage;

@Service("crawlService")
public class CrawlService {

	private Map<Long, Crawl> crawls = new HashMap<Long, Crawl>();
	
	@Autowired
	private CorprationRepository repo; 
	
	private ConfigurationManager seeds;
	
	private Recognition recog = new Recognition();  
	
	public CrawlService() throws Exception {
		seeds = ConfigurationManager.newInstance("saic.xml");
	}
	
	public SaicImage query(Division province, String companyName) {
		
		String conf = seeds.getConf(province.getName());
		
		Crawl crawl = new Crawl(conf, repo);
		
		Interrupt interrupt = crawl.run();
		
		return new SaicImage(crawl.getId(), urlImage);
	}
	
	public void browse(String name) throws Exception {
		String conf = seeds.getConf(name);
		
		Crawl crawl = new Crawl(conf, repo);
		Interrupt interrupt = crawl.run();
		
	}
	
	public Graph answer(Long fetcherId, String answer) throws Exception {
		Crawl crawl = crawls.get(fetcherId);
		if (crawl == null) {
			return null;
		}
		
		crawl.setUserAnswer(answer);
		
		crawl.run();
		
		String company = crawl.getComapnyName();
		
		Publicity publicity = repo.getPublicity(company);
		
		Graph graph = new Graph();
		graph.addNode(new KeywordNode(company));
		
		List<String> investors = publicity.bureau.investor;
		for(String investor : investors) {
			Keyword[] keywords = recog.search(investor);
			for(Keyword keyword : keywords) {
			
				if ("nt".equals(keyword.getAttr())) {
					graph.addNode(new KeywordNode(keyword));
					graph.addEdge(new Edge(company, keyword.getWord()));
				}
				
			}
		}

		return graph;
	}
}
