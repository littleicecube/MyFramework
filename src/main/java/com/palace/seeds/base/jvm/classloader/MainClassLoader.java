package com.palace.seeds.base.jvm.classloader;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.Test;

public class MainClassLoader {

	
	String fileName="pro.txt";
	public static void main(String[] args) {
		
		Class clazz = MainClassLoader.class;
		
		URL url1 = MainClassLoader.class.getClassLoader().getResource("classloader/pro.txt");
		System.out.println(url1);
		System.out.println("=================================================");
		URL url = MainClassLoader.class.getResource("classloader/pro.txt");
		System.out.println(url);
		
		System.out.println("curr class loader:"+MainClassLoader.class.getClassLoader().getClass());
		System.out.println("parent class loader:"+MainClassLoader.class.getClassLoader().getParent());
		
		
	}
	
	@Test
	public void testLoader() throws Exception{
		URL url = MainClassLoader.class.getResource("MainClassLoader.class");
		File file=new File(url.getPath());
		System.out.println(file.getAbsolutePath());
		Paths.get(url.toURI().getPath());
		System.out.println(url);
	}
	
	public void printMsg(){
		URL url = MainClassLoader.class.getResource("");
		System.out.println("classloader msg");
	}
}
