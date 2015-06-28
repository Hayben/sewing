package com.sidooo.crawl;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.server.common.JspHelper.Url;

public class Store {
	
	private Configuration conf;
	
	private DFSClient dfs;
	
	public Store() throws IOException {
		this.conf = HBaseConfiguration.create();
		this.dfs = new DFSClient(new HdfsConfiguration());
	}

	public void save(URL url, byte[] data) {
		
		HTablePool pool = new HTablePool(conf, 1000);
		HTable table = (HTable) pool.getTable("crawl");
		Put put = new Put(url.toString().getBytes());
		
		put.add("fetch".getBytes(), "content".getBytes(), data);
		
		try {
			table.put(put);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public OutputStream createFile(URL url) throws IOException {
		OutputStream out = new BufferedOutputStream(dfs.create(url.toString(), true));
		return out;
	}
	
	public void closeFile(OutputStream out) throws IOException {
		out.close();
	}
}
