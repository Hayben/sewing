package com.sidooo.serest;

import java.util.ArrayList;
import java.util.List;

import com.sidooo.item.Item;

public class Pagination {

	private int pageSize = 30;
	
	private int pageNo;
	
	@SuppressWarnings("unused")
	private int perviousPage;
	
	@SuppressWarnings("unused")
	private int nextPage;
	
	private long totalCount;
	
	private int totalPage;
	
	private List<Item> items = new ArrayList<Item>();
	
	public Pagination(int pageNo, int pageSize, long totalCount) {
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.totalCount = totalCount;
		
		this.totalPage = (int)((this.totalCount % this.pageSize > 0)?
				(this.totalCount/this.pageSize + 1) : 
					this.totalCount/this.pageSize);
		this.perviousPage = (this.pageNo > 1) ? 
				this.pageNo - 1 : this.pageNo;
		this.nextPage = (this.pageNo == this.totalPage) ? 
				this.pageNo : this.pageNo + 1;
	}
	
	public int getFirstResult() {
		return (this.pageNo - 1) * this.pageSize;
	}
	
	public int getLastResult() {
		return this.pageNo * this.pageSize;
	}
	
	public void addItem(Item item) {
		items.add(item);
	}
	
}
