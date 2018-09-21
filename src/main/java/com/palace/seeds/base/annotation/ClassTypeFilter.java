package com.palace.seeds.base.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class ClassTypeFilter {
	
	public void doFilte(Class<Object> clazz){}
	public void doFilteAnnotation(Annotation[] annotationArr ){}
	public void doFilteField(Field[] fieldArr){}
	public void doFilteMethod(Method[] methodArr){}
}
