package com.palace.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.Test;

import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ClassInhe {

	

	@Test
	public void printVal() {
		Class clazz = NioServerSocketChannel.class;
		System.out.println(clazz.getSimpleName());
		System.out.println(clazz.getSuperclass().getSimpleName());
		System.out.println(clazz.getInterfaces()[0].getName());
	}
	
 @Test
 public void printInhe() {
	 Class clazz = NioServerSocketChannel.class;
	 decInhe(clazz);
 }
 
 public void decInhe(Class clazz) {
	 
	 if(clazz == null) {
		 return ;
	 }
	 StringBuilder sb = new StringBuilder();
	 sb.append(getModifiers(clazz.getModifiers())).append(" class ").append(clazz.getSimpleName());
	 Class supClass = clazz.getSuperclass();
	 if(supClass != null ) {
		 sb.append(" extends ").append(supClass.getSimpleName());
	 }
	 Class[] interfaceArr = clazz.getInterfaces();
	 if(interfaceArr != null && interfaceArr.length > 0) {
		 for(Class inte :interfaceArr) {
			 sb.append(" implements ").append(inte.getSimpleName());
		 }
	 }
	 sb.append("{\r\n");
	 Field[] fieldArr = clazz.getDeclaredFields();
	 if(fieldArr != null && fieldArr.length > 0 ) {
		 for(Field f : fieldArr) {
			 int md = f.getModifiers();
			 sb.append("    ").append(getModifiers(md)).append(" ").append(f.getType().getSimpleName()).append(" ").append(f.getName()).append(";\r\n");
		 }
	 }
	 sb.append(" } ");
	 
	 decInhe(supClass);
	 for(Class inte : interfaceArr) {
		 decInhe(inte);
	 }
	 System.out.println(sb.toString());
 }
 
 public String getModifiers(int i) {
	 StringBuilder sb = new StringBuilder();
	 if(Modifier.isPublic(i)) {
		 sb.append(" public");
	 }
	 if(Modifier.isProtected(i)) {
		 sb.append(" protected");
	 }
	 if(Modifier.isPrivate(i)) {
		 sb.append(" private");
	 }
	 if(Modifier.isStatic(i)) {
		 sb.append(" static");
	 }
	 return sb.toString();
 }
 
 public void printClassInfo(Class claxx) {
	 String className = claxx.getName();
	 StringBuilder sb = new StringBuilder();
	 sb.append(" class ").append(className);
	 for(Class sup : claxx.getClasses()) {
		 if(sup.isInterface()) {
			 sb.append(" implements ").append(sup.getName());
		 }else if(sup.isLocalClass()) {
			 sb.append(" exnteds ").append(sup.getName());
		 }
	 }
	 sb.append("{\r\n");
	 System.out.println(sb);
 }
 
 
 public static class Cla{
		public String name ;
		
	}
}

