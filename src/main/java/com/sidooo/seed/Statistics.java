package com.sidooo.seed;

public class Statistics {

	public long success = 0;
	public long fail = 0;
	public long wait = 0;
	public long update = 0;
	public long limit = 0;
	
	public long point = 0;
	public long link = 0;

	public String toString() {
		return "SUCCESS " + success + ", FAIL " + fail + ", WAIT " + wait
				+ ", UPDATE " + update + ", LIMIT " + limit;
	}
}
