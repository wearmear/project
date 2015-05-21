package com.wm.framework.sh.controller.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface CheckPermission {

//	String[] value() default "";

	/**
	 * 方法名称: needUserLogin<br>
	 * 描述：是否需要登录 <br>
	 * 作者: 王猛 <br>
	 * 修改日期：2014年11月19日下午6:12:16
	 * @return
	 */
	boolean needUserLogin() default false;
	
	/**
	 * 方法名称: needValid<br>
	 * 描述：是否需要权限验证 <br>
	 * 作者: 王猛 <br>
	 * 修改日期：2014年11月19日下午6:12:04
	 * @return
	 */
	boolean needValid() default false;
}