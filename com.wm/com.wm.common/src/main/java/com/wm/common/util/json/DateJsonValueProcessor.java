package com.wm.common.util.json;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

public class DateJsonValueProcessor implements JsonValueProcessor {

	public DateJsonValueProcessor() {
	}

	public DateJsonValueProcessor(String pattern) {
		this.pattern = pattern;
	}

	private String pattern = "yyyy-MM-dd";

	@Override
	public Object processArrayValue(Object obj, JsonConfig jsonconfig) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object processObjectValue(String s, Object value,
			JsonConfig jsonconfig) {
		try {
			if (value instanceof Timestamp) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
						Locale.UK);
				return sdf.format(value);
			} else if (value instanceof java.sql.Date) {
				SimpleDateFormat sdf = new SimpleDateFormat(pattern,
						Locale.UK);
				return sdf.format(value);
			} else if (value instanceof Date) {
				SimpleDateFormat sdf = new SimpleDateFormat(pattern,
						Locale.UK);
				return sdf.format(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return value;
		}
		return value;
	}

}
