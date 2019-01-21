package com.palace.seeds.spring.aop;

import java.sql.Connection;
import java.sql.Statement;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.annotation.AnnotationClassFilter;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

public class TransactionAopTest {
	
	static BasicDataSource ds = getDS();
	
	
	/**
	 *	TranactionManger主要作用
	 *		1)获取一个connection
	 *		2)取消事务的自动提交
	 *		3)设置一些参数到当前的connection上,如超时时间,是否是只读事务
	 *		4)是否开启新的事务
	 *		5)根据业务的执行结果来决定connection事务的提交和回滚
	 */
	
	
	@Test
	public void DataSourceTransactionManager() {
		
		try {
			BasicDataSource ds = getDS();
			//将ds作为参数传入到DataSourceTransactionManager,表示要DataSourceTransactionManager去管理ds中的connection的事务
			DataSourceTransactionManager tx = new DataSourceTransactionManager(ds);
			//根据ds获取一个connection,利用ThreadLocal(TransactionSynchronizationManager.resources)和当前线程绑定,其中key是ds的引用
			//并取消自动提交con.setAutoCommit(false);
			//设置一些connection的参数信息
			TransactionStatus status = tx.getTransaction(null);
			//静态方法,以ds引用为key从TheadLocal中获取上一步中和线程绑定的connection
			Connection con = DataSourceUtils.getConnection(ds);
			Statement stat = con.createStatement();
			stat.execute("update ds.tbDict set nCode = nCode+1");
			if(1 == 1) {
				//如果想回滚只需要调用tx的回滚方法即可,内部处理时会获取和当前线程绑定的connection然后回滚
				tx.rollback(status);
			}else {
				tx.commit(status);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void DataSourceTransactionManagerAndJDBCTemplate() {
		
		try {
			BasicDataSource ds = getDS();
			//在没有DataSourceTransactionManager的配合下JdbcTemplate的操作是自动提交的
			//1)从ds中获取一个connection
			//2)执行update sql并自动提交
			//3)释放链接
			JdbcTemplate plate = new JdbcTemplate(ds);
			plate.update("update ds.tbDict set nCode = nCode+1");
			
			
			//将ds作为参数传入到DataSourceTransactionManager,表示要DataSourceTransactionManager去管理ds中的connection的事务
			DataSourceTransactionManager tx = new DataSourceTransactionManager(ds);
			//根据ds获取一个connection,利用ThreadLocal(TransactionSynchronizationManager.resources)和当前线程绑定,其中key是ds的引用
			//并取消自动提交con.setAutoCommit(false);
			//设置一些connection的参数信息
			TransactionStatus status = tx.getTransaction(null);
			//sql语句在执行时会判断是否存在和当前线程绑定的connection,由于上一步已经将一个connection和当前线程绑定
			//在此执行时会获取和线程绑定的设置为非自动提交的connection用来执行sql语句
			//语句执行完成后,由于connection事务被DataSourceTransactionManager设置为非自动提交故不会自动提交
			plate.update("update ds.tbDict set nCode = nCode+1");
			if(1 == 1) {
				//如果不在此手动提交事务,上面的sql执行并不会生效
				tx.commit(status);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		//需要被代理的对象
		UserService us = new UserService();
		//用于创建代理类的工厂
		ProxyFactory proxyFactory = new ProxyFactory();
		//代理当前class
		proxyFactory.setProxyTargetClass(true);
		//设置Advisor
		proxyFactory.addAdvisors(getAdvisor());
		proxyFactory.setFrozen(false);
		proxyFactory.setTargetSource(new SingletonTargetSource(us));
		Object proxy = proxyFactory.getProxy(Thread.currentThread().getContextClassLoader());
		
		us = (UserService)proxy;
		us.query();
		System.out.println("########################");
		us.update();
		System.out.println("########################");

 	}

	//advisor中定义了要拦截那些方法,以及过滤到被拦截的方法后执行那些操作
	public static Advisor[] getAdvisor() {
		BeanFactoryTransactionAttributeSourceAdvisor advisor = new BeanFactoryTransactionAttributeSourceAdvisor();
		//添加一些事务需要的默认或者自定义的参数,如超时时间,是否只读事务
		advisor.setTransactionAttributeSource(transactionAttributeSource());
		//设置对于拦截到的方法应用那些操作
		advisor.setAdvice(transactionInterceptor());
		//配置那些标示的方法是需要被进行事务操作
		advisor.setClassFilter(new AnnotationClassFilter(Transactional.class));
		return new Advisor[] {advisor};
	}
	public static TransactionInterceptor transactionInterceptor() {
		TransactionInterceptor interceptor = new TransactionInterceptor();
		interceptor.setTransactionAttributeSource(transactionAttributeSource());
		//创建一个事务管理器用来管理数据源中的连接
		interceptor.setTransactionManager(getTX());
		return interceptor;
	}
	public static TransactionAttributeSource transactionAttributeSource() {
		return new AnnotationTransactionAttributeSource();
	}
	public static DataSourceTransactionManager getTX() {
		return new DataSourceTransactionManager(ds);
	}
	
	@Transactional
	public static class UserService {
		public String query() {
			System.out.println("####query exe");
			return "queryRet:xiaoming";
		}
		
		public void update() {
			System.out.println("###update exe start");
			new JdbcTemplate(ds).update("update ds.tbDict set nCode = nCode+1");
			if(1 == 1) {
				throw new RuntimeException("update exception");
			}
			System.out.println("###update exe end");
		}

	}
	
	
	
	public static BasicDataSource getDS() {
		BasicDataSource ds = new BasicDataSource();
		ds.setUrl("jdbc:mysql://localhost:3306/?");
		ds.setUsername("root");
		ds.setPassword("111111");
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		return ds;
	}
 
}


