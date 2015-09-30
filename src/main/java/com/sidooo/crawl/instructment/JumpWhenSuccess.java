package com.sidooo.crawl.instructment;

import com.sidooo.crawl.cpu.Flag;
import com.sidooo.crawl.memory.Code;
import com.sidooo.crawl.memory.Data;
import com.sidooo.crawl.network.Fetcher;
import com.sidooo.crawl.store.CorprationStore;

public class JumpWhenSuccess extends BaseInstructment{

	private int newAddress = 0;
	
	public JumpWhenSuccess(int newAddress) {
		this.newAddress = newAddress;
	}
	
	public int getTargetAddress() {
		return this.newAddress;
	}

	@Override
	public boolean execute(Context context) {
		if (context.flag.succeed) {
			context.reg.eip = newAddress;
		}
		return false;
	}

}
