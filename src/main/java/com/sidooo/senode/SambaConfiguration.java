package com.sidooo.senode;

import jcifs.smb.NtlmPasswordAuthentication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan({"com.sidooo.snapshot"})
@PropertySource(value = "classpath:samba.properties")
public class SambaConfiguration {
	
	@Value("${samba.host}")
	private String sambaHost;

	@Value("${samba.user}")
	private String sambaUser;

	@Value("${samba.password}")
	private String sambaPassword;
	
	@Bean
	public NtlmPasswordAuthentication sambaAuth() {
		jcifs.Config.registerSmbURLHandler();
		NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(
				sambaHost, sambaUser, sambaPassword);
		return auth;
	}
}
