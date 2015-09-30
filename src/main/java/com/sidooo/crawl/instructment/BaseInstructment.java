package com.sidooo.crawl.instructment;

import com.sidooo.crawl.cpu.Flag;
import com.sidooo.crawl.memory.Code;
import com.sidooo.crawl.memory.Data;
import com.sidooo.crawl.network.Fetcher;
import com.sidooo.crawl.store.CorprationStore;

abstract
public class BaseInstructment {
	
	abstract public boolean execute(Context context);

}
