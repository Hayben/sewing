package com.sidooo.crawl.instructment;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSelect {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		String html = "<dl class='list'>" +
					  	"<dt>" +
					  		"<a href='javascript:void(0)' onclick='/ecipplatform/inner_ci/ci_queryCorpInfor_gsRelease.jsp,1022,23200210,3,320282000190134,ecipplatform)'>无锡市天茂胶辊有限公司</a>" +
			  			"</dt>" +
			  			"<dd>注册号:<span>320282000190134</span>     法定代表人:<span>黄伟</span>     登记机关:<span>宜兴市市场监督管理局</span>     成立日期:<span>2008年01月21日</span>" +
			  			"</dd><br>" +
			  			"<font color='red'>&nbsp;&nbsp;&nbsp;&nbsp;</font>" +
		  			  "</dl>";	
		
		
		Context context = new Context();
		String[] array = {html};
		context.data.setContent(array);
		Select select = new Select(SelectMethod.JQUERY, "dl.list dt a[onclick]", 0, "onclick");
		select.execute(context);
		
		String[] response = context.data.getContent();
		assertEquals(response.length, 1);
		assertEquals(response[0], "/ecipplatform/inner_ci/ci_queryCorpInfor_gsRelease.jsp,1022,23200210,3,320282000190134,ecipplatform)");
		
	}

}
