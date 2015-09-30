package com.sidooo.crawl.instructment;

import com.sidooo.crawl.cpu.Flag;
import com.sidooo.crawl.cpu.Register;
import com.sidooo.crawl.memory.Code;
import com.sidooo.crawl.memory.Data;
import com.sidooo.crawl.network.Fetcher;
import com.sidooo.crawl.store.CorprationRepository;
import com.sidooo.crawl.store.CorprationStore;

public class Context {
	
	public Register reg = new Register();
	public Flag flag = new Flag();
	public Code code = new Code();
	public Data data = new Data();
	
	public String error = null;
	
	public Fetcher fetcher = new Fetcher();
	
	public CorprationRepository store = new CorprationRepository();

}
