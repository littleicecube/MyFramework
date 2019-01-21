package com.palace.seeds.db.jta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Properties;

import javax.sql.DataSource;
import javax.transaction.SystemException;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.palace.seeds.model.User;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;


@Configuration
@PropertySource("classpath:/spring/jdbc.properties")
@ComponentScan(basePackages = {"com.palace.seeds.jta"})
class JtaConfig{
	
	@Value("${jdbc.driverClass}") private String driverClass;
	@Value("${jdbc.url}") private String url;
	@Value("${jdbc.user}") private String userName;
	@Value("${jdbc.password}") private String password;

	@Autowired
	Environment environment;
	
	@Bean(name="ds0")
	public DataSource getDataSource0(){
		BasicDataSource ds = new BasicDataSource();
		ds.setUrl(url);
		ds.setUsername(userName);
		ds.setPassword(password);
		System.out.println("==datasource0:"+ds);
		return ds;
	}
	@Bean(name="ds1")
	public DataSource getDataSource1(){
		BasicDataSource ds = new BasicDataSource();
		ds.setUrl("jdbc.url=jdbc:mysql://localhost:3306/ds_1");
		ds.setUsername(userName);
		ds.setPassword(password);
		System.out.println("==datasource1："+ds);
		return ds;
	}
	
	@Bean(name="atomikosDS0")
	public AtomikosDataSourceBean getAtomikosDSBean0(){
		AtomikosDataSourceBean aBean =new AtomikosDataSourceBean();
		aBean.setPoolSize(5);
		aBean.setMinPoolSize(3);
		aBean.setMaxPoolSize(10);
		aBean.setUniqueResourceName("atomikosDS)");
		aBean.setXaDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
		Properties properties = new Properties();
	    properties.put("URL", url);
	    properties.put("user", userName);
	    properties.put("password", password);
	    aBean.setXaProperties(properties);
	    return aBean;
	}
	
	@Bean(name="atomikosDS1")
	public AtomikosDataSourceBean getAtomikosDSBean1(){
		AtomikosDataSourceBean aBean=new AtomikosDataSourceBean();
		aBean.setPoolSize(5);
		aBean.setMinPoolSize(3);
		aBean.setMaxPoolSize(10);
		aBean.setUniqueResourceName("atomikosDS1");
		aBean.setXaDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
		Properties properties = new Properties();
	    properties.put("URL", url);
	    properties.put("user", userName);
	    properties.put("password", password);
	    aBean.setXaProperties(properties);
	    return aBean;
	}
	
	
	
	
	@Bean
	public User getUser(){
		return new User();
	}
	
	 @Bean
     public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
     }
}


@Service
class ActionMain{
	
	@Autowired
	@Qualifier("ds0")
	DataSource ds0;
	
	@Autowired
	@Qualifier("ds1")
	DataSource ds1;
	
	public void theNote(){
		/**
		 一般情况来说连接分为：Connection,TransactionManager,TransactionDefintion;
		 spring将transactionManager封装了，主平台是PlatformTransactionManager,然后在
		 根据连接的不同，指定不同的transaction,如下
		PlatformTransactionManager pm;
			HibernateTransactionManager hm;
			DataSourceTransactionManager dm; 一般数据源的事务管理 
			JtaTransactionManager jtm;		java平台的分布式两阶段提交的事务管理
			JpaTransactionManager jm;		java平台的jpa事务管理
			
		*/
	}
	
	
	public void run() throws Exception{
		Connection con= ds0.getConnection();
		System.out.println("con="+con);
		con.close();
	}
	
	
	/**
	 * 基本事务的开始和提交
	 * @throws Exception
	 */
	public void baseTransaction() throws Exception{
		Connection con =ds0.getConnection();
		con.setAutoCommit(false);
		PreparedStatement  ps =con.prepareStatement(" update ds_0.t_order_0 set user_id=? where order_id=?");
		ps.setInt(1,20);
		ps.setInt(2,1008);
		ps.execute();
		con.commit();
		System.out.println("==baseTransaction end ");
		con.close();
	}
	
	
	/**
	 * 利用spring提供的jdbctemplate来操作事务
	 * 
	 */
	public void trasactionTemplate(){
		//创建jdbcTempalte，用来简化操作sql
		final JdbcTemplate template=new JdbcTemplate(ds0);
		//用来支持模板事务，声明式事务
		TransactionTemplate tt =new TransactionTemplate();
		tt.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
		tt.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		tt.setTransactionManager(new DataSourceTransactionManager(ds0));
		
		tt.execute(new TransactionCallback() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				try {
					template.update("update ds_0.t_order_0 set 	user_id=1211 where order_id=1008");
					if(true)
						throw new RuntimeException();
				} catch (Exception e) {
					e.printStackTrace();
					status.setRollbackOnly();
				}
				return null;
			}
		});
	}
	
	
	//====================================================================================
	/**
	 * 测试分布式事务
	 */
	String ds0Sql="update ds_0.t_order_0 set 	user_id=1221 where order_id=1008";
	String ds1Sql="update ds_1.t_order_0 set 	user_id=1221 where order_id=1108";
	
	@Autowired
	@Qualifier("atomikosDS0")
	AtomikosDataSourceBean atomikosBean0;
	
	@Autowired
	@Qualifier("atomikosDS1")
	AtomikosDataSourceBean atomikosBean1;
	
	public void jtaTransactionTemplate(){
		//创建两个数据源
		final JdbcTemplate tmp0=new JdbcTemplate(atomikosBean0);
		final JdbcTemplate tmp1=new JdbcTemplate(atomikosBean1);
		
		
		//创建Atomikos transaction manager
		UserTransactionManager userTransactionManager = new UserTransactionManager();
		userTransactionManager.setForceShutdown(true);
		
		//创建Atomikos transaction imp
		UserTransactionImp userTransactionImp=null;
		try {
			userTransactionImp=new UserTransactionImp();
			userTransactionImp.setTransactionTimeout(60);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		//创建spring jta的transaction manager
		JtaTransactionManager springTransactionManager=new JtaTransactionManager(userTransactionImp,userTransactionManager);
		
		//创建事务模板
		TransactionTemplate tt=new TransactionTemplate();
		tt.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
		tt.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		tt.setTransactionManager(springTransactionManager);
		tt.execute(new TransactionCallbackWithoutResult(){
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				tmp0.update(ds0Sql);
				if(true)
					status.setRollbackOnly();
				tmp1.update(ds1Sql);
				
				System.out.println("====jta transaction template end");
			}
			
		});
	}
	
}


public class JtaMain {
	
	public static void main(String[] args) throws Exception {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(JtaConfig.class);
		//ctx.register(JtaConfig.class);
	    JtaConfig jtaConfig = ctx.getBean(JtaConfig.class);
	    
	    
	    ActionMain actionMain = ctx.getBean(ActionMain.class);
	    //actionMain.run();
	    //actionMain.baseTransaction();
	    actionMain.trasactionTemplate();
	    //actionMain.jtaTransactionTemplate();
	}
	
}
