package com.sidooo.senode;

import java.io.IOException;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.sidooo.point2"})
public class HtableConfiguration {

	@Bean
	public HTableInterface htable() throws MasterNotRunningException, ZooKeeperConnectionException, IOException {
		org.apache.hadoop.conf.Configuration conf =
				(org.apache.hadoop.conf.Configuration) HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum",
				"node4.sidooo.com,node8.sidooo.com,node13.sidooo.com");
		HConnection hConnection = HConnectionManager.createConnection(conf);
		return hConnection.getTable("wmouth");
	}
}
