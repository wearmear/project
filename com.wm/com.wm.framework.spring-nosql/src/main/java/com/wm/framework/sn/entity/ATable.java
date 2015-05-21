package com.wm.framework.sn.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface ATable {
//	String value() default "";
	/**
	 * 方法名称: name<br>
	 * 描述：表名 <br>
	 * 作者: 王猛 <br>
	 * 修改日期：2015年3月20日下午3:08:07
	 * 
	 * @return
	 */
	public String name() default "";

	/**
	 * 方法名称: space<br>
	 * 描述：空间名 <br>
	 * 作者: 王猛 <br>
	 * 修改日期：2015年3月20日下午3:08:15
	 * 
	 * @return
	 */
	public String space() default "";
}
