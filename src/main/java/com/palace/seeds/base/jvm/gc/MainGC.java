package com.palace.seeds.base.jvm.gc;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Test;

import com.palace.seeds.base.random.WRandom;

public class MainGC {

	public static void main(String[] args) {
		 
		try {
			new MainGC().poiTest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	@Test
	public void poiTest() throws Exception, IOException{
		
		getJVMParams();
		URL url = MainGC.class.getResource("");
		String path = url.getPath()+"aa.xls";
		System.out.println(path);
		File file=new File(path);
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		file.delete();
		file.createNewFile();
		HSSFWorkbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet();
		Row row = sheet.createRow(0);
		List head =getHead();
		for (int i = 0; i < head.size(); i++) {
			Cell headCell = row.createCell(i);
			headCell.setCellType(Cell.CELL_TYPE_STRING);// 设置这个单元格的数据的类型,是文本类型还是数字类型
			headCell.setCellValue(String.valueOf(head.get(i)));// 给这个单元格设置值
		}
		List<Map<String,Object>> dataList = getListMap();
		for (int i = 0; i < dataList.size(); i++) {
			Row rowdata = sheet.createRow(i + 1);// 创建数据行
			Map<String, Object> mapdata = dataList.get(i);
			Iterator it = mapdata.keySet().iterator();
			int j = 0;
			while (it.hasNext()) {
				String strdata = String.valueOf(mapdata.get(it.next()));
				Cell celldata = rowdata.createCell(j);// 在一行中创建某列..
				celldata.setCellType(Cell.CELL_TYPE_STRING);
				celldata.setCellValue(strdata);
				j++;
			}
		}
		System.out.println("====begin write ");
		wb.write(file);
		System.out.println("====end write");
	}
	
	public List getHead(){
		List<String> list = new ArrayList<>();
		for(int i=0;i<20;i++){
			list.add(WRandom.getString(15));
		}
		return list;
	}
	public List<Map<String,Object>> getListMap(){
		List<Map<String,Object>> listMap =new ArrayList<>();
		for(int i=0;i<19000;i++){
			Map<String,Object> map  = new HashMap<String,Object>();
			for(int j=0;j<35;j++){
				map.put(String.valueOf(j), WRandom.getString(118));
			}
			listMap.add(map);
		}
		return listMap;
	}
	
	
	
	public static void getJVMParams(){
		MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
		MemoryUsage memUsage = memBean.getHeapMemoryUsage();
		System.out.println("max:"+memUsage.getMax()/getM());
		System.out.println("usage:"+memUsage.getUsed()/getM());
		System.out.println("init:"+memUsage.getInit()/getM());
		System.out.println("Heap Memory Usage: "+memBean.getHeapMemoryUsage());
		
		List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
		System.out.println("===================java options=============== ");
		System.out.println(inputArguments);
		
	}
	
	public static long getM(){
		return 1024*1024;
	}
}
