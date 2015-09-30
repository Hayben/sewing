package com.sidooo.saic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sidooo.crawl.instructment.Captcha;
import com.sidooo.crawl.instructment.CrawlService;
import com.sidooo.division.Division;
import com.sidooo.division.DivisionService;
import com.sidooo.point2.Graph;

@Controller
public class SaicContronller {
	@Autowired
	private SaicService saicService;
	
	@Autowired
	private DivisionService divisionService;

	@RequestMapping(value = "/saic/query", method = RequestMethod.GET)
	public @ResponseBody SaicImage crawl(@RequestParam String companyName) throws Exception {
		
		Division division = divisionService.getDivisionByCompanyName(companyName);
		if (division == null) {
			return null;
		}
		
		Division province = divisionService.getProvince(division);
		if (province == null) {
			return null;
		}
		
		return saicService.query(province, companyName);
	}
	
	@RequestMapping(value = "/saic/answer", method = RequestMethod.GET)
	public @ResponseBody Graph answerQuestion(Long id, String answer) {
		try {
			return saicService.answer(id, answer);
		} catch (Exception e) {
			return null;
		}
	}
}
