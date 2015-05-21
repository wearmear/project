package com.wm.framework.sh.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wm.common.lang.StringUtil;

/**
 * 描述：<br>
 * 作者：王猛 <br>
 * 修改日期：2014年8月16日下午1:33:05 <br>
 */
public abstract class BaseController {
	private static final Logger log = LogManager.getLogger(BaseController.class);

	protected String uuid;
	protected HttpServletRequest request;
	protected HttpServletResponse response;

	private final static String current_nav_key = "CURRENT_NAV";
	protected final static int currentNav_index = 0;
	protected final static int currentNav_find_user = 1;
	protected final static int currentNav_find_travel = 2;
	protected final static int currentNav_booking_hotel = 3;
	protected final static int currentNav_booking_airTickets = 4;
	protected final static int currentNav_find_blog = 5;
	protected final static int currentNav_shop = 6;
	protected final static int currentNav_myspace = 7;

	public void handle(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		this.uuid = this.getCookie("JSESSIONID");

		StringBuffer info = new StringBuffer();
		info.append("\n").append(uuid).append("\thttp request ").append(request.getMethod())
				.append(" ").append(request.getRequestURL());
		info.append("\n").append("params: ")
				.append(JSONObject.fromObject(request.getParameterMap()));
		log.info(info);
//		StringBuffer cookieStr = new StringBuffer();
//		cookieStr.append("[");
//		for (int i = 0; i < request.getCookies().length; i++) {
//			Cookie cookie = request.getCookies()[i];
//			cookieStr.append(cookie.getName()).append(":")
//					.append(cookie.getValue());
//			if (i < request.getCookies().length-1) {
//				cookieStr.append(", ");
//			}
//		}
//		cookieStr.append("]");
//		info.append("\n").append("cookies: ").append(cookieStr);
	}

	/**
	 * 方法名称: getCookie<br>
	 * 描述：获取cookie值 <br>
	 * 作者: 王猛 <br>
	 * 修改日期：2015年2月12日下午5:35:23
	 * @param cookieName
	 * @return
	 */
	protected String getCookie(String cookieName) {
		if (null!=request.getCookies()) {
			int count = request.getCookies().length;
			for (int i = 0; i < count; i++) {
				Cookie itemCookie = request.getCookies()[i];
				if (cookieName.equalsIgnoreCase(itemCookie.getName())) {
					return itemCookie.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * 获取request参数
	 * 
	 * @param key
	 * @return
	 */
	protected String getRequestParameter(String key) {
		String value = request.getParameter(key);
		if (null != value) {
			boolean isCn = StringUtil.isHasCn(value);
			if (!isCn) {
				try {
					value = new String(value.getBytes("ISO8859-1"), "UTF-8");
				} catch (UnsupportedEncodingException e) {
				}
			}
		}
		return value;
	}
	
	protected JSONObject getRequestJsonParams() {
		if (request == null) {
			return null;
		}
		JSONObject params = new JSONObject();
		Enumeration<String> keys = request.getParameterNames();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String[] values = request.getParameterValues(key);
			if (values.length == 1) {
				params.put(key, values[0]);
			} else {
				params.put(key, values);
			}
		}
		return params;
	}

	/**
	 * request存值
	 * 
	 * @param String
	 * @param Object
	 */
	protected void setRequestAttibute(String key, Object object) {
		request.setAttribute(key, object);
	}

	/**
	 * 方法名称: setCurrentNav<br>
	 * 描述：设置当前访问所属的导航标示 作者: 王猛 修改日期：2014年7月29日下午1:34:24
	 * 
	 * @param currentNav
	 */
	protected void setCurrentNav(int currentNav) {
		request.getSession().setAttribute(current_nav_key, currentNav);
	}

	protected HttpSession getSession() {
		return request.getSession();
	};

	/**
	 * 返回json数据
	 * 
	 * @param
	 * @throws IOException
	 */
//	protected void writeJSON(Object object) throws IOException {
//		response.setCharacterEncoding("UTF-8");
//		response.setContentType("text/json;charset=UTF-8");
//		response.setHeader("cache-control", "no-cache");
//		response.setHeader("pragma", "no-cache");
//		response.setDateHeader("expires", 0L);
//
//		response.getWriter().write(
//				JSONUtil.convertJSONObject(object).toString());
//		response.getWriter().flush();
//		response.getWriter().close();
//	}

	protected void setSessionAttribute(String key, Object value) {
		request.getSession().setAttribute(key, value);
	}

	protected Object getSessionAttribute(String key) {
		return request.getSession().getAttribute(key);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
