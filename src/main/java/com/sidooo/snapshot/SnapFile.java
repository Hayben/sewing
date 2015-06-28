package com.sidooo.snapshot;

import java.net.MalformedURLException;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class SnapFile extends SmbFile {

	public SnapFile(String url, NtlmPasswordAuthentication sambaAuth)
			throws MalformedURLException {
		super(url, sambaAuth);
	}

	public boolean isDirectory() {
		try {
			return super.isDirectory();
		} catch (Exception e) {
			return false;
		}
	}

	public void delete() {
		try {
			if (super.exists()) {

				super.delete();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
