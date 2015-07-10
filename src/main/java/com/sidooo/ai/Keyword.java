package com.sidooo.ai;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.zip.CRC32;

import org.apache.hadoop.io.WritableComparable;

public class Keyword implements WritableComparable<Keyword>{
	
	private String word;
	
	private String attr;
	
	public Keyword() {

	}
	
	public Keyword(String word, String attr) {
		this.word = word;
		this.attr = attr;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj) 
			return true;
		if (obj instanceof Keyword) {
			Keyword keyword = (Keyword)obj;
			if (this.word.equalsIgnoreCase(keyword.getWord())) {
				return true;
			} 
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		if (word == null) 
			return 0;
		return word.hashCode();
	}
	
	public void setWord(String word) {
		this.word = word;
	}
	
	public String getWord() {
		return word;
	}
	
	public void setAttr(String attr) {
		this.attr = attr;
	}
	
	public String getAttr() {
		return attr;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(word);
		out.writeUTF(attr);
	}
	
	public static Keyword read(DataInput in) throws IOException {
		Keyword keyword = new Keyword();
		keyword.readFields(in);
		return keyword;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		this.word = in.readUTF();
		this.attr = in.readUTF();
	}

	@Override
	public int compareTo(Keyword keyword) {
		if (this.word.equalsIgnoreCase(keyword.getWord())) {
			return 0;
		} else {
			return this.word.compareTo(keyword.getWord());
		}
	}
	
	public String hash() {
		CRC32 crc32 = new CRC32();
		crc32.update(this.word.getBytes());
		return Long.toHexString(crc32.getValue());
	}
	
	public static String hash(String word) {
		CRC32 crc32 = new CRC32();
		crc32.update(word.getBytes());
		return Long.toHexString(crc32.getValue());
	}
	
	
}
