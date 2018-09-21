package com.palace.seeds.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class User {
	public String name="xiaoming";
	public String addr="beijing";
	public Integer age=12;
	public String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	
	public User(String name, Integer age, String addr) {
		super();
		this.name = name;
		this.addr = addr;
		this.age = age;
	}
	public User() {
		
	}
	public String getName() {
		return name;
	}
	public User setName(String name) {
		this.name = name;
		return this;
	}
	public String getAddr() {
		return addr;
	}
	public User setAddr(String addr) {
		this.addr = addr;
		return this;
	}
	public Integer getAge() {
		return age;
	}
	public User setAge(Integer age) {
		this.age = age;
		return this;
	}
	@Override
	public String toString() {
		return "User [name=" + name + ", addr=" + addr + ", age=" + age + ", currTime=" + createTime + "]";
	}
	
	
	
}
