package com.sidooo.crawl.instructment;

import com.sidooo.content.HttpContent;
import com.sidooo.crawl.cpu.Flag;
import com.sidooo.crawl.memory.Code;
import com.sidooo.crawl.memory.Data;
import com.sidooo.crawl.network.Fetcher;
import com.sidooo.crawl.store.CorprationStore;

public class Connect extends BaseInstructment{

	private String path;
	
	public Connect(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return this.path;
	}

	@Override
	public boolean execute(Context context) {
		String host = context.data.getVariable("host");
		
		HttpContent content = context.fetcher.get(host+path);
		
		String[] input = {new String(content.getContent())};
		context.data.setContent(input);
		
		return true;
	}

}
