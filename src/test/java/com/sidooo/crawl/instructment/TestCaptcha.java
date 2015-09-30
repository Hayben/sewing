package com.sidooo.crawl.instructment;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sidooo.content.HttpContent;

public class TestCaptcha {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		String reponse = "<span id='updateVerifyCode1'> <img border='0' src='/province/rand_img.jsp?type=7&amp;temp=Tue Jul 21 2015 12:10:19 GMT+0800 (中国标准时间)' width='120px' height='43px'> </span>";

		String[] array = {reponse};
		
		
		Context context = new Context();
		context.data.setContent(array);
		context.data.setVariable("host", "http://www.jsgsj.gov.cn:58888");
		Captcha captcha = new Captcha(SelectMethod.JQUERY, "#updateVerifyCode1 img[src]", 0, "src");
		captcha.execute(context);
		assertEquals(captcha.getImage(), "http://www.jsgsj.gov.cn:58888/province/rand_img.jsp?type=7&temp=Tue Jul 21 2015 12:10:19 GMT+0800 (中国标准时间)");
		System.out.println(captcha.getImage());
	}

}
