package com.sidooo.crawl.instructment;

import org.w3c.dom.Node;

import com.sidooo.crawl.interrupt.AddressInterrupt;
import com.sidooo.crawl.interrupt.CaptchaInterrupt;
import com.sidooo.crawl.interrupt.ExceptionInterrupt;
import com.sidooo.crawl.interrupt.ExitInterrupt;
import com.sidooo.crawl.interrupt.Interrupt;
import com.sidooo.crawl.store.CorprationRepository;

public class Crawl {

	private long id;
	private Context context;

	public Crawl(String conf, CorprationRepository repo) throws Exception {
		id = (long) (1000 + Math.random() * 2913);
		context = new Context();
		context.store = repo;
		context.code.compile(conf);
	}

	public Long getId() {
		return Long.valueOf(this.id);
	}

	public Interrupt run() {

		while (true) {

			BaseInstructment instructment = null;
			try {
				instructment = context.code.getInstuctment(context.reg.eip);
			} catch (Exception e) {
				context.flag.excepted = true;
				return new AddressInterrupt(context.reg.eip);
			}
			context.reg.eip++;

			try {
				instructment.execute(context);
			} catch (Exception e) {
				context.flag.interrupted = true;
				context.flag.excepted = true;
				return new ExceptionInterrupt(context.reg.eip - 1,
						instructment.toString(), e.getMessage());
			}

			if (context.flag.interrupted) {
				if (instructment instanceof Captcha) {
					Captcha captcha = (Captcha) instructment;
					return new CaptchaInterrupt(captcha.getUrl(),
							captcha.getImage());
				}
			} else if (context.flag.finished) {
				return new ExitInterrupt();
			}

		}
	}

	public void setUserAnswer(String answer) {
		this.context.data.setVariable("captcha", answer);

	}

	public String getComapnyName() {
		return this.context.data.getVariable("company");
	}

}
