package com.sidooo.wheart;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.sidooo.item.ItemRepository;
import com.sidooo.point.PointRepository;
import com.sidooo.queue.QueueRepository;
import com.sidooo.seed.SeedRepository;
import com.sidooo.snapshot.SnapshotRepository;

@Service
@PropertySource(value = "classpath:wheart.properties")
public class WheartService {

	@Autowired
	private PointRepository pointRepo;
	
	@Autowired
	private ItemRepository itemRepo;
	
	@Autowired
	private SnapshotRepository snapRepo;
	
	@Autowired
	private SeedRepository seedRepo;
	
	@Autowired
	private QueueRepository queue;
	
	@Value("${processor.count}")
	private int count;
	
	private List<Processor> processors = new ArrayList<Processor>();
	
	public void start() {

		for(int i=0; i<count; i++) {
			Processor processor = new Processor(
					pointRepo, itemRepo, snapRepo, seedRepo, queue);
			processor.start();
			processors.add(processor);
		}
		
		
	}
	
	public static void main(String[] args) {
		
		AnnotationConfigApplicationContext context
	        = new AnnotationConfigApplicationContext(DatawareConfiguration.class);
		WheartService wheartService = 
				context.getBean("wheartService", WheartService.class);
		wheartService.start();
		
	}
}
