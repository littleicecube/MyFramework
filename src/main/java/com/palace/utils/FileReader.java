package com.palace.utils;

import java.io.IOException;
import java.io.InputStreamReader;

public class FileReader {

	 public static InputStreamReader getReader(String name){
		 return new InputStreamReader(FileReader.class.getClassLoader().getResourceAsStream(name));
	 }
	 public static String getTextContent(String name){
		StringBuilder sb=new StringBuilder();
		InputStreamReader reader = getReader(name);
		try {
			char[] arr=new char[1024*1024];
			int l=0;
			l = reader.read(arr);
			while(l>0){
				sb.append(new String(arr,0,l));
				l = reader.read(arr);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	 }
}
