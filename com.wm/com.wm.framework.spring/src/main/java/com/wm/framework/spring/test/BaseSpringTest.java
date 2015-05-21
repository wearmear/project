package com.wm.framework.spring.test;

import java.util.Collection;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wm.common.util.json.DateJsonValueProcessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 描述：<br>
 * 作者：王猛 <br>
 * 修改日期：2014年8月16日上午11:14:19 <br>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class BaseSpringTest {
	protected Logger log = LogManager.getLogger(BaseSpringTest.class);

	protected void print(Object obj) {
		this.print(null, obj);
	}

	protected void print(String ms, Object obj) {
		JsonConfig jsonConfig = new JsonConfig();

		jsonConfig.registerJsonValueProcessor(Date.class,
				new DateJsonValueProcessor());

		// jsonConfig.setJsonPropertyFilter(new HibernatePropertyFilter());
		// jsonConfig.setExcludes(new
		// String[]{"handler","hibernateLazyInitializer"});
		Object result = null;
		if (obj instanceof Collection) {
			result = JSONArray.fromObject(obj, jsonConfig);
		} else {
			result = JSONObject.fromObject(obj, jsonConfig);
		}

		log.debug((null == ms ? "" : "\n" + ms + " ") + result);
	}
}
