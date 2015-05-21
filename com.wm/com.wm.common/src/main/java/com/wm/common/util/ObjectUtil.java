package com.wm.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ObjectUtil {
	private Logger log = LogManager.getLogger(ObjectUtil.class);
	
	public Object getFieldValueByName(String fieldName, Object o) {
		try {
			String firstLetter = fieldName.substring(0, 1).toUpperCase();
			String getter = "get" + firstLetter + fieldName.substring(1);
			Method method = o.getClass().getMethod(getter, new Class[] {});
			Object value = method.invoke(o, new Object[] {});
			return value;
		} catch (Exception e) {
			try {
				Method method = o.getClass().getMethod(fieldName,
						new Class[] {});
				Object value = method.invoke(o, new Object[] {});
				return value;
			} catch (Exception e1) {
				log.error("",e);
				return null;
			}
		}
	}

	public Map<String, Object> getFieldsInfo(Object o) {
		Field[] fields = o.getClass().getDeclaredFields();
		Map<String, Object> result = new HashMap<String, Object>();
		for (int i = 0; i < fields.length; i++) {
			String name = fields[i].getName();
			Object value = getFieldValueByName(fields[i].getName(), o);
			result.put(name, value);
		}
		return result;
	}
	
	/**
	 * 获取属性名数组
	 */
	public String[] getFiledNames(Object o) {
		return (String[]) this.getFiledNames(o.getClass(), 0).toArray();
	}
	
	public List<String> getFiledNames(Class<?> clazz) {
		return this.getFiledNames(clazz, 0);
	}
	
	/**
	 * 方法名称: getFiledNames<br>
	 * 描述：获取属性名数组 <br>
	 * 作者: 王猛 <br>
	 * 修改日期：2015年3月23日下午6:12:20
	 * @param clazz
	 * @param parentCount	父级个数
	 * @return
	 */
	public List<String> getFiledNames(Class<?> clazz, int parentCount) {
		List<String> fieldNames = new ArrayList<String>();
		Field[] fields = clazz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			boolean isStatic = Modifier.isStatic(field.getModifiers());
			if (isStatic) {
				continue;
			}
//			boolean isFinal = Modifier.isFinal(field.getModifiers()));
//			if (isFinal) {
//				continue;
//			}
			fieldNames.add(field.getName());
		}
		for (int i = 0; i < parentCount; i++) {
			Class<?> parentClass = clazz.getSuperclass();
			if (null != parentClass) {
				List<String> parentFieldNames = this.getFiledNames(parentClass, 0);
				fieldNames.addAll(parentFieldNames);
			}
		}
		return fieldNames;
	}

	/**
	 * 获取对象的所有属性值，返回一个对象数组
	 * */
	public Object[] getFiledValues(Object o) {
		String[] fieldNames = this.getFiledNames(o);
		Object[] value = new Object[fieldNames.length];
		for (int i = 0; i < fieldNames.length; i++) {
			value[i] = this.getFieldValueByName(fieldNames[i], o);
		}
		return value;
	}

	public void setFiledValue(Object obj, String filedName, Object value) {
		if (null == value) {
			return;
		}
		try {
			filedName = filedName.substring(0, 1).toUpperCase()
					+ filedName.substring(1);
			Class<?> c = value.getClass();
			if (value instanceof java.sql.Date) {
				c = java.util.Date.class;
				java.sql.Date sqlDate = (Date) value;
				value = new java.util.Date(sqlDate.getTime());
			}
			Method m = obj.getClass().getMethod("set" + filedName, c);
			m.invoke(obj, value);
		} catch (Exception e) {
			log.error("",e);
		}
	}

	public void setFiledValues(Object obj, String[] fieldNames, Object[] values) {
		for (int i = 0; i < fieldNames.length; i++) {
			this.setFiledValue(obj, fieldNames[i], values[i]);
		}
	}
}
