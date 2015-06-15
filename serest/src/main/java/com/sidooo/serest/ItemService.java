package com.sidooo.serest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sidooo.item.Item;
import com.sidooo.item.ItemRepository;

@Service
public class ItemService {

	@Autowired
	private ItemRepository itemRepo;

	public Pagination getItemList(String seedId, int pageNo, int pageSize) {

		long totalCount = itemRepo.getItemCountBySeed(seedId);
		Pagination page = new Pagination(pageNo, pageSize, totalCount);

		List<Item> items = itemRepo.getItemList(seedId, page.getFirstResult(),
				pageSize);
		for (Item item : items) {
			page.addItem(item);
		}

		return page;
	}

	public long getItemCountBySeed(String seedId) {
		return itemRepo.getItemCountBySeed(seedId);
	}

}
