package com.sidooo.tool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.sidooo.seed.Seed;
import com.sidooo.seed.SeedService;
import com.sidooo.senode.MongoConfiguration;

@Service("seedBackup")
public class SeedBackup {
	
	@Autowired
	private SeedService seedService;
	
	public int run() throws IOException {

		File backDir = new File("backup");
		if (!backDir.exists()) {
			backDir.mkdir();
		}
		
		String backId = (new SimpleDateFormat("yyyyMMddHHmm")).format(new Date());
		File backFile = new File("backup/seed_"+backId+".dat");
		
		
		Gson gson = new Gson();
		List<Seed> seeds = seedService.getSeeds();
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(backFile)));
		for(Seed seed : seeds) {
			writer.write(gson.toJson(seed) + "\n");
		}
		writer.close();
		return 0;
	}

	public static void main(String args[]) throws Exception {

		@SuppressWarnings("resource")
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				MongoConfiguration.class);
		context.scan("com.sidooo.seed", "com.sidooo.tool");
		SeedBackup backup = context.getBean("seedBackup", SeedBackup.class);
		try {
			backup.run();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
			return;
		} 
		
		System.exit(0);
	}
}
