package com.sidooo.snapshot;

import java.net.MalformedURLException;
import java.net.UnknownHostException;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFileInputStream;

public class SnapFileInputStream extends SmbFileInputStream {

	public SnapFileInputStream(SnapFile snapFile) throws SmbException,
			MalformedURLException, UnknownHostException {
		super(snapFile);
	}

}
