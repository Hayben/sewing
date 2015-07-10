package com.sidooo.seed;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "seed")
public class Seed {

	@Id
	private String id;

	private Integer division;

	@Field("name")
	private String name;

	@Field("url")
	private String url;

	@Field("enabled")
	private boolean enabled;

	@Field("type")
	private String type;

	@Field("level")
	private String level;

	@Field("reliability")
	private String reliability;
	
	@Field("account")
	private Account account;
	
	@Field("statistics")
	private Statistics statistics = new Statistics();

	// @Transient
	// private Task task = null;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLevel() {
		return this.level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getReliability() {
		return this.reliability;
	}

	public void setReliability(String reliability) {
		this.reliability = reliability;
	}

	public Integer getDivision() {
		return this.division;
	}

	public void setDivision(Integer divisionId) {
		this.division = divisionId;
	}

	public boolean getEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enable) {
		this.enabled = enable;
	}
	
	public Account getAccount() {
		return account;
	}
	
	public void setSuccessCount(long count) {
		this.statistics.success = count;
	}
	
	public void setFailCount(long count) {
		this.statistics.fail = count;
	}
	
	public void setLimitCount(long count) {
		this.statistics.limit = count;
	}
	
	public void setWaitCount(long count) {
		this.statistics.wait = count;
	}
	
	public void setPointCount(long count) {
		this.statistics.point = count;
	}
	
	public long getPointCount() {
		return this.statistics.point;
	}
	
	public void incPointCount() {
		this.statistics.point ++;
	}
	
	public void incPointCount(long count) {
		this.statistics.point += count;
	}
	
	public void setLinkCount(long count) {
		this.statistics.link = count;
	}
	
	public long getLinkCount() {
		return this.statistics.link;
	}
	
	public void incLinkCount() {
		this.statistics.link ++;
	}
	
	public void incLinkCount(long count) {
		this.statistics.link += count;
	}
	
	public Statistics getStatistics() {
		return this.statistics;
	}

	// public void startTask(Task newTask) throws Exception {
	// if (task != null) {
	// if (task.isAlive()) {
	// throw new Exception("seed is working.");
	// } else {
	// task = null;
	// }
	// }
	//
	// task = newTask;
	// task.start();
	// }
	//
	// public void stopTask() throws InterruptedException {
	//
	// if (task != null ) {
	// if (task.isAlive()) {
	// //正在工作
	// task.interrupt();
	// task.join();
	// }
	//
	// task = null;
	// }
	// }
	//
	// public boolean isWorking() {
	// if (task == null) {
	// return false;
	// }
	//
	// return task.isAlive();
	// }

	// public JSONObject toJson(SeedService service) throws Exception{
	// JSONObject node = new JSONObject();
	// node.put("id", this.id);
	// node.put("config", this.config.toJson());
	// node.put("count", service.getItemCount(id));
	// JSONObject jsonTask = new JSONObject();
	// if (task != null) {
	// jsonTask.put("status", "RUNNING");
	// jsonTask.put("type", task.getType());
	// jsonTask.put("finish", task.getFinishSize());
	// jsonTask.put("total", task.getTotalSize());
	// } else {
	// jsonTask.put("status", "IDLE");
	// jsonTask.put("type", "none");
	// jsonTask.put("finish", 0);
	// jsonTask.put("total", 0);
	// }
	// node.put("task", jsonTask);
	//
	// return node;
	// }
	
	public String toString() {
		return "Name:" + this.name + ", Statistics:" + this.statistics.success+"," + this.statistics.fail;
	}

}
