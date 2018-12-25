package com.palace.seeds.spring.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
	

	@Autowired
	public JdbcTemplate tem ;
	
	public void query() {
		System.out.println(tem.queryForMap("select 1 "));
	}
	
	@Transactional
	public void update() {
		tem.update("update tbDict set nCode = nCode+1 ");
		throw new RuntimeException("abc");
	}


}
