package com.sidooo.sewing;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Prepare {
	
	
	private static Logger LOG = LoggerFactory.getLogger(Prepare.class);
	
	public static void main(String[] args) throws Exception {
		
		LOG.info("Load HBase Configuration");
		HBaseConfiguration conf = new HBaseConfiguration();
		conf.set("hbase.zookeeper.quorum", "node4.sidooo.com,node8.sidooo.com,node13.sidooo.com");
		LOG.info("HBase Zookeeper Quorum: " + conf.get("hbase.zookeeper.quorum"));
		
		HBaseAdmin hbase = new HBaseAdmin(conf);
		
		if (!hbase.tableExists("crawl")) {
			LOG.info("Create HBase Table: crawl");
			HTableDescriptor table = new HTableDescriptor("crawl");
			HColumnDescriptor column = new HColumnDescriptor("fetch".getBytes());
			
			table.addFamily(column);
			hbase.createTable(table);
			
			LOG.info("Create HBase Table crawl successful.");
		} else {
			HTableDescriptor table = hbase.getTableDescriptor("crawl".getBytes());
			
			if (!table.hasFamily("fetch".getBytes())) {
				HColumnDescriptor column = new HColumnDescriptor("fetch".getBytes());
				table.addFamily(column);
				
				LOG.info("Add Column Family: fetch");
			}
		}
		
		hbase.close();
		
		LOG.info("Prepare finished.");
	}

}
