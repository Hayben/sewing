package com.sidooo.senode;

import java.io.IOException;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTablePool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HtableConfiguration {

	@Bean
	public HTable htable() throws MasterNotRunningException, ZooKeeperConnectionException, IOException {
		org.apache.hadoop.conf.Configuration conf =
				(org.apache.hadoop.conf.Configuration) HBaseConfiguration.create();
		
		HTablePool pool = new HTablePool(conf, 1000);
		return (HTable) pool.getTable("wmouth");
	}
}
