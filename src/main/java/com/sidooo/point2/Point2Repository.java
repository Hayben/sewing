package com.sidooo.point2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.protobuf.generated.CellProtos.KeyValue;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.sidooo.ai.Keyword;

public class Point2Repository {
	
	@Autowired
	private HTable hbaseTable;
	
	private Gson gson = new Gson();
	
	public List<Point2> getPoints(String word) {
		
		List<Point2> points = new ArrayList<Point2>();
		
		String keyHash = Keyword.hash(word);
		Get get = new Get(Bytes.toBytes(keyHash));
		get.addColumn(Bytes.toBytes("points"), Bytes.toBytes("point"));
		
		Result result;
		try {
			result = hbaseTable.get(get);
		} catch (IOException e) {
			return null;
		}
		List<Cell> cells = result.listCells();
		for(Cell cell : cells) {
			String jsonPoint = Bytes.toString(cell.getValue());
			Point2 point = gson.fromJson(jsonPoint, Point2.class);
			points.add(point);
		}
		
		return points;
	}
	
	public List<Keyword> getKeywords(String pointId) {
		List<Keyword> keywords = new ArrayList<Keyword>();
		
		Get get = new Get(Bytes.toBytes(pointId));
		get.addColumn(Bytes.toBytes("keywords"), Bytes.toBytes("keyword"));
		
		Result result;
		try {
			result = hbaseTable.get(get);
		} catch (IOException e) {
			return null;
		}
		List<Cell> cells = result.listCells();
		for(Cell cell : cells) {
			String jsonKeyword = Bytes.toString(cell.getValue());
			Keyword keyword = gson.fromJson(jsonKeyword, Keyword.class);
			keywords.add(keyword);
		}
		
		return keywords;
	}

}
