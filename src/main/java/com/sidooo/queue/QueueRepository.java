package com.sidooo.queue;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.sidooo.point.Item;
import com.sidooo.seed.Seed;
import com.sidooo.seed.SeedUrl;

@Repository("queueRepository")
@PropertySource(value = "classpath:wbrain.properties")
public class QueueRepository {

	@Autowired
	private RabbitTemplate rabbit;

	private final static String SEED_QUEUE = "seed";
	
	@Value("${rabbit.queue.url}")
	private String URL_QUEUE;
	
	private final static String ITEM_QUEUE = "item";
	
	public void sendSeed(Seed seed) {
		Gson gson = new Gson();
		String json = gson.toJson(seed);
		rabbit.convertAndSend(SEED_QUEUE, json);
	}

	public Seed receiveSeed() {
		Gson gson = new Gson();	
		String message = rabbit.receiveAndConvert(SEED_QUEUE).toString();
		return  gson.fromJson(message, Seed.class);
	}	
	
	public void sendUrl(String url) {
		rabbit.convertAndSend(URL_QUEUE, url);
	}
	
	public String receiveUrl() {
		return rabbit.receiveAndConvert(URL_QUEUE).toString();
	}
	
	public void sendSeedUrl(String seedId, String url) {
		
		Gson gson = new Gson();
		SeedUrl request = new SeedUrl();
		request.seedId = seedId;
		request.url = url;
		String json = gson.toJson(request);
		rabbit.convertAndSend(URL_QUEUE, json);
	}
	
	public SeedUrl receiveSeedUrl() {
		Gson gson = new Gson();
		String json = rabbit.receiveAndConvert(URL_QUEUE).toString();
		return gson.fromJson(json, SeedUrl.class);
	}

	public void sendItem(Item item) {
		Gson gson = new Gson();
		String request = gson.toJson(item);
		rabbit.convertAndSend(ITEM_QUEUE, request);
	}
	
	public Item receiiveItem() {
		Gson gson = new Gson();
		String json = rabbit.receiveAndConvert(ITEM_QUEUE).toString();
		return gson.fromJson(json, Item.class);
	}
	
	
}
