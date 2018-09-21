package com.palace.experience.tomcat;

import java.util.LinkedHashSet;
import java.util.Set;

public class Simple {
	
/**
 * 从Embed Tomcat看webapp的启动
 * 1)启动分为加载webppp中的资源
 * 2)启动网络服务
 */

/**
  第一部分启动加载webapp.
 1)tomcat的启动主要过程可以从其conf/server.xml配置文件中得到,server.xml中的一个简单配置如下:
 <Server port="8005" shutdown="SHUTDOWN">
  <Service name="Catalina">
	<Connector port="8080" protocol="HTTP/1.1" connectionTimeout="20000"  redirectPort="8443" />
	<Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />
	<Engine name="Catalina" defaultHost="localhost">
		<Realm className="org.apache.catalina.realm.LockOutRealm">
			<Realm className="org.apache.catalina.realm.UserDatabaseRealm" resourceName="UserDatabase"/>
		</Realm>
		<Host name="mydomain.google.com"  appBase="" unpackWARs="true" autoDeploy="true">
			<Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"  prefix="pre." suffix=".log" timestamp="true" pattern="%h %l %u %t &quot;%r&quot; %s %b" resolveHosts="false"/>
			<Context path="DataDashboard" docBase="/user/local/appName" caseSensitive="false" ></Context>
		</Host>
	</Engine>
  </Service>
 </Server>
 2)Embed Tomcat的启动代码如下
	Tomcat tom = new Tomcat();
	tom.setPort(8080);
	//创建一个标准server
	StandardServer server = new StandardServer();
	
	//创建一个标准service,并将其作为参数添加到server中
	StandardService service = new StandardService();
	server.addService(service);
	
	//创建一个标准engine,并将其作为参数添加到service中
	StandardEngine engine = new StandardEngine(); 
	service.setContainer(engine);
	
	//创建一个标准host,并将其作为参数添加到engine中
	StandardHost host = new StandardHost();
	host.setDomain("mydomain.google.com");
	engine.addChild(host);
	
	//创建一个标准容器,这个容器用来描述我们要发布的app
	//设置其地址如:/user/local/appName
	//设置其对外path如DataDashboard
	StandardContext ctx = new StandardContext();
	ctx.setPath("DataDashboard");
	ctx.setDocBase("/user/local/appName");
	ctx.addLifecycleListener(new DefaultWebXmlListener());
	//访问地址如:http://mydomain.google.com:8080/DataDashboard/static/index.html
	//将创建的appContext添加到engine中
	host.addChild(ctx);
	//启动tomcat
	tom.start();
        
        可以看到的是上述代码的创建流程大体上是和conf/service.xml中结构相匹配的
    1)创建server-->service-->engien-->host-->AppContext
    2)主要节点创建完成后,启动server,server.start();
    	2.1)server						service						engine						host			appContext
    		|start
    		|init(遍历service)			|init						|init
    		|							|启动connecter
        2.1)server.start方法-->server.init方法-->遍历添加的service-->调用service.init方法-->获取添加的engine-->调用engine.init方法
        	
       
 */
	static Set<String> getJarSet() {
		Set<String> patterns = new LinkedHashSet<>();
		patterns.add("xerces-J_1.4.0*.jar");
		patterns.add("commons-logging-1.1*.jar");
		patterns.add("ant-*.jar");
		patterns.add("aspectj*.jar");
		patterns.add("commons-beanutils*.jar");
		patterns.add("commons-codec*.jar");
		patterns.add("commons-collections*.jar");
		patterns.add("commons-dbcp*.jar");
		patterns.add("commons-digester*.jar");
		patterns.add("commons-fileupload*.jar");
		patterns.add("commons-httpclient*.jar");
		patterns.add("commons-io*.jar");
		patterns.add("commons-lang*.jar");
		patterns.add("commons-logging*.jar");
		patterns.add("commons-math*.jar");
		patterns.add("commons-pool*.jar");
		patterns.add("geronimo-spec-jaxrpc*.jar");
		patterns.add("h2*.jar");
		patterns.add("hamcrest*.jar");
		patterns.add("hibernate*.jar");
		patterns.add("jmx*.jar");
		patterns.add("jmx-tools-*.jar");
		patterns.add("jta*.jar");
		patterns.add("junit-*.jar");
		patterns.add("httpclient*.jar");
		patterns.add("log4j-*.jar");
		patterns.add("mail*.jar");
		patterns.add("org.hamcrest*.jar");
		patterns.add("slf4j*.jar");
		patterns.add("tomcat-embed-core-*.jar");
		patterns.add("tomcat-embed-logging-*.jar");
		patterns.add("tomcat-jdbc-*.jar");
		patterns.add("tomcat-juli-*.jar");
		patterns.add("tools.jar");
		patterns.add("wsdl4j*.jar");
		patterns.add("xercesImpl-*.jar");
		patterns.add("xmlParserAPIs-*.jar");
		patterns.add("xml-apis-*.jar");
		patterns.add("antlr-*.jar");
		patterns.add("aopalliance-*.jar");
		patterns.add("aspectjrt-*.jar");
		patterns.add("aspectjweaver-*.jar");
		patterns.add("classmate-*.jar");
		patterns.add("dom4j-*.jar");
		patterns.add("ecj-*.jar");
		patterns.add("ehcache-core-*.jar");
		patterns.add("hibernate-core-*.jar");
		patterns.add("hibernate-commons-annotations-*.jar");
		patterns.add("hibernate-entitymanager-*.jar");
		patterns.add("hibernate-jpa-2.1-api-*.jar");
		patterns.add("hibernate-validator-*.jar");
		patterns.add("hsqldb-*.jar");
		patterns.add("jackson-annotations-*.jar");
		patterns.add("jackson-core-*.jar");
		patterns.add("jackson-databind-*.jar");
		patterns.add("jandex-*.jar");
		patterns.add("javassist-*.jar");
		patterns.add("jboss-logging-*.jar");
		patterns.add("jboss-transaction-api_*.jar");
		patterns.add("jcl-over-slf4j-*.jar");
		patterns.add("jdom-*.jar");
		patterns.add("jul-to-slf4j-*.jar");
		patterns.add("log4j-over-slf4j-*.jar");
		patterns.add("logback-classic-*.jar");
		patterns.add("logback-core-*.jar");
		patterns.add("rome-*.jar");
		patterns.add("slf4j-api-*.jar");
		patterns.add("spring-aop-*.jar");
		patterns.add("spring-aspects-*.jar");
		patterns.add("spring-beans-*.jar");
		patterns.add("spring-boot-*.jar");
		patterns.add("spring-core-*.jar");
		patterns.add("spring-context-*.jar");
		patterns.add("spring-data-*.jar");
		patterns.add("spring-expression-*.jar");
		patterns.add("spring-jdbc-*.jar,");
		patterns.add("spring-orm-*.jar");
		patterns.add("spring-oxm-*.jar");
		patterns.add("spring-tx-*.jar");
		patterns.add("snakeyaml-*.jar");
		patterns.add("tomcat-embed-el-*.jar");
		patterns.add("validation-api-*.jar");
		patterns.add("xml-apis-*.jar");
		return patterns;
	}
}
