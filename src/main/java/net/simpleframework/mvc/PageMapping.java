package net.simpleframework.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface PageMapping {

	/**
	 * 获取Page的url路径
	 * 
	 * @return
	 */
	String url();

	/**
	 * 装载高优先级的Page类
	 * 
	 * @return
	 */
	int priority() default 0;

	String title() default "";
}