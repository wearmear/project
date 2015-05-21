package com.wm.framework.sh.entity;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.beans.propertyeditors.PropertiesEditor;
import org.springframework.util.StringUtils;

public class TimestampEditor extends PropertiesEditor {
	private String pattern = "yyyy-MM-dd HH:mm:ss";
	private boolean allowEmpty = true;
	private int exactDateLength = -1;
	
	public TimestampEditor(){}
	
	public TimestampEditor(String pattern){
		this.pattern = pattern;
	}

	public void setAsText(String text) throws IllegalArgumentException {
		if ((this.allowEmpty) && (!(StringUtils.hasText(text)))) {
			setValue(null);
		} else {
			if ((text != null) && (this.exactDateLength >= 0)
					&& (text.length() != this.exactDateLength)) {
				throw new IllegalArgumentException(
						"Could not parse date: it is not exactly"
								+ this.exactDateLength + "characters long");
			}
			try {
				SimpleDateFormat dateFormat;
				if (text.length() == 10) {
					dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				}else {
					dateFormat = new SimpleDateFormat(pattern);
				}
				setValue(new Timestamp(dateFormat.parse(text).getTime()));
			} catch (ParseException ex) {
				throw new IllegalArgumentException("Could not parse date: "
						+ ex.getMessage(), ex);
			}
		}
	}

	public String getAsText() {
		Timestamp value = (Timestamp) getValue();
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return ((value != null) ? dateFormat.format(value) : "");
	}
	
	public static void main(String[] args) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date date = dateFormat.parse("2014-10-29 17:55:01");
		Timestamp t = new Timestamp(date.getTime());
		System.out.println(date.getTime());
		System.out.println(t.getTime());
		System.out.println(t);
	}
}
