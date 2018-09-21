package com.palace.sketch.jsengine;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.apache.commons.collections.MapUtils;
import org.junit.Before;
import org.junit.Test;

import com.palace.utils.FileReader;

public class JSEngineTest {
	ScriptEngineManager sem=null;
	ScriptEngine engine=null;
	Invocable invo=null;
	@Before
	public void testBefore() throws Exception{
		printRuntimeJreVersion();
		sem = new ScriptEngineManager();
		engine = sem.getEngineByName("js");
		printEnginInfo(engine);
		
	}
	
	@Test
	public void compairTest() throws Exception, ScriptException{
		Compilable comp = (Compilable) engine;
		CompiledScript com =comp.compile(FileReader.getTextContent("index.js"));
		//ScriptableObject.getProperty(null,null);
		invo =(Invocable)engine;
		invo.invokeMethod(com, "getName",null);
	//	invo.invokeFunction("getName");
		
		 /*localObject1 = ScriptableObject.getProperty(localScriptable2, paramString);
		  285        if (!(localObject1 instanceof Function)) {
		  286          throw new NoSuchMethodException("no such method: " + paramString);
		             }
		       
		  289        Function localFunction = (Function)localObject1;
		  290        Scriptable localScriptable3 = localFunction.getParentScope();
		  291        if (localScriptable3 == null) {
		  292          localScriptable3 = localScriptable1;
		             }
		  294        Object localObject2 = localFunction.call(localContext, localScriptable3, localScriptable2, wrapArguments(paramArrayOfObject));*/
		/*Script localScript
		 if (localScript != null) {
			     return localScript.exec(this, paramScriptable);
			     }*/
		invo =(Invocable)com.getEngine();
		while(true){
			invo.invokeFunction("addTest");
		}
	}

	@Test
	public void systemOutTest() throws Exception, ScriptException{
		engine.eval(new InputStreamReader(JSEngineTest.class.getClassLoader().getResourceAsStream("index.js")));
		invo =(Invocable)engine;
		System.out.println(invo.invokeFunction("getName"));
	}
 
	
	@Test
	public void threadTest(){
		engine.put("name", "xiaoli");
		new Thread(new Runnable() {
			@Override
			public void run() {
				engine.put("name", "xiaohong");
				System.out.println("");
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println(engine.get("name"));
			}
		}).start();
		
		Thread.currentThread().getId();
		try {
			Thread.currentThread().sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void invokTest() throws Exception{
      System.out.println(invo.invokeFunction("getName","xiaoli"));
      System.out.println(invo.invokeFunction("sum",1,4));
      System.out.println(invo.invokeFunction("mul", 1,2,3));
	}
	
	@Test
	public void engineTest(){
		ScriptEngine engine1 = (ScriptEngine) sem.getEngineByName("js");
		ScriptEngine engine2 = (ScriptEngine) sem.getEngineByName("js");
		System.out.println(engine1.hashCode()+"###"+engine2.hashCode());

	}
	@Test
	public void scopeTest(){
		ScriptContext context = engine.getContext();
		context.setAttribute("addr", "beijing",ScriptContext.ENGINE_SCOPE);
		//设置自定义绑定域出错，只能使用系统自定的200,100
		SimpleBindings bindings=new SimpleBindings();
		bindings.put("addr", "tianjin");
		context.setBindings(bindings, 55);
		System.out.println(engine.getBindings(55).get("addr"));
	}
	
	@Test
	public void listMapJS() throws Exception{
		List<Map<String,Object>> listMap=new ArrayList<Map<String,Object>>();
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("name", "xiaohong");
		map.put("age",29);
		listMap.add(map);
		assert listMap.get(0)!=null;
		engine.put("listMap", listMap);
		String code=" 	for(var i=0;i< listMap.size();i++){	"
					+ " 	var map=listMap.get(i);				"
					+ " 	if(map!=null){						"
					+ "			  map.get('age');			"
					+ "		} 									"
					+ " }										";
		Object obj=engine.eval(code);
		System.out.println(obj);
	}

	@Test
	public void mapJS() throws Exception{
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("name", "xiaozhang");
		engine.put("map", map);
		engine.put("MapUtils", new MapUtils());
		Object obj0=engine.eval("MapUtils.getString(map, 'name')");
		Object obj=engine.eval("map.get('name')");
		System.out.println(obj0);
	}
	
	@Test
	public void dateJS() throws Exception{
		Date d = new Date();
		engine.put("date",new Date());
		Object obj = engine.eval("date.getDate()");
		System.out.println(obj);
	}
	
	@Test
	public void simpleTest() throws Exception{
	  ScriptEngineManager sem = new ScriptEngineManager();
      ScriptEngine engine = sem.getEngineByName("js");
      engine.eval(new InputStreamReader(JSEngineTest.class.getClassLoader().getResourceAsStream("index.js")));
      if (engine instanceof Invocable) {
          Invocable invoke = (Invocable) engine; 
          //NoSuchMethodException
          String key = (String) invoke.invokeFunction("getName1");
          System.out.println(key);
      }
	}
	
	
	public void printRuntimeJreVersion(){
		System.out.println("java.runtime.version###"+System.getProperty("java.runtime.version"));
		
	}
	public void printEnv(){
		for(Map.Entry<String,String> ent : System.getenv().entrySet()){
			System.out.println(ent.getKey()+"###"+ent.getValue());
		}
	}
	public void printEnginInfo(ScriptEngine engine){
		ScriptEngineFactory factory = engine.getFactory();
		System.out.println("enginName:"+factory.getEngineName()+"###enginVersion:"+factory.getEngineVersion());
	}
}
