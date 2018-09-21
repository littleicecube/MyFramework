package com.palace.seeds.base.annotation;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;

public class ClassScanner {
	private Logger log = Logger.getLogger(ClassScanner.class);
	private List<ClassTypeFilter> classTypeFilterList =new ArrayList<ClassTypeFilter>();
	private String basePackagePath;

	public void init(){
	}
	public void scan(String str){
		basePackagePath= str.trim().replace(".", File.separator);
		try {
			log.info("###开始扫描路径下的文件");
			Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(basePackagePath);
		    while (dirs.hasMoreElements()) {  
                URL url = dirs.nextElement();  
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {  
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    log.debug("###class base path:"+filePath);  
                    doScanClassFile(new File(filePath));
                }  
            } 
		    log.info("###结束扫描路径下的文件");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//扫描class
	private void doScanClassFile(File file) throws ClassNotFoundException{
		if(file.isDirectory()){
			for(File cFile  : file.listFiles()){
				if(cFile.isDirectory()){
					doScanClassFile(cFile);
				}else{
					classCheck(cFile.getAbsolutePath());
				}
			}
		}else{
			classCheck(file.getAbsolutePath());
		}
	}
	
	//扫描jar包
	private void doScanJar(String filePath){
		
	}
	
	private synchronized void classCheck(String file) throws ClassNotFoundException{
		log.debug("==========================================");
		if(file==null||(file=file.trim()).isEmpty()){
			return ;
		}else{
			if(!file.endsWith(".class")){
				return ;
			}
			log.debug("###class文件的绝度路径:"+file);
			String classQua = convertToRelative(file);
			classQua = classQua.replace(File.separator, ".").substring(0,classQua.length()-6);
			log.debug("###class文件相对路径:"+classQua);
			Class clazz = Thread.currentThread().getContextClassLoader().loadClass(classQua);
			for(ClassTypeFilter filter : classTypeFilterList){
				try{
					filter.doFilte(clazz);
					filter.doFilteAnnotation(clazz.getDeclaredAnnotations());
					filter.doFilteField(clazz.getDeclaredFields());
					filter.doFilteMethod(clazz.getDeclaredMethods());
				}catch(Exception e){
					log.warn(e.getMessage());
				}
			}
		}
	}
	private String convertToRelative(String path){
		return path.substring(path.indexOf(basePackagePath),path.length());
	}
	private synchronized void addClassTypeFilter(ClassTypeFilter  filter){
		classTypeFilterList.add(filter);
	}
	
	
	public static void main(String[] args) {
		String path ="com.palace.seeds";
		ClassScanner scanner = new ClassScanner();
		scanner.addClassTypeFilter(new ClassTypeFilter() {
			@Override
			public void doFilteAnnotation(Annotation[] arr){
				for(Annotation a : arr){
					System.out.println("res:"+a.toString());
				}
			}
			@Override
			public void doFilteField(Field[] arr){
				for(Field a : arr){
					System.out.println("res:"+a.toString());
				}
			}
			
		});
		scanner.scan(path);
	}
	
}
