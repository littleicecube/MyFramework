package com.jdk.newFeature;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import org.junit.Test;


public class CompleteFeature {

	@Test
	public void func() throws Exception {
		
	}
	
	public static void callFunc(Function<String,String> func) {
		
	}
	
	@Test
	public void whenComp() throws Exception {
		CompletableFuture<String> com = CompletableFuture.completedFuture("string");
        //Function<? super T, ? extends CompletionStage<U>> fn
		com.thenCompose(str->{System.out.println(str) ; return new CompletableFuture<>();});
		System.out.println("===end===");
		Thread.currentThread().sleep(100*1000);
	}
	
	@Test
	public void whenCompTest() throws Exception, ExecutionException {
		CompletableFuture<String> cmp = new CompletableFuture<>();
		CompletableFuture<String> cmp1 = cmp.whenComplete((result,error)->System.out.println("complete1"));
		System.out.println(cmp.equals(cmp1));
		System.out.println(cmp1.get());
	}
 
	@Test
	public void simpleCompletTest() throws Exception, ExecutionException {
		CompletableFuture<String> cmp = new CompletableFuture();
		new Thread(()->{
			try {
				Thread.currentThread().sleep(60000);
			} catch (InterruptedException e) {
			}
			System.out.println("innerThread end ");
			cmp.complete("end");
		}).start();
		System.out.println("outerRes start");
		String res = cmp.get();
		System.out.println("outerRes:"+res);
	}
}
