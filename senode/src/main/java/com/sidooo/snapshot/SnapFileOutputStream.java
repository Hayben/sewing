package com.sidooo.snapshot;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFileOutputStream;

public class SnapFileOutputStream extends SmbFileOutputStream {

	public SnapFileOutputStream(SnapFile file) throws SmbException,
			MalformedURLException, UnknownHostException {
		super(file);
	}

}
