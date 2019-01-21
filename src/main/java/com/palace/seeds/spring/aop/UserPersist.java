package com.palace.seeds.spring.aop;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;

@Service
public class UserPersist {
	@Autowired
	public DataSource ds;
	@Bean(name="ds")
	public DataSource getDataSource1(){
		BasicDataSource ds = new BasicDataSource();
		ds.setUrl("jdbc:mysql://localhost:3306/ds");
		ds.setUsername("root");
		ds.setPassword("111111");
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		return ds;
	}
	
	@Bean(name="tem")
	public JdbcTemplate getTem() {
		return new JdbcTemplate(ds);
	}
	
	@Bean(name="transactionManager")
	@DependsOn("ds")
	public DataSourceTransactionManager transactionManager() {
		return new org.springframework.jdbc.datasource.DataSourceTransactionManager(ds);
	}
	
}
