package com.palace.seeds.spring.transaction.aspectj;

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
	@Bean(name="tem")
	public JdbcTemplate getTem() {
		return new JdbcTemplate(getDataSource());
	}
	
	@Bean(name="transactionManager")
	public DataSourceTransactionManager transactionManager() {
		return new org.springframework.jdbc.datasource.DataSourceTransactionManager(getDataSource());
	}
	
	public DataSource getDataSource(){
		BasicDataSource ds = new BasicDataSource();
		ds.setUrl("jdbc:mysql://localhost:3306/ds");
		ds.setUsername("root");
		ds.setPassword("111111");
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		return ds;
	}
}
