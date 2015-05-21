package com.wm.framework.sh.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 描述：<br>
 * 作者：王猛 <br>
 * 修改日期：2014年8月16日下午7:35:12 <br>
 */
public class BaseControllerInterceptor extends HandlerInterceptorAdapter {

	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		BaseController baseController = (BaseController) handlerMethod
				.getBean();
		baseController.handle(request, response);
		return true;
	}
}
