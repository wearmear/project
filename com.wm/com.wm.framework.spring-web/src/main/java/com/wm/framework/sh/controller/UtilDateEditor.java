package com.wm.framework.sh.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.propertyeditors.PropertiesEditor;
import org.springframework.util.StringUtils;

public class UtilDateEditor extends PropertiesEditor {
	private String pattern = "yyyy-MM-dd";
	private boolean allowEmpty = true;
	private int exactDateLength = -1;
	
	public UtilDateEditor(){}
	
	public UtilDateEditor(String pattern){
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
				setValue(new Date(dateFormat.parse(text).getTime()));
			} catch (ParseException ex) {
				throw new IllegalArgumentException("Could not parse date: "
						+ ex.getMessage(), ex);
			}
		}
	}

	public String getAsText() {
		Date value = (Date) getValue();
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return ((value != null) ? dateFormat.format(value) : "");
	}
	
	public static void main(String[] args) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date = dateFormat.parse("2014-10-29");
		Date t = new Date(date.getTime());
		System.out.println(date.getTime());
		System.out.println(t.getTime());
		System.out.println(t);
	}
}
