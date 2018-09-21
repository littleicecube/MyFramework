package com.palace.seeds.base.excep;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Test;

public class MainException {

	@Test
	public void secondExcep(){
		try{
			excep();
		}catch(Exception e){
			if(e instanceof RuntimeException){
				System.out.println("get check");
				throw e;
				//不可能被执行的代码
				//System.out.println("after get check");
			}
		}
	}
	
	public static void excep(){
		System.out.println("start");
		if(true)
			throw new RuntimeException("uncheck excep");
	}
	
	@Test
	public void testError(){
		try {  
			testError();
		} catch (StackOverflowError e) {  
		    e.printStackTrace();  
		} 
		System.out.println("error print");
	}
	
	@Test
	public void TestRuntimeException(){
		try{
			if(true){
				throw new RuntimeException("exception test");
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		System.out.println("last print");
	}
	
	
	
	@Test
	public void TestRuntimeExceptionNoCheck(){
		if(true){
			throw new RuntimeException("exception test");
		}
		System.out.println("last print");
	}
	
	
	/**
	 * 受检查的异常
	 * @throws FileNotFoundException
	 */
	@Test
	public void needCheckException() throws FileNotFoundException{
	 
		new FileInputStream(new File("name"));
	}
}
