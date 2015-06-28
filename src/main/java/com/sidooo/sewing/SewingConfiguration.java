package com.sidooo.sewing;

import org.apache.hadoop.conf.Configuration;

public class SewingConfiguration {

	public static Configuration create() {
		Configuration conf = new Configuration();
		//conf.addResource("nutch-default.xml");
		return conf;
	}
}
