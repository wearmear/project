package com.wm.framework.sh.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wm.common.entity.JsonResult;
import com.wm.framework.util.json.JSONUtil;

public class ResultJsonUtil {
	private static final Logger log = LogManager
			.getLogger(ResultJsonUtil.class);

	public ResultJsonUtil() {
	}

	public ResultJsonUtil(String uuid) {
		this.uuid = uuid;
	}

	private String uuid = "";
	protected JsonResult jsonResult;

	public JsonResult getJsonResult() {
		if (null == this.jsonResult) {
			this.jsonResult = new JsonResult();
		}
		return jsonResult;
	}

	/**
	 * 方法名称: returnJSONSuccess<br>
	 * 描述：请求成功 作者: 王叶 修改日期：2014-7-22下午3:12:49
	 * 
	 * @throws IOException
	 */
	public String returnJSONSuccess(HttpServletResponse response, Object object)
			throws IOException {
		this.returnJSONSuccess(response, object, true);
		return null;
	}

	public String returnJSONSuccess(HttpServletResponse response,
			Object object, boolean needClose) throws IOException {
		this.jsonResult = this.getJsonResult();
		this.jsonResult.setSuccess(true);
		this.jsonResult.setData(object);
		this.writeJSON(response, this.jsonResult, needClose);
		return null;
	}

	public String returnNoTempleteSuccess(HttpServletResponse response,
			Object object) throws IOException {
		this.writeJSON(response, object);
		return null;
	}

	/**
	 * 方法名称: returnJSONFailure<br>
	 * 描述：请求失败 作者: 王叶 修改日期：2014-7-22下午3:14:19
	 * 
	 * @param object
	 * @throws IOException
	 */
	public String returnJSONFailure(HttpServletResponse response, String ms)
			throws IOException {
		this.jsonResult = this.getJsonResult();
		this.jsonResult.setSuccess(false);
		this.jsonResult.setMs(ms);
		this.writeJSON(response, this.jsonResult);
		return null;
	}

	/**
	 * 返回json数据
	 * 
	 * @param
	 * @throws IOException
	 */
	public void writeJSON(HttpServletResponse response, Object object)
			throws IOException {
		this.writeJSON(response, object, true);
	}

	public void writeJSON(HttpServletResponse response, Object object,
			boolean needClose) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/json;charset=UTF-8");
		response.setHeader("cache-control", "no-cache");
		response.setHeader("pragma", "no-cache");
		response.setDateHeader("expires", 0L);

		String result = JSONUtil.convertJSON(object).toString();
		log.info("\n " + uuid + "\t http response " + response.getStatus()
				+ ": " + result);

		response.getWriter().write(result);
		response.getWriter().flush();
		if (needClose) {
			response.getWriter().close();
		}
	}

	/**
	 * 返回Tree json数据
	 * 
	 * @param
	 * @throws IOException
	 */
	// public void writeTreeJSON(HttpServletResponse response, Object object)
	// throws IOException{
	// response.setCharacterEncoding("UTF-8");
	// response.setContentType("text/json;charset=UTF-8");
	// response.setHeader("cache-control", "no-cache");
	// response.setHeader("pragma", "no-cache");
	// response.setDateHeader("expires", 0L);
	//
	//
	// String result = JSONUtil.convertJSON(object).toString();
	//
	// log.info(uuid + "\nhttp response "+response.getStatus()+": " + result);
	//
	// response.getWriter().write(result);
	// response.getWriter().flush();
	// response.getWriter().close();
	//
	// }

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
