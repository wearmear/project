package com.wm.framework.util.json;

import java.util.Date;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import com.wm.common.util.json.DateJsonValueProcessor;

public class JSONUtil {
	public static JSONObject convertJSONObject(Object obj) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerJsonValueProcessor(Date.class,
				new DateJsonValueProcessor());
		jsonConfig.setJsonPropertyFilter(new HibernatePropertyFilter());
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.NOPROP);
		jsonConfig.setExcludes(new String[] { "handler",
				"hibernateLazyInitializer" });
		return JSONObject.fromObject(obj, jsonConfig);
	}

	public static JSONArray convertJSONArray(Object obj) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerJsonValueProcessor(Date.class,
				new DateJsonValueProcessor());
		jsonConfig.setJsonPropertyFilter(new HibernatePropertyFilter());
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.NOPROP);
		return JSONArray.fromObject(obj, jsonConfig);
	}

	public static JSON convertJSON(Object obj) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerJsonValueProcessor(Date.class,
				new DateJsonValueProcessor());
		jsonConfig.setJsonPropertyFilter(new HibernatePropertyFilter());
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.NOPROP);
		return JSONSerializer.toJSON(obj, jsonConfig);
	}
}
