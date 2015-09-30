package com.sidooo.crawl.instructment;

import org.jsoup.nodes.Element;

import com.sidooo.saic.SaicInfo;
import com.sidooo.saic.SaicPublisher;

public class Save extends BaseInstructment {

	private SaicPublisher publisher;

	private SaicInfo info;

	public Save(SaicPublisher publisher, SaicInfo info) {
		this.publisher = publisher;
		this.info = info;
	}
	
	public SaicPublisher getFrom() {
		return this.publisher;
	}
	
	public SaicInfo getType() {
		return this.info;
	}

	@Override
	public boolean execute(Context context) {

		String company = context.data.getVariable("company");
		String[] content = context.data.getContent();
		context.store.save(company, publisher, info, content);
		return true;
	}
	
	public static Save parse(Element node) throws Exception{
		
		SaicPublisher publisher = SaicPublisher.valueOf(node.attr("from").toUpperCase());
		SaicInfo info = SaicInfo.valueOf(node.attr("type").toUpperCase());
		Save save = new Save(publisher, info);
		return save;
	}

}
